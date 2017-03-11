package org.codefx.libfx.control.webview;

import java.util.function.Function;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * A listener to {@link HyperlinkEvent}s which are dispatched by a {@link WebView}.
 * <p>
 * Very similar do the {@link HyperlinkListener} but it can cancel the further processing of events by the
 * {@link WebEngine}. This does not extent to other listeners of this type to the same {@code WebView} - these are
 * always called.
 */
@FunctionalInterface
public interface WebViewHyperlinkListener {

	/**
	 * Adapts the specified (Swing) hyperlink listener to a web view hyperlink listener.
	 *
	 * @param listener
	 *            the {@link HyperlinkListener} to adapt
	 * @param cancel
	 *            a {@link Function} which checks for the specified event whether it should be canceled
	 * @return a {@link WebViewHyperlinkListener}
	 */
	static WebViewHyperlinkListener fromHyperlinkListener(HyperlinkListener listener,
			Function<HyperlinkEvent, Boolean> cancel) {
		return event -> {
			listener.hyperlinkUpdate(event);
			return cancel.apply(event);
		};
	}

	/**
	 * Adapts the specified (Swing) hyperlink listener to a web view hyperlink listener.
	 *
	 * @param listener
	 *            the {@link HyperlinkListener} to adapt
	 * @param cancel
	 *            whether the created listener should cancel every event
	 * @return a {@link WebViewHyperlinkListener}
	 */
	static WebViewHyperlinkListener fromHyperlinkListener(HyperlinkListener listener, boolean cancel) {
		return event -> {
			listener.hyperlinkUpdate(event);
			return cancel;
		};
	}

	/**
	 * Called when a hypertext link is updated.
	 *
	 * @param event
	 *            the event responsible for the update
	 * @return whether the event should be canceled; if more than one listener is called on a single event, their return
	 *         values are "ored".
	 */
	boolean hyperlinkUpdate(HyperlinkEvent event);

}
