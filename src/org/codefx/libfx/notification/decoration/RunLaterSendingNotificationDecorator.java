package org.codefx.libfx.notification.decoration;

import javafx.application.Platform;

import org.codefx.libfx.notification.SendingNotification;

/**
 * A decoration of a {@link SendingNotification} which delegates sending the message to
 * {@link Platform#runLater(Runnable) Platform.runLater}.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
public class RunLaterSendingNotificationDecorator<M> extends AbstractSendingNotificationDecorator<M> {

	/**
	 * Creates a new run later decorator.
	 *
	 * @param decorated
	 *            the notification decorated by this decorator
	 */
	public RunLaterSendingNotificationDecorator(SendingNotification<M> decorated) {
		super(decorated);
	}

	@Override
	public void send(M message) {
		Platform.runLater(() -> super.send(message));
	}

}
