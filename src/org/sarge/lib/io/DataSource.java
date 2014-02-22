package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines a generic data-source.
 * @author Sarge
 */
public interface DataSource {
	/**
	 * Opens a data-source with the given path.
	 * @param path Path
	 * @return Input-stream
	 * @throws IOException if the data-source cannot be opened
	 */
	InputStream open( String path ) throws IOException;
}
