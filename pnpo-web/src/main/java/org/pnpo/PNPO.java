package org.pnpo;

import java.io.IOException;
import java.util.Properties;

public class PNPO {
	public static final int MAJOR_VERSION = Integer
			.parseInt(PNPOProperties.properties.getProperty(PNPOProperties.PROPERTY_MAJOR_VERSION));
	public static final int MINOR_VERSION = Integer
			.parseInt(PNPOProperties.properties.getProperty(PNPOProperties.PROPERTY_MINOR_VERSION));
	public static final int MICRO_VERSION = Integer
			.parseInt(PNPOProperties.properties.getProperty(PNPOProperties.PROPERTY_MICRO_VERSION));
	public static final String QUALIFIER_VERSION = PNPOProperties.properties
			.getProperty(PNPOProperties.PROPERTY_QUALIFIER_VERSION);

	public static final String VERSION = "" + MAJOR_VERSION + "." + MINOR_VERSION + "." + MICRO_VERSION + "-"
			+ QUALIFIER_VERSION;

	private static class PNPOProperties {
		private static final String PROPERTY_VERSION = "version";
		private static final String PROPERTY_MAJOR_VERSION = "version.major";
		private static final String PROPERTY_MINOR_VERSION = "version.minor";
		private static final String PROPERTY_MICRO_VERSION = "version.micro";
		private static final String PROPERTY_QUALIFIER_VERSION = "version.qualifier";

		public static Properties properties = new Properties();

		static {
			try {
				properties.load(PNPOProperties.class.getResourceAsStream("/pnpo-web.properties"));
			} catch (IOException e) {
			}

			String version = properties.getProperty(PROPERTY_VERSION);
			String versions[] = version.split("\\.");
			String tails[] = versions[2].split("(?<=[0-9]+)\\-");

			properties.put(PROPERTY_MAJOR_VERSION, versions[0]);
			properties.put(PROPERTY_MINOR_VERSION, versions[1]);
			properties.put(PROPERTY_MICRO_VERSION, tails[0]);
			properties.put(PROPERTY_QUALIFIER_VERSION, tails[1]);
		}
	}
}
