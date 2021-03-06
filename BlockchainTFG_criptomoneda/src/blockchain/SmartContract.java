package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.swing.JOptionPane;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;

public class SmartContract{

	private String IDsmartContract;
	private long fecha;
	private int cantidad;
	private String PK_remitente;
	private String PK_receptor;
	private byte[] firmaTransaccion;
	private int id = 0;
	
	public SmartContract() {
		
	}
	
	public SmartContract(long pFecha, int pCant, String pRemitente, String pReceptor) {
		IDsmartContract = StringUtils.applySha3_256(pFecha + pCant + pRemitente + pReceptor + this.id);
		fecha = pFecha;
		cantidad = pCant;
		PK_remitente = pRemitente;
		PK_receptor = pReceptor;
		try {
			while(databaseControl.contratoHashExiste(IDsmartContract)) {
				id = id + 1;
				IDsmartContract = StringUtils.applySha3_256(pFecha + pCant + pRemitente + pReceptor + this.id);
			}
		} catch (Exception e) {}
	}
	
	public void ejecutarContrato() {
		
		if(databaseControl.haSidoConfirmado(this.IDsmartContract) && !databaseControl.haSidoEjecutado(this.IDsmartContract) && esContratoValido() 
				&& databaseControl.existenRemitenteYReceptor(this.IDsmartContract) && !databaseControl.contratoEliminado(this.IDsmartContract)) {
			
			Bloque bl = null;
			try {
				bl = new Bloque(databaseControl.getHashUltimoBloque());
				bl.setContratoConfirmado("");
				bl.setContratoEjecutado("true");
				bl.setContratoPorEliminar("");
				bl.anadirContrato(this);
			} catch (Exception e1) {}
			
			Cartera carteraRemitente = new Cartera();
			carteraRemitente.setClavePublica((PublicKey) StringUtils.getClaveDesdeString(PK_remitente, true));
			
			Transaccion tranTemp = carteraRemitente.enviarFondosSmartContract((PublicKey) 
					StringUtils.getClaveDesdeString(PK_receptor, true), cantidad, firmaTransaccion, IDsmartContract);
			if(tranTemp != null) { //Si la transaccion es correcta
				bl.anadirTransaccion(tranTemp);
			
				if(bl.getTransacciones().size() > 0) //anadir tran a la lista de transacciones
					ProgramaPrincipal.getTransacciones().add(bl.getTransacciones().get(0));
				
				if(ProgramaPrincipal.anadirBloque(bl)) { //si la cadena es valida, anadir todo a la BD
					try {
						databaseControl.insertarTransaccion(bl.getTransacciones().get(0));
						databaseControl.insertarBloque(bl);
					} catch (Exception exc) {
						//exc.printStackTrace();
					}
				}
				else { //si no es valida, no a�adir a la bd y borrar bloque y tran de las listas en tiempo de ejecucion
					ProgramaPrincipal.getTransacciones().remove(bl.getTransacciones().get(0));
					ProgramaPrincipal.getBlockchain().remove(bl);
				}
				System.out.println("Se acaba de ejecutar un smart contract.");
			}
			else { //si la transaccion NO es correcta (supera el saldo de la cartera...)
				bl.anadirTransaccion(carteraRemitente.enviarFondosSmartContract((PublicKey) 
						StringUtils.getClaveDesdeString(PK_receptor, true), 0, firmaTransaccion, IDsmartContract)); //enviar cero
			
				if(bl.getTransacciones().size() > 0) //anadir tran a la lista de transacciones
					ProgramaPrincipal.getTransacciones().add(bl.getTransacciones().get(0));
				
				if(ProgramaPrincipal.anadirBloque(bl)) { //si la cadena es valida, anadir todo a la BD
					try {
						databaseControl.insertarTransaccion(bl.getTransacciones().get(0));
						databaseControl.insertarBloque(bl);
					} catch (Exception exc) {
						//exc.printStackTrace();
					}
				}
				else { //si no es valida, no a�adir a la bd y borrar bloque y tran de las listas en tiempo de ejecucion
					ProgramaPrincipal.getTransacciones().remove(bl.getTransacciones().get(0));
					ProgramaPrincipal.getBlockchain().remove(bl);
				}	
			}			
		}// Fin if ha sido confirmado, no ejecutado, los actores siguen existiendo y es valido
		else if(!esContratoValido()) {
			JOptionPane.showMessageDialog(null, "El registro de los smart contracts ha sido manipulado.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else if(!databaseControl.existenRemitenteYReceptor(this.IDsmartContract) || !databaseControl.haSidoConfirmado(this.IDsmartContract)){
			SmartContract sc = databaseControl.getContrato(this.IDsmartContract);
			Bloque bl = null;
			try {
				bl = new Bloque(databaseControl.getHashUltimoBloque());
				bl.setContratoConfirmado("");
				bl.setContratoEjecutado("");
				bl.setContratoPorEliminar("true");
				bl.anadirContrato(sc);
				if(ProgramaPrincipal.anadirBloque(bl)) {
					databaseControl.insertarBloque(bl);
				}
			} catch (Exception e1) {}
		}
		
		//Si se ha ejecutado correctamente o no, hay que borrarlo del programa y de la BD
		ProgramaPrincipal.borrarContrato(this.IDsmartContract);		
	}
	
	public void generarFirmaTransaccionContract(PrivateKey pClavePrivada, String pRemitente, String pReceptor, float pValor) {
		String datos = pRemitente + pReceptor + Float.toString(pValor);
		this.firmaTransaccion = StringUtils.applyQTESLASig(pClavePrivada, datos);
		//this.IDsmartContract = StringUtils.applySha3_256(fecha + cantidad + pRemitente + pReceptor + firmaTransaccion); //evitar la manipulacion de la firma en la BD
	}
	
	public boolean esContratoValido() {
		String id = StringUtils.applySha3_256(fecha + cantidad + PK_remitente + PK_receptor + this.id);
		
		if(id.equals(IDsmartContract)) {
			return true;
		}
		else
			return false;
	}
	

	public String getID() {
		return IDsmartContract;
	}

	public String getIDsmartContract() {
		return IDsmartContract;
	}

	public long getFecha() {
		return fecha;
	}

	public int getCantidad() {
		return cantidad;
	}

	public String getPK_remitente() {
		return PK_remitente;
	}

	public String getPK_receptor() {
		return PK_receptor;
	}

	public void setIDsmartContract(String iDsmartContract) {
		IDsmartContract = iDsmartContract;
	}

	public void setFecha(long fecha) {
		this.fecha = fecha;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public void setPK_remitente(String pK_remitente) {
		PK_remitente = pK_remitente;
	}

	public void setPK_receptor(String pK_receptor) {
		PK_receptor = pK_receptor;
	}
	
	public byte[] getFirmaTransaccion(){
		return firmaTransaccion;
	}

	public void setFirmaTransaccion(byte[] pFirma) {
		this.firmaTransaccion = pFirma;		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
