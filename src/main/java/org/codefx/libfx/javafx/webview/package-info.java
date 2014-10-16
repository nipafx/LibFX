/**
 * This package provides functionality around JavaFX' {@link javafx.scene.web.WebView WebView}. All of it can be
 * accessed via the utility class {@link org.codefx.libfx.javafx.webview.WebViews WebViews}
 * <p>
 * <h2>Hyperlink Listener</h2> The {@code WebView} provides no pleasant way to add an equivalent of Swing's
 * {@link javax.swing.event.HyperlinkListener HyperlinkListener}.
 * <p>
 * This can now be done by implementing a {@link org.codefx.libfx.javafx.webview.WebViewHyperlinkListener
 * WebViewHyperlinkListener}, which is very similar to Swing's {@code HyperlinkListener} and also processes
 * {@link javax.swing.event.HyperlinkEvent HyperlinkEvents}. Together with a {@code WebView} and optionally an event
 * filter it can be handed to {@code WebViews}'
 * {@link org.codefx.libfx.javafx.webview.WebViews#addHyperlinkListener(javafx.scene.web.WebView, WebViewHyperlinkListener)
 * addHyperlinkListener} method.
 */
package org.codefx.libfx.javafx.webview;