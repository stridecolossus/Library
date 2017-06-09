package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.sarge.lib.util.Check;

/**
 * Data-source using a specified class-path.
 * @author Sarge
 */
public final class ClasspathDataSource implements DataSource {
	private final Class<?> clazz;
	
	/**
	 * Constructor.
	 * @param clazz Class
	 */
	public ClasspathDataSource( Class<?> clazz ) {
		Check.notNull( clazz );
		this.clazz = clazz;
	}

	@Override
	public InputStream getInputStream( String path ) throws IOException {
		final InputStream is = clazz.getResourceAsStream( path );
		if( is == null ) throw new IOException( "Class-path resource not found: " + path );
		return is;
	}
	
	@Override
	public OutputStream getOutputStream( String path ) throws IOException {
		throw new UnsupportedOperationException( "Cannot write to a class-path data-source" );
	}
	
	@Override
	public String toString() {
		return clazz.getName();
	}
}
