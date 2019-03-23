package blockchain;

import java.security.PublicKey;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import vista.VentanaLogin;

public class SmartContract{

	private String IDsmartContract;
	private long fecha;
	private int cantidad;
	private String PK_remitente;
	private String PK_receptor;
	
	protected void ejecutarContrato() {
		//falta borrarlo si el remitente o receptor ya no están!!!!
		
			Bloque bl = null;
			try {
				bl = new Bloque(databaseControl.getHashUltimoBloque());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			Transaccion tranTemp = VentanaLogin.getCarteraActual().enviarFondos((PublicKey) StringUtils.getClaveDesdeString(PK_receptor, true), cantidad);
			if(tranTemp != null) { //Si la transaccion es correcta
				bl.anadirTransaccion(VentanaLogin.getCarteraActual().enviarFondos((PublicKey) StringUtils.getClaveDesdeString(PK_receptor, true), cantidad));
			
				if(bl.getTransacciones().size() > 0) //anadir tran a la lista de transacciones
					ProgramaPrincipal.getTransacciones().add(bl.getTransacciones().get(0));
				
				if(ProgramaPrincipal.anadirBloque(bl)) { //si la cadena es valida, anadir todo a la BD
					try {
						databaseControl.insertarTransaccion(bl.getTransacciones().get(0));
						databaseControl.insertarBloque(bl);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
				else { //si no es valida, no añadir a la bd y borrar bloque y tran de las listas en tiempo de ejecucion
					ProgramaPrincipal.getTransacciones().remove(bl.getTransacciones().get(0));
					ProgramaPrincipal.getBlockchain().remove(bl);
				}
			}
			else { //si la transaccion NO es correcta (supera el saldo de la cartera...)
				bl.anadirTransaccion(VentanaLogin.getCarteraActual().enviarFondos((PublicKey) 
						StringUtils.getClaveDesdeString(PK_receptor, true), 0)); //enviar cero
			
				if(bl.getTransacciones().size() > 0) //anadir tran a la lista de transacciones
					ProgramaPrincipal.getTransacciones().add(bl.getTransacciones().get(0));
				
				if(ProgramaPrincipal.anadirBloque(bl)) { //si la cadena es valida, anadir todo a la BD
					try {
						databaseControl.insertarTransaccion(bl.getTransacciones().get(0));
						databaseControl.insertarBloque(bl);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
				else { //si no es valida, no añadir a la bd y borrar bloque y tran de las listas en tiempo de ejecucion
					ProgramaPrincipal.getTransacciones().remove(bl.getTransacciones().get(0));
					ProgramaPrincipal.getBlockchain().remove(bl);
				}					
			}

	}

	public SmartContract(long pFecha, int pCant, String pRemitente, String pReceptor) {
		IDsmartContract = StringUtils.applySha3_256(pFecha + pCant + pRemitente + pReceptor);
		fecha = pFecha;
		cantidad = pCant;
		PK_remitente = pRemitente;
		PK_receptor = pReceptor;
	}
	
	public SmartContract() {}

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
	
}
