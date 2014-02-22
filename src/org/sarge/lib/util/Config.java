package org.sarge.lib.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Immutable configuration settings based on a properties file.
 * @author Sarge
 */
public class Config extends ConverterAdapter {
	private final Properties cfg;

	/**
	 * Constructor.
	 * @param cfg Config file
	 */
	public Config( Properties cfg ) {
		Check.notNull( cfg );
		this.cfg = cfg;
	}

	/**
	 * Constructor for a config-file.
	 * @param is Configuration file
	 * @throws IOException if the file is invalid
	 */
	public Config( InputStream is ) throws IOException {
		Check.notNull( is );
		cfg = new Properties();
		cfg.load( is );
	}

	@Override
	protected String getValue( String name ) {
		return cfg.getProperty( name );
	}
}
