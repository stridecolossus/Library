package org.sarge.lib.xml;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Exception relating to an {@link Element}.
 */
public class ElementException extends RuntimeException {
    /**
     * Maps elements to XML name.
     * @return Element name/index
     */
    protected static final Function<Element, String> ELEMENT_NAME = element -> {
        final String name = element.name();
        final Element parent = element.parent();
        if((parent == null) || (parent.children().count() == 1)) {
            return name;
        }
        else {
            final List<Element> children = parent.children(name).collect(Collectors.toList());
            if(children.size() == 1) {
                return name;
            }
            else {
                final int index = children.indexOf(element);
                return String.format("%s[%d]", name, index + 1);
            }
        }
    };

    private final Element element;
    
    /**
     * constructor.
     * @param element       Related element
     * @param reason        Reason text
     */
    public ElementException(Element element, String reason) {
        this(element, reason, ELEMENT_NAME);
    }

    public ElementException(Element element, Exception e) {
        this(element, e, ELEMENT_NAME);
    }

    // TODO - tidy up ctors
    
    protected ElementException(Element element, String reason, Function<Element, String> mapper) {
        super(build(element, reason, mapper));
        this.element = element;
    }
    
    protected ElementException(Element element, Exception e, Function<Element, String> mapper) {
        super(build(element, e.getMessage(), mapper), e);
        this.element = element;
    }

    /**
     * @return Element that caused this exception
     */
    public Element getElement() {
        return element;
    }
    
    private static String build(Element element, String reason, Function<Element, String> mapper) {
        final String path = element.path().map(mapper).collect(Collectors.joining("/"));
        return reason + " at /" + path;
    }
}
