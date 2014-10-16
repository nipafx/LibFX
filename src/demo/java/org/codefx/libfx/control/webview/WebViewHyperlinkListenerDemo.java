package org.codefx.libfx.control.webview;

import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;

/**
 * Demonstrates how to use the {@link WebViewHyperlinkListener}.
 */
public class WebViewHyperlinkListenerDemo extends Application {

	/**
	 * Runs this demo.
	 *
	 * @param args
	 *            command line arguments (will not be used)
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		WebView webView = new WebView();
		webView.getEngine().getLoadWorker().stateProperty().addListener((obs, o, n) -> System.out.println(n));
		webView.getEngine().load("file:D://Downloads//test.html");
		addListeners(webView);
//		addListenersDelayed(webView, 1000);

		Scene scene = new Scene(webView);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void addListenersDelayed(WebView webView, long delayInMs) {
		Runnable doAdd = () -> addListeners(webView);
		Runnable waitAndAdd = () -> {
			try {
				Thread.sleep(delayInMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Platform.runLater(doAdd);
		};

		Executors.newCachedThreadPool().execute(waitAndAdd);
	}

	private void addListeners(WebView webView) {
		System.out.println("Adding Listeners...");

		WebViews.addHyperlinkListener(webView, event -> {
			System.out.println("Listener #1: " + WebViews.hyperlinkEventToString(event));
			return false;
		});
		WebViews.addHyperlinkListener(webView, event -> {
			System.out.println("Listener #2: " + WebViews.hyperlinkEventToString(event));
			return false;
		});
	}
}
