package org.codefx.libfx.dom;

import java.util.Objects;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Class which provides {@link DomEventConverter} methods statically.
 * <p>
 * This class is thread-safe, i.e. the provided methods can be called from different threads and concurrent executions
 * do not interfere with each other.
 */
public final class StaticDomEventConverter {

	/**
	 * @see DomEventConverter#canConvertToHyperlinkEvent(Event)
	 */
	@SuppressWarnings("javadoc")
	public static boolean canConvertToHyperlinkEvent(Event domEvent) {
		Objects.requireNonNull(domEvent, "The argument 'domEvent' must not be null.");

		Object source = "the source does not matter for this call";
		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.canConvert();
	}

	/**
	 * @see DomEventConverter#convertToHyperlinkEvent(Event, Object)
	 */
	@SuppressWarnings("javadoc")
	public static HyperlinkEvent convertToHyperlinkEvent(Event domEvent, Object source)
			throws IllegalArgumentException {

		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.convert();
	}

}
