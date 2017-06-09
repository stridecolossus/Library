package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class StringDataSourceTest {
	@Test
	public void getInputStream() throws IOException {
		final String str = "path";
		final DataSource ds = new StringDataSource();
		final InputStream in = ds.getInputStream( str );
		final BufferedReader r = new BufferedReader( new InputStreamReader( in ) );
		assertEquals( str, r.readLine() );
	}

	@Test(expected=UnsupportedOperationException.class)
	public void getOutputStream() throws IOException {
		final DataSource ds = new StringDataSource();
		ds.getOutputStream( "doh" );
	}
}
