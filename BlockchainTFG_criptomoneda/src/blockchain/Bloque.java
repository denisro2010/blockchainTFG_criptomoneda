package blockchain;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import algoritmosCriptograficos.StringUtils;

//
public class Bloque {

	private String hash; //La "firma" del bloque
	private String hashAnterior; //La funcion hash del bloque anterior
	private long marcaTemporal; //numero en milisegundos desde el 01/01/1970
	private int nonce; //"number used once"
	private String merkleRoot;
	private ArrayList<Transaccion> transacciones = new ArrayList<Transaccion>();
	private ArrayList<SmartContract> contratos = new ArrayList<SmartContract>();
	private String contratoEjecutado = "";
	private String contratoConfirmado = "";
	private String contratoPorEliminar = "";
	
	public Bloque(String pHashAnterior) {
		this.hashAnterior = pHashAnterior;
		this.marcaTemporal = new Date().getTime();
		this.hash = calcularHash(); 
	}
	
	public Bloque() {
	}
	
	public String calcularHash() {
		String hash;
		
		if(transacciones.size() > 0 && contratos.size() > 0)
			hash = StringUtils.applySha3_256(hashAnterior + Long.toString(marcaTemporal) + Integer.toString(nonce) + transacciones.get(0).getIDtransaccion() + contratos.get(0).getIDsmartContract() + contratoEjecutado + contratoConfirmado + contratoPorEliminar);
		else if(transacciones.size() == 0 && contratos.size() > 0)
			hash = StringUtils.applySha3_256(hashAnterior + Long.toString(marcaTemporal) + Integer.toString(nonce) + contratos.get(0).getIDsmartContract() + contratoEjecutado + contratoConfirmado + contratoPorEliminar);
		else if(transacciones.size() > 0 && contratos.size() == 0)
			hash = StringUtils.applySha3_256(hashAnterior + Long.toString(marcaTemporal) + Integer.toString(nonce) + transacciones.get(0).getIDtransaccion());
		else
			hash = StringUtils.applySha3_256(hashAnterior + Long.toString(marcaTemporal) + Integer.toString(nonce));
		
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
					JOptionPane.showMessageDialog(null, "La transacción no se ha procesado correctamente, por lo que se ha descartado.", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}

			transacciones.add(pTransaccion);
			this.hash = calcularHash();
			System.out.println("La transacción se ha añadido correctamente al bloque.");
			return true;
		}
		
		public boolean anadirContrato(SmartContract pContract) {
			
			if(pContract == null) 
				return false;		

			contratos.add(pContract);
			this.hash = calcularHash();
			System.out.println("El contrato se ha añadido correctamente al bloque.");
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

		public ArrayList<SmartContract> getContratos() {
			return contratos;
		}

		public String getContratoEjecutado() {
			return contratoEjecutado;
		}

		public void setContratoEjecutado(String contratoEjecutado) {
			this.contratoEjecutado = contratoEjecutado;
		}

		public String getContratoConfirmado() {
			return contratoConfirmado;
		}

		public void setContratoConfirmado(String contratoConfirmado) {
			this.contratoConfirmado = contratoConfirmado;
		}

		public void setContratos(ArrayList<SmartContract> contratos) {
			this.contratos = contratos;
		}

		public String getContratoPorEliminar() {
			return contratoPorEliminar;
		}

		public void setContratoPorEliminar(String contratoPorEliminar) {
			this.contratoPorEliminar = contratoPorEliminar;
		}
		
}