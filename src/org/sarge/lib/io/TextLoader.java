package org.sarge.lib.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.ToString;

/**
 * General text-file loader.
 * @author Sarge
 */
public class TextLoader {
	private Collection<String> comments = Collections.emptySet();
	private boolean skipEmpty = true;
	private int headers;
	private String delimiter = "\n";

	/**
	 * Sets the number of header lines to skip.
	 * @param headers Number of header lines
	 */
	public void setHeadersLines( int headers ) {
		Check.zeroOrMore( headers );
		this.headers = headers;
	}

	/**
	 * Sets a single comment string.
	 * @param comment Comment identifier
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
	 * Sets the delimiter for separating lines.
	 * @param delimiter Line delimiter
	 */
	public void setDelimiter( String delimiter ) {
		Check.notNull( delimiter );
		this.delimiter = delimiter;
	}

	/**
	 * Loads a text-file as a stream of lines.
	 * @param in Input
	 * @throws IOException if the text cannot be loaded
	 */
	public Stream<String> load( Reader in ) throws IOException {
		final BufferedReader r = new BufferedReader( in );
		return r.lines()
			.skip( headers )
			.map( String::trim )
			.filter( str -> !( skipEmpty && str.isEmpty() ) )
			.filter( this::isTextLine );
	}

	private boolean isTextLine( String line ) {
		return !comments.stream().filter( str -> line.startsWith( str ) ).findAny().isPresent();
	}

	/**
	 * Loads a text-file line-by-line.
	 * @param in		Input
	 * @param parser	Parser for lines of the file
	 * @throws IOException if the text cannot be loaded
	 */
	public void load( Reader in, Consumer<String> parser ) throws IOException {
		try( final LineNumberReader r = new LineNumberReader( in ) ) {
			try {
				load( r ).forEach( line -> parser.accept( line ) );
			}
			catch( Exception e ) {
				throw new IOException( e.getMessage() + " at line " + ( r.getLineNumber() + 1 ) );
			}
		}
	}

	/**
	 * Loads a text-file.
	 * @param in Input
	 * @return Text-file as a string
	 * @throws IOException if the text cannot be loaded
	 */
	public String loadFile( Reader in ) throws IOException {
		final StringJoiner text = new StringJoiner( delimiter );
		load( in, text::add );
		return text.toString();
	}
	
	@Override
	public String toString() {
		return ToString.toString( this );
	}
}
