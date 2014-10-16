package org.codefx.libfx.dom;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.w3c.dom.events.Event;

/**
 * Transforms {@link Event DOM Events} to Swing's {@link HyperlinkEvent HyperlinkEvents}.
 */
public interface EventTransformer {

	/**
	 * Indicates whether the specified DOM event can be transformed to a {@link HyperlinkEvent}.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 * @return true if the event's {@link Event#getType() type} has an equivalent {@link EventType EventType}
	 */
	boolean canTransformToHyperlinkEvent(Event domEvent);

	/**
	 * Transforms the specified DOM event to a hyperlink event.
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
	 *             if the specified event can not be transformed to a hyperlink event; this is the case if
	 *             {@link #canTransformToHyperlinkEvent(Event)} returns false
	 */
	HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source) throws IllegalArgumentException;

}
