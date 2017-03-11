package org.codefx.libfx.control.webview;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;

import org.codefx.libfx.concurrent.when.ExecuteAlwaysWhen;
import org.codefx.libfx.concurrent.when.ExecuteWhen;
import org.codefx.libfx.dom.DomEventConverter;
import org.codefx.libfx.dom.DomEventType;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * A default implementation of {@link WebViewHyperlinkListenerHandle} which acts on the {@link WebView} and
 * {@link WebViewHyperlinkListener} specified during construction.
 */
class DefaultWebViewHyperlinkListenerHandle implements WebViewHyperlinkListenerHandle {

	// Inspired by :
	//  - http://stackoverflow.com/q/17555937 -
	//  - http://blogs.kiyut.com/tonny/2013/07/30/javafx-webview-addhyperlinklistener/

	/*
	 * Many type names do not allow to easily recognize whether they come from the DOM- or the JavaFX-packages. To make
	 * it easier, all instances of org.w3c.dom classes carry a 'dom'-prefix.
	 */

	// #begin FIELDS

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
	 * Converts the observed DOM {@link Event}s to {@link HyperlinkEvent}s.
	 */
	private final DomEventConverter eventConverter;

	/**
	 * Executes {@link #attachListenerInApplicationThread()} each time the web view's load worker changes its state to
	 * {@link State#SUCCEEDED SUCCEEDED}.
	 * <p>
	 * The executer is only present while the listener is {@link #attached}.
	 */
	private Optional<ExecuteAlwaysWhen<State>> attachWhenLoadSucceeds;

	/**
	 * Indicates whether the listener is currently attached.
	 */
	private boolean attached;

	// #end FIELDS

	/**
	 * Creates a new listener handle for the specified arguments. The listener is not attached to the web view.
	 *
	 * @param webView
	 *            the {@link WebView} to which the {@code eventListener} will be attached
	 * @param eventListener
	 *            the {@link WebViewHyperlinkListener} which will be attached to the {@code webView}
	 * @param eventTypeFilter
	 *            the filter for events by their {@link EventType}
	 * @param eventConverter
	 *            the converter for DOM {@link Event}s
	 */
	public DefaultWebViewHyperlinkListenerHandle(
			WebView webView, WebViewHyperlinkListener eventListener, Optional<EventType> eventTypeFilter,
			DomEventConverter eventConverter) {

		this.webView = webView;
		this.eventListener = eventListener;
		this.eventTypeFilter = eventTypeFilter;
		this.eventConverter = eventConverter;

		domEventListener = this::callHyperlinkListenerWithEvent;
	}

	// #begin ATTACH

	@Override
	public void attach() {
		if (attached)
			return;

		attached = true;
		if (Platform.isFxApplicationThread())
			attachInApplicationThreadEachTimeLoadSucceeds();
		else
			Platform.runLater(this::attachInApplicationThreadEachTimeLoadSucceeds);
	}

	/**
	 * Attaches the {@link #domEventListener} to the {@link #webView} every time the {@code webView} successfully loaded
	 * a page.
	 * <p>
	 * Must be called in JavaFX application thread.
	 */
	private void attachInApplicationThreadEachTimeLoadSucceeds() {
		ObservableValue<State> webWorkerState = webView.getEngine().getLoadWorker().stateProperty();

		ExecuteAlwaysWhen<State> attachWhenLoadSucceeds = ExecuteWhen
				.on(webWorkerState)
				.when(state -> state == State.SUCCEEDED)
				.thenAlways(state -> attachListenerInApplicationThread());
		this.attachWhenLoadSucceeds = Optional.of(attachWhenLoadSucceeds);

		attachWhenLoadSucceeds.executeWhen();
	}

	/**
	 * Attaches the {@link #domEventListener} to the {@link #webView}.
	 * <p>
	 * Must be called in JavaFX application thread.
	 */
	private void attachListenerInApplicationThread() {
		BiConsumer<EventTarget, String> addListener =
				(eventTarget, eventType) -> eventTarget.addEventListener(eventType, domEventListener, false);
		onEachLinkForEachManagedEventType(addListener);
	}

	// #end ATTACH

	// #begin DETACH

	@Override
	public void detach() {
		if (!attached)
			return;

		attached = false;
		if (Platform.isFxApplicationThread())
			detachInApplicationThread();
		else
			Platform.runLater(this::detachInApplicationThread);
	}

	/**
	 * Detaches the {@link #domEventListener} from the {@link #webView} and cancels and resets
	 * {@link #attachWhenLoadSucceeds}.
	 * <p>
	 * Must be called in JavaFX application thread.
	 */
	private void detachInApplicationThread() {
		attachWhenLoadSucceeds.ifPresent(ExecuteAlwaysWhen::cancel);
		attachWhenLoadSucceeds = Optional.empty();

		// it suffices to remove the listener if the worker state is on SUCCEEDED;
		// because when the view is currently loading, the canceled 'attachWhen' will not re-add the listener
		State webWorkerState = webView.getEngine().getLoadWorker().getState();
		if (webWorkerState == State.SUCCEEDED) {
			BiConsumer<EventTarget, String> removeListener =
					(eventTarget, eventType) -> eventTarget.removeEventListener(eventType, domEventListener, false);
			onEachLinkForEachManagedEventType(removeListener);
		}
	}

	// #end DETACH

	// #begin COMMON MANAGEMENT METHODS

	/**
	 * Executes the specified function on each link in the {@link #webView}'s current document for each
	 * {@link DomEventType} for which {@link #manageListenerForEventType(DomEventType)} returns true.
	 * <p>
	 * Must be called in JavaFX application thread.
	 *
	 * @param manageListener
	 *            a {@link BiConsumer} which acts on a link and a DOM event type
	 */
	private void onEachLinkForEachManagedEventType(BiConsumer<EventTarget, String> manageListener) {
		NodeList domNodeList = webView.getEngine().getDocument().getElementsByTagName("a");
		for (int i = 0; i < domNodeList.getLength(); i++) {
			EventTarget domTarget = (EventTarget) domNodeList.item(i);
			onLinkForEachManagedEventType(domTarget, manageListener);
		}
	}

	/**
	 * Executes the specified function on the specified link for each {@link DomEventType} for which
	 * {@link #manageListenerForEventType(DomEventType)} returns true.
	 * <p>
	 * Must be called in JavaFX application thread.
	 *
	 * @param link
	 *            The {@link EventTarget} with which {@code manageListener} will be called
	 * @param manageListener
	 *            a {@link BiConsumer} which acts on a link and a DOM event type
	 */
	private void onLinkForEachManagedEventType(EventTarget link, BiConsumer<EventTarget, String> manageListener) {
		Consumer<DomEventType> manageListenerForType =
				domEventType -> manageListener.accept(link, domEventType.getDomName());
		Stream.of(DomEventType.values())
				.filter(this::manageListenerForEventType)
				.forEach(manageListenerForType);
	}

	/**
	 * Indicates whether a listener must be added for the specified DOM event type and the {@link #eventTypeFilter}.
	 *
	 * @param domEventType
	 *            the {@link DomEventType} for which a listener might be added
	 * @return true if the DOM event type has a representation as an hyperlink event type and is not filtered out; false
	 *         otherwise
	 */
	private boolean manageListenerForEventType(DomEventType domEventType) {
		boolean domEventTypeHasRepresentation = domEventType.toHyperlinkEventType().isPresent();
		if (!domEventTypeHasRepresentation)
			return false;

		boolean filterOn = eventTypeFilter.isPresent();
		if (!filterOn)
			return true;

		return domEventType.toHyperlinkEventType().get() == eventTypeFilter.get();
	}

	// #end COMMON MANAGEMENT METHODS

	// #begin PROCESS EVENT

	/**
	 * Converts the specified {@code domEvent} into a {@link HyperlinkEvent} and calls the {@link #eventListener} with
	 * it.
	 *
	 * @param domEvent
	 *            the DOM-{@link Event}
	 */
	private void callHyperlinkListenerWithEvent(Event domEvent) {
		boolean canNotConvertEvent = !eventConverter.canConvertToHyperlinkEvent(domEvent);
		if (canNotConvertEvent)
			return;

		HyperlinkEvent event = eventConverter.convertToHyperlinkEvent(domEvent, webView);
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
