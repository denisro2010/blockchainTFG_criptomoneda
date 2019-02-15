package blockchain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import bd.databaseControl;

public class ProgramaPrincipal {

	public static ArrayList<Bloque> blockchain = new ArrayList<Bloque>();
	public static HashMap<String, SalidaTransaccion> transaccionesNoGastadas = new HashMap<String, SalidaTransaccion>();
	public static int dificultad = 3;
	public static float transaccionMin = 0.1f;
	public static Cartera cartera1;
	public static Cartera cartera2;
	public static Transaccion transaccionGenesis;
	
	public static void main(String[] args) {
				//Añadir los bloques a la lista de bloques
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
				
				//Create wallets:
				cartera1 = new Cartera();
				cartera2 = new Cartera();		
				Cartera coinbase = new Cartera();
				
				// Transaccion genesis, mandar 100 coins a la cartera 1: 
				transaccionGenesis = new Transaccion(coinbase.clavePublica, cartera1.clavePublica, 100f, null);
				transaccionGenesis.generarFirma(coinbase.clavePrivada);	 //firma manual de la transaccion genesis	
				transaccionGenesis.IDtransaccion = "0"; //id de la transaccion manual
				SalidaTransaccion outputManual = new SalidaTransaccion(transaccionGenesis.receptor, transaccionGenesis.valor, transaccionGenesis.IDtransaccion);
				transaccionGenesis.outputs.add(outputManual); //anadir el output manualmente
				transaccionesNoGastadas.put(transaccionGenesis.outputs.get(0).id, transaccionGenesis.outputs.get(0)); //guarda la primera transaccion en la lista de transacciones no gastadas
				
				System.out.println("Creando y minando el bloque génesis... ");
				Bloque genesis = new Bloque("0");
				genesis.anadirTransaccion(transaccionGenesis);
				anadirBloque(genesis);
				
				System.out.println("\nEl balance de la cartera 1 es de: " + cartera1.getBalanceCartera());
				System.out.println("El balance de la cartera 2 es de: " + cartera2.getBalanceCartera());
				
				System.out.println(cartera1.clavePublica);
				
				/*
				//testing
				Bloque bloque1 = new Bloque(genesis.hash);
				System.out.println("\nEl balance de la cartera 1 es de: " + cartera1.getBalanceCartera());
				System.out.println("\nLa cartera 1 quiere enviar 40 monedas a cartera 2...");
				bloque1.anadirTransaccion(cartera1.enviarFondos(cartera2.clavePublica, 40f));
				anadirBloque(bloque1);
				System.out.println("\nEl balance de la cartera 1 es de: " + cartera1.getBalanceCartera());
				System.out.println("El balance de la cartera 2 es de: " + cartera2.getBalanceCartera());
				
				Bloque bloque2 = new Bloque(bloque1.hash);
				System.out.println("\nLa cartera 1 está intentando mandar más monedas de las que tiene (1000)...");
				bloque2.anadirTransaccion(cartera1.enviarFondos(cartera2.clavePublica, 1000f));
				anadirBloque(bloque2);
				System.out.println("\nEl balance de la cartera 1 es de: " + cartera1.getBalanceCartera());
				System.out.println("El balance de la cartera 2 es de: " + cartera2.getBalanceCartera());
				
				Bloque block3 = new Bloque(bloque2.hash);
				System.out.println("\nLa cartera 2 quiere enviar 20 monedas a cartera 1...");
				block3.anadirTransaccion(cartera2.enviarFondos( cartera1.clavePublica, 20));
				System.out.println("\nEl balance de la cartera 1 es de: " + cartera1.getBalanceCartera());
				System.out.println("El balance de la cartera 1 es de: " + cartera2.getBalanceCartera());
				
				*/
				
				esCadenaValida();
				
				//testing DB - crea transacción y bloque genesis - añade 100 coins a la cartera1
				try {
					databaseControl.crearCartera("usuario001", "qwerty123", cartera1.clavePublica.toString(), cartera1.clavePrivada.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					databaseControl.crearTransaccion(transaccionGenesis.IDtransaccion, transaccionGenesis.remitente.toString(), transaccionGenesis.receptor.toString(), transaccionGenesis.valor, transaccionGenesis.firma.toString(), Transaccion.getSecuencia());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					databaseControl.crearBloque(genesis.hash, genesis.hashAnterior, genesis.getMarcaTemporal(), genesis.getNonce(), genesis.merkleRoot, genesis.transacciones.get(0).IDtransaccion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					databaseControl.crearOutput(outputManual.id, outputManual.cantidad, outputManual.IDtransaccion, outputManual.receptor.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} //FIN MAIN()
	
	public static Boolean esCadenaValida() {
		Bloque bloqueActual; 
		Bloque bloqueAnterior;
		String meta = new String(new char[dificultad]).replace('\0', '0');
		HashMap<String,SalidaTransaccion> tranSinGastarTemp = new HashMap<String,SalidaTransaccion>(); //lista temporal de transacciones sin gastar
		tranSinGastarTemp.put(transaccionGenesis.outputs.get(0).id, transaccionGenesis.outputs.get(0));
		
		//comprobar hashes del blockchain
		for(int i=1; i < blockchain.size(); i++) {
			
			bloqueActual = blockchain.get(i);
			bloqueAnterior = blockchain.get(i-1);
			
			//comparar el hash registrado con el que se ha calculado
			if(!bloqueActual.hash.equals(bloqueActual.calcularHash()) ){
				System.out.println("Las funciones hash actuales no coinciden.");
				return false;
			}
			
			//comparar el hash anterior registrado con el anterior calculado
			if(!bloqueAnterior.hash.equals(bloqueActual.hashAnterior) ) {
				System.out.println("Las funciones hash anteriores no coinciden.");
				return false;
			}
			
			//comprobar que la función hash cumple las condiciones
			if(!bloqueActual.hash.substring( 0, dificultad).equals(meta)) {
				System.out.println("Este bloque NO se ha minado.");
				return false;
			}
			
			//loop sobre las transacciones
			SalidaTransaccion tempOutput;
			for(int t=0; t <bloqueActual.transacciones.size(); t++) {
				Transaccion transaccionActual = bloqueActual.transacciones.get(t);
				
				if(!transaccionActual.verificarFirma()) {
					System.out.println("La firma de la transacción (" + t + ") NO es válida.");
					return false; 
				}
				if(transaccionActual.getInputs() != transaccionActual.getOutputs()) {
					System.out.println("Los inputs de la transacción (" + t + ") no coinciden con los outputs.");
					return false; 
				}
				
				for(EntradaTransaccion input: transaccionActual.inputs) {	
					tempOutput = tranSinGastarTemp.get(input.IDsalidaTransaccion);
					
					if(tempOutput == null) {
						System.out.println("El input de la transacción (" + t + ") no se ha podido encontrar.");
						return false;
					}
					
					if(input.transaccionNoGastada.cantidad != tempOutput.cantidad) {
						System.out.println("La entrada (input) de la transacción (" + t + ") no es válida.");
						return false;
					}
					
					tranSinGastarTemp.remove(input.IDsalidaTransaccion);
				}
				
				for(SalidaTransaccion output: transaccionActual.outputs) {
					tranSinGastarTemp.put(output.id, output);
				}
				
				if( transaccionActual.outputs.get(0).receptor != transaccionActual.receptor) {
					System.out.println("El receptor de la transacción (" + t + ") no es quien debería ser.");
					return false;
				}
				if( transaccionActual.outputs.get(1).receptor != transaccionActual.remitente) {
					System.out.println("El que recibe el cambio sobrante de la transacción (" + t + ") NO es el remitente.");
					return false;
				}
				
			}
			
		}
		System.out.println("El blockchain es válido.");
		return true;
	}
	
	public static void anadirBloque(Bloque pNuevoBloque) {
		pNuevoBloque.minarBloque(dificultad);
		blockchain.add(pNuevoBloque);
	}


}

