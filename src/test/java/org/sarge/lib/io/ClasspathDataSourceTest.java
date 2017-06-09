package org.sarge.lib.io;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

public class ClasspathDataSourceTest {
	private DataSource ds;
	
	@Before
	public void before() {
		ds = new ClasspathDataSource( ClasspathDataSourceTest.class );
	}
	
	@Test
	public void getInputStream() throws IOException {
		final InputStream in = ds.getInputStream( "./ClasspathDataSourceTest.class" );
		assertNotNull( new InputStreamReader( in ) );
	}

	@Test(expected=UnsupportedOperationException.class)
	public void getOutputStream() throws IOException {
		ds.getOutputStream( "doh" );
	}
}
