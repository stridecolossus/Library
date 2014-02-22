package org.sarge.lib.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.ToString;

/**
 * Data-source relative to another data-source.
 * @author Sarge
 */
public class RelativeDataSource implements DataSource {
	private final DataSource src;
	private final String prefix;

	/**
	 * Constructor.
	 * @param src		Parent data-source
	 * @param prefix	Path prefix
	 */
	public RelativeDataSource( DataSource src, String prefix ) {
		Check.notNull( src );
		Check.notEmpty( prefix );

		this.src = src;
		this.prefix = prefix;
	}

	@Override
	public InputStream open( String path ) throws IOException {
		return src.open( prefix + File.separator + path );
	}

	@Override
	public String toString() {
		return ToString.toString( this );
	}
}
