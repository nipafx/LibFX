package org.codefx.libfx.control.webview;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.codefx.libfx.dom.DomEventType;
import org.codefx.libfx.dom.EventTransformer;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * A handle on the created listener which allows to {@link #attach()} and TODO detach it.
 */
class WebViewHyperlinkListenerHandle {

	// Inspired by :
	//  - http://stackoverflow.com/q/17555937 -
	//  - http://blogs.kiyut.com/tonny/2013/07/30/javafx-webview-addhyperlinklistener/

	/*
	 * Many type names do not allow to easily recognize whether they come from the DOM- or the JavaFX-packages. To make
	 * it easier, all instances of org.w3c.dom classes carry a 'dom'-prefix.
	 */

	// #region ATTRIBUTES

	/**
	 * The {@link WebView} to which the {@link #domEventListener} will be attached.
	 */
	private final WebView webView;

	/**
	 * The {@link WebViewHyperlinkListener} which will be called by {@link #domEventListener} when an event occurs.
	 */
	private final WebViewHyperlinkListener eventListener;

	/**
	 * The filter for events by their {@link EventType}. If the filter is empty, all events will be processed. Otherwise
	 * only events of the present type will be processed.
	 */
	private final Optional<EventType> eventTypeFilter;

	/**
	 * The DOM-{@link EventListener} which will be attached to the {@link #webView}.
	 */
	private final EventListener domEventListener;

	/**
	 * Transforms DOM {@link Event}s to {@link HyperlinkEvent}s.
	 */
	private final EventTransformer eventTransformer;

	// #end ATTRIBUTES

	/**
	 * Creates a new listener handle for the specified arguments.
	 *
	 * @param webView
	 *            the {@link WebView} to which the {@code eventListener} will be attached
	 * @param eventListener
	 *            the {@link WebViewHyperlinkListener} which will be attached to the {@code webView}
	 * @param eventTypeFilter
	 *            the filter for events by their {@link EventType}
	 * @param eventTransformer
	 *            the transformer for DOM {@link Event}s
	 */
	public WebViewHyperlinkListenerHandle(
			WebView webView, WebViewHyperlinkListener eventListener, Optional<EventType> eventTypeFilter,
			EventTransformer eventTransformer) {

		this.webView = webView;
		this.eventListener = eventListener;
		this.eventTypeFilter = eventTypeFilter;
		this.eventTransformer = eventTransformer;

		domEventListener = this::callHyperlinkListenerWithEvent;
	}

	// #region ATTACH

	/**
	 * Attaches the {@link WebViewHyperlinkListener} to the {@link WebView}.
	 */
	public void attach() {
		if (Platform.isFxApplicationThread())
			attachInApplicationThreadEachTimeLoadSucceeds();
		else
			Platform.runLater(() -> attachInApplicationThreadEachTimeLoadSucceeds());
	}

	/**
	 * Attaches the {@link #domEventListener} to the {@link #webView} every time the {@code webView} successfully loaded
	 * a page.
	 * <p>
	 * Must be called in JavaFX application thread.
	 */
	private void attachInApplicationThreadEachTimeLoadSucceeds() {
		ObservableValue<State> webWorkerState = webView.getEngine().getLoadWorker().stateProperty();
		webWorkerState.addListener((observable, oldState, newState) -> {
			if (newState != State.SUCCEEDED)
				return;
			attachInApplicationThread();
		});
	}

	/**
	 * Attaches the {@link #domEventListener} to the {@link #webView}.
	 * <p>
	 * Must be called in JavaFX application thread.
	 */
	private void attachInApplicationThread() {
		NodeList domNodeList = webView.getEngine().getDocument().getElementsByTagName("a");
		for (int i = 0; i < domNodeList.getLength(); i++) {
			EventTarget domTarget = (EventTarget) domNodeList.item(i);
			for (DomEventType domEventType : DomEventType.values())
				addDomListenerToTarget(domTarget, domEventType);
		}
	}

	/**
	 * Adds the {@link #domEventListener} to the specified DOM event target if the specified DOM event type passes the
	 * {@link #eventTypeFilter}.
	 *
	 * @param domTarget
	 *            the {@link EventTarget} to which the listener might be added
	 * @param domEventType
	 *            the {@link DomEventType} for which a listener might be added
	 */
	private void addDomListenerToTarget(EventTarget domTarget, DomEventType domEventType) {
		boolean addListenerForEventType = mustAddListenerForEventType(domEventType);
		if (addListenerForEventType)
			domTarget.addEventListener(domEventType.getDomName(), domEventListener, false);
	}

	/**
	 * Indicates whether a listener must be added for the specified DOM event type and the {@link #eventTypeFilter}.
	 *
	 * @param domEventType
	 *            the {@link DomEventType} for which a listener might be added
	 * @return true if the DOM event type has a representation as an hyperlink event type and is not filtered out; false
	 *         otherwise
	 */
	private boolean mustAddListenerForEventType(DomEventType domEventType) {
		boolean domEventTypeHasRepresentation = domEventType.toHyperlinkEventType().isPresent();
		if (!domEventTypeHasRepresentation)
			return false;

		boolean filterOn = eventTypeFilter.isPresent();
		if (!filterOn)
			return true;

		return domEventType.toHyperlinkEventType().get() == eventTypeFilter.get();
	}

	// #end ATTACH

	// #region PROCESS EVENT

	/**
	 * Transforms the specified {@code domEvent} into a {@link HyperlinkEvent} and calls the {@link #eventListener} with
	 * it.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 */
	private void callHyperlinkListenerWithEvent(Event domEvent) {
		boolean canNotTransformEvent = !eventTransformer.canTransformToHyperlinkEvent(domEvent);
		if (canNotTransformEvent)
			return;

		HyperlinkEvent event = eventTransformer.transformToHyperlinkEvent(domEvent, webView);
		boolean cancel = eventListener.hyperlinkUpdate(event);
		cancel(domEvent, cancel);
	}

	/**
	 * Cancels the specified event if it is cancelable and cancellation is indicated by the specified flag.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event} to be canceled
	 * @param cancel
	 *            indicates whether the event should be canceled
	 */
	private static void cancel(Event domEvent, boolean cancel) {
		if (domEvent.getCancelable() && cancel)
			domEvent.preventDefault();
	}

	// #end PROCESS EVENT

}
