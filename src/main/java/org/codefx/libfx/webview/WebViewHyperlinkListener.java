package org.codefx.libfx.webview;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A listener to {@link HyperlinkEvent}s which are dispatched by a {@link WebView}.
 * <p>
 * Very similar do the {@link HyperlinkListener} but it can cancel the further processing of events by the
 * {@link WebEngine}. This does not extent to other listeners of this type to the same {@code WebView} - these are
 * always called.
 */
public interface WebViewHyperlinkListener {

	/**
	 * Called when a hypertext link is updated.
	 *
	 * @param event
	 *            the event responsible for the update
	 * @return true if the event is to be canceled
	 */
	public boolean hyperlinkUpdate(HyperlinkEvent event);

}
