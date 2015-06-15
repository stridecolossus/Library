package org.sarge.lib.util;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General utilities.
 * @author Sarge
 */
public class Util {
	private static final Logger log = Logger.getLogger( Util.class.getName() );

	private static Map<Class<?>, Class<?>> primitives = new HashMap<>();
	private static Map<Class<?>, Class<?>> wrappers = new HashMap<>();

	private static void add( Class<?> clazz, Class<?> wrapper ) {
		wrappers.put( clazz, wrapper );
		primitives.put( wrapper, clazz );
	}

	static {
		add( float.class, Float.class );
		add( int.class, Integer.class );
		add( long.class, Long.class );
		add( boolean.class, Boolean.class );
	}

	private Util() {
		// Utilities class
	}

	/**
	 * @param str String to test
	 * @return Whether the given string is <tt>null</tt> or empty
	 */
	public static boolean isEmpty( String str ) {
		return ( str == null ) || ( str.length() == 0 );
	}

	/**
	 * @param c Collection to test
	 * @return Whether the given collection is <tt>null</tt> or empty
	 */
	public static boolean isEmpty( Collection<?> c ) {
		return ( c == null ) || c.isEmpty();
	}

	/**
	 * @param c Collection to test
	 * @return Whether the given collection contains any duplicate elements
	 */
	public static boolean hasDuplicates(Collection<?> c) {
		final Set<?> set = new HashSet<>(c);
		return set.size() != c.size();
	}

	/**
	 * Appends a file separator to the given string.
	 * @param path Path
	 * @return Path with appended file separator
	 */
	public static String appendFileSeparator( String path ) {
		if( path.endsWith( File.separator ) ) {
			return path;
		}
		else {
			return path + File.separator;
		}
	}

	/**
	 * Looks up the enum constant with the given name.
	 * <p>
	 * The name is formatted to Java enum style:
	 * <ul>
	 * <li>upper case</li>
	 * <li>trimmed</li>
	 * <li>hyphens replaced by under-scores</li>
	 * </ul>
	 * @param name		Enum constant name
	 * @param clazz		Enum class
	 * @return Enum constant
	 * @throws IllegalArgumentException if the specified constant does not exist
	 */
	public static <E extends Enum<E>> E getEnumConstant( String name, Class<E> clazz ) {
		// Format to Java enum style
		final String str = name.trim().toUpperCase().replace( '-', '_' );
		
		// Find constant
		for( E e : clazz.getEnumConstants() ) {
			if( e.name().equals( str ) ) return e;
		}
		
		// No matching constant
		throw new IllegalArgumentException( "Unknown enum constant " + name + " for " + clazz.getName() );
	}

	/**
	 * Sleeps the current thread.
	 * @param time Duration
	 */
	public static void kip( long time ) {
		try {
			Thread.sleep( time );
		}
		catch( InterruptedException e ) {
			log.log( Level.WARNING, "Sleep interrupted", e );
		}
	}

	/**
	 * Maps primitive types to the corresponding wrapper class.
	 * @param type Primitive type
	 * @return Wrapper type
	 */
	public static Class<?> toWrapper( Class<?> type ) {
		return wrappers.get( type );
	}

	/**
	 * Maps wrapper types to primitives.
	 * @param wrapper Wrapper type
	 * @return Primitive type
	 */
	public static Class<?> toPrimitive( Class<?> wrapper ) {
		return wrappers.get( wrapper );
	}
}
