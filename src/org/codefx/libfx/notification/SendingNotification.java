package org.codefx.libfx.notification;

/**
 * A {@link Notification} which provides a method to send messages.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
public interface SendingNotification<M> extends Notification<M> {

	/**
	 * Sends the specified message to all currently subscribed consumers.
	 *
	 * @param message
	 *            the message to send
	 */
	void send(M message);

}
