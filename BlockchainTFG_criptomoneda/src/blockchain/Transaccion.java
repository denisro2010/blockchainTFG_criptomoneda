package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.security.*;

public class Transaccion {

		public String IDtransaccion; //Hash de una transaccion
		public PublicKey remitente; //Clave publica del remitente
		public PublicKey receptor; //clave publica del receptor
		public float valor; //valor que se desea enviar 
		public byte[] firma; //Para prevenir que otra persona gaste nuestros fondos
		
		public ArrayList<EntradaTransaccion> inputs = new ArrayList<EntradaTransaccion>();
		public ArrayList<SalidaTransaccion> outputs = new ArrayList<SalidaTransaccion>();
		
		private static int secuencia = 0; //Contador de cuantas transacciones se han generado
		
		// Constructor: 
		public Transaccion(PublicKey pRemitente, PublicKey pReceptor, float pValor,  ArrayList<EntradaTransaccion> pInputs) {
			this.remitente = pRemitente;
			this.receptor = pReceptor;
			this.valor = pValor;
			this.inputs = pInputs;
		}
		
		public boolean procesarTransaccion() {
			
			if(verificarFirma() == false) {
				System.out.println("La firma no se ha podido verificar.");
				return false;
			}
					
			//Recoge los inputs de la transaccion (asegurarse de que no se han gastado ya):
			for(EntradaTransaccion i : inputs) {
				i.transaccionNoGastada = ProgramaPrincipal.transaccionesNoGastadas.get(i.IDsalidaTransaccion);
			}

			//Comprueba la validez de la transaccion
			if(getInputs() < ProgramaPrincipal.transaccionMin) {
				System.out.println("El valor de la transacción es demasiado pequeño: " + getInputs());
				System.out.println("Por favor, introduzca un valor mayor que " + ProgramaPrincipal.transaccionMin);
				return false;
			}
			
			//Genera las salidas de la transaccion:
			float cambioSobrante = getInputs() - valor; //get valor de la transaccion y el cambio sobrante
			IDtransaccion = calularHash();
			outputs.add(new SalidaTransaccion( this.receptor, valor, IDtransaccion)); //enviar fondos al receptor
			outputs.add(new SalidaTransaccion( this.remitente, cambioSobrante, IDtransaccion)); //enviar el cambio sobrante de vuelta al remitente	
					
			//Add outputs to Unspent list
			for(SalidaTransaccion o : outputs) {
				ProgramaPrincipal.transaccionesNoGastadas.put(o.id , o);
			}
			
			//Remove transaction inputs from UTXO lists as spent:
			for(EntradaTransaccion i : inputs) {
				if(i.transaccionNoGastada == null) continue; //if Transaction can't be found skip it 
				ProgramaPrincipal.transaccionesNoGastadas.remove(i.transaccionNoGastada.id);
			}
			
			return true;
		}
		
		public float getInputs() {
			float total = 0;
			for(EntradaTransaccion i : inputs) {
				if(i.transaccionNoGastada == null) continue; //si la transaccion no se encuentra, omitirla
				total += i.transaccionNoGastada.cantidad;
			}
			return total;
		}
		
		public void generarFirma(PrivateKey pClavePrivada) {
			String datos = StringUtils.getStringClave(remitente) + StringUtils.getStringClave(receptor) + Float.toString(valor)	;
			firma = StringUtils.applyECDSASig(pClavePrivada, datos);		
		}
		
		public boolean verificarFirma() {
			String datos = StringUtils.getStringClave(remitente) + StringUtils.getStringClave(receptor) + Float.toString(valor)	;
			return StringUtils.verifyECDSASig(remitente, datos, firma);
		}
		
		public float getOutputs() {
			float total = 0;
			for(SalidaTransaccion o : outputs) {
				total += o.cantidad;
			}
			return total;
		}
		
		private String calularHash() {
			secuencia++; //evitar que dos transacciones tengan el mismo hash
			return StringUtils.applySha256(
					StringUtils.getStringClave(remitente) +
					StringUtils.getStringClave(receptor) +
					Float.toString(valor) + secuencia
					);
		}
}

