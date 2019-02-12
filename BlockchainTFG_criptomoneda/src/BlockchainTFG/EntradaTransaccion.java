package BlockchainTFG;

public class EntradaTransaccion {

	public SalidaTransaccion transaccionNoGastada; //salida de la transaccion no hecha
	public String IDsalidaTransaccion; //Referencia a SalidaTransaccion ---> IDtransaccion
	
	public EntradaTransaccion(String pIDsalidaTransaccion) {
		this.IDsalidaTransaccion = pIDsalidaTransaccion;
	}
	
}
