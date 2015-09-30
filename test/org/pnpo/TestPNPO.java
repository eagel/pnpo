package org.pnpo;

import org.junit.Assert;
import org.junit.Test;

public class TestPNPO {
	@Test
	public void testVersion() {
		Assert.assertEquals("0.0.2.M2", PNPO.VERSION);
	}
}
