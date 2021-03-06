package blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import vista.VentanaDatos;
import vista.VentanaLogin;
import vista.VentanaPrincipal;

public class ProgramaPrincipal{

	private static ArrayList<Bloque> blockchain = new ArrayList<Bloque>();
	private static HashMap<String, SalidaTransaccion> transaccionesNoGastadas = new HashMap<String, SalidaTransaccion>();
	private static int dificultad = 2;
	public static float transaccionMin = 0;
	private static Cartera cartera1;
	public static Transaccion transaccionGenesis;
	private static Transaccion t1;
	private static ArrayList<Transaccion> transacciones;
	private static ArrayList<SmartContract> listaContratos = new ArrayList<SmartContract>();
	
	public static void main(String[] args) {
				Security.addProvider(new BouncyCastlePQCProvider()); //Setup Bouncey castle as a Security Provider (POST-QUANTUM SUPPORT)

				try {
					databaseControl.tablaCartera();
					databaseControl.tablaTransaccion();
					databaseControl.tablaBloque();
					databaseControl.tablaOutputs();
					databaseControl.tablaSmartContracts();
				} catch (Exception e) {
					//e.printStackTrace();
				}

				t1 = databaseControl.getTranGenesis();
				
				try {
					databaseControl.getOutputsMain();
				} catch (Exception e) {
					//e.printStackTrace();
				}

				SalidaTransaccion out; //= new SalidaTransaccion();
				transacciones = databaseControl.getTransacciones();
				
			    for (Map.Entry<String, SalidaTransaccion> it : transaccionesNoGastadas.entrySet()) {
			    	out = it.getValue();
			       for(Transaccion tr: transacciones) {
			    	   if(tr.getIDtransaccion().equals(out.getIDtransaccion()))
			    		   tr.getSalidas().add(out);
			       }
			    }
			    
			    if(transacciones.size() > 0)
			    	transaccionGenesis = transacciones.get(transacciones.size()-1);
				
				try {
					blockchain = databaseControl.getBloques();
				} catch (Exception e) {
					//e.printStackTrace();
				}

				//Get contratos de la BD
				listaContratos = databaseControl.getContratosBD();
				
				//Comprobar smart contracts cada minuto
				ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		        Runnable task = new ComprobarContratos();
		        int initialDelay = 0;
		        int periodicDelay = 1;
		        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay, TimeUnit.MINUTES);
		        
		        if(blockchainValido()) {
					VentanaPrincipal v = new VentanaPrincipal();
					v.setVisible(true);	
		        }
		        else {
		        	JOptionPane.showMessageDialog(null, "La cadena de bloques ha sido manipulada.", "Error", JOptionPane.ERROR_MESSAGE);
		        	System.exit(0);
		        }
				
			} //FIN MAIN
	
	public static void transGenesis() {
		//Create wallets:
		cartera1 = VentanaLogin.getCarteraActual();		
		Cartera coinbase = new Cartera();
		
		// Transaccion genesis, mandar 100 coins a la cartera 1: 
		transaccionGenesis = new Transaccion(coinbase.getClavePublica(), cartera1.getClavePublica(), 100, null, 0);
		transaccionGenesis.generarFirma(coinbase.getClavePrivada());	 //firma manual de la transaccion genesis	
		transaccionGenesis.IDtransaccion = "0"; //id de la transaccion manual
		SalidaTransaccion outputManual = new SalidaTransaccion(transaccionGenesis.getReceptor(), transaccionGenesis.getValor(), transaccionGenesis.getIDtransaccion());
		transaccionGenesis.getSalidas().add(outputManual); //anadir el output manualmente
		transaccionesNoGastadas.put(transaccionGenesis.getSalidas().get(0).getId(), transaccionGenesis.getSalidas().get(0)); //guarda la primera transaccion en la lista de transacciones no gastadas
		
		System.out.println("Creando y minando el bloque g�nesis... ");
		Bloque genesis = new Bloque("0");
		genesis.anadirTransaccion(transaccionGenesis);
		genesis.minarBloque(dificultad);
		blockchain.add(genesis);
		
		//anadirBloque(genesis);
		
		
		try {
			VentanaDatos.setLblMonedasText(VentanaLogin.getCarteraActual().getBalanceCartera() - databaseControl.misContratosPendientesCantidad(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica())) + " monedas");
			databaseControl.crearTransaccion(transaccionGenesis.getIDtransaccion(), StringUtils.getStringClave(transaccionGenesis.getRemitente()), StringUtils.getStringClave(transaccionGenesis.getReceptor()), transaccionGenesis.getValor(), transaccionGenesis.getFirma(), transaccionGenesis.getSecuencia());
			databaseControl.crearOutput(outputManual.getId(), outputManual.getCantidad(), outputManual.getIDtransaccion(), StringUtils.getStringClave(outputManual.getReceptor()));
			databaseControl.insertarBloque(genesis);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static Boolean blockchainValido() {
		Bloque bloqueActual; 
		Bloque bloqueAnterior;
		String meta = new String(new char[dificultad]).replace('\0', '0');
		//comprobar hashes del blockchain
				for(int i=1; i < blockchain.size(); i++) {
					
					bloqueActual = blockchain.get(i);
					if(i > 0)
						bloqueAnterior = blockchain.get(i-1);
					else 
						bloqueAnterior = null;
					
					//comparar el hash anterior registrado con el anterior calculado
					if(i > 0) {
						if(!bloqueAnterior.getHash().equals(bloqueActual.getHashAnterior()) ) {
							System.out.println("Las funciones hash anteriores no coinciden.");
							return false;
						}
					}
					
					if(!bloqueActual.getHash().equals(bloqueActual.calcularHash()) ) {
						System.out.println("Las funciones hash actuales no coinciden.");
						return false;
					}
					
					//comprobar que la funci�n hash cumple las condiciones
					if(!bloqueActual.getHash().substring( 0, dificultad).equals(meta)) {
						System.out.println("El bloque no se ha minado.");
						return false;
					}
				}
				System.out.println("Blockchain v�lido.");
				return true;
	}
	
	public static Boolean esCadenaValida() {
		Bloque bloqueActual; 
		Bloque bloqueAnterior;
		String meta = new String(new char[dificultad]).replace('\0', '0');
		HashMap<String,SalidaTransaccion> tranSinGastarTemp = new HashMap<String,SalidaTransaccion>(); //lista temporal de transacciones sin gastar
		try {
		tranSinGastarTemp.put(transaccionGenesis.getSalidas().get(0).getId(), transaccionGenesis.getSalidas().get(0));
		}catch (Exception e) {}

		//comprobar hashes del blockchain
		for(int i=1; i < blockchain.size(); i++) {
			
			bloqueActual = blockchain.get(i);
			if(i > 0)
				bloqueAnterior = blockchain.get(i-1);
			else 
				bloqueAnterior = null;
			
			//comparar el hash anterior registrado con el anterior calculado
			if(i > 0) {
				if(!bloqueAnterior.getHash().equals(bloqueActual.getHashAnterior()) ) {
					//System.out.println("Las funciones hash anteriores no coinciden. " + bloqueActual.hashAnterior);
					JOptionPane.showMessageDialog(null, "Las funciones hash anteriores no coinciden.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			
			if(!bloqueActual.getHash().equals(bloqueActual.calcularHash()) ) {
				//System.out.println("Las funciones hash actuales no coinciden.");
				JOptionPane.showMessageDialog(null, "Las funciones hash de un bloque no coinciden.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			//comprobar que la funci�n hash cumple las condiciones
			if(!bloqueActual.getHash().substring( 0, dificultad).equals(meta)) {
				//System.out.println("Este bloque NO se ha minado.");
				JOptionPane.showMessageDialog(null, "Un bloque no se ha minado.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			 try{ //por si se trata de un contract sin transacci�n
			
			//loop sobre las transacciones
			SalidaTransaccion tempOutput;
			for(int t=0; t <bloqueActual.getTransacciones().size(); t++) {
				Transaccion transaccionActual = bloqueActual.getTransacciones().get(t);
				
				if(!transaccionActual.verificarFirma()) {
					//System.out.println("La firma de la transacci�n (" + t + ") NO es v�lida.");
					JOptionPane.showMessageDialog(null, "La firma de la transacci�n no es v�lida.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
					return false; 
				}
				if(transaccionActual.getInputs() != transaccionActual.getOutputs()) {
					//System.out.println("Los inputs de la transacci�n (" + t + ") no coinciden con los outputs.");
					JOptionPane.showMessageDialog(null, "Los inputs de la transacci�n no coinciden con los outputs.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
					return false; 
				}
				
				for(EntradaTransaccion input: transaccionActual.getEntradas()) {	
					tranSinGastarTemp.put(transaccionActual.getSalidas().get(0).getId(), transaccionActual.getSalidas().get(0));
					tempOutput = tranSinGastarTemp.get(input.IDsalidaTransaccion);
					
					/*if(tempOutput == null) {
						//System.out.println("El input de la transacci�n (" + t + ") no se ha podido encontrar.");
						JOptionPane.showMessageDialog(null, "El input de la transacci�n no se ha podido encontrar.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
						return false;
					}*/
					
					if(tempOutput != null) {
						if(input.transaccionNoGastada.getCantidad() != tempOutput.getCantidad()) {
							//System.out.println("La entrada (input) de la transacci�n (" + t + ") no es v�lida.");
							JOptionPane.showMessageDialog(null, "El input de la transacci�n no es v�lido.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					
					tranSinGastarTemp.remove(input.IDsalidaTransaccion);
				}
				
				for(SalidaTransaccion output: transaccionActual.getSalidas()) {
					tranSinGastarTemp.put(output.getId(), output);
				}
				
				if( transaccionActual.getSalidas().get(0).getReceptor() != transaccionActual.getReceptor()) {
					//System.out.println("El receptor de la transacci�n (" + t + ") no es quien deber�a ser.");
					JOptionPane.showMessageDialog(null, "El receptor de una transacci�n no es quien deber�a ser.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if( transaccionActual.getSalidas().get(1).getReceptor() != transaccionActual.getRemitente()) {
					//System.out.println("El que recibe el cambio sobrante de la transacci�n (" + t + ") NO es el remitente.");
					JOptionPane.showMessageDialog(null, "El que recibe el cambio sobrante de la transacci�n NO es el remitente.", "Blockchain no v�lido", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} //end for
			
		}catch(Exception ex){}
			
		}
		System.out.println("Blockchain v�lido.");
		return true;
	}
	
	public static boolean anadirBloque(Bloque pNuevoBloque) {
		pNuevoBloque.minarBloque(dificultad);
		blockchain.add(pNuevoBloque);
		
		if(esCadenaValida());
			return true;
	}
	
	public static void borrarContrato(String pID) {

		for(int i=0; i<listaContratos.size(); i++) {
			if(listaContratos.get(i).getID().equals(pID)) 
				listaContratos.remove(i);
		}
	}

	public static HashMap<String, SalidaTransaccion> getTransaccionesNoGastadas() {
		return transaccionesNoGastadas;
	}

	public static Transaccion getT1() {
		return t1;
	}

	public static ArrayList<Transaccion> getTransacciones() {
		return transacciones;
	}

	public static ArrayList<Bloque> getBlockchain() {
		return blockchain;
	}

	public static ArrayList<SmartContract> getContratos() {
		return listaContratos;
	}

	public static void setDificultad(int dificultad) {
		ProgramaPrincipal.dificultad = dificultad;
	}
	
}

