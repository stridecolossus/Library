package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Data-source using the local file-system.
 * @author Sarge
 */
public final class FileDataSource implements DataSource {
	private final Path root;

	/**
	 * Constructor.
	 * @param root Root directory
	 */
	public FileDataSource( Path root ) {
		if( !root.toFile().isDirectory() ) throw new IllegalArgumentException( "Not a directory: " + root );
		this.root = root;
	}

	@Override
	public InputStream getInputStream( String path ) throws IOException {
		return Files.newInputStream( toPath( path ) );
	}
	
	@Override
	public OutputStream getOutputStream( String path ) throws IOException {
		return Files.newOutputStream( toPath( path ) );
	}
	
	private Path toPath( String path ) {
		return root.resolve( Paths.get( path ) );
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
