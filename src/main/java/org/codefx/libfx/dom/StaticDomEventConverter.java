package org.codefx.libfx.dom;

import java.util.Objects;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.w3c.dom.events.Event;

/**
 * Class which provides {@link DomEventConverter} methods statically.
 * <p>
 * This class is thread-safe, i.e. the provided methods can be called from different threads and concurrent executions
 * do not interfere with each other.
 */
public final class StaticDomEventConverter {

	/**
	 * Indicates whether the specified DOM event can be converted to a {@link HyperlinkEvent}.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 * @return true if the event's {@link Event#getType() type} has an equivalent {@link EventType EventType}
	 * @see DomEventConverter#canConvertToHyperlinkEvent(Event)
	 */
	public static boolean canConvertToHyperlinkEvent(Event domEvent) {
		Objects.requireNonNull(domEvent, "The argument 'domEvent' must not be null.");

		Object source = "the source does not matter for this call";
		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.canConvert();
	}

	/**
	 * Converts the specified DOM event to a hyperlink event.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event} from which the {@link HyperlinkEvent} will be created
	 * @param source
	 *            the source of the {@code domEvent}
	 * @return a {@link HyperlinkEvent} with the following properties:
	 *         <ul>
	 *         <li> {@link HyperlinkEvent#getEventType() getEventType()} returns the {@link EventType} corresponding to
	 *         the domEvent's type as defined by {@link DomEventType}
	 *         <li> {@link HyperlinkEvent#getSource() getSource()} returns the specified {@code source}
	 *         <li> {@link HyperlinkEvent#getURL() getUrl()} returns the href-attribute's value of the event's source
	 *         element
	 *         <li> {@link HyperlinkEvent#getDescription() getDescription()} returns the text content of the event's
	 *         source element
	 *         <li> {@link HyperlinkEvent#getInputEvent() getInputEvent()} returns null
	 *         <li> {@link HyperlinkEvent#getSourceElement() getSourceElement()} returns null
	 *         </ul>
	 * @throws IllegalArgumentException
	 *             if the specified event can not be converted to a hyperlink event; this is the case if
	 *             {@link #canConvertToHyperlinkEvent(Event)} returns false
	 * @see DomEventConverter#convertToHyperlinkEvent(Event, Object)
	 */
	public static HyperlinkEvent convertToHyperlinkEvent(Event domEvent, Object source)
			throws IllegalArgumentException {

		SingleDomEventConverter converter = new SingleDomEventConverter(domEvent, source);
		return converter.convert();
	}

}
