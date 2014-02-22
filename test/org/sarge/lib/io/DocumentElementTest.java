package org.sarge.lib.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.lang.model.element.Modifier;

import org.junit.Before;
import org.junit.Test;
import org.sarge.lib.io.DocumentElement.DocumentException;

public class DocumentElementTest {
	private DocumentElement root;

	@Before
	public void before() throws Exception {
		root = DocumentElement.load( DocumentElementTest.class.getResourceAsStream( "ElementTest.xml" ) );
	}

	@Test
	public void load() {
		assertNotNull( root );
		assertEquals( "root", root.getName() );
		assertEquals( null, root.getParent() );
	}

	@Test
	public void getChildByName() throws DocumentException {
		final DocumentElement child = root.getChild( "child" );
		assertNotNull( child );
		assertEquals( "child", child.getName() );
	}

	@Test
	public void getChild() throws DocumentException {
		final DocumentElement parent = root.getChild( "other" );
		final DocumentElement child = parent.getChild();
		assertNotNull( child );
		assertEquals( "single", child.getName() );
	}

	@Test(expected=DocumentException.class)
	public void getChildNone() throws DocumentException {
		final DocumentElement parent = root.getChild( "child" );
		parent.getChild();
	}

	@Test(expected=DocumentException.class)
	public void getChildMultiple() throws DocumentException {
		root.getChild();
	}

	@Test
	public void getPath() throws DocumentException {
		final DocumentElement child = root.getChild( "child" );
		final List<DocumentElement> path = child.getPath();
		assertNotNull( path );
		assertEquals( 2, path.size() );
		assertEquals( root, path.get( 0 ) );
		assertEquals( child, path.get( 1 ) );

	}

	@Test
	public void getChildren() {
		final List<DocumentElement> children = root.getChildren();
		assertNotNull( children );
		assertEquals( 3, children.size() );
	}

	@Test
	public void getChildrenByName() {
		final List<DocumentElement> children = root.getChildren( "child" );
		assertNotNull( children );
		assertEquals( 2, children.size() );
		assertEquals( "child", children.get( 0 ).getName() );
		assertEquals( "child", children.get( 1 ).getName() );
	}


	@Test
	public void getChildrenText() {
		final List<String> text = root.getChildrenText( "child" );
		assertNotNull( text );
		assertEquals( 2, text.size() );
		assertEquals( "one", text.get( 0 ).trim() );
		assertEquals( "two", text.get( 1 ).trim() );
	}

	@Test
	public void hasAttribute() {
		assertEquals( true, root.hasAttribute( "integer" ) );
		assertEquals( false, root.hasAttribute( "cobblers" ) );
	}

	@Test
	public void attributes() throws DocumentException {
		assertEquals( "42", root.getString( "integer", null ) );
		assertEquals( new Integer( 42 ), root.getInteger( "integer", null ) );
		assertEquals( new Long( 1234567890 ), root.getLong( "long", null ) );
		assertEquals( new Float( 3.142 ), root.getFloat( "float", null ) );
		assertEquals( Boolean.TRUE, root.getBoolean( "boolean", null ) );
		assertEquals( Modifier.PUBLIC, root.getEnum( "enum", null, Modifier.class ) );
	}
}
