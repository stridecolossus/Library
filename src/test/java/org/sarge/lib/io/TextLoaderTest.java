package org.sarge.lib.io;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

public class TextLoaderTest {
	private static final String FILE =
		"1\n" +				// First line
		"\n" +				// Empty line
		"  3  \n" +			// Line with padding
		"#4\n" +			// Commented line
		"5";				// Last line
	
	private TextLoader loader;
	private Path path;
	
	@Before
	public void before() throws IOException {
		loader = new TextLoader();
		path = mockPath(FILE);
	}
	
	/**
	 * TODO - move to helper
	 * Creates a mock NIO path that returns string data.
	 * @param data String data
	 * @return Path
	 * @throws IOException
	 */
	static Path mockPath(String data) throws IOException {
		// Create a file-system
		final FileSystem sys = mock(FileSystem.class);

		// Create path in this file-system
		final Path path = mock(Path.class);
		when(path.getFileSystem()).thenReturn(sys);

		// Link to input-stream provider
		final FileSystemProvider provider = mock(FileSystemProvider.class);
		when(sys.provider()).thenReturn(provider);
		
		// Return data for this path
		when(provider.newInputStream(path)).thenReturn(new ByteArrayInputStream(data.getBytes()));
		return path;
	}
	
	/**
	 * Loads file and verifies results.
	 * @param expected Expected lines to be returned
	 */
	private void run(String... expected) {
		try {
			assertArrayEquals(expected, loader.loadLines(path).toArray());
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void load() {
		run("1", "", "3", "#4", "5");
	}
	
	@Test
	public void loadSkipLine() {
		loader.setSkipLine(1);
		run("", "3", "#4", "5");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadSkipLineInvalid() {
		loader.setSkipLine(-1);
	}
	
	@Test
	public void loadSkipEmpty() {
		loader.setSkipEmpty(true);
		run("1", "3", "#4", "5");
	}
	
	@Test
	public void loadSkipComment() {
		loader.setCommentIdentifiers(Collections.singleton("#"));
		run("1", "", "3", "5");
	}
}
