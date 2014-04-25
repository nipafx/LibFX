package org.codefx.libfx.notification.decoration;

import java.util.function.Consumer;

import org.codefx.libfx.notification.SendingNotification;

/**
 * A decoration of a {@link SendingNotification} which synchronizes all method calls.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
public class SynchronizedSendingNotificationDecorator<M> extends AbstractSendingNotificationDecorator<M> {

	/**
	 * Creates a new synchronizing decorator.
	 *
	 * @param decorated
	 *            the notification decorated by this decorator
	 */
	public SynchronizedSendingNotificationDecorator(SendingNotification<M> decorated) {
		super(decorated);
	}

	// #region SYNCHRONIZATION

	@Override
	public synchronized void subscribe(Consumer<? super M> messageConsumer) {
		super.subscribe(messageConsumer);
	}

	@Override
	public synchronized void unsubscribe(Consumer<? super M> messageConsumer) {
		super.unsubscribe(messageConsumer);
	}

	@Override
	public synchronized void send(M message) {
		super.send(message);
	}

	//#end SYNCHRONIZATION
}
