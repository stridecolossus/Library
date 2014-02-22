package org.sarge.lib.io;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.junit.Test;

@SuppressWarnings("resource")
public class DataSourceTest {
	@Test
	public void openFileDataSource() throws IOException {
		final File root = new File( "./test/org/sarge/lib/io" );
		final DataSource src = new FileDataSource( root );
		final InputStream is = src.open( "DataSourceTest.java" );
		assertNotNull( is );
	}

	@Test
	public void openClasspathDataSource() throws IOException {
		final DataSource src = new ClasspathDataSource( DataSourceTest.class );
		final InputStream is = src.open( "DataSourceTest.class" );
		assertNotNull( is );
	}

	@Test
	public void openZipFileDataSource() throws IOException {
		final ZipFile file = new ZipFile( "./test/org/sarge/lib/io/DataSourceTest.zip" );
		final DataSource src = new ZipfileDataSource( file );
		final InputStream is = src.open( "file.txt" );
		assertNotNull( is );
	}
}
