package org.codefx.libfx.notification;

import java.util.function.Consumer;

/**
 * A notification sends a message when an object changes its state. Messages are sent to all subscribed message
 * consumers, which can subscribed or unsubscribed by other objects.
 * <p>
 * The consumers are managed with a set-like logic: Subscribing the same consumer (in the sense of
 * {@link Object#equals(Object) equals}) more than once has no effect after the first subscription. A consumer which was
 * subscribed several times and is then unsubscribed once will receive no more messages. Repeated unsubscription is a
 * no-op.
 * <p>
 * In the nomenclature of the observer pattern: The {@code Notification} can be used by the subject to collect observers
 * interested in a certain state change and notify them when it occurs.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
public interface Notification<M> {

	/**
	 * Subscribes the specified message consumer to this notification, which means it from now on receives messages.
	 *
	 * @param messageConsumer
	 *            the {@link Consumer} which from now on receives messages
	 */
	void subscribe(Consumer<? super M> messageConsumer);

	/**
	 * Unsubscribes the specified message consumer from this notification, which means it will no longer receive
	 * messages.
	 *
	 * @param messageConsumer
	 *            the {@link Consumer} which from now on receives no more messages
	 */
	void unsubscribe(Consumer<? super M> messageConsumer);

}
