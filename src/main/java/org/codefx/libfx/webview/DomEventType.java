package org.codefx.libfx.webview;

import java.util.Objects;
import java.util.Optional;

import javax.swing.event.HyperlinkEvent.EventType;

/**
 * The types of DOM events names. Actually, this is only a selection of those needed to determine a hyperlink
 * {@link EventType EventType}s.
 * <p>
 *
 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list">DOM Level 3 Events Specification - Event
 *      Type List</a>
 */
public enum DomEventType {

	// #region INSTANCES

	/**
	 * A mouse click.
	 * <p>
	 * This event can be canceled.
	 *
	 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-type-click">DOM Level 3 Events Specification - CLICK
	 *      Event</a>
	 */
	CLICK("click"),

	/**
	 * The mouse entered an element's boundaries. Is <b>not</b> dispatched when the mouse moves inside the element
	 * between its descendant elements.
	 * <p>
	 * This event can not be canceled, i.e. canceling it has no effect.
	 *
	 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-type-mouseenter">DOM Level 3 Events Specification -
	 *      MOUSEENTER Event</a>
	 */
	MOUSE_ENTER("mouseenter"),

	/**
	 * The mouse left an element's boundaries. Is <b>not</b> dispatched when the mouse moves inside the element between
	 * its descendant elements.
	 * <p>
	 * This event can not be canceled, i.e. canceling it has no effect.
	 *
	 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-type-mouseleave">DOM Level 3 Events Specification -
	 *      MOUSELEAVE Event</a>
	 */
	MOUSE_LEAVE("mouseleave");

	// #end INSTANCES

	// #region DEFINITION

	/**
	 * The event's name.
	 */
	private final String domName;

	/**
	 * Creates a new DOM event type with the specified name.
	 *
	 * @param domName
	 *            the name of the event as per <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list">DOM
	 *            Level 3 Events Specification </a>
	 */
	private DomEventType(String domName) {
		this.domName = domName;
	}

	/**
	 * @return the name of the event as per <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list">DOM
	 *         Level 3 Events Specification </a>
	 */
	public String getDomName() {
		return domName;
	}

	// #end DEFINITION

	// #region HELPER

	/**
	 * Returns the DOM event type for the specified event name.
	 * 
	 * @param domEventName
	 *            the name of the DOM event as per W3C specification
	 * @return a {@link DomEventType} if it could be determined; otherwise {@link Optional#empty()}
	 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list">DOM Level 3 Events Specification - Event
	 *      Type List</a>
	 */
	public static Optional<DomEventType> byName(String domEventName) {
		Objects.requireNonNull(domEventName, "The argument 'domEventName' must not be null.");

		for (DomEventType type : DomEventType.values())
			if (type.getDomName().equals(domEventName))
				return Optional.of(type);

		return Optional.empty();
	}

	/**
	 * Returns the representation of this DOM event as an {@link EventType HyperlinkEventType} if that is possible.
	 * Otherwise returns an empty Optional.
	 *
	 * @return <ul>
	 *         <li> {@link #CLICK} &rarr; {@link EventType#ACTIVATED ACTIVATED}
	 *         <li> {@link #MOUSE_ENTER} &rarr; {@link EventType#ENTERED ENTERED}
	 *         <li> {@link #MOUSE_LEAVE} &rarr; {@link EventType#EXITED EXITED}
	 *         <li>Otherwise &rarr; {@link Optional#empty() empty}
	 *         </ul>
	 */
	public Optional<EventType> toHyperlinkEventType() {
		switch (this) {
			case CLICK:
				return Optional.of(EventType.ACTIVATED);
			case MOUSE_ENTER:
				return Optional.of(EventType.ENTERED);
			case MOUSE_LEAVE:
				return Optional.of(EventType.EXITED);
			default:
				return Optional.empty();
		}
	}

	// #end HELPER

}
