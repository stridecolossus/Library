package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.sarge.lib.util.Check;

/**
 * Data-source based on a ZIP file.
 * @author Sarge
 */
public final class ZipfileDataSource implements DataSource {
	private final ZipFile file;
	
	/**
	 * Constructor.
	 * @param file Zip-file
	 */
	public ZipfileDataSource( ZipFile file ) {
		Check.notNull( file );
		this.file = file;
	}
	
	@Override
	public InputStream getInputStream( String path ) throws IOException {
		final ZipEntry entry = file.getEntry( path );
		if( entry == null ) throw new IOException( "Entry not found: " + path );
		return file.getInputStream( entry );
	}
	
	@Override
	public OutputStream getOutputStream( String path ) throws IOException {
		throw new UnsupportedOperationException( "Read-only data-source" );
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
