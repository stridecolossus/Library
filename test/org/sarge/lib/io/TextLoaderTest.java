package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.io.TextLoader.LineParser;
import org.sarge.lib.util.Util;

public class TextLoaderTest {
	private TextLoader loader;

	@Before
	public void before() {
		loader = new TextLoader(Util.set("#", "//"), true);
	}

	@Test
	public void load() throws IOException {
		final String data =
				"text\n" +
				"\n\n\n" + 					// <-- empty
				"# commented\n" +
				"// also commented";
		final String str = loader.load(new StringReader(data));
		assertEquals("text", str);
	}
	
	@Test
	public void lineParser() throws IOException {
		final LongAdder adder = new LongAdder();
		final LineParser parser = (line, lineno) -> {
			adder.add(1);
			assertEquals(lineno, adder.intValue());
			assertEquals(line, String.valueOf(lineno));
		};
		loader.load(parser, new StringReader("1\n2\n3"));
		assertEquals(3, adder.intValue());
	}
}
