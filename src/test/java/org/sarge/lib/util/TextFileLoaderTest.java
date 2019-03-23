package org.sarge.lib.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextFileLoaderTest {
	private TextFileLoader loader;
	private Consumer<String> line;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void before() {
		loader = new TextFileLoader();
		line = mock(Consumer.class);
	}

	@Test
	public void load() throws IOException {
		loader.load(new StringReader("one\ntwo"), line);
		verify(line).accept("one");
		verify(line).accept("two");
		verifyNoMoreInteractions(line);
	}

	@Test
	public void empty() throws IOException {
		loader.load(new StringReader(""), line);
		verifyZeroInteractions(line);
	}

	@Test
	public void comment() throws IOException {
		loader.load(new StringReader("#comment"), line);
		verifyZeroInteractions(line);
	}
}
