package blockchain;

import java.awt.Toolkit;

public class SmartContract implements Runnable{

	//Comprobar si se tiene que ejecutar alg�n contrato
	@Override
	public void run() {
		System.out.println("ok");
	    Toolkit.getDefaultToolkit().beep();
	}


}
