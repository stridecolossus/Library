package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Defines a generic data-source.
 * @author Sarge
 */
public interface DataSource {
	/**
	 * Opens this data-source for reading.
	 * @param path Path
	 * @return Input-stream
	 * @throws IOException if the data-source cannot be opened
	 */
	InputStream getInputStream( String path ) throws IOException;
	
	/**
	 * Opens this data-source for writing.
	 * @param path Path
	 * @return Output-stream
	 * @throws IOException if the data-source cannot be opened
	 */
	OutputStream getOutputStream( String path ) throws IOException;
}
