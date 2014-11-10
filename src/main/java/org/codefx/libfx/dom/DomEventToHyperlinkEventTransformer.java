package org.codefx.libfx.dom;

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
 * <p>
 * In that sense it acts like an {@link EventTransformer} but because the {@link #domEvent} and its {@link #source} have
 * to be provided during construction it can not actually implement that interface.
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

	// #region TRANSFORM

	/**
	 * Indicates whether the DOM event specified during construction can be transformed to a {@link HyperlinkEvent}.
	 *
	 * @return true if the event's {@link Event#getType() type} has an equivalent {@link EventType EventType}
	 */
	public boolean canTransform() {
		Optional<EventType> eventType = getEventTypeFrom(domEvent);
		return eventType.isPresent();
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
	 * Transforms the event specified during construction to a hyperlink event.
	 *
	 * @return a {@link HyperlinkEvent}
	 * @throws IllegalArgumentException
	 *             if the specified event can not be transformed to a hyperlink event; this is the case if
	 *             {@link #canTransform()} returns false
	 */
	public HyperlinkEvent transform() throws IllegalArgumentException {
		EventType type = getEventTypeForDomEvent();
		Optional<URL> url = getURL();
		String linkDescription = getTextContent();

		return new HyperlinkEvent(source, type, url.orElse(null), linkDescription);
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
		return eventType.orElseThrow(
				() -> new IllegalArgumentException(
						"The DOM event '" + domEvent + "' of type '" + domEvent.getType()
								+ "' can not be transformed to a hyperlink event."));
	}

	/**
	 * Returns the {@link #domEvent}'s target's text content.
	 *
	 * @return the description
	 */
	private String getTextContent() {
		Element targetElement = (Element) domEvent.getTarget();
		return targetElement.getTextContent();
	}

	/**
	 * Returns the URL the interacted hyperlink points to.
	 *
	 * @return the {@link URL} if it could be created
	 */
	private Optional<URL> getURL() {
		Element targetElement = (Element) domEvent.getTarget();
		Element anchor = getAnchor(targetElement);

		Optional<String> baseURI = Optional.ofNullable(anchor.getBaseURI());
		String href = anchor.getAttribute("href");
		return createURL(baseURI, href);
	}

	/**
	 * Returns the same element if it is an anchor (in the sense of HTML, i.e. has the a-tag). If it is not the closest
	 * parent which is an anchor is returned. If no such parent exists, an {@link IllegalArgumentException} is thrown.
	 *
	 * @param domElement
	 *            the {@link Element} on which the search for an anchor element starts
	 * @return an {@link Element} which is an anchor
	 * @throws IllegalArgumentException
	 *             if neither the specified element nor one of its parents is an anchor
	 */
	private static Element getAnchor(Element domElement) throws IllegalArgumentException {
		Optional<Element> anchor = getAnchorAncestor(Optional.of(domElement));
		return anchor.orElseThrow(() -> new IllegalArgumentException(
				"Neither the event's target element nor one of its parent nodes is an anchor."));
	}

	/**
	 * Searches for an a-tag starting on the specified and recursing to its ancestors.
	 *
	 * @param domNode
	 *            the node which is checked for the a-tag
	 * @return an {@link Optional} containing an anchor if one was found; otherwise an empty {@code Optional}
	 */
	private static Optional<Element> getAnchorAncestor(Optional<Node> domNode) {
		// if there is no node, there was no anchor, so return empty
		if (!domNode.isPresent())
			return Optional.empty();

		Node node = domNode.get();

		// if the node is no element, recurse to its parent
		boolean nodeIsNoElement = !(node instanceof Element);
		if (nodeIsNoElement)
			return getAnchorAncestor(Optional.ofNullable(node.getParentNode()));

		// if the node is an element, check whether it is an anchor
		Element element = (Element) node;
		boolean isAnchor = element.getTagName().equalsIgnoreCase("a");
		if (isAnchor)
			return Optional.of(element);

		// if the element is no anchor, recurse to its parent
		return getAnchorAncestor(Optional.ofNullable(element.getParentNode()));
	}

	/**
	 * Creates a URL from the specified base URI and href of the link which caused the event.
	 *
	 * @param baseURI
	 *            the base URI of the anchor {@link Element} which caused the event
	 * @param href
	 *            the href attribute value of the {@link Element} which caused the event
	 * @return a URL if one could be created
	 */
	private static Optional<URL> createURL(Optional<String> baseURI, String href) {
		// create URL context from the document's base URI
		URL context = null;
		try {
			if (baseURI.isPresent())
				context = new URL(baseURI.get());
		} catch (MalformedURLException e) {
			// if LibFX supports logging, this could be logged:
			//     "Could not create a URL context from the base URI \"" + baseURI + "\".", e
			// until then return empty
		}

		// create URL from context and href
		try {
			URL url = new URL(context, href);
			return Optional.of(url);
		} catch (MalformedURLException e) {
			// if LibFX supports logging, this could be logged:
			//     "Could not create a URL from href \"" + href + "\" and context \"" + context + "\"."
			// until then return empty
		}

		return Optional.empty();
	}

	// #end TRANSFORM

}
