package tests;

import static org.junit.Assert.*;

import java.security.Security;
import java.util.ArrayList;
import java.util.Random;

import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.Before;
import org.junit.Test;

import algoritmosCriptograficos.Aes;
import algoritmosCriptograficos.StringUtils;
import blockchain.Cartera;
import blockchain.Transaccion;

public class TestAlgoritmosCripto {

	String cadena;
	String clave;
	String clave2;
	Cartera c1;
	Cartera c2;
	ArrayList<Transaccion> transacciones;
	Random rand;
	
	@Before
	public void setUp() throws Exception {
		Security.addProvider(new BouncyCastlePQCProvider());
		cadena = "texto";
		clave = "contra123";
		clave2 = "123contra";
		c1 = new Cartera();
		c2 = new Cartera();
		transacciones = new ArrayList<Transaccion>();
		int min = 1;
		int max = 10;
		for(int i=0; i<10; i++) {
			rand = new Random();
			transacciones.add(new Transaccion(c1.getClavePublica(), c2.getClavePublica(), i*rand.nextInt((max - min) + 1) + min, null, i));
		}
	}

	@Test
	public void testAES() {
		assertNotEquals(Aes.encrypt(cadena, clave), cadena);
		assertNotNull(Aes.encrypt(cadena, clave));
		assertEquals(cadena, Aes.decrypt(Aes.encrypt(cadena, clave), clave));
		assertNotEquals(cadena, Aes.decrypt(Aes.encrypt(cadena, clave2), clave));
		assertNotEquals(cadena, Aes.decrypt(Aes.encrypt(cadena, clave), clave2));
	}
	
	@Test
	public void testClaveDesdeString() {
		String publi = StringUtils.getStringClave(c1.getClavePublica());
		String priv = StringUtils.getStringClave(c1.getClavePrivada());
		
		assertEquals(StringUtils.getClaveDesdeString(publi, true), c1.getClavePublica());
		assertEquals(StringUtils.getClaveDesdeString(priv, false), c1.getClavePrivada());
	}
	
	@Test
	public void testRaizMerkle() {
		assertNotNull(StringUtils.getMerkleRoot(transacciones));
	}
	
}
