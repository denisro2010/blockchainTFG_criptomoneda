package blockchain;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.bouncycastle.pqc.jcajce.spec.QTESLAParameterSpec;
import bd.databaseControl;

public class Cartera {

		private PrivateKey clavePrivada;
		private PublicKey clavePublica;
		
		private HashMap<String, SalidaTransaccion> transaccionesNoGastadas = new HashMap<String, SalidaTransaccion>();
		
		public Cartera(){
			generarParClaves();	
		}
			
		//Genera un par de claves con qTESLA, el algoritmo post cuántico
		public void generarParClaves() {
			
			 KeyPairGenerator kpg = null;
			try {
				kpg = KeyPairGenerator.getInstance("qTESLA", "BCPQC");
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		        try {
					kpg.initialize(new QTESLAParameterSpec(QTESLAParameterSpec.HEURISTIC_I), new SecureRandom());
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		        KeyPair kp = kpg.generateKeyPair();

		        clavePrivada = kp.getPrivate();
		        clavePublica = kp.getPublic();
			
		        //ECDSA
			/*try {
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
			}*/
			
		}
		
		public float getBalanceCartera() {
			
			float total = 0;
			
	        for (Map.Entry<String, SalidaTransaccion> transacciones: ProgramaPrincipal.getTransaccionesNoGastadas().entrySet()){
	        	SalidaTransaccion transaccionNoGastada = transacciones.getValue();
	            if(transaccionNoGastada.misMonedas(clavePublica)) { //si las monedas me pertenecen...
	            	transaccionesNoGastadas.put(transaccionNoGastada.getId(), transaccionNoGastada); //añadir transaccion a mi lista de transacciones no realizadas
	            	total += transaccionNoGastada.getCantidad(); 
	            }
	        }  
			return total;
		}
		
		public Transaccion enviarFondos(PublicKey pReceptor, float pCantidad) {
			
			if(getBalanceCartera() < pCantidad) {
				//System.out.println("No hay fondos suficientes como para enviar esta cantidad. La transacción se ha descartado.");
				JOptionPane.showMessageDialog(null,
					    "No hay fondos suficientes como para enviar esta cantidad. La transacción se ha descartado.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			ArrayList<EntradaTransaccion> entrantes = new ArrayList<EntradaTransaccion>();
			
			float total = 0;
			for (Map.Entry<String, SalidaTransaccion> item: transaccionesNoGastadas.entrySet()){
				SalidaTransaccion transaccionNoGastada = item.getValue();
				total += transaccionNoGastada.getCantidad();
				entrantes.add(new EntradaTransaccion(transaccionNoGastada.getId()));
				if(total > pCantidad) 
					break;
			}
			
			Transaccion nuevaTransaccion = null;
			try {
				nuevaTransaccion = new Transaccion(clavePublica, pReceptor , pCantidad, entrantes, databaseControl.getSecuenciaMayor());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nuevaTransaccion.generarFirma(clavePrivada);
			
			for(EntradaTransaccion entrante: entrantes){
				transaccionesNoGastadas.remove(entrante.IDsalidaTransaccion);
			}
			
			return nuevaTransaccion;
		}
		
		public Transaccion enviarFondosSmartContract(PublicKey pReceptor, float pCantidad, byte[] pFirma, String pIDcontrato) {
			
			if(getBalanceCartera() < pCantidad) {
				JOptionPane.showMessageDialog(null,
					    "El remitente del smart contract no tiene suficientes fondos. El contrato se ha descartado.",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				
				try { 
					databaseControl.borrarContrato(pIDcontrato); 
				} 
				catch (Exception e) {}
				
				for(int i=0; i < ProgramaPrincipal.getContratos().size(); i++) {
					if(ProgramaPrincipal.getContratos().get(i).getIDsmartContract().equals(pIDcontrato)) {
						ProgramaPrincipal.getContratos().remove(i);
					}
				}
				
				return null;
			}
			
			ArrayList<EntradaTransaccion> entrantes = new ArrayList<EntradaTransaccion>();
			
			float total = 0;
			for (Map.Entry<String, SalidaTransaccion> item: transaccionesNoGastadas.entrySet()){
				SalidaTransaccion transaccionNoGastada = item.getValue();
				total += transaccionNoGastada.getCantidad();
				entrantes.add(new EntradaTransaccion(transaccionNoGastada.getId()));
				if(total > pCantidad) 
					break;
			}
			
			Transaccion nuevaTransaccion = null;
			try {
				nuevaTransaccion = new Transaccion(clavePublica, pReceptor , pCantidad, entrantes, databaseControl.getSecuenciaMayor());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nuevaTransaccion.setFirma(pFirma);
			
			for(EntradaTransaccion entrante: entrantes){
				transaccionesNoGastadas.remove(entrante.IDsalidaTransaccion);
			}
			
			return nuevaTransaccion;
		}
		
		public PrivateKey getClavePrivada() {
			return clavePrivada;
		}

		public void setClavePrivada(PrivateKey clavePrivada) {
			this.clavePrivada = clavePrivada;
		}

		public PublicKey getClavePublica() {
			return clavePublica;
		}

		public void setClavePublica(PublicKey clavePublica) {
			this.clavePublica = clavePublica;
		}

		public HashMap<String, SalidaTransaccion> getTransaccionesNoGastadas() {
			return transaccionesNoGastadas;
		}

		public void setTransaccionesNoGastadas(HashMap<String, SalidaTransaccion> transaccionesNoGastadas) {
			this.transaccionesNoGastadas = transaccionesNoGastadas;
		}
		
		
		
}
