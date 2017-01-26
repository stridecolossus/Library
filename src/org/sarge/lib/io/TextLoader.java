package org.sarge.lib.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

import org.sarge.lib.util.ToString;

/**
 * General text-file loader.
 * @author Sarge
 */
public class TextLoader {
	/**
	 * Parser for lines in a text file.
	 */
	@FunctionalInterface
	public static interface LineParser {
		/**
		 * Parses the given line of text.
		 * @param line Line of text
		 * @param lineno Line number
		 */
		void parse(String line, int lineno);
	}

	private final Predicate<String> commentsFilter;
	private final boolean skipEmpty;
	
	/**
	 * Convenience constructor with hash character comments.
	 */
	public TextLoader() {
		this(Collections.singleton("#"), true);
	}

	/**
	 * Constructor.
	 * @param comments		Comment identifier(s)
	 * @param skipEmpty		Whether to skip empty lines
	 */
	public TextLoader(Collection<String> comments, boolean skipEmpty) {
		this.commentsFilter = line -> comments.stream().noneMatch(line::startsWith);
		this.skipEmpty = skipEmpty;
	}

	/**
	 * Loads a text-file.
	 * @param path file-path
	 * @return Text-file as a string
	 * @throws IOException if the data cannot be loaded
	 */
	public String load(Reader in) throws IOException {
		final StringJoiner text = new StringJoiner("\n");
		final LineParser parser = (line, lineno) -> text.add(line);
		load(parser, in);
		return text.toString();
	}

	/**
	 * Loads a text-file using the given parser to load lines.
	 * @param parser	Line parser
	 * @param in		Input stream
	 * @throws IOException if the data cannot be loaded
	 */
	public void load(LineParser parser, Reader in) throws IOException {
		try(final LineNumberReader r = new LineNumberReader(new BufferedReader(in))) {
			final Predicate<String> filter = buildLineFilter();
			r.lines().filter(filter).forEach(str -> parser.parse(str, r.getLineNumber()));
		}
	}
	
	/**
	 * @return Line filter for this loader
	 */
	private Predicate<String> buildLineFilter() {
		final List<Predicate<String>> list = new ArrayList<>();
		list.add(commentsFilter);
		if(skipEmpty) {
			list.add(line -> !line.isEmpty());
		}
		return list.stream().reduce(str -> true, Predicate::and); // TODO - utility method?
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
