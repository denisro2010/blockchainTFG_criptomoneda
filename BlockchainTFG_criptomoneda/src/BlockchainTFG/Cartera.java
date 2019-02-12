package BlockchainTFG;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cartera {

		public PrivateKey clavePrivada;
		public PublicKey clavePublica;
		
		public HashMap<String, SalidaTransaccion> transaccionesNoGastadas = new HashMap<String, SalidaTransaccion>();
		
		public Cartera(){
			generarParClaves();	
		}
			
		public void generarParClaves() {
			try {
				KeyPairGenerator generadorClaves = KeyPairGenerator.getInstance("ECDSA","BC");
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
				
				// Inicializar el generador de claves y generar las claves
				generadorClaves.initialize(ecSpec, random);
		        KeyPair parClaves = generadorClaves.generateKeyPair();
		        	
		        // Asigna las claves generadas a los atributos de la clase
		        clavePrivada = parClaves.getPrivate();
		        clavePublica = parClaves.getPublic();
		        	
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public float getBalanceCartera() {
			
			float total = 0;
			
	        for (Map.Entry<String, SalidaTransaccion> transacciones: ProgramaPrincipal.transaccionesNoGastadas.entrySet()){
	        	SalidaTransaccion transaccionNoGastada = transacciones.getValue();
	            if(transaccionNoGastada.miTransaccion(clavePublica)) { //si las monedas me pertenecen...
	            	transaccionesNoGastadas.put(transaccionNoGastada.id, transaccionNoGastada); //a�adir transaccion a mi lista de transacciones no realizadas
	            	total += transaccionNoGastada.value; 
	            }
	        }  
			return total;
		}
		
		public Transaccion enviarFondos(PublicKey pReceptor, float pCantidad) {
			
			if(getBalanceCartera() < pCantidad) {
				System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
				return null;
			}
			
			ArrayList<EntradaTransaccion> entrantes = new ArrayList<EntradaTransaccion>();
			
			float total = 0;
			for (Map.Entry<String, SalidaTransaccion> item: transaccionesNoGastadas.entrySet()){
				SalidaTransaccion transaccionNoGastada = item.getValue();
				total += transaccionNoGastada.cantidad;
				entrantes.add(new EntradaTransaccion(transaccionNoGastada.id));
				if(total > pCantidad) 
					break;
			}
			
			Transaccion nuevaTransaccion = new Transaccion(clavePublica, pReceptor , pCantidad, entrantes);
			nuevaTransaccion.generateSignature(clavePrivada);
			
			for(EntradaTransaccion entrante: entrantes){
				transaccionesNoGastadas.remove(entrante.IDsalidaTransaccion);
			}
			
			return nuevaTransaccion;
		}
		
}
