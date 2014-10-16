package org.codefx.libfx.dom;

import java.util.Objects;

import javax.swing.event.HyperlinkEvent;

import org.w3c.dom.events.Event;

/**
 * Class which provides {@link EventTransformer} methods statically.
 */
public class StaticEventTransformer {

	/**
	 * @see EventTransformer#canTransformToHyperlinkEvent(Event)
	 */
	@SuppressWarnings("javadoc")
	public static boolean canTransformToHyperlinkEvent(Event domEvent) {
		Objects.requireNonNull(domEvent, "The argument 'domEvent' must not be null.");

		Object source = "the source does not matter for this call";
		DomEventToHyperlinkEventTransformer transformer = new DomEventToHyperlinkEventTransformer(domEvent, source);
		return transformer.canTransform();
	}

	/**
	 * @see EventTransformer#transformToHyperlinkEvent(Event, Object)
	 */
	@SuppressWarnings("javadoc")
	public static HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source)
			throws IllegalArgumentException {

		DomEventToHyperlinkEventTransformer transformer = new DomEventToHyperlinkEventTransformer(domEvent, source);
		return transformer.transform();
	}

}
