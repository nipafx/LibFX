package org.codefx.libfx.concurrent.act;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the class {@link ActOnce}.
 */
public class ActOnceTest {

	// #region ATTRIBUTES & INITIALIZATION

	/**
	 * The string which passes the {@link #ACTION_GATEWAY}.
	 */
	private static final String ACTION_STRING = "action!";

	/**
	 * A string which does not pass the {@link #ACTION_GATEWAY}.
	 */
	private static final String NO_ACTION_STRING = "no action...";

	/**
	 * The gateway which has to be passed for the action to be executed.
	 */
	private static final Predicate<String> ACTION_GATEWAY = string -> Objects.equals(string, ACTION_STRING);

	/**
	 * The observable on which is acted.
	 */
	private Property<String> observable;

	/**
	 * The action which is undertaken. Increases {@link #executedActionCount}.
	 */
	private Consumer<String> action;

	/**
	 * Counts how many actions were executed.
	 */
	private AtomicInteger executedActionCount;

	/**
	 * Initializes the instances used to test.
	 */
	@Before
	public void setUp() {
		observable = new SimpleStringProperty(NO_ACTION_STRING);
		executedActionCount = new AtomicInteger(0);
		action = string -> executedActionCount.incrementAndGet();
	}

	// #end ATTRIBUTES & INITIALIZATION

	// #region SINGLE THREADED TESTS

	/**
	 * Tests whether an {@link IllegalStateException} is thrown when {@link ActOnce#act() act()} is called for the
	 * second time.
	 */
	@Test(expected = IllegalStateException.class)
	public void testThrowExceptionIfCallActTwice() {
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		act.act();
		act.act();
	}

	/**
	 * Tests whether no action is executed if the initial value does not pass the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testDoNotActIfInitialValueWrong() {
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		act.act();

		assertEquals(0, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed when the initial value passes the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testActWhenInitialValueCorrect() {
		observable.setValue(ACTION_STRING);
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		act.act();

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed only once after the initial value already passed the {@link #ACTION_GATEWAY}
	 * .
	 */
	@Test
	public void testActOnlyOnceWhenInitialValueWasCorrect() {
		observable.setValue(ACTION_STRING);
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		// this executes the action for the first time
		act.act();

		// change the value and set the action string again; if this executes the action again, there is a bug
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed when the value is changed to one which passes the {@link #ACTION_GATEWAY}
	 * after waiting began.
	 */
	@Test
	public void testActWhenCorrectValueIsObserved() {
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		act.act();

		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	/**
	 * Tests whether the action is executed only once after some value already passed the {@link #ACTION_GATEWAY}.
	 */
	@Test
	public void testActOnlyOnceWhenCorrectValueWasObserved() {
		ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
		act.act();

		// this executes the action for the first time
		observable.setValue(ACTION_STRING);

		// change the value and set the action string again; if this executes the action again, there is a bug
		observable.setValue(NO_ACTION_STRING);
		observable.setValue(ACTION_STRING);

		assertEquals(1, executedActionCount.get());
	}

	// TODO: tests for cancel and isWaiting

	// #end SINGLE THREADED TESTS

	// #region MULTI THREADED TESTS

	/**
	 * Creates a number of threads which repeatedly change the {@link #observable}'s value and a number of threads which
	 * execute {@link #action} once when the correct value is set. The value setting threads behave randomly but will
	 * definitely set the correct value at least once. This means that the action must be executed exactly as often as
	 * acting threads exist.
	 * <p>
	 * This is tested.
	 *
	 * @throws InterruptedException
	 *             if waiting for the {@link CountDownLatch} fails
	 */
	@Test
	public void testWithMultipleThreads() throws InterruptedException {
		int nrOfActThreads = 4;
		int nrOfValueThreads = 16;
		int nrOfLoopsPerThread = (int) 1e5;
		CountDownLatch latch = new CountDownLatch(nrOfValueThreads);

		createThreadsWhichActAndSetValues(
				latch, nrOfActThreads, nrOfValueThreads, nrOfLoopsPerThread)
				.forEach(thread -> thread.start());

		latch.await();

		assertEquals(nrOfActThreads, executedActionCount.get());
	}

	/**
	 * Creates threads where some repeatedly set a value on {@link #observable} (setting {@link #ACTION_STRING} approx.
	 * half of the time) and some execute {@link #action} when the correct value is set.
	 *
	 * @param latch
	 *            the latch used to signal that the value threads are done
	 * @param nrOfActThreads
	 *            number of threads which execute {@link #action}.
	 * @param nrOfValueThreads
	 *            the number of created threads
	 * @param nrOfLoopsPerValueThread
	 *            the number of times each thread sets a new value on {@link #observable}
	 * @return a {@link List} of {@link Thread}s which did not yet start
	 */
	private List<Thread> createThreadsWhichActAndSetValues(
			CountDownLatch latch, int nrOfActThreads, int nrOfValueThreads, int nrOfLoopsPerValueThread) {

		Random random = new Random();
		List<Thread> threads = createThreadsWhichSetCorrectValueOften(latch, nrOfValueThreads, nrOfLoopsPerValueThread);
		for (int i = 0; i < nrOfActThreads; i++) {
			int randomIndex = random.nextInt(threads.size());
			Thread threadWhichActs = createThreadWhichActs();
			threadWhichActs.setName("ACT #" + i);
			threads.add(randomIndex, threadWhichActs);
		}

		return threads;
	}

	/**
	 * Creates a thread which execute {@link #action} when the correct value is set to {@link #observable}.
	 *
	 * @return a {@link Thread} which did not yet start
	 */
	private Thread createThreadWhichActs() {
		Runnable runnable = () -> {
			ActOnce<String> act = new ActOnce<>(observable, ACTION_GATEWAY, action);
			act.act();
		};

		return new Thread(runnable);
	}

	/**
	 * Creates threads which set the correct value in approximately half of the specified number of loops.
	 *
	 * @param latch
	 *            the latch used to signal that the thread is done
	 * @param nrOfThreads
	 *            the number of created threads
	 * @param nrOfLoopsPerThread
	 *            the number of times each thread sets a new value on {@link #observable}
	 * @return a {@link List} of {@link Thread}s which did not yet start
	 */
	private List<Thread> createThreadsWhichSetCorrectValueOften(
			CountDownLatch latch, int nrOfThreads, int nrOfLoopsPerThread) {

		List<Thread> threads = new ArrayList<>(nrOfThreads * 2);
		for (int i = 0; i < nrOfThreads; i++) {
			Thread thread = createThreadWhichSetsCorrectValueOften(latch, nrOfLoopsPerThread);
			thread.setName("CORRECT VALUE #" + i);
			threads.add(thread);
		}

		return threads;
	}

	/**
	 * Creates a thread which sets the correct value in approximately half of the specified number of loops.
	 *
	 * @param latch
	 *            the latch used to signal that the thread is done
	 * @param nrOfLoops
	 *            the number of times the thread sets a new value on {@link #observable}
	 * @return a {@link Thread} which did not yet start
	 */
	private Thread createThreadWhichSetsCorrectValueOften(CountDownLatch latch, int nrOfLoops) {
		Random random = new Random();
		Runnable runnable = () -> {
			// make the first n-1 loops random
			for (int i = 0; i < nrOfLoops - 1; i++) {
				boolean setCorrectValue = random.nextBoolean();
				if (setCorrectValue)
					observable.setValue(ACTION_STRING);
				else {
					String randomValue = "" + random.nextDouble();
					observable.setValue(randomValue);
				}
			}
			// set the correct value at the end to give acting threads which start afterwards a chance to act
			observable.setValue(ACTION_STRING);

			latch.countDown();
		};

		return new Thread(runnable, "CorrectValue");
	}

	// #end MULTI THREADED TESTS
}
