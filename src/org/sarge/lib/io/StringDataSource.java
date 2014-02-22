package org.sarge.lib.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mock implementation that returns the given path as the input-stream.
 * @author Sarge
 */
public class StringDataSource implements DataSource {
	@Override
	public InputStream open( String path ) throws IOException {
		return new ByteArrayInputStream( path.getBytes() );
	}
}
