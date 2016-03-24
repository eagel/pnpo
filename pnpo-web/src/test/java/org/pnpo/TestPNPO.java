package org.pnpo;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class TestPNPO {
	@Test
	public void testVersion() throws IOException {
		Properties properties = new Properties();

		properties.load(TestPNPO.class.getResourceAsStream("/pnpo-web.properties"));

		Assert.assertEquals(properties.getProperty("version"), PNPO.VERSION);
	}
}
