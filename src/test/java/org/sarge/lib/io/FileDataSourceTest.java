package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileDataSourceTest {
	private static Path dir;

	@BeforeClass
	public static void beforeClass() throws IOException {
		dir = Files.createTempDirectory( FileDataSourceTest.class.getSimpleName() );
		System.out.println(dir);
	}
	
	@AfterClass
	public static void afterClass() throws IOException {
		// Delete any files
		for( File f : dir.toFile().listFiles() ) {
			f.delete();
		}
		
		// Delete directory
		dir.toFile().delete();
	}
	
	@SuppressWarnings("resource")
	@Test
	public void readWrite() throws IOException {
		// Create data-source
		final DataSource ds = new FileDataSource( dir );
		
		// Open for writing
		final String path = "file";
		final OutputStream out = ds.getOutputStream( path );
		assertNotNull( out );
		
		// Write something
		final String str = "stuff";
		out.write( str.getBytes() );
		out.close();
		
		// Open for reading
		final InputStream in = ds.getInputStream( path );
		assertNotNull( in );
		
		// Read and check
		final byte[] bytes = new byte[ 100 ];
		final int len = in.read( bytes );
		in.close();
		assertEquals( str, new String( bytes, 0, len ) );
	}
}
