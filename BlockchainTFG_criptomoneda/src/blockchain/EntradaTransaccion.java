package blockchain;

public class EntradaTransaccion {

	protected SalidaTransaccion transaccionNoGastada; //salida de la transaccion no hecha
	protected String IDsalidaTransaccion; //Referencia a SalidaTransaccion ---> IDtransaccion
	
	public EntradaTransaccion(String pIDsalidaTransaccion) {
		this.IDsalidaTransaccion = pIDsalidaTransaccion;
	}
}
