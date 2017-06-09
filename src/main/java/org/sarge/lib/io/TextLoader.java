package org.sarge.lib.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.sarge.lib.util.Check;
import org.sarge.lib.util.Util;

/**
 * Text loader utility providing helper functionality to skip lines and comments.
 * @author Sarge
 */
public class TextLoader {
	private int skip;
	private boolean skipEmpty;
	private Set<String> comments = Collections.emptySet();
	
	/**
	 * Loads a file as a stream of lines.
	 * @param path File-path
	 * @return Lines
	 * @throws IOException if the file cannot be loaded
	 */
	public Stream<String> loadLines(Path path) throws IOException {
		return Files.lines(path)
			.skip(skip)
			.map(String::trim)
			.filter(buildFilter());
	}

	/**
	 * @return Line filter for this loader
	 */
	private Predicate<String> buildFilter() {
		final List<Predicate<String>> filter = new ArrayList<>();
		if(skipEmpty) filter.add(String::isEmpty);
		filter.add(this::isComment);
		return Util.compoundPredicate(filter, Predicate::or, true).negate();
	}

	/**
	 * Sets the number of line to skip.
	 * @param skip Number of lines to skip
	 */
	public void setSkipLine(int skip) {
		Check.zeroOrMore(skip);
		this.skip = skip;
	}
	
	/**
	 * @param skipEmpty Whether to skip empty lines (default is <tt>false</tt>)
	 */
	public void setSkipEmpty(boolean skipEmpty) {
		this.skipEmpty = skipEmpty;
	}

	/**
	 * Sets the line comment identifier(s) to be omitted.
	 * @param comments Comment identifier(s)
	 */
	public void setCommentIdentifiers(Set<String> comments) {
		this.comments = new HashSet<>(comments);
	}

	/**
	 * @param line Line of text
	 * @return Whether the given line starts with a comment identifier
	 */
	private boolean isComment(String line) {
		return comments.stream().anyMatch(comment -> line.startsWith(comment));
	}
	
	@Override
	public String toString() {
		// TODO
		return super.toString();
	}
}
