package org.codefx.libfx.notification;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A simple implementation for {@link SendingNotification}.
 *
 * @param <M>
 *            the type of message sent by the notification
 */
public final class SimpleSendingNotification<M> implements SendingNotification<M> {

	/*
	 * The only non-trivial part of the Notification contract is the one dealing with repeated (un-)subscriptions of the
	 * same message consumer. It can simply be implemented by storing them in a set.
	 */

	// #region PROPERTIES

	/**
	 * The set of message consumers.
	 */
	private final Set<Consumer<? super M>> consumers;

	//#end PROPERTIES

	// #region CONSTRUCTOR

	/**
	 * Creates a new simple sending notification.
	 */
	public SimpleSendingNotification() {
		consumers = new HashSet<>();
	}

	//#end CONSTRUCTOR

	// #region IMPLEMENTATION OF 'SimpleSendingNotification'

	@Override
	public void subscribe(Consumer<? super M> messageConsumer) {
		consumers.add(messageConsumer);
	}

	@Override
	public void unsubscribe(Consumer<? super M> messageConsumer) {
		consumers.remove(messageConsumer);
	}

	@Override
	public void send(M message) {
		consumers.forEach(consumer -> consumer.accept(message));
	}

	//#end IMPLEMENTATION OF 'SimpleSendingNotification'

}
