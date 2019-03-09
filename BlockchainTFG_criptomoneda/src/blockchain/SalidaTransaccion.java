package blockchain;

import java.security.PublicKey;

import algoritmosCriptograficos.StringUtils;

public class SalidaTransaccion {

	private String id;
	private PublicKey receptor;
	private float cantidad; //cantidad de monedas que tienen
	private String IDtransaccion; //id transaccion

	public SalidaTransaccion(PublicKey pReceptor, float pCantidad, String pTransaccion) {
		this.receptor = pReceptor;
		this.cantidad = pCantidad;
		this.IDtransaccion = pTransaccion;
		this.id = StringUtils.applySha3_256(StringUtils.getStringClave(pReceptor) + Float.toString(pCantidad) + pTransaccion);
	}

		public SalidaTransaccion() {
		// TODO Auto-generated constructor stub
	}

		//Comprueba que los fondos me pertenecen
		public boolean misMonedas(PublicKey pClavePublica) {
			return (pClavePublica.equals(receptor));
		}

		public String getId() {
			return id;
		}

		public PublicKey getReceptor() {
			return receptor;
		}

		public float getCantidad() {
			return cantidad;
		}

		public String getIDtransaccion() {
			return IDtransaccion;
		}
		
		
		
}
