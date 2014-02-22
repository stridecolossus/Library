package org.sarge.lib.io;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class RelativeDataSourceTest {
	@Test
	public void open() throws IOException {
		final DataSource parent = mock( DataSource.class );
		final RelativeDataSource src = new RelativeDataSource( parent, "prefix" );
		src.open( "path" );
		verify( parent ).open( "prefix" + File.separator + "path" );
	}
}
