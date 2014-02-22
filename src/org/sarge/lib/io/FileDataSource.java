package org.sarge.lib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.Util;

/**
 * Data-source using the local file-system.
 * @author Sarge
 */
public class FileDataSource implements DataSource {
	private final String root;

	/**
	 * Constructor.
	 * @param root Root directory
	 */
	public FileDataSource( File root ) {
		Check.notNull( root );
		if( !root.isDirectory() ) throw new IllegalArgumentException( "Invalid root directory: " + root );
		this.root = Util.appendFileSeparator( root.getPath() );
	}

	@Override
	public InputStream open( String path ) throws IOException {
		return new FileInputStream( root + path );
	}

	@Override
	public String toString() {
		return root;
	}
}
