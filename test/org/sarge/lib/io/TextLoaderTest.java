package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.io.TextLoader.LineParser;

public class TextLoaderTest {
	private TextLoader loader;

	@Before
	public void before() {
		loader = new TextLoader( new StringDataSource() );
	}

	@Test
	public void load() throws IOException {
		final String str = "one\ntwo\n";
		final String result = loader.load( str );
		assertEquals( str, result );
	}

	@Test
	public void loadSkipComments() throws IOException {
		loader.setCommentIdentifier( "#" );
		final String str = "one\n#comment\ntwo";
		final String result = loader.load( str );
		assertEquals( "one\ntwo\n", result );
	}

	@Test
	public void loadSkipEmptyLines() throws IOException {
		loader.setSkipEmptyLines( true );
		final String str = "one\n\ntwo";
		final String result = loader.load( str );
		assertEquals( "one\ntwo\n", result );
	}

	@Test
	public void loadParser() throws IOException {
		final LineParser parser = mock( LineParser.class );
		final String str = "string";
		loader.load( parser, str );
		verify( parser ).parse( str, 1 );
	}
}
