package org.codefx.libfx.dom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.apache.xerces.dom.events.EventImpl;
import org.cyberneko.html.parsers.DOMParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.InputSource;

/**
 * Abstract superclass to all test classes which test implementations of {@link EventTransformer}.
 */
public abstract class AbstractEventTransformerTest {

	// #region ATTRIBUTES & INITIALIZATION

	/**
	 * The URL used for all links.
	 */
	private static final String LINK_URL = "http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list";

	/**
	 * The ID of the {@link #simpleLink}. Used to retrieve the corresponding element from the parsed HTML.
	 */
	private static final String SIMPLE_LINK_ID = "simple_link";

	/**
	 * The text displayed for the {@link #simpleLink}.
	 */
	private static final String SIMPLE_LINK_TEXT = "Link!";

	/**
	 * A simple HTML string.
	 */
	private static final String SIMPLE_HTML_STRING = ""
			+ "<a"
			+ "    id=\"" + SIMPLE_LINK_ID + "\""
			+ "    href=\"" + LINK_URL + "\">"
			+ SIMPLE_LINK_TEXT
			+ "</a>";

	/**
	 * A simple link which is used to generate DOM events.
	 */
	private EventTarget simpleLink;

	/**
	 * Parses the HTML strings and extracts the id'd DOM event targets.
	 *
	 * @throws Exception
	 *             if parsing fails
	 */
	@Before
	public void setUp() throws Exception {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new StringReader(SIMPLE_HTML_STRING)));
		Document htmlDocument = parser.getDocument();
		simpleLink = (EventTarget) htmlDocument.getElementById(SIMPLE_LINK_ID);
	}

	// #end ATTRIBUTES & INITIALIZATION

	// #region TESTS

	// #end ATTRIBUTES & INITIALIZATION

	// #region TESTS

	/**
	 * Tests whether all DOM events[1] which have a corresponding {@link EventType HyperlinkEventType} are correctly
	 * reported to be transformable.
	 * <p>
	 * [1] http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list
	 */
	@Test
	public void testCanTransformToHyperlinkEvent() {
		// all transformable DOM events
		String[] transformableEventNames = new String[] {
				DomEventType.CLICK.getDomName(),
				DomEventType.MOUSE_ENTER.getDomName(),
				DomEventType.MOUSE_LEAVE.getDomName() };

		for (String domEventName : transformableEventNames) {
			Event domEvent = createDispatchAndCatchEvent(simpleLink, domEventName);
			boolean canTransform = canTransformToHyperlinkEvent(domEvent);

			assertTrue("Should be able to transform '" + domEventName + "'.", canTransform);
		}
	}

	/**
	 * Tests whether all DOM events[1] which have no corresponding {@link EventType HyperlinkEventType} are correctly
	 * reported to be not transformable.
	 * <p>
	 * [1] http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list
	 */
	@Test
	public void testCanNotTransform() {
		// all existing DOM events[1] minus the transformable ones
		String[] notTransformableEventNames = new String[] { "abort", "beforeinput", "blur", "compositionstart",
				"compositionupdate", "compositionend", "dblclick", "error", "focus", "focusin", "focusout", "input",
				"keydown", "keyup", "load", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "resize",
				"scroll", "select", "unload", "wheel" };

		for (String domEventName : notTransformableEventNames) {
			Event domEvent = createDispatchAndCatchEvent(simpleLink, domEventName);
			boolean canTransform = canTransformToHyperlinkEvent(domEvent);

			assertFalse("Should not be able to transform '" + domEventName + "'.", canTransform);
		}
	}

	/**
	 * Tests whether transformed events have the correct event type.
	 */
	@Test
	public void testEventTypes() {
		for (DomEventType domEventType : DomEventType.values()) {
			if (!domEventType.toHyperlinkEventType().isPresent())
				continue;

			Event domEvent = createDispatchAndCatchEvent(simpleLink, domEventType.getDomName());
			HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, new Object());

			assertEquals(domEventType.toHyperlinkEventType().get(), transformedEvent.getEventType());
		}
	}

	/**
	 * Tests whether the transformed event's {@link HyperlinkEvent#getSource() source} is correctly set.
	 */
	@Test
	public void testSource() {
		Event domEvent = createDispatchAndCatchEvent(simpleLink, DomEventType.CLICK.getDomName());
		Object source = "the source";
		HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, source);

		assertSame(source, transformedEvent.getSource());
	}

	/**
	 * Tests whether the transformed event's {@link HyperlinkEvent#getURL() URL} is correctly set.
	 */
	@Test
	public void testUrl() {
		Event domEvent = createDispatchAndCatchEvent(simpleLink, DomEventType.CLICK.getDomName());
		HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, new Object());

		assertEquals(LINK_URL, transformedEvent.getURL().toExternalForm());
	}

	/**
	 * Tests whether the transformed event's {@link HyperlinkEvent#getDescription description} is correctly set.
	 */
	@Test
	public void testDescription() {
		Event domEvent = createDispatchAndCatchEvent(simpleLink, DomEventType.CLICK.getDomName());
		HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, new Object());

		assertEquals(SIMPLE_LINK_TEXT, transformedEvent.getDescription());
	}

	/**
	 * Tests whether the transformed event's {@link HyperlinkEvent#getInputEvent() inputEvent} is null as per contract.
	 */
	@Test
	public void testInputEvent() {
		Event domEvent = createDispatchAndCatchEvent(simpleLink, DomEventType.CLICK.getDomName());
		HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, new Object());

		assertNull(transformedEvent.getInputEvent());
	}

	/**
	 * Tests whether the transformed event's {@link HyperlinkEvent#getSourceElement() sourceElement} is null as per
	 * contract.
	 */
	@Test
	public void testSourceElement() {
		Event domEvent = createDispatchAndCatchEvent(simpleLink, DomEventType.CLICK.getDomName());
		HyperlinkEvent transformedEvent = transformToHyperlinkEvent(domEvent, new Object());

		assertNull(transformedEvent.getSourceElement());
	}

	// #end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Implemented by subclasses to check whether the specified event can be transformed.
	 *
	 * @param domEvent
	 *            the {@link Event} to check
	 * @return true if {@link #transformToHyperlinkEvent(Event, Object)} will succeed
	 */
	protected abstract boolean canTransformToHyperlinkEvent(Event domEvent);

	/**
	 * Implemented by subclasses to transform the specified DOM event to a hyperlink event.
	 *
	 * @param domEvent
	 *            the {@link Event} to be transformed
	 * @param object
	 *            the new hyperlink event's source
	 * @return a {@link HyperlinkEvent}
	 */
	protected abstract HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object object);

	// #end ABSTRACT METHODS

	// #region HELPER METHODS

	/**
	 * Creates a DOM event and dispatches it with the specified target. A listener on the same target catches any event
	 * and returns it.
	 *
	 * @param target
	 *            the {@link EventTarget} which will {@link EventTarget#dispatchEvent(Event) dispatch} the created event
	 * @param eventType
	 *            the type of the event as specified here: http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list
	 * @return the DOM-{@link Event} caught from the target
	 */
	private static Event createDispatchAndCatchEvent(EventTarget target, String eventType) {
		return createDispatchAndCatchEvent(target, eventType, true, true);
	}

	/**
	 * Creates a DOM event and dispatches it with the specified target. A listener on the same target catches any event
	 * and returns it.
	 *
	 * @param target
	 *            the {@link EventTarget} which will {@link EventTarget#dispatchEvent(Event) dispatch} the created event
	 * @param eventType
	 *            the type of the event as specified here: http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list
	 * @param canBubbleArg
	 *            indicates whether the event can bubble
	 * @param cancelableArg
	 *            indicates whether the event can be canceled
	 * @return the DOM-{@link Event} caught from the target
	 */
	private static Event createDispatchAndCatchEvent(
			EventTarget target, String eventType, boolean canBubbleArg, boolean cancelableArg) {

		Property<Event> caughtEvent = new SimpleObjectProperty<>();
		target.addEventListener(eventType, caughtEvent::setValue, false);

		Event createdEvent = createEvent(eventType, canBubbleArg, cancelableArg);
		target.dispatchEvent(createdEvent);

		return caughtEvent.getValue();
	}

	/**
	 * Creates and initializes a DOM event from the specified arguments.
	 *
	 * @param eventType
	 *            the type of the event as specified here: http://www.w3.org/TR/DOM-Level-3-Events/#event-types-list
	 * @param canBubbleArg
	 *            indicates whether the event can bubble
	 * @param cancelableArg
	 *            indicates whether the event can be canceled
	 * @return the created and initialized DOM-{@link Event}
	 */
	private static Event createEvent(String eventType, boolean canBubbleArg, boolean cancelableArg) {
		Event event = new EventImpl();
		event.initEvent(eventType, canBubbleArg, cancelableArg);
		return event;
	}

	// #end HELPER METHODS

}
