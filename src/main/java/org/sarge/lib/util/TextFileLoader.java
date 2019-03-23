package org.sarge.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A <i>text file loader</i> loads a simple text file ignoring comments and empty lines and delegating to a line consumer.
 * @author Sarge
 */
public class TextFileLoader {
	private final Predicate<String> comment;

	/**
	 * Default constructor.
	 * <p>
	 * Uses the hash character as a comment.
	 */
	public TextFileLoader() {
		this("#");
	}

	/**
	 * Constructor.
	 * @param comment Comment identifier
	 */
	public TextFileLoader(String comment) {
		this.comment = Predicate.not(line -> line.startsWith(comment));
	}

	/**
	 * Loads a text-file and delegates to the given line processor.
	 * @param r			Input
	 * @param line		Line processor
	 * @throws IOException if the file cannot be loaded
	 */
	public void load(Reader r, Consumer<String> line) throws IOException {
		try(final BufferedReader in = new BufferedReader(r)) {
			in.lines()
				.map(String::trim)
				.filter(Predicate.not(String::isEmpty))
				.filter(comment)
				.forEach(line);
		}
	}
}
