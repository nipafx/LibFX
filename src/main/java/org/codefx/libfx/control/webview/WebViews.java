package org.codefx.libfx.control.webview;

import java.util.Objects;
import java.util.Optional;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.codefx.libfx.dom.DefaultEventTransformer;
import org.codefx.libfx.dom.DomEventType;
import org.codefx.libfx.dom.StaticEventTransformer;
import org.w3c.dom.events.Event;

/**
 * Usability methods for the {@link WebView}.
 */
public final class WebViews {

	/**
	 * Private constructor for utility class.
	 */
	private WebViews() {
		// nothing to do
	}

	// #region HYPERLINK LISTENERS

	/**
	 * Adds the specified listener to the specified WebView.
	 * <p>
	 * The listener will be called on any event on an hyperlink (i.e. any element with tag name "a") which can be
	 * represented as a {@link HyperlinkEvent}. This is the case on {@link DomEventType#MOUSE_ENTER MOUSE_ENTER},
	 * {@link DomEventType#MOUSE_LEAVE MOUSE_LEAVE} and {@link DomEventType#CLICK CLICK}.
	 * <p>
	 * This method can be called from any thread and regardless of the state of the {@code WebView}'s
	 * {@link WebEngine#getLoadWorker() loadWorker}. If it is not called on the FX Application Thread, the listener will
	 * be added at some unspecified time in the future.
	 *
	 * @param webView
	 *            the {@link WebView} to which the listener will be added
	 * @param listener
	 *            the {@link WebViewHyperlinkListener} to add to the web view
	 * @return a handle on the created listener which allows to attach and detach it
	 */
	public static WebViewHyperlinkListenerHandle addHyperlinkListener(
			WebView webView, WebViewHyperlinkListener listener) {

		return addHyperlinkListener(webView, listener, Optional.empty());
	}

	/**
	 * Adds the specified listener to the specified WebView.
	 * <p>
	 * The listener will be called on any event on an hyperlink (i.e. any element with tag name "a") which can be
	 * represented as a {@link HyperlinkEvent} with the specified event type. See
	 * {@link DomEventType#toHyperlinkEventType()} for the transformation of event types.
	 * <p>
	 * This method can be called from any thread and regardless of the state of the {@code WebView}'s
	 * {@link WebEngine#getLoadWorker() loadWorker}. If it is not called on the FX Application Thread, the listener will
	 * be added at some unspecified time in the future.
	 *
	 * @param webView
	 *            the {@link WebView} to which the listener will be added
	 * @param listener
	 *            the {@link WebViewHyperlinkListener} to add to the web view
	 * @param eventType
	 *            the {@link EventType} of all events passed to the listener
	 * @return a handle on the created listener which allows to attach and detach it
	 */
	public static WebViewHyperlinkListenerHandle addHyperlinkListener(
			WebView webView, WebViewHyperlinkListener listener, EventType eventType) {

		Objects.requireNonNull(eventType, "The argument 'eventType' must not be null.");
		return addHyperlinkListener(webView, listener, Optional.empty());
	}

	/**
	 * Adds the specified listener to the specified WebView.
	 * <p>
	 * If necessary this method switches to the FX application thread.
	 *
	 * @param webView
	 *            the {@link WebView} to which the listener will be added
	 * @param listener
	 *            the {@link WebViewHyperlinkListener} to add to the web view
	 * @param eventTypeFilter
	 *            the {@link EventType} of all events passed to the listener; {@link Optional#empty()} means all events
	 *            are passed
	 * @return a handle on the created listener which allows to attach and detach it
	 */
	private static WebViewHyperlinkListenerHandle addHyperlinkListener(
			WebView webView, WebViewHyperlinkListener listener, Optional<EventType> eventTypeFilter) {

		Objects.requireNonNull(webView, "The argument 'webView' must not be null.");
		Objects.requireNonNull(listener, "The argument 'listener' must not be null.");
		Objects.requireNonNull(eventTypeFilter, "The argument 'eventTypeFilter' must not be null.");

		WebViewHyperlinkListenerHandle handle =
				new WebViewHyperlinkListenerHandle(webView, listener, eventTypeFilter, new DefaultEventTransformer());
		handle.attach();
		return handle;
	}

	// #end HYPERLINK LISTENERS

	// #region EVENTS

	/**
	 * Indicates whether the specified DOM event can be transformed to a {@link HyperlinkEvent}.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 * @return true if the event's {@link Event#getType() type} has an equivalent {@link EventType EventType}
	 */
	public static boolean canTransformToHyperlinkEvent(Event domEvent) {
		return StaticEventTransformer.canTransformToHyperlinkEvent(domEvent);
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
	 *             {@link #canTransformToHyperlinkEvent(Event)} returns false
	 */
	public static HyperlinkEvent transformToHyperlinkEvent(Event domEvent, Object source)
			throws IllegalArgumentException {

		return StaticEventTransformer.transformToHyperlinkEvent(domEvent, source);
	}

	/**
	 * Returns a string representation of the specified event.
	 *
	 * @param event
	 *            the {@link HyperlinkEvent} which will be converted to a string
	 * @return a string representation of the event
	 */
	public static String hyperlinkEventToString(HyperlinkEvent event) {
		Objects.requireNonNull(event, "The parameter 'event' must not be null.");

		return "HyperlinkEvent ["
				+ "type: "
				+ event.getEventType()
				+ "; "
				+ "URL (description): "
				+ event.getURL()
				+ " ("
				+ event.getDescription()
				+ "); "
				+ "source: "
				+ event.getSource()
				+ "; "
				+ "source element: "
				+ event.getSourceElement()
				+ "]";
	}

	// #end HYPERLINK EVENTS

	// #region DOM EVENTS

	/**
	 * Returns a string representation of the specified event.
	 *
	 * @param event
	 *            the DOM-{@link Event} which will be converted to a string
	 * @return a string representation of the event
	 */
	public static String domEventToString(Event event) {
		Objects.requireNonNull(event, "The parameter 'event' must not be null.");

		return "DOM-Event ["
				+ "target: "
				+ event.getTarget()
				+ "; "
				+ "type: "
				+ event.getType()
				+ "; "
				+ "time stamp: "
				+ event.getTimeStamp()
				+ "; "
				+ "bubbles: "
				+ event.getBubbles()
				+ "; "
				+ "cancelable: "
				+ event.getCancelable()
				+ "]";
	}

	// #end DOM EVENTS

}
