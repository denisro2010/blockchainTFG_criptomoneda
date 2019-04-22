package tests;

import java.security.Security;

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.Before;
import org.junit.Test;

import blockchain.Bloque;
import blockchain.Cartera;
import blockchain.ProgramaPrincipal;
import blockchain.SalidaTransaccion;
import blockchain.Transaccion;
import junit.framework.TestCase;
import vista.VentanaLogin;

public class CarteraTest extends TestCase {

	Cartera c1;
	Cartera c2;
	Cartera coinbase;
	Transaccion t1;
	Transaccion transaccionGenesis;
	Bloque genesis;
	Bloque b1;
	
	public CarteraTest() {}
	
	@Before
	public void setUp() throws Exception {
		Security.addProvider(new BouncyCastlePQCProvider());
		c1 = new Cartera();
		c2 = new Cartera();
		VentanaLogin.setCarteraActual(c1);
			
		coinbase = new Cartera();	
		// Transaccion genesis, mandar 100 coins a la cartera 1: 
		transaccionGenesis = new Transaccion(coinbase.getClavePublica(), c1.getClavePublica(), 100, null, 0);
		transaccionGenesis.generarFirma(coinbase.getClavePrivada());	 //firma manual de la transaccion genesis	
		transaccionGenesis.setIDtransaccion("0"); //id de la transaccion manual
		SalidaTransaccion outputManual = new SalidaTransaccion(transaccionGenesis.getReceptor(), transaccionGenesis.getValor(), transaccionGenesis.getIDtransaccion());
		transaccionGenesis.getSalidas().add(outputManual); //anadir el output manualmente
		ProgramaPrincipal.getTransaccionesNoGastadas().put(transaccionGenesis.getSalidas().get(0).getId(), transaccionGenesis.getSalidas().get(0)); 
		genesis = new Bloque("0");
		genesis.anadirTransaccion(transaccionGenesis);
		genesis.minarBloque(1);
		b1 = new Bloque(genesis.getHash());
		
	}
	
	@Test
	public void testEnviarFondos() {
		assertTrue(b1.anadirTransaccion(c1.enviarFondos(c2.getClavePublica(), 10)));
		assertFalse(b1.anadirTransaccion(c1.enviarFondos(c2.getClavePublica(), 1000)));
	}
	
	@Test
	public void testBalance() {
		assertEquals((int) c1.getBalanceCartera(), 100);
		assertEquals((int) c2.getBalanceCartera(), 0);
	}

}