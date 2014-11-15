package org.codefx.libfx.control.webview;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.codefx.libfx.listener.handle.ListenerHandle;

/**
 * A {@link ListenerHandle} for a {@link WebViewHyperlinkListener}.
 *
 * @see ListenerHandle
 * @see WebViewHyperlinkListener
 */
public interface WebViewHyperlinkListenerHandle extends ListenerHandle {

	/**
	 * Attaches/adds the {@link WebViewHyperlinkListener} to the {@link WebView}.
	 * <p>
	 * This method can be called from any thread and regardless of the state of the {@code WebView}'s
	 * {@link WebEngine#getLoadWorker() loadWorker}. If it is not called on the FX Application Thread, the listener will
	 * be added at some unspecified time in the future. If the {@code loadWorker} is currently loading, the listener is
	 * attached as soon as it is done.
	 *
	 * @see ListenerHandle#attach()
	 */
	@Override
	void attach();

	/**
	 * Detaches/removes the {@link WebViewHyperlinkListener} from the {@link WebView}.
	 * <p>
	 * This method can be called from any thread and regardless of the state of the {@code WebView}'s
	 * {@link WebEngine#getLoadWorker() loadWorker}.
	 *
	 * @see ListenerHandle#detach()
	 */
	@Override
	void detach();

}
