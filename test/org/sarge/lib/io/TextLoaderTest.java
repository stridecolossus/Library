package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class TextLoaderTest {
	private TextLoader loader;
	private String result;

	@Before
	public void before() throws IOException {
		loader = new TextLoader();
		result = null;
	}
	
	private void load( String str ) throws IOException {
		result = loader.loadFile( new StringReader( str ) );
	}
	
	@Test
	public void load() throws IOException {
		final Stream<String> lines = loader.load( new StringReader( "one\ntwo" ) );
		final List<String> list = lines.collect( Collectors.toList() );
		assertEquals( 2, list.size() );
		assertEquals( "one", list.get( 0 ) );
		assertEquals( "two", list.get( 1 ) );
	}
	
	@Test
	public void lineParser() throws IOException {
		final String line = "line";
		final Consumer<String> parser = str -> assertEquals( line, str );
		loader.load( new StringReader( line ), parser );
	}
	
	@Test
	public void loadFile() throws IOException {
		final String str = "one\ntwo";
		load( str );
		assertEquals( str, result );
	}
	
	@Test
	public void loadSkipEmpty() throws IOException {
		loader.setSkipEmptyLines( true );
		final String str = "one \n\n two \n\n";
		load( str );
		assertEquals( "one\ntwo", result );
	}
	
	@Test
	public void loadSkipHeaders() throws IOException {
		loader.setHeadersLines( 1 );
		final String str = "header\none\ntwo";
		load( str );
		assertEquals( "one\ntwo", result );
	}
	
	@Test
	public void loadSkipComments() throws IOException {
		loader.setCommentIdentifier( "#" );
		final String str = "one\n#two\nthree";
		load( str );
		assertEquals( "one\nthree", result );
	}
	
	@Test
	public void loadDelimiter() throws IOException {
		loader.setDelimiter( "+" );
		final String str = "one\ntwo";
		load( str );
		assertEquals( "one+two", result );
	}
}
