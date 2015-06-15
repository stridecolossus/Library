package org.sarge.lib.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Read-only data-source that returns the path as its data.
 * @author Sarge
 */
public final class StringDataSource implements DataSource {
	@Override
	public InputStream getInputStream( String path ) throws IOException {
		return new ByteArrayInputStream( path.getBytes() );
	}
	
	@Override
	public OutputStream getOutputStream( String path ) throws IOException {
		throw new UnsupportedOperationException( "Cannot write to a string data-source" );
	}
}
