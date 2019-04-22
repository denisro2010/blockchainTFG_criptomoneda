package tests;

import java.security.Security;
import java.util.Date;

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.Before;
import org.junit.Test;
import algoritmosCriptograficos.StringUtils;
import blockchain.Cartera;
import blockchain.SmartContract;
import junit.framework.TestCase;
import vista.VentanaLogin;

public class SmartContractTest extends TestCase{

	SmartContract sc;
	Cartera c1;
	Cartera c2;
	long fecha;
	
	@Before
	public void setUp() {
		Security.addProvider(new BouncyCastlePQCProvider());
		fecha = new Date().getTime();
		c1 = new Cartera();
		c2 = new Cartera();
		VentanaLogin.setCarteraActual(c1);
		sc = new SmartContract(fecha, 10, StringUtils.getStringClave(c1.getClavePublica()), StringUtils.getStringClave(c2.getClavePublica()));
	}
	
	@Test
	public void testContratoValido() {
		assertTrue(sc.esContratoValido());
	}
	
	@Test
	public void testFirma() {
		sc.generarFirmaTransaccionContract(c1.getClavePrivada(), StringUtils.getStringClave(c1.getClavePublica()), StringUtils.getStringClave(c2.getClavePublica()), 10);
		assertNotNull(sc.getFirmaTransaccion());
	}

}
