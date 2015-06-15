package org.sarge.lib.util;

/**
 * String converter for an enumerated type.
 * @author Sarge
 * @param <E> Enumerated type
 */
public class EnumConverter<E extends Enum<E>> implements Converter<E> {
	private final Class<E> clazz;
	
	/**
	 * Constructor.
	 * @param clazz Enum class
	 */
	public EnumConverter( Class<E> clazz ) {
		Check.notNull( clazz );
		this.clazz = clazz;
	}
	
	@Override
	public E convert( String str ) throws NumberFormatException {
		return Util.getEnumConstant( str, clazz );
	}
}
