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
	private static int dificultad = 3;
	public static float transaccionMin = 0;
	private static Cartera cartera1;
	public static Transaccion transaccionGenesis;
	private static Transaccion t1;
	private static ArrayList<Transaccion> transacciones;
	private static int posBlockchain; //despues de abrir y cerrar el programa solo recorre la lista a partir de los nuevos bloques que se crean en esa ejecución
	private static ArrayList<SmartContract> listaContratos = new ArrayList<SmartContract>();
	
	public static void main(String[] args) {
				Security.addProvider(new BouncyCastlePQCProvider()); //Setup Bouncey castle as a Security Provider (POST-QUANTUM SUPPORT)

				try {
					databaseControl.tablaCartera();
					databaseControl.tablaTransaccion();
					databaseControl.tablaBloque();
					databaseControl.tablaOutputs();
					//TODO FALTA TABLA SMART CONTRACTS!!!!!!
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//Get contratos de la BD
				listaContratos = databaseControl.getContratosBD();
				
				t1 = databaseControl.getTranGenesis();
				
				try {
					databaseControl.getOutputsMain();
				} catch (Exception e) {
					e.printStackTrace();
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
					e.printStackTrace();
				}
				
				if(blockchain.size() > 1)
					posBlockchain = blockchain.size();
				else
					posBlockchain = 1;

				//Comprobar smart contracts cada minuto
				ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		        Runnable task = new ComprobarContratos();
		        int initialDelay = 0;
		        int periodicDelay = 1;
		        scheduler.scheduleAtFixedRate(task, initialDelay, periodicDelay, TimeUnit.MINUTES);
				
				VentanaPrincipal v = new VentanaPrincipal();
				v.setVisible(true);		
				
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
		
		System.out.println("Creando y minando el bloque génesis... ");
		Bloque genesis = new Bloque("0");
		genesis.anadirTransaccion(transaccionGenesis);
		anadirBloque(genesis);
		
		VentanaDatos.setLblMonedasText(VentanaLogin.getCarteraActual().getBalanceCartera() + " monedas");
		try {
			databaseControl.crearTransaccion(transaccionGenesis.getIDtransaccion(), StringUtils.getStringClave(transaccionGenesis.getRemitente()), StringUtils.getStringClave(transaccionGenesis.getReceptor()), transaccionGenesis.getValor(), transaccionGenesis.getFirma().toString(), transaccionGenesis.getSecuencia());
			databaseControl.crearOutput(outputManual.getId(), outputManual.getCantidad(), outputManual.getIDtransaccion(), StringUtils.getStringClave(outputManual.getReceptor()));
			databaseControl.insertarBloque(genesis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean esCadenaValida() {
		Bloque bloqueActual; 
		Bloque bloqueAnterior;
		String meta = new String(new char[dificultad]).replace('\0', '0');
		HashMap<String,SalidaTransaccion> tranSinGastarTemp = new HashMap<String,SalidaTransaccion>(); //lista temporal de transacciones sin gastar
		tranSinGastarTemp.put(transaccionGenesis.getSalidas().get(0).getId(), transaccionGenesis.getSalidas().get(0));

		//comprobar hashes del blockchain
		for(int i=posBlockchain; i < blockchain.size(); i++) {
			
			bloqueActual = blockchain.get(i);
			bloqueAnterior = blockchain.get(i-1);
			
			//comparar el hash anterior registrado con el anterior calculado
			if(!bloqueAnterior.getHash().equals(bloqueActual.getHashAnterior()) ) {
				//System.out.println("Las funciones hash anteriores no coinciden. " + bloqueActual.hashAnterior);
				JOptionPane.showMessageDialog(null, "Las funciones hash anteriores no coinciden.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			if(!bloqueActual.getHash().equals(bloqueActual.calcularHash()) ) {
				//System.out.println("Las funciones hash actuales no coinciden.");
				JOptionPane.showMessageDialog(null, "Las funciones hash actuales no coinciden.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			//comprobar que la función hash cumple las condiciones
			if(!bloqueActual.getHash().substring( 0, dificultad).equals(meta)) {
				//System.out.println("Este bloque NO se ha minado.");
				JOptionPane.showMessageDialog(null, "Este bloque no se ha minado.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			//loop sobre las transacciones
			SalidaTransaccion tempOutput;
			for(int t=0; t <bloqueActual.getTransacciones().size(); t++) {
				Transaccion transaccionActual = bloqueActual.getTransacciones().get(t);
				
				if(!transaccionActual.verificarFirma()) {
					//System.out.println("La firma de la transacción (" + t + ") NO es válida.");
					JOptionPane.showMessageDialog(null, "La firma de la transacción no es válida.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
					return false; 
				}
				if(transaccionActual.getInputs() != transaccionActual.getOutputs()) {
					//System.out.println("Los inputs de la transacción (" + t + ") no coinciden con los outputs.");
					JOptionPane.showMessageDialog(null, "Los inputs de la transacción no coinciden con los outputs.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
					return false; 
				}
				
				for(EntradaTransaccion input: transaccionActual.getEntradas()) {	
					tranSinGastarTemp.put(transaccionActual.getSalidas().get(0).getId(), transaccionActual.getSalidas().get(0));
					tempOutput = tranSinGastarTemp.get(input.IDsalidaTransaccion);
					
					/*if(tempOutput == null) {
						//System.out.println("El input de la transacción (" + t + ") no se ha podido encontrar.");
						JOptionPane.showMessageDialog(null, "El input de la transacción no se ha podido encontrar.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
						return false;
					}*/
					
					if(tempOutput != null) {
						if(input.transaccionNoGastada.getCantidad() != tempOutput.getCantidad()) {
							//System.out.println("La entrada (input) de la transacción (" + t + ") no es válida.");
							JOptionPane.showMessageDialog(null, "El input de la transacción no es válido.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
							return false;
						}
					}
					
					tranSinGastarTemp.remove(input.IDsalidaTransaccion);
				}
				
				for(SalidaTransaccion output: transaccionActual.getSalidas()) {
					tranSinGastarTemp.put(output.getId(), output);
				}
				
				if( transaccionActual.getSalidas().get(0).getReceptor() != transaccionActual.getReceptor()) {
					//System.out.println("El receptor de la transacción (" + t + ") no es quien debería ser.");
					JOptionPane.showMessageDialog(null, "El receptor de la transacción no es quien debería ser.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if( transaccionActual.getSalidas().get(1).getReceptor() != transaccionActual.getRemitente()) {
					//System.out.println("El que recibe el cambio sobrante de la transacción (" + t + ") NO es el remitente.");
					JOptionPane.showMessageDialog(null, "El que recibe el cambio sobrante de la transacción NO es el remitente.", "Blockchain no válido", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
			}
			
		}
		JOptionPane.showMessageDialog(null, "El blockchain es válido.", "", JOptionPane.PLAIN_MESSAGE);
		return true;
	}
	
	public static boolean anadirBloque(Bloque pNuevoBloque) {
		pNuevoBloque.minarBloque(dificultad);
		blockchain.add(pNuevoBloque);
		
		if(esCadenaValida());
			return true;
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
}

