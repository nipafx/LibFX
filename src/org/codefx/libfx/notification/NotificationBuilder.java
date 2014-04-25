package org.codefx.libfx.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.application.Platform;

import org.codefx.libfx.notification.decoration.RunLaterSendingNotificationDecorator;
import org.codefx.libfx.notification.decoration.SynchronizedSendingNotificationDecorator;

/**
 * Usability class for easier creation of decorated {@link SendingNotification SendingNotifications}. The same builder
 * can be used to create several notification instances by repeatedly calling {@link #build()}.
 * <p>
 * <h3>Examples</h3>
 * <p>
 * TODO
 * <p>
 *
 * @param <M>
 *            the type of message sent by the notification this builder builds
 */
public class NotificationBuilder<M> {

	/*
	 * In order to be reused the builder only stores lambdas which create or decorate a notification (instead of a
	 * Notification-instance). Calling 'build()' will then execute those lambdas and return the result.
	 */

	// #region PROPERTIES

	/**
	 * The root notification which might be decorated by the {@link #decorators}.
	 */
	private final Supplier<SendingNotification<M>> creator;

	/**
	 * The decorators for the {@link #creator}.
	 */
	private final List<UnaryOperator<SendingNotification<M>>> decorators;

	//#end PROPERTIES

	// #region CONSTRUCTION

	/**
	 * Creates a new builder which starts with the specified creator and an empty list of decorators.
	 *
	 * @param creator
	 *            the {@link Supplier} of the root notification
	 */
	private NotificationBuilder(Supplier<SendingNotification<M>> creator) {
		this.creator = creator;
		this.decorators = new ArrayList<>();
	}

	/**
	 * Creates a new notification builder.
	 *
	 * @param <M>
	 *            the type of message sent by the notification this builder builds
	 * @return a notification builder
	 */
	public static <M> NotificationBuilder<M> of() {
		return new NotificationBuilder<>(() -> new SimpleSendingNotification<>());
	}

	/**
	 * Creates a new notification builder which uses the specified supplier to create a first sending notification. That
	 * notification instance will be decorated by further calls to the builder's methods.
	 *
	 * @param <M>
	 *            the type of message sent by the notification this builder builds
	 * @param creator
	 *            the {@link Supplier} which creates an instance of {@link SendingNotification}
	 * @return a notification builder
	 */
	public static <M> NotificationBuilder<M> of(Supplier<SendingNotification<M>> creator) {
		return new NotificationBuilder<>(creator);
	}

	//#end CONSTRUCTION

	// #region DECORATE

	/**
	 * Decorates the notification returned by {@link #build()}.
	 *
	 * @param decorator
	 *            the decorator used to add behavior to the notifications which this builder builds
	 * @return this instance of the builder
	 */
	public NotificationBuilder<M> decorate(UnaryOperator<SendingNotification<M>> decorator) {
		decorators.add(decorator);
		return this;
	}

	/**
	 * Synchronizes all of the notification's methods making it thread safe.
	 *
	 * @return this instance of the builder
	 */
	public NotificationBuilder<M> synchronize() {
		return decorate(notification -> new SynchronizedSendingNotificationDecorator<>(notification));
	}

	/**
	 * Delegates sending the message to {@link Platform#runLater(Runnable) Platform.runLater}.
	 *
	 * @return this instance of the builder
	 */
	public NotificationBuilder<M> runLater() {
		return decorate(notification -> new RunLaterSendingNotificationDecorator<>(notification));
	}

	//#end DECORATE

	/**
	 * Builds a sending notification according to the current state of this builder.
	 * <p>
	 * Calling this method repeatedly will return different instances. The builder can safely be edited after calling
	 * this method without side-effects on already returned instances.
	 *
	 * @return a {@link SendingNotification} according to this builder's current state
	 */
	public SendingNotification<M> build() {
		SendingNotification<M> notification = creator.get();
		for (UnaryOperator<SendingNotification<M>> decorator : decorators)
			notification = decorator.apply(notification);
		return notification;
	}

}
