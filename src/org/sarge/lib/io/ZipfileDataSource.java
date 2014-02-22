package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.sarge.lib.util.Check;

/**
 * Data-source using a ZIP file.
 * @author Sarge
 */
public class ZipfileDataSource implements DataSource {
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
	public InputStream open( String path ) throws IOException {
		return file.getInputStream( new ZipEntry( path ) );
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
