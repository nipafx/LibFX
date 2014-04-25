package org.codefx.libfx.notification.decoration;

import java.util.function.Consumer;

import org.codefx.libfx.notification.SendingNotification;

/**
 * Abstract base class for all decorators. By default all method calls are delegated to the decorated
 * {@link SendingNotification}. Decorators which want to change behavior can overwrite the relevant methods.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
class AbstractSendingNotificationDecorator<M> implements SendingNotification<M> {

	// #region PROPERTIES

	/**
	 * The decorated notification.
	 */
	private final SendingNotification<M> decorated;

	//#end PROPERTIES

	// #region CONSTRUCTOR

	/**
	 * Creates a new abstract decorator for the specified decorated notification.
	 *
	 * @param decorated
	 *            the notification decorated by this decorator
	 */
	protected AbstractSendingNotificationDecorator(SendingNotification<M> decorated) {
		this.decorated = decorated;
	}

	//#end CONSTRUCTOR

	// #region IMPLEMENTATION OF 'SendingNotification'

	@Override
	public void subscribe(Consumer<? super M> messageConsumer) {
		decorated.subscribe(messageConsumer);
	}

	@Override
	public void unsubscribe(Consumer<? super M> messageConsumer) {
		decorated.unsubscribe(messageConsumer);
	}

	@Override
	public void send(M message) {
		decorated.send(message);
	}

	//#end IMPLEMENTATION OF 'SendingNotification'

}
