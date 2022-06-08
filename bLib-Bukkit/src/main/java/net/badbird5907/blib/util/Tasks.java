package net.badbird5907.blib.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.isPrimaryThread;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings({"unused", "FieldAccessedSynchronizedAndUnsynchronized"})
public class Tasks<T> {
	/**
	 * =============================================================================================
	 */
	private static final Map<String, Tasks<?>> sharedChains = new HashMap<>();
	private static final ThreadLocal<Tasks<?>> currentChain = new ThreadLocal<>();
	private static Plugin plugin;
	private final Map<String, Object> taskMap = new HashMap<>(0);
	private final ConcurrentLinkedQueue<TaskHolder<?, ?>> chainQueue = new ConcurrentLinkedQueue<>();
	protected Runnable doneCallback;
	protected BiConsumer<Exception, Task<?, ?>> errorHandler;
	private boolean shared = false;
	private boolean done = false;
	/* https://gist.githubusercontent.com/aikar/77f8caee3c153074c99b/raw/2b7f9491ad7dc34ab3f4a9db0adf57c9e5cdee15/TaskChain.java */
	private boolean executed = false;
	private boolean async;
	private String sharedName;
	private Object previous;
	private TaskHolder<?, ?> currentHolder;

	public static void init(Plugin plugin1) {
		plugin = plugin1;
	}

	public static void run(Runnable callable) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTask(plugin, callable);
	}

	public static void runSync(Runnable callable) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		run(callable);
	}

	public static void runAsync(Runnable callable) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTaskAsynchronously(plugin, callable);
	}

	public static void runLater(Runnable callable, long delay) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTaskLater(plugin, callable, delay);
	}

	public static void runAsyncLater(Runnable callable, long delay) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTaskLaterAsynchronously(plugin, callable, delay);
	}

	public static void runTimer(Runnable callable, long delay, long interval) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTaskTimer(plugin, callable, delay, interval);
	}

	public static void runAsyncTimer(Runnable callable, long delay, long interval) {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		getScheduler().runTaskTimerAsynchronously(plugin, callable, delay, interval);
	}

	/**
	 * A useless example of registering multiple task signatures and states
	 */
	public static void example() {
		log("Starting example");
		Tasks<?> chain = newSharedChain("TEST");
		chain.delay(20 * 3).sync(() -> {
			Object test = chain.setTaskData("test", 1);
			log("==> 1st test");
		}).delay(20).async(() -> log("==> 2nd test: " + chain.getTaskData("test") + " = should be 1")).sync(Tasks::abort).execute();
		// This chain essentially appends onto the previous one, and will not overlap
		getScheduler().runTaskAsynchronously(plugin, () -> {
			Tasks<?> chain2 = newSharedChain("TEST");
			chain2.sync(() -> log("==> 3rd test: " + chain2.getTaskData("test") + " = should be null")).delay(20).async(Tasks::abort).execute();
			newSharedChain("TEST").async(() -> log("==> 4th test - should print")).returnData("notthere").abortIfNull().syncLast((val) -> log("Shouldn't execute due to null abort")).execute();
		});
		newSharedChain("TEST2").delay(20 * 3).sync(() -> log("this should run at same time as 1st test")).delay(20).async(() -> log("this should run at same time as 2nd test")).execute();
		newChain().sync(() -> log("THE FIRST!")).delay(20 * 10).async(() -> log("This ran async - with no input or return")).<Integer>asyncFirstCallback(next -> {
			log("this also ran async, but will call next task in 3 seconds.");
			getScheduler().scheduleSyncDelayedTask(plugin, () -> next.accept(3), 60);
		}).sync(input -> {
			log("should of got 3: " + input);
			return 5 + input;
		}).storeAsData("Test1").syncLast(input2 -> log("should be 8: " + input2)).delay(20).sync(() -> log("Generic 1s later")).asyncFirst(() -> 3).delay(5 * 20).asyncLast(input1 -> log("async last value 5s later: " + input1)).<Integer>returnData("Test1").asyncLast((val) -> log("Should of got 8 back from data: " + val)).sync(Tasks::abort).sync(() -> log("Shouldn't be called")).execute();
	}

	/**
	 * Util method for example logging
	 */
	private static void log(String log) {
		stream(log.split("\n")).forEach(Logger::info);
	}

	public static void logError(String log) {
		stream(log.split("\n")).forEach(Logger::severe);
	}

	/*
	 * =============================================================================================
	 */

	/**
	 * Starts a new chain.
	 */
	public static <T> Tasks<T> newChain() {
		assert plugin != null : "bLib has not been initialized! Please use bLib.create!";
		return new Tasks<>();
	}

	/**
	 * Allows re-use of a Chain by giving it a name. This lets you keep adding Tasks to
	 * an already executing chain. This allows you to assure a sequence of events to only
	 * execute one at a time, but may be registered and executed from multiple execution points
	 * or threads.
	 * <p>
	 * Task Data is not shared between chains of the same name. The only thing that is shared
	 * is execution order, in that 2 sequences of events can not run at the same time.
	 * <p>
	 * If 2 chains are created at same time under same name, the first chain will execute fully before the 2nd chain will start, no matter how long
	 */
	public static synchronized <T> Tasks<T> newSharedChain(String name) {
		Tasks<?> chain;
		synchronized (sharedChains) {
			chain = sharedChains.get(name);
		}
		if (chain != null) synchronized (chain) {
			if (chain.done) chain = null;
		}
		if (chain == null) {
			chain = newChain();
			chain.shared = true;
			chain.sharedName = name;
			sharedChains.put(name, chain);
		}
		return new SharedTasks<>((Tasks<T>) chain);
	}

	/**
	 * Creates a shared chain bound to a specific player with default name
	 *
	 * @see #newSharedChain(String) for full documentation
	 */
	public static <T> Tasks<T> newSharedChain(Player player) {
		return newSharedChain(player, "__MAIN__");
	}

	/**
	 * Creates a shared chain bound to a specific player with specified name
	 *
	 * @see #newSharedChain(String) for full documentation
	 */
	public static <T> Tasks<T> newSharedChain(Player player, String name) {
		return newSharedChain(player.getUniqueId() + "__PlayerChain__" + name);
	}


	/**
	 * Call to abort execution of the chain.
	 */
	public static void abort() throws AbortChainException {
		throw new AbortChainException();
	}

	/*
	 * =============================================================================================
	 */

	/**
	 * Checks if the chain has a value saved for the specified key.
	 */
	public boolean hasTaskData(String key) {
		return taskMap.containsKey(key);
	}

	/**
	 * Retrieves a value relating to a specific key, saved by a previous task.
	 */
	public <R> R getTaskData(String key) {
		return (R) taskMap.get(key);
	}

	/**
	 * Saves a value for this chain so that a task furthur up the chain can access it.
	 * <p>
	 * Useful for passing multiple values to the next (or furthur) tasks.
	 */
	public <R> R setTaskData(String key, Object val) {
		return (R) taskMap.put(key, val);
	}

	/**
	 * Removes a saved value on the chain.
	 */
	public <R> R removeTaskData(String key) {
		return (R) taskMap.remove(key);
	}

	/*
	 * =============================================================================================
	 */

	/**
	 * Checks if the previous task return was null.
	 * <p>
	 * If not null, the previous task return will forward to the next task.
	 */
	public Tasks<T> abortIfNull() {
		return abortIfNull(null, null);
	}

	/**
	 * Checks if the previous task return was null, and aborts if it was, optionally
	 * sending a message to the player.
	 * <p>
	 * If not null, the previous task return will forward to the next task.
	 */
	public Tasks<T> abortIfNull(Player player, String msg) {
		return current((obj) -> {
			if (obj == null) {
				if (msg != null && player != null) player.sendMessage(translateAlternateColorCodes('&', msg));
				abort();
				return null;
			}
			return obj;
		});
	}

	/**
	 * Takes the previous tasks return value, stores it to the specified key
	 * as Task Data, and then forwards that value to the next task.
	 */
	public Tasks<T> storeAsData(String key) {
		return current((val) -> {
			setTaskData(key, val);
			return val;
		});
	}

	/**
	 * Reads the specified key from Task Data, and passes it to the next task.
	 * <p>
	 * Will need to pass expected type such as chain.<Foo>returnData("key")
	 */
	public <R> Tasks<R> returnData(String key) {
		return currentFirst(() -> getTaskData(key));
	}

	public Tasks<Tasks<?>> returnChain() {
		return currentFirst(() -> this);
	}

	/**
	 * Adds a delay to the chain execution.
	 *
	 * @param ticks # of ticks to delay before next task (20 = 1 second)
	 */
	public Tasks<T> delay(final int ticks) {
		//noinspection CodeBlock2Expr
		return currentCallback((input, next) -> {
			getScheduler().scheduleSyncDelayedTask(plugin, () -> next.accept(input), ticks);
		});
	}

	/**
	 * Execute a task on the main thread, with no previous input, and a callback to return the response to.
	 * <p>
	 * It's important you don't perform blocking operations in this method. Only use this if
	 * the task will be scheduling a different sync operation outside of the Taskss scope.
	 * <p>
	 * Usually you could achieve the same design with a blocking API by switching to an async task
	 * for the next task and running it there.
	 * <p>
	 * This method would primarily be for cases where you need to use an API that ONLY provides
	 * a callback style API.
	 */
	public <R> Tasks<R> syncFirstCallback(AsyncExecutingFirstTask<R> task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * @see #syncFirstCallback(AsyncExecutingFirstTask) but ran off main thread
	 */
	public <R> Tasks<R> asyncFirstCallback(AsyncExecutingFirstTask<R> task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #syncFirstCallback(AsyncExecutingFirstTask) but ran on current thread the Chain was created on
	 */
	public <R> Tasks<R> currentFirstCallback(AsyncExecutingFirstTask<R> task) {
		return add0(new TaskHolder<>(this, null, task));
	}

	/**
	 * Execute a task on the main thread, with the last output, and a callback to return the response to.
	 * <p>
	 * It's important you don't perform blocking operations in this method. Only use this if
	 * the task will be scheduling a different sync operation outside of the Taskss scope.
	 * <p>
	 * Usually you could achieve the same design with a blocking API by switching to an async task
	 * for the next task and running it there.
	 * <p>
	 * This method would primarily be for cases where you need to use an API that ONLY provides
	 * a callback style API.
	 */
	public <R> Tasks<?> syncCallback(AsyncExecutingTask<R, T> task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	public Tasks<?> syncCallback(AsyncExecutingGenericTask task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * @see #syncCallback(AsyncExecutingTask) but ran off main thread
	 */
	public <R> Tasks<?> asyncCallback(AsyncExecutingTask<R, T> task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #syncCallback(AsyncExecutingTask) but ran off main thread
	 */
	public Tasks<?> asyncCallback(AsyncExecutingGenericTask task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #syncCallback(AsyncExecutingTask) but ran on current thread the Chain was created on
	 */
	public <R> Tasks<R> currentCallback(AsyncExecutingTask<R, T> task) {
		return add0(new TaskHolder<>(this, null, task));
	}

	/**
	 * @see #syncCallback(AsyncExecutingTask) but ran on current thread the Chain was created on
	 */
	public Tasks<?> currentCallback(AsyncExecutingGenericTask task) {
		return add0(new TaskHolder<>(this, null, task));
	}

	/**
	 * Execute task on main thread, with no input, returning an output
	 */
	public <R> Tasks<R> syncFirst(FirstTask<R> task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * @see #syncFirst(FirstTask) but ran off main thread
	 */
	public <R> Tasks<R> asyncFirst(FirstTask<R> task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #syncFirst(FirstTask) but ran on current thread the Chain was created on
	 */
	public <R> Tasks<R> currentFirst(FirstTask<R> task) {
		return add0(new TaskHolder<>(this, null, task));
	}

	/**
	 * Execute task on main thread, with the last returned input, returning an output
	 */
	public <R> Tasks<R> sync(Task<R, T> task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * Execute task on main thread, with no input or output
	 */
	public Tasks<?> sync(GenericTask task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * @see #sync(Task) but ran off main thread
	 */
	public <R> Tasks<R> async(Task<R, T> task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #sync(GenericTask) but ran off main thread
	 */
	public Tasks<?> async(GenericTask task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #sync(Task) but ran on current thread the Chain was created on
	 */
	public <R> Tasks<R> current(Task<R, T> task) {
		return add0(new TaskHolder<>(this, null, task));
	}

	/**
	 * @see #sync(GenericTask) but ran on current thread the Chain was created on
	 */
	public Tasks<?> current(GenericTask task) {
		return add0(new TaskHolder<>(this, null, task));
	}


	/**
	 * Execute task on main thread, with the last output, and no furthur output
	 */
	public Tasks<?> syncLast(LastTask<T> task) {
		return add0(new TaskHolder<>(this, false, task));
	}

	/**
	 * @see #syncLast(LastTask) but ran off main thread
	 */
	public Tasks<?> asyncLast(LastTask<T> task) {
		return add0(new TaskHolder<>(this, true, task));
	}

	/**
	 * @see #syncLast(LastTask) but ran on current thread the Chain was created on
	 */
	public Tasks<?> currentLast(LastTask<T> task) {
		return add0(new TaskHolder<>(this, null, task));
	}


	/**
	 * Finished adding tasks, begins executing them.
	 */
	public void execute() {
		execute0();
	}

	protected void execute0() {
		synchronized (this) {
			if (this.executed) {
				if (this.shared) return;
				throw new RuntimeException("Already executed and not a shared chain");
			}
			this.executed = true;
		}
		async = !isPrimaryThread();
		nextTask();
	}

	public void executeNext() {
		getScheduler().scheduleSyncDelayedTask(plugin, this::execute, 1);
	}

	public void execute(Runnable done) {
		this.doneCallback = done;
		execute();
	}

	public void execute(BiConsumer<Exception, Task<?, ?>> errorHandler) {
		this.errorHandler = errorHandler;
		execute();
	}

	public void execute(Runnable done, BiConsumer<Exception, Task<?, ?>> errorHandler) {
		this.doneCallback = done;
		this.errorHandler = errorHandler;
		execute();
	}

	protected void done() {
		this.done = true;
		if (this.shared) synchronized (sharedChains) {
			sharedChains.remove(this.sharedName);
		}
		if (this.doneCallback != null) this.doneCallback.run();
	}

	@SuppressWarnings("rawtypes")
	protected Tasks add0(TaskHolder<?, ?> task) {
		synchronized (this) {
			assert this.shared || !this.executed : "Tasks is executing and not shared";
		}

		this.chainQueue.add(task);
		return this;
	}

	/**
	 * Fires off the next task, and switches between Async/Sync as necessary.
	 */
	private void nextTask() {
		synchronized (this) {
			this.currentHolder = this.chainQueue.poll();
			if (this.currentHolder == null) this.done = true; // to ensure its done while synchronized
		}
		if (this.currentHolder == null) {
			this.previous = null;
			// All Done!
			this.done();
			return;
		}
		Boolean isNextAsync = this.currentHolder.async;
		if (isNextAsync == null) isNextAsync = this.async;
		if (isNextAsync) {
			if (this.async) this.currentHolder.run();
			else getScheduler().runTaskAsynchronously(plugin, () -> {
				this.async = true;
				this.currentHolder.run();
			});
		} else if (this.async) getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			this.async = false;
			this.currentHolder.run();
		});
		else this.currentHolder.run();
	}

	/**
	 * Generic task with synchronous return (but may execute on any thread)
	 *
	 * @param <R>
	 * @param <A>
	 */
	public interface Task<R, A> {
		/**
		 * Gets the current chain that is executing this task. This method should only be called on the same thread
		 * that is executing the task.
		 */
		default Tasks<?> getCurrentChain() {
			return currentChain.get();
		}

		R run(A input) throws AbortChainException;
	}

	public interface AsyncExecutingTask<R, A> extends Task<R, A> {
		/**
		 * Gets the current chain that is executing this task. This method should only be called on the same thread
		 * that is executing the task.
		 * <p>
		 * Since this is an AsyncExecutingTask, You must call this method BEFORE passing control to another thread.
		 */
		default Tasks<?> getCurrentChain() {
			return currentChain.get();
		}

		@Override
		default R run(A input) throws AbortChainException {
			// unused
			return null;
		}

		void runAsync(A input, Consumer<R> next) throws AbortChainException;
	}

	public interface FirstTask<R> extends Task<R, Object> {
		@Override
		default R run(Object input) throws AbortChainException {
			return run();
		}

		R run() throws AbortChainException;
	}

	public interface AsyncExecutingFirstTask<R> extends AsyncExecutingTask<R, Object> {
		@Override
		default R run(Object input) throws AbortChainException {
			// Unused
			return null;
		}

		@Override
		default void runAsync(Object input, Consumer<R> next) throws AbortChainException {
			run(next);
		}

		void run(Consumer<R> next) throws AbortChainException;
	}

	public interface LastTask<A> extends Task<Object, A> {
		@Override
		default Object run(A input) throws AbortChainException {
			runLast(input);
			return null;
		}

		void runLast(A input) throws AbortChainException;
	}

	public interface GenericTask extends Task<Object, Object> {
		@Override
		default Object run(Object input) throws AbortChainException {
			runGeneric();
			return null;
		}

		void runGeneric() throws AbortChainException;
	}

	public interface AsyncExecutingGenericTask extends AsyncExecutingTask<Object, Object> {
		@Override
		default Object run(Object input) throws AbortChainException {
			return null;
		}

		@Override
		default void runAsync(Object input, Consumer<Object> next) throws AbortChainException {
			run(() -> next.accept(null));
		}

		void run(Runnable next) throws AbortChainException;
	}

	/**
	 * Provides foundation of a task with what the previous task type should return
	 * to pass to this and what this task will return.
	 *
	 * @param <R> Return Type
	 * @param <A> Argument Type Expected
	 */
	private static class TaskHolder<R, A> {
		public final Boolean async;
		private final Tasks<?> chain;
		private final Task<R, A> task;
		private boolean executed = false;
		private boolean aborted = false;

		private TaskHolder(Tasks<?> chain, Boolean async, Task<R, A> task) {
			this.task = task;
			this.chain = chain;
			this.async = async;
		}

		/**
		 * Called internally by Task Chain to facilitate executing the task and then the next task.
		 */
		private void run() {
			final Object arg = this.chain.previous;
			this.chain.previous = null;
			final R res;
			try {
				currentChain.set(this.chain);
				if (this.task instanceof AsyncExecutingTask)
					((AsyncExecutingTask<R, A>) this.task).runAsync((A) arg, this::next);
				else next(this.task.run((A) arg));
			} catch (AbortChainException ignored) {
				this.abort();
			} catch (Exception e) {
				if (this.chain.errorHandler != null) this.chain.errorHandler.accept(e, this.task);
				else {
					logError("Tasks Exception on " + this.task.getClass().getName());
					logError(getFullStackTrace(e));
				}
				this.abort();
			} finally {
				currentChain.remove();
			}
		}

		/**
		 * Abort the chain, and clear tasks for GC.
		 */
		private synchronized void abort() {
			this.aborted = true;
			this.chain.previous = null;
			this.chain.chainQueue.clear();
			this.chain.done();
		}

		/**
		 * Accepts result of previous task and executes the next
		 */
		private void next(Object resp) {
			synchronized (this) {
				if (this.aborted) {
					this.chain.done();
					return;
				}
				if (this.executed) {
					this.chain.done();
					throw new RuntimeException("This task has already been executed.");
				}
				this.executed = true;
			}
			this.chain.async = !isPrimaryThread(); // We don't know where the task called this from.
			this.chain.previous = resp;
			this.chain.nextTask();
		}
	}

	public static class AbortChainException extends Throwable {
	}

	private static class SharedTasks<R> extends Tasks<R> {
		private final Tasks<R> backingChain;

		private SharedTasks(Tasks<R> backingChain) {
			this.backingChain = backingChain;
		}

		@Override
		public void execute() {
			synchronized (backingChain) {
				// This executes SharedTasks.execute(Runnable), which says execute
				// my wrapped chains queue of events, but pass a done callback for when it's done.
				// We then use the backing chain callback method to not execute the next task in the
				// backing chain until the current one is fully done.
				SharedTasks<R> sharedChain = this;
				backingChain.currentCallback((AsyncExecutingGenericTask) sharedChain::execute);
				backingChain.execute();
			}
		}

		@Override
		public void execute(Runnable done) {
			this.doneCallback = done;
			execute0();
		}
	}
}
