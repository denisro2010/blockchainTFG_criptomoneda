package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;

import java.security.*;

public class Transaccion {

		protected String IDtransaccion; //Hash de una transaccion
		private PublicKey remitente; //Clave publica del remitente
		private PublicKey receptor; //clave publica del receptor
		private float valor; //valor que se desea enviar 
		private byte[] firma; //Para prevenir que otra persona gaste nuestros fondos
		
		private ArrayList<EntradaTransaccion> inputs = new ArrayList<EntradaTransaccion>();
		private ArrayList<SalidaTransaccion> outputs = new ArrayList<SalidaTransaccion>();
		
		private int secuencia; //Contador de cuantas transacciones se han generado
		
		// Constructor: 
		public Transaccion(PublicKey pRemitente, PublicKey pReceptor, float pValor,  ArrayList<EntradaTransaccion> pInputs, int pSecuencia) {
			this.remitente = pRemitente;
			this.receptor = pReceptor;
			this.valor = pValor;
			this.inputs = pInputs;
			this.secuencia = pSecuencia;
		}
		
		public Transaccion() {}

		public boolean procesarTransaccion() {
			
			if(verificarFirma() == false) {
				//System.out.println("La firma no se ha podido verificar.");
				JOptionPane.showMessageDialog(null, "La firma de la transacción no se ha podido verificar.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
					
			//Recoge los inputs de la transaccion (asegurarse de que no se han gastado ya):
			for(EntradaTransaccion i : inputs) {
				i.transaccionNoGastada = ProgramaPrincipal.getTransaccionesNoGastadas().get(i.IDsalidaTransaccion);
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
				ProgramaPrincipal.getTransaccionesNoGastadas().put(o.getId() , o);
				try {
					databaseControl.crearOutput(o.getId(), o.getCantidad(), o.getIDtransaccion(), StringUtils.getStringClave(o.getReceptor()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//Remove transaction inputs from UTXO lists as spent:
			for(EntradaTransaccion i : inputs) {
				if(i.transaccionNoGastada == null) continue; //if Transaction can't be found skip it 
					ProgramaPrincipal.getTransaccionesNoGastadas().remove(i.transaccionNoGastada.getId());
					try {
						databaseControl.borrarOutput(i.transaccionNoGastada.getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			
			return true;
		}
		
		public float getInputs() {
			float total = 0;
			for(EntradaTransaccion i : inputs) {
				if(i.transaccionNoGastada == null) continue; //si la transaccion no se encuentra, omitirla
				total += i.transaccionNoGastada.getCantidad();
			}
			return total;
		}
		
		public void generarFirma(PrivateKey pClavePrivada) {
			String datos = StringUtils.getStringClave(remitente) + StringUtils.getStringClave(receptor) + Float.toString(valor)	;
			firma = StringUtils.applyQTESLASig(pClavePrivada, datos);
		}
		
		public boolean verificarFirma() {
			String datos = StringUtils.getStringClave(remitente) + StringUtils.getStringClave(receptor) + Float.toString(valor)	;
			return StringUtils.verifyQTESLASig(remitente, datos, firma);
		}
		
		public float getOutputs() {
			float total = 0;
			for(SalidaTransaccion o : outputs) {
				total += o.getCantidad();
			}
			return total;
		}
		
		private String calularHash() {
			secuencia++; //evitar que dos transacciones tengan el mismo hash
			return StringUtils.applySha3_256(
					StringUtils.getStringClave(remitente) +
					StringUtils.getStringClave(receptor) +
					Float.toString(valor) + secuencia
					);
		}

		public int getSecuencia() {
			return secuencia;
		}

		public String getIDtransaccion() {
			return IDtransaccion;
		}

		public PublicKey getRemitente() {
			return remitente;
		}

		public PublicKey getReceptor() {
			return receptor;
		}

		public float getValor() {
			return valor;
		}

		public byte[] getFirma() {
			return firma;
		}

		public void setIDtransaccion(String iDtransaccion) {
			IDtransaccion = iDtransaccion;
		}

		public void setRemitente(PublicKey remitente) {
			this.remitente = remitente;
		}

		public void setReceptor(PublicKey receptor) {
			this.receptor = receptor;
		}

		public void setValor(float valor) {
			this.valor = valor;
		}

		public void setFirma(byte[] firma) {
			this.firma = firma;
		}

		public void setSecuencia(int secuencia) {
			this.secuencia = secuencia;
		}
		
		public ArrayList<SalidaTransaccion> getSalidas(){
			return this.outputs;
		}

		public void setSalidas(ArrayList<SalidaTransaccion> outputs) {
			this.outputs = outputs;
		}
		
		public ArrayList<EntradaTransaccion> getEntradas(){
			return inputs;
		}
		
}

