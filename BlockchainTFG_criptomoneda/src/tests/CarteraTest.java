package tests;

import blockchain.Cartera;
import junit.framework.TestCase;

class CarteraTest extends TestCase{
	
	Cartera c1;
	Cartera c2; 
	
	@Override
	protected void setUp() throws Exception {
		c1 = new Cartera();
		c2 = new Cartera();
	}

	public void testBalance() {
		assertEquals(c1.getBalanceCartera(), 0);
	}

}