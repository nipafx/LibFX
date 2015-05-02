package org.codefx.tarkastus;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule which evaluates all statements (i.e. runs all tests) on the JavaFX platform thread.
 * <p>
 * The platform thread is created on demand.
 * <p>
 * TODO: superficial tests
 */
public class JavaFXRule implements TestRule {

	@Override
	public Statement apply(Statement statement, Description description) {
		return new JavaFXStatement(statement);
	}

	/**
	 * Evaluates statements on the JavaFX platform thread.
	 */
	private static class JavaFXStatement extends Statement {

		private final StatementOnPlatformThread statement;

		/**
		 * Creates a new JavaFX statement.
		 *
		 * @param statement
		 *            the statement which will be evaluated on the platform thread
		 */
		public JavaFXStatement(Statement statement) {
			this.statement = new StatementOnPlatformThread(statement);
		}

		@Override
		public void evaluate() throws Throwable {
			JavaFXInitializer.ensureInitialized();
			statement.evaluate();
		}

	}

	/**
	 * Ensures that JavaFX is initialized.
	 */
	private static class JavaFXInitializer {

		private static boolean initialized = false;

		/**
		 * Ensures that JavaFX is initialized.
		 *
		 * @throws InterruptedException
		 *             when waiting for the initialization (which happens in another thread) to complete is interrupted
		 */
		public static void ensureInitialized() throws InterruptedException {
			if (initialized)
				return;

			initializeOnce();
		}

		private static synchronized void initializeOnce() throws InterruptedException {
			if (initialized)
				return;

			initialize();
			initialized = true;
		}

		@SuppressWarnings("unused")
		private static void initialize() throws InterruptedException {

			/*
			 * To initialize JavaFX, create a JFXPanel on the Swing EDT.
			 */

			final CountDownLatch initialized = new CountDownLatch(1);
			SwingUtilities.invokeLater(() -> {
				new JFXPanel();
				initialized.countDown();
			});
			initialized.await();
		}

	}

	/**
	 * Evaluates a statement on the JavaFX platform thread.
	 */
	private static class StatementOnPlatformThread {

		private final Statement statement;

		/**
		 * Creates a new statement.
		 *
		 * @param statement
		 *            the statement which will be evaluated on the platform thread
		 */
		public StatementOnPlatformThread(Statement statement) {
			this.statement = statement;
		}

		/**
		 * Evaluates the statement specified during construction
		 *
		 * @throws Throwable
		 *             when the statement evaluation throws an exception
		 */
		public void evaluate() throws Throwable {
			Property<Optional<Throwable>> caughtThrowable = new SimpleObjectProperty<>(Optional.empty());
			CountDownLatch evaluated = new CountDownLatch(1);

			Platform.runLater(() -> {
				caughtThrowable.setValue(evaluateOnPlatform(statement));
				evaluated.countDown();
			});

			evaluated.await();

			// make sure to rethrow any exception which occurred during evaluation
			if (caughtThrowable.getValue().isPresent())
				throw caughtThrowable.getValue().get();
		}

		private static Optional<Throwable> evaluateOnPlatform(Statement statement) {
			try {
				statement.evaluate();
				return Optional.empty();
			} catch (Throwable ex) {
				return Optional.of(ex);
			}
		}

	}
}
