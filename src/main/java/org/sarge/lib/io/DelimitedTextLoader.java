package org.sarge.lib.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Delimited text loader.
 * <p>
 * Default settings:
 * <ul>
 * <li>skips header</li>
 * <li>skips empty lines</li>
 * <li>default delimiter is the comma separator</li>
 * </ul>
 * @see #getTextLoader()
 * @author Sarge
 */
public class DelimitedTextLoader {
	private final TextLoader loader = new TextLoader();
	
	private String delimiter = ",";

	/**
	 * Constructor.
	 */
	public DelimitedTextLoader() {
		loader.setSkipLine(1);
		loader.setSkipEmpty(true);
	}

	/**
	 * @return Underlying text loader
	 */
	public TextLoader getTextLoader() {
		return loader;
	}

	/**
	 * Sets the cell delimiter.
	 * @param delimiter Delimiter
	 */
	public void setDelimiter(String delimiter) {
		//Check.notEmpty(delimiter);
		this.delimiter = delimiter;
	}

	/**
	 * Loads a delimited file as a stream of string-arrays.
	 * @param path File path
	 * @return Delimited strings
	 * @throws IOException if the file cannot be loaded
	 */
	public Stream<String[]> load(Path path) throws IOException {
		return loader.loadLines(path).map(line -> line.split(delimiter));
	}
}
