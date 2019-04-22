package tests;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import blockchain.Bloque;
import blockchain.SmartContract;
import blockchain.Transaccion;
import junit.framework.TestCase;

public class BloqueTest extends TestCase{
	
	Bloque b1;
	
	public BloqueTest() {}
	
	@Before
	protected void setUp() throws Exception {
		b1 = new Bloque("0");
	}
	
	@Test
	public void testHash() {
		Bloque b2 = new Bloque();
		b2.setMarcaTemporal(b1.getMarcaTemporal());
		b2.setHashAnterior("0");
		assertEquals(b1.getHash(), b2.calcularHash());
	}
	
	@Test
	public void testAnadirTransaccion() {
		assertTrue(b1.anadirTransaccion(new Transaccion()));
	}
	
	@Test
	public void testAnadirContrato() {
		assertTrue(b1.anadirContrato(new SmartContract()));
	}
}

