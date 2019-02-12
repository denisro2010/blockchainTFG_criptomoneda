package BlockchainTFG;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

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
				transaccionGenesis.outputs.add(new SalidaTransaccion(transaccionGenesis.receptor, transaccionGenesis.valor, transaccionGenesis.IDtransaccion)); //anadir el output manualmente
				transaccionesNoGastadas.put(transaccionGenesis.outputs.get(0).id, transaccionGenesis.outputs.get(0)); //guarda la primera transaccion en la lista de transacciones no gastadas
				
				System.out.println("Creando y minando el bloque génesis... ");
				Bloque genesis = new Bloque("0");
				genesis.anadirTransaccion(transaccionGenesis);
				anadirBloque(genesis);
				
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
				
				esCadenaValida();
				
			}

	}

}
