package blockchain;

import java.security.PublicKey;

public class SalidaTransaccion {

	public String id;
	public PublicKey receptor;
	public float cantidad; //cantidad de monedas que tienen
	public String IDtransaccion; //id transaccion

	public SalidaTransaccion(PublicKey pReceptor, float pCantidad, String pTransaccion) {
		this.receptor = pReceptor;
		this.cantidad = pCantidad;
		this.IDtransaccion = pTransaccion;
		this.id = StringUtils.applySha256(StringUtils.getStringClave(pReceptor) + Float.toString(pCantidad) + pTransaccion);
	}

		//Comprueba que los fondos me pertenecen
		public boolean misMonedas(PublicKey pClavePublica) {
			return (pClavePublica.equals(receptor));
		}
		
}
