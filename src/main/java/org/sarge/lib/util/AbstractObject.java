package org.sarge.lib.util;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Base-class object with a default Apache commons implementation for the {@link #toString()} method.
 * @author Sarge
 */
public abstract class AbstractObject {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
