package org.codefx.libfx.webview;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;

/**
 * Creates a {@link HyperlinkEvent} from the DOM-{@link Event} specified during construction.
 */
class DomEventToHyperlinkEventTransformer {

	/**
	 * The DOM-{@link Event} from which the {@link HyperlinkEvent} will be created.
	 */
	private final Event domEvent;

	/**
	 * The source of the {@link #domEvent}.
	 */
	private final Object source;

	/**
	 * Creates a new transformer for the specified arguments.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event} from which the {@link HyperlinkEvent} will be created
	 * @param source
	 *            the source of the {@code domEvent}
	 */
	public DomEventToHyperlinkEventTransformer(Event domEvent, Object source) {
		Objects.requireNonNull(domEvent, "The argument 'domEvent' must not be null.");
		Objects.requireNonNull(source, "The argument 'source' must not be null.");

		this.domEvent = domEvent;
		this.source = source;
	}

	// #region PUBLIC STATIC

	/**
	 * Indicates whether the specified DOM event can be transformed to a {@link HyperlinkEvent}.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 * @return true if the event's {@link Event#getType() type} has an equivalent {@link EventType EventType}
	 */
	public static boolean canTransform(Event domEvent) {
		Optional<EventType> eventType = getEventTypeFrom(domEvent);
		return eventType.isPresent();
	}

	/**
	 * Transforms the specified DOM event to a hyperlink event.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event} from which the {@link HyperlinkEvent} will be created
	 * @param source
	 *            the source of the {@code domEvent}
	 * @return a {@link HyperlinkEvent}
	 * @throws IllegalArgumentException
	 *             if the specified event can not be transformed to a hyperlink event; this is the case if
	 *             {@link #canTransform(Event)} returns false
	 */
	public static HyperlinkEvent transform(Event domEvent, Object source) throws IllegalArgumentException {
		DomEventToHyperlinkEventTransformer transformer = new DomEventToHyperlinkEventTransformer(domEvent, source);
		return transformer.transform();
	}

	// #end PUBLIC STATIC

	// #region TRANSFORM

	/**
	 * Transforms the event specified during construction to a hyperlink event.
	 *
	 * @return a {@link HyperlinkEvent}
	 * @throws IllegalArgumentException
	 *             if the specified event can not be transformed to a hyperlink event; this is the case if
	 *             {@link #canTransform(Event)} returns false
	 */
	public HyperlinkEvent transform() throws IllegalArgumentException {
		EventType type = getEventTypeForDomEvent();
		URL url = getURLPossiblyNull();
		String linkDescription = getDescription();

		return new HyperlinkEvent(source, type, url, linkDescription);
	}

	/**
	 * Returns the hyperlink event type equivalent of the specified DOM event if it exists.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event} whose {@link Event#getType() type} will be determined
	 * @return the equivalent Hyperlink.{@link EventType} if it exists
	 */
	private static Optional<EventType> getEventTypeFrom(Event domEvent) {
		String domEventName = domEvent.getType();
		Optional<EventType> eventType = DomEventType
				.byName(domEventName)
				.flatMap(domEventType -> domEventType.toHyperlinkEventType());
		return eventType;
	}

	/**
	 * Returns the hyperlink event type equivalent of the specified DOM event.
	 *
	 * @return the equivalent Hyperlink.{@link EventType}
	 * @throws IllegalArgumentException
	 *             if the {@link #domEvent}'s type has no equivalent hyperlink event type
	 */
	private EventType getEventTypeForDomEvent() throws IllegalArgumentException {
		Optional<EventType> eventType = getEventTypeFrom(domEvent);
		if (eventType.isPresent())
			return eventType.get();
		else
			throw new IllegalArgumentException(
					"The DOM event '" + domEvent + "' of type '" + domEvent.getType()
							+ "' can not be transformed to a hyperlink event.");
	}

	/**
	 * Returns the {@link #domEvent}'s target's attribute value which will be used as the created hyperlink event's
	 * {@link HyperlinkEvent#getDescription() description}.
	 *
	 * @return the description
	 */
	private String getDescription() {
		Element targetElement = (Element) domEvent.getTarget();
		// TODO: get the actual text of the link
		return targetElement.getAttribute("text");
	}

	/**
	 * Returns the URL the interacted hyperlink points to.
	 * 
	 * @return the {@link URL} if it could be created; otherwise null
	 */
	private URL getURLPossiblyNull() {
		Element targetElement = (Element) domEvent.getTarget();
		Element anchor = getAnchor(targetElement);

		String baseURI = anchor.getBaseURI();
		String href = anchor.getAttribute("href");
		return createURLPossiblyNull(baseURI, href);
	}

	private static Element getAnchor(Element targetElement) {
		Optional<Element> anchor = getAnchorAncestor(targetElement);
		if (anchor.isPresent())
			return anchor.get();
		else
			throw new IllegalArgumentException(
					"Neither the event's target element nor one of its parent nodes is an anchor.");
	}

	private static Optional<Element> getAnchorAncestor(Node domNode) {
		// if the node is null, there was no anchor, so return empty
		if (domNode == null)
			return Optional.empty();

		// if the node is no element, recurse to its parent
		boolean nodeIsNoElement = !(domNode instanceof Element);
		if (nodeIsNoElement)
			return getAnchorAncestor(domNode.getParentNode());

		// if the node is an element, check whether it is an anchor
		Element element = (Element) domNode;
		boolean isAnchor = element.getTagName().equalsIgnoreCase("a");
		if (isAnchor)
			return Optional.of(element);

		// if the element is no anchor, recurse to its parent
		return getAnchorAncestor(element.getParentNode());
	}

	/**
	 * Creates a URL from the specified base URI and href of the link which caused the event.
	 *
	 * @param baseURI
	 *            the base URI of the anchor {@link Element} which caused the event
	 * @param href
	 *            the href attribute value of the {@link Element} which caused the event
	 * @return a URL if one could be created; otherwise null
	 */
	private static URL createURLPossiblyNull(String baseURI, String href) {
		// create URL context from the document's base URI
		URL context = null;
		try {
			if (baseURI != null)
				context = new URL(baseURI);
		} catch (MalformedURLException e) {
			// if LibFX supports logging, this could be logged:
			//     "Could not create a URL context from the base URI \"" + baseURI + "\".", e
			// until then return null, which is a legal value for a URL in an HyperlinkEvent
		}

		// create URL from context and href
		try {
			return new URL(context, href);
		} catch (MalformedURLException e) {
			// if LibFX supports logging, this could be logged:
			//     "Could not create a URL from href \"" + href + "\" and context \"" + context + "\"."
			// until then return null, which is a legal value for a URL in an HyperlinkEvent
		}

		return null;
	}

	// #end TRANSFORM

}
