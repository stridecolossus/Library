package org.sarge.lib.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.ToString;

/**
 * General text-file loader.
 * @author Sarge
 */
public class TextLoader {
	/**
	 * Parser for lines in a text file.
	 */
	public static interface LineParser {
		/**
		 * Parses the given line of text.
		 * @param line 		Line of text
		 * @param lineno	Line number
		 */
		void parse( String line, int lineno );
	}

	private final DataSource src;

	private Collection<String> comments = new HashSet<>();
	private boolean skipEmpty = true;

	/**
	 * Constructor.
	 * @param src Data-source
	 */
	public TextLoader( DataSource src ) {
		Check.notNull( src );
		this.src = src;
	}

	/**
	 * Sets the comment string.
	 * @param comment Comment identifier or <tt>null</tt> if none (default)
	 */
	public void setCommentIdentifier( String comment ) {
		Check.notNull( comment );
		setCommentIdentifiers( Collections.singleton( comment ) );
	}

	/**
	 * Sets the comment identifiers.
	 * @param comments Comment identifiers
	 */
	public void setCommentIdentifiers( Collection<String> comments ) {
		Check.notNull( comments );
		this.comments = comments;
	}

	/**
	 * @param skipEmpty Whether to skip empty lines (default is <tt>true</tt>)
	 */
	public void setSkipEmptyLines( boolean skipEmpty ) {
		this.skipEmpty = skipEmpty;
	}

	/**
	 * Loads a text-file.
	 * @param path file-path
	 * @return Text-file as a string
	 * @throws IOException if the file cannot be opened or is invalid
	 */
	public String load( String path ) throws IOException {
		// Create parser to buffer file
		final StringBuilder sb = new StringBuilder();
		final LineParser parser = new LineParser() {
			@Override
			public void parse( String line, int num ) {
				sb.append( line );
				sb.append( '\n' );
			}
		};

		// Load file
		load( parser, path );

		// Convert to string
		return sb.toString();
	}

	/**
	 * Loads a text-file using the given parser to load lines.
	 * @param parser	Line parser
	 * @param path		file-path
	 * @throws IOException if the file cannot be opened or is invalid
	 */
	public void load( LineParser parser, String path ) throws IOException {
		// Load file
		final LineNumberReader r = new LineNumberReader( new InputStreamReader( src.open( path ) ) );
		try {
			while( true ) {
				// Load next line
				String line = r.readLine();

				// Stop at EOF
				if( line == null ) break;

				// Skip empty lines
				line = line.trim();
				if( skipEmpty && ( line.length() == 0 ) ) continue;

				// Skip comments
				if( isComment( line ) ) continue;

				// Delegate to parser
				parser.parse( line, r.getLineNumber() );
			}
		}
		catch( Throwable t ) {
			throw new IOException( t.getMessage() + " at line " + r.getLineNumber(), t );
		}
	}

	private boolean isComment( String line ) {
		for( String str : comments ) {
			if( line.startsWith( str ) ) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return ToString.toString( this );
	}
}
