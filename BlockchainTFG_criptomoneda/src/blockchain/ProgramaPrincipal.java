package blockchain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import bd.databaseControl;
import vista.VentanaDatos;
import vista.VentanaLogin;
import vista.VentanaPrincipal;

public class ProgramaPrincipal {

	public static ArrayList<Bloque> blockchain = new ArrayList<Bloque>();
	public static HashMap<String, SalidaTransaccion> transaccionesNoGastadas = new HashMap<String, SalidaTransaccion>();
	public static int dificultad = 3;
	public static float transaccionMin = 0;
	public static Cartera cartera1;
	public static Transaccion transaccionGenesis;
	public static Transaccion t1;
	public static ArrayList<Transaccion> transacciones;
	private static int posBlockchain; //despues de abrir y cerrar el programa solo recorre la lista a partir de los nuevos bloques que se crean en esa ejecuci�n
	
	public static void main(String[] args) {
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
				
				try {
					databaseControl.tablaBloque();
					databaseControl.tablaCartera();
					databaseControl.tablaOutputs();
					databaseControl.tablaTransaccion();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
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

				VentanaPrincipal v = new VentanaPrincipal();
				v.setVisible(true);		
				
			} //FIN MAIN
	
	public static void transGenesis() {
		//Create wallets:
		cartera1 = VentanaLogin.getCarteraActual();		
		Cartera coinbase = new Cartera();
		
		// Transaccion genesis, mandar 100 coins a la cartera 1: 
		transaccionGenesis = new Transaccion(coinbase.clavePublica, cartera1.clavePublica, 100, null, 0);
		transaccionGenesis.generarFirma(coinbase.clavePrivada);	 //firma manual de la transaccion genesis	
		transaccionGenesis.IDtransaccion = "0"; //id de la transaccion manual
		SalidaTransaccion outputManual = new SalidaTransaccion(transaccionGenesis.receptor, transaccionGenesis.valor, transaccionGenesis.IDtransaccion);
		transaccionGenesis.outputs.add(outputManual); //anadir el output manualmente
		transaccionesNoGastadas.put(transaccionGenesis.outputs.get(0).id, transaccionGenesis.outputs.get(0)); //guarda la primera transaccion en la lista de transacciones no gastadas
		
		System.out.println("Creando y minando el bloque g�nesis... ");
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
		tranSinGastarTemp.put(transaccionGenesis.outputs.get(0).id, transaccionGenesis.outputs.get(0));

		//comprobar hashes del blockchain
		for(int i=posBlockchain; i < blockchain.size(); i++) {
			
			bloqueActual = blockchain.get(i);
			bloqueAnterior = blockchain.get(i-1);
			
			//comparar el hash anterior registrado con el anterior calculado
			if(!bloqueAnterior.hash.equals(bloqueActual.hashAnterior) ) {
				System.out.println("Las funciones hash anteriores no coinciden. " + bloqueActual.hashAnterior);
				return false;
			}
			
			if(!bloqueActual.hash.equals(bloqueActual.calcularHash()) ) {
				System.out.println("Las funciones hash actuales no coinciden.");
				return false;
			}
			
			//comprobar que la funci�n hash cumple las condiciones
			if(!bloqueActual.hash.substring( 0, dificultad).equals(meta)) {
				System.out.println("Este bloque NO se ha minado.");
				return false;
			}
			
			//loop sobre las transacciones
			SalidaTransaccion tempOutput;
			for(int t=0; t <bloqueActual.transacciones.size(); t++) {
				Transaccion transaccionActual = bloqueActual.transacciones.get(t);
				
				if(!transaccionActual.verificarFirma()) {
					System.out.println("La firma de la transacci�n (" + t + ") NO es v�lida.");
					return false; 
				}
				if(transaccionActual.getInputs() != transaccionActual.getOutputs()) {
					System.out.println("Los inputs de la transacci�n (" + t + ") no coinciden con los outputs.");
					return false; 
				}
				
				for(EntradaTransaccion input: transaccionActual.inputs) {	
					tranSinGastarTemp.put(transaccionActual.getSalidas().get(0).getId(), transaccionActual.getSalidas().get(0));
					tempOutput = tranSinGastarTemp.get(input.IDsalidaTransaccion);
					
					if(tempOutput == null) {
						System.out.println("El input de la transacci�n (" + t + ") no se ha podido encontrar.");
						return false;
					}
					
					if(input.transaccionNoGastada.cantidad != tempOutput.cantidad) {
						System.out.println("La entrada (input) de la transacci�n (" + t + ") no es v�lida.");
						return false;
					}
					
					tranSinGastarTemp.remove(input.IDsalidaTransaccion);
				}
				
				for(SalidaTransaccion output: transaccionActual.outputs) {
					tranSinGastarTemp.put(output.id, output);
				}
				
				if( transaccionActual.outputs.get(0).receptor != transaccionActual.receptor) {
					System.out.println("El receptor de la transacci�n (" + t + ") no es quien deber�a ser.");
					return false;
				}
				if( transaccionActual.outputs.get(1).receptor != transaccionActual.remitente) {
					System.out.println("El que recibe el cambio sobrante de la transacci�n (" + t + ") NO es el remitente.");
					return false;
				}
				
			}
			
		}
		System.out.println("El blockchain es v�lido.");
		return true;
	}
	
	public static void anadirBloque(Bloque pNuevoBloque) {
		pNuevoBloque.minarBloque(dificultad);
		blockchain.add(pNuevoBloque);
		esCadenaValida();
	}


}

