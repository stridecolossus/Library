package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class DelimitedTextLoaderTest {
	private static final String FILE =
		"header\n" +
		"1,2\n" +
		"";
	
	private DelimitedTextLoader loader;
	private Path path;
	
	@Before
	public void before() throws IOException {
		loader = new DelimitedTextLoader();
		path = TextLoaderTest.mockPath(FILE);
	}
	
	@Test
	public void load() throws IOException {
		assertEquals("[1, 2]", Arrays.toString(loader.load(path).iterator().next()));
	}
}
