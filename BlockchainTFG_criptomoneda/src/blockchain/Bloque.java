package blockchain;

import java.util.ArrayList;
import java.util.Date;

//
public class Bloque {

	public String hash; //La "firma" del bloque
	public String hashAnterior; //La funcion hash del bloque anterior
	private long marcaTemporal; //numero en milisegundos desde el 01/01/1970
	private int nonce; //"number used once"
	public String merkleRoot;
	public ArrayList<Transaccion> transacciones = new ArrayList<Transaccion>();

	//Constructor
	public Bloque(String pHashAnterior ) {
		this.hashAnterior = pHashAnterior;
		this.marcaTemporal = new Date().getTime();
		this.hash = calcularHash(); 
	}
	
	public Bloque() {}
	
	public String calcularHash() {
		String hash;
		
		/*
		 * Debemos calcular el hash de todas las partes del bloque que no queremos que se alteren. 
		 * Entonces, para nuestro bloque incluiremos el Hash del bloque anterior, los datos y la marca temporal.
		 */
		hash = StringUtils.applySha256(hashAnterior + Long.toString(marcaTemporal) + Integer.toString(nonce) + merkleRoot);
		
		return hash;
	}
	
	public void minarBloque(int dificultad) {
		String meta = new String(new char[dificultad]).replace('\0', '0'); 
		while(!hash.substring( 0, dificultad).equals(meta)) {
			nonce ++;
			hash = calcularHash();
		}
		System.out.println("El bloque se ha minado!!! : " + hash + "\n");
	}
	
	    //Agregar transaccion al bloque
		public boolean anadirTransaccion(Transaccion pTransaccion) {
			
			//procesar transaccion y comprobar validez, menos si se trata del bloque genesis
			if(pTransaccion == null) 
				return false;		
			if((!"0".equals(hashAnterior))) {
				if((pTransaccion.procesarTransaccion() != true)) {
					System.out.println("La transacción no se ha procesado correctamente, por lo que se ha descartado.");
					return false;
				}
			}

			transacciones.add(pTransaccion);
			System.out.println("La transacción se ha añadido correctamente al bloque.");
			return true;
		}

		public long getMarcaTemporal() {
			return marcaTemporal;
		}

		public int getNonce() {
			return nonce;
		}

		public String getHash() {
			return hash;
		}

		public String getHashAnterior() {
			return hashAnterior;
		}

		public String getMerkleRoot() {
			return merkleRoot;
		}

		public ArrayList<Transaccion> getTransacciones() {
			return transacciones;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		public void setHashAnterior(String hashAnterior) {
			this.hashAnterior = hashAnterior;
		}

		public void setMarcaTemporal(long marcaTemporal) {
			this.marcaTemporal = marcaTemporal;
		}

		public void setNonce(int nonce) {
			this.nonce = nonce;
		}

		public void setMerkleRoot(String merkleRoot) {
			this.merkleRoot = merkleRoot;
		}

		public void setTransacciones(ArrayList<Transaccion> transacciones) {
			this.transacciones = transacciones;
		}
		
		

}