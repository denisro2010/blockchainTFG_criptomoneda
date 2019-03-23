package blockchain;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Date;

public class ComprobarContratos implements Runnable {

	public void run() {

		ArrayList<SmartContract> lista = ProgramaPrincipal.getContratos();
		long marcaActual = new Date().getTime();
		System.out.println(marcaActual); //TODO
				
		for(int i=0; i<lista.size(); i++) {
			SmartContract sc = lista.get(i);
					
			if(sc.getFecha() >= marcaActual) {
				sc.ejecutarContrato();
			}
		}
				
		Toolkit.getDefaultToolkit().beep();
	}
	
	public ComprobarContratos() {}
}
