package org.sarge.lib.object;

/**
 * Generates a hash-code using reflection.
 * @author Sarge
 */
public final class HashCodeBuilder {
	private HashCodeBuilder() {
		// Utility class
	}

	/**
	 * Generates a hash-code for the given object using reflection.
	 * @param obj Object
	 * @return Hash-code
	 */
	public static int hashCode(Object obj) {
		return ReflectionUtils.getMembers(obj.getClass())
			.map(field -> ReflectionUtils.getValue(field, obj))
			.mapToInt(Object::hashCode)
			.reduce(17, (total, hash) -> total * 37 + hash);
	}
}
