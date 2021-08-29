package net.badbird5907.blib.util;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "FieldAccessedSynchronizedAndUnsynchronized"})
public class Tasks <T> {
    private static Plugin plugin;
    public static void init(Plugin plugin){
        plugin = plugin;
    }

    public static void run( Runnable callable) {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }
        Bukkit.getScheduler().runTask(plugin, callable);
    }
    public static void runSync(Runnable callable){
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }
        run(callable);
    }

    public static void runAsync( Runnable callable) {

        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        } Bukkit.getScheduler().runTaskAsynchronously(plugin, callable);
    }

    public static void runLater( Runnable callable, long delay) {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }   Bukkit.getScheduler().runTaskLater(plugin, callable, delay);
    }

    public static void runAsyncLater( Runnable callable, long delay) {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }  Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, callable, delay);
    }

    public static void runTimer( Runnable callable, long delay, long interval) {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }  Bukkit.getScheduler().runTaskTimer(plugin, callable, delay, interval);
    }

    public static void runAsyncTimer( Runnable callable, long delay, long interval) {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }  Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, callable, delay, interval);
    }


    /* https://gist.githubusercontent.com/aikar/77f8caee3c153074c99b/raw/2b7f9491ad7dc34ab3f4a9db0adf57c9e5cdee15/TaskChain.java */
    /**
     * A useless example of registering multiple task signatures and states
     */
    public static void example() {
        log("Starting example");

        Tasks<?> chain = Tasks.newSharedChain("TEST");
        chain
                .delay(20 * 3)
                .sync(() -> {
                    Object test = chain.setTaskData("test", 1);
                    log("==> 1st test");
                })
                .delay(20)
                .async(() -> {
                    Object test = chain.getTaskData("test");
                    log("==> 2nd test: " + test + " = should be 1");
                })
                .sync(Tasks::abort)
                .execute();


        // This chain essentially appends onto the previous one, and will not overlap
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Tasks<?> chain2 = Tasks.newSharedChain("TEST");
            chain2
                    .sync(() -> {
                        Object test = chain2.getTaskData("test");
                        log("==> 3rd test: " + test + " = should be null");
                    })
                    .delay(20)
                    .async(Tasks::abort)
                    .execute();

            Tasks
                    .newSharedChain("TEST")
                    .async(() -> log("==> 4th test - should print"))
                    .returnData("notthere")
                    .abortIfNull()
                    .syncLast((val) -> log("Shouldn't execute due to null abort"))
                    .execute();
        });
        Tasks
                .newSharedChain("TEST2")
                .delay(20 * 3)
                .sync(() -> log("this should run at same time as 1st test"))
                .delay(20)
                .async(() -> log("this should run at same time as 2nd test"))
                .execute();
        Tasks
                .newChain()
                .sync(() -> log("THE FIRST!"))
                .delay(20 * 10) // Wait 20s to start any task
                .async(() -> log("This ran async - with no input or return"))
                .<Integer>asyncFirstCallback(next -> {
                    // Use a callback to provide result
                    log("this also ran async, but will call next task in 3 seconds.");
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> next.accept(3), 60);
                })
                .sync(input -> { // Will be ran 3s later but didn't use .delay()
                    log("should of got 3: " + input);
                    return 5 + input;
                })
                .storeAsData("Test1")
                .syncLast(input2 -> log("should be 8: " + input2)) // Consumes last result, but doesn't pass a new one
                .delay(20) // Wait 1s until next
                .sync(() -> log("Generic 1s later")) // no input expected, no output, run sync
                .asyncFirst(() -> 3) // Run task async and return 3
                .delay(5 * 20) // Wait 5s
                .asyncLast(input1 -> log("async last value 5s later: " + input1)) // Run async again, with value of 3
                .<Integer>returnData("Test1")
                .asyncLast((val) -> log("Should of got 8 back from data: " + val))
                .sync(Tasks::abort)
                .sync(() -> log("Shouldn't be called"))
                .execute();
    }

    /**
     * Util method for example logging
     * @param log
     */
    private static void log(String log) {
        for (String s : log.split("\n")) {
            Logger.info(s);
        }
    }
    public static void logError(String log) {
        for (String s : log.split("\n")) {
            Logger.severe(s);
        }
    }

    /**
     * =============================================================================================
     */
    private static final Map<String, Tasks<?>> sharedChains = new HashMap<>();
    private static final ThreadLocal<Tasks<?>> currentChain = new ThreadLocal<>();

    private boolean shared = false;
    private boolean done = false;
    private boolean executed = false;
    private boolean async;
    private String sharedName;
    private Object previous;
    private final Map<String, Object> taskMap = new HashMap<>(0);

    private TaskHolder<?, ?> currentHolder;
    @SuppressWarnings("WeakerAccess") // IDE is wrong, can't be private
    protected Runnable doneCallback;
    protected BiConsumer<Exception, Task<?, ?>> errorHandler;
    private final ConcurrentLinkedQueue<TaskHolder<?,?>> chainQueue = new ConcurrentLinkedQueue<>();

    /**
     * =============================================================================================
     */

    /**
     * Starts a new chain.
     * @return
     */
    public static <T> Tasks<T> newChain() {
        if (plugin == null) {
            throw new IllegalStateException("bLib has not been initialized! Please use bLib.create!");
        }
        return new Tasks<>();
    }

    /**
     * Allows re-use of a Chain by giving it a name. This lets you keep adding Tasks to
     * an already executing chain. This allows you to assure a sequence of events to only
     * execute one at a time, but may be registered and executed from multiple execution points
     * or threads.
     *
     * Task Data is not shared between chains of the same name. The only thing that is shared
     * is execution order, in that 2 sequences of events can not run at the same time.
     *
     * If 2 chains are created at same time under same name, the first chain will execute fully before the 2nd chain will start, no matter how long
     *
     * @param name
     * @param <T>
     * @return
     */
    public static synchronized <T> Tasks<T> newSharedChain(String name) {
        Tasks<?> chain;
        synchronized (sharedChains) {
            chain = sharedChains.get(name);
        }

        if (chain != null) {
            synchronized (chain) {
                if (chain.done) {
                    chain = null;
                }
            }
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
     * @see #newSharedChain(String) for full documentation
     * @param player
     * @param <T>
     * @return
     */
    public static <T> Tasks<T> newSharedChain(Player player) {
        return newSharedChain(player, "__MAIN__");
    }

    /**
     * Creates a shared chain bound to a specific player with specified name
     * @see #newSharedChain(String) for full documentation
     * @param player
     * @param name
     * @param <T>
     * @return
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

    /**
     * =============================================================================================
     */

    /**
     * Checks if the chain has a value saved for the specified key.
     * @param key
     * @return
     */
    public boolean hasTaskData(String key) {
        return taskMap.containsKey(key);
    }

    /**
     * Retrieves a value relating to a specific key, saved by a previous task.
     *
     * @param key
     * @param <R>
     * @return
     */
    public <R> R getTaskData(String key) {
        return (R) taskMap.get(key);
    }

    /**
     * Saves a value for this chain so that a task furthur up the chain can access it.
     *
     * Useful for passing multiple values to the next (or furthur) tasks.
     *
     * @param key
     * @param val
     * @param <R>
     * @return
     */
    public <R> R setTaskData(String key, Object val) {
        return (R) taskMap.put(key, val);
    }

    /**
     * Removes a saved value on the chain.
     *
     * @param key
     * @param <R>
     * @return
     */
    public <R> R removeTaskData(String key) {
        return (R) taskMap.remove(key);
    }

    /**
     * =============================================================================================
     */

    /**
     * Checks if the previous task return was null.
     *
     * If not null, the previous task return will forward to the next task.
     * @return
     */
    public Tasks<T> abortIfNull() {
        return abortIfNull(null, null);
    }

    /**
     * Checks if the previous task return was null, and aborts if it was, optionally
     * sending a message to the player.
     *
     * If not null, the previous task return will forward to the next task.
     * @param player
     * @param msg
     * @return
     */
    public Tasks<T> abortIfNull(Player player, String msg) {
        return current((obj) -> {
            if (obj == null) {
                if (msg != null && player != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                abort();
                return null;
            }
            return obj;
        });
    }

    /**
     * Takes the previous tasks return value, stores it to the specified key
     * as Task Data, and then forwards that value to the next task.
     *
     * @param key
     * @return
     */
    public Tasks<T> storeAsData(String key) {
        return current((val) -> {
            setTaskData(key, val);
            return val;
        });
    }

    /**
     * Reads the specified key from Task Data, and passes it to the next task.
     *
     * Will need to pass expected type such as chain.<Foo>returnData("key")
     *
     * @param key
     * @param <R>
     * @return
     */
    public <R> Tasks<R> returnData(String key) {
        return currentFirst(() -> (R) getTaskData(key));
    }

    public Tasks<Tasks<?>> returnChain() {
        return currentFirst(() -> this);
    }

    /**
     * Adds a delay to the chain execution.
     *
     * @param ticks # of ticks to delay before next task (20 = 1 second)
     * @return
     */
    public Tasks<T> delay(final int ticks) {
        //noinspection CodeBlock2Expr
        return currentCallback((input, next) -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> next.accept(input), ticks);
        });
    }

    /**
     * Execute a task on the main thread, with no previous input, and a callback to return the response to.
     *
     * It's important you don't perform blocking operations in this method. Only use this if
     * the task will be scheduling a different sync operation outside of the Taskss scope.
     *
     * Usually you could achieve the same design with a blocking API by switching to an async task
     * for the next task and running it there.
     *
     * This method would primarily be for cases where you need to use an API that ONLY provides
     * a callback style API.
     *
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> syncFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * @see #syncFirstCallback(AsyncExecutingFirstTask) but ran off main thread
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> asyncFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #syncFirstCallback(AsyncExecutingFirstTask) but ran on current thread the Chain was created on
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> currentFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    /**
     * Execute a task on the main thread, with the last output, and a callback to return the response to.
     *
     * It's important you don't perform blocking operations in this method. Only use this if
     * the task will be scheduling a different sync operation outside of the Taskss scope.
     *
     * Usually you could achieve the same design with a blocking API by switching to an async task
     * for the next task and running it there.
     *
     * This method would primarily be for cases where you need to use an API that ONLY provides
     * a callback style API.
     *
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> syncCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public Tasks<?> syncCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * @see #syncCallback(AsyncExecutingTask) but ran off main thread
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> asyncCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #syncCallback(AsyncExecutingTask) but ran off main thread
     * @param task
     * @return
     */
    public Tasks<?> asyncCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #syncCallback(AsyncExecutingTask) but ran on current thread the Chain was created on
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> currentCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    /**
     * @see #syncCallback(AsyncExecutingTask) but ran on current thread the Chain was created on
     * @param task
     * @return
     */
    public Tasks<?> currentCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    /**
     * Execute task on main thread, with no input, returning an output
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> syncFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * @see #syncFirst(FirstTask) but ran off main thread
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> asyncFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #syncFirst(FirstTask) but ran on current thread the Chain was created on
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> currentFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    /**
     * Execute task on main thread, with the last returned input, returning an output
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> sync(Task<R, T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * Execute task on main thread, with no input or output
     * @param task
     * @return
     */
    public Tasks<?> sync(GenericTask task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * @see #sync(Task) but ran off main thread
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> async(Task<R, T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }
    /**
     * @see #sync(GenericTask) but ran off main thread
     * @param task
     * @return
     */
    public Tasks<?> async(GenericTask task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #sync(Task) but ran on current thread the Chain was created on
     * @param task
     * @param <R>
     * @return
     */
    public <R> Tasks<R> current(Task<R, T> task) {
        return add0(new TaskHolder<>(this, null, task));
    }
    /**
     * @see #sync(GenericTask) but ran on current thread the Chain was created on
     * @param task
     * @return
     */
    public Tasks<?> current(GenericTask task) {
        return add0(new TaskHolder<>(this, null, task));
    }


    /**
     * Execute task on main thread, with the last output, and no furthur output
     * @param task
     * @return
     */
    public Tasks<?> syncLast(LastTask<T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    /**
     * @see #syncLast(LastTask) but ran off main thread
     * @param task
     * @return
     */
    public Tasks<?> asyncLast(LastTask<T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    /**
     * @see #syncLast(LastTask) but ran on current thread the Chain was created on
     * @param task
     * @return
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
                if (this.shared) {
                    return;
                }
                throw new RuntimeException("Already executed and not a shared chain");
            }
            this.executed = true;
        }
        async = !Bukkit.isPrimaryThread();
        nextTask();
    }
    public void executeNext() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::execute, 1);
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
        if (this.shared) {
            synchronized (sharedChains) {
                sharedChains.remove(this.sharedName);
            }
        }
        if (this.doneCallback != null) {
            this.doneCallback.run();
        }
    }

    @SuppressWarnings("rawtypes")
    protected Tasks add0(TaskHolder<?,?> task) {
        synchronized (this) {
            if (!this.shared && this.executed) {
                throw new RuntimeException("Tasks is executing and not shared");
            }
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
            if (this.currentHolder == null) {
                this.done = true; // to ensure its done while synchronized
            }
        }

        if (this.currentHolder == null) {
            this.previous = null;
            // All Done!
            this.done();
            return;
        }

        Boolean isNextAsync = this.currentHolder.async;
        if (isNextAsync == null) {
            isNextAsync = this.async;
        }

        if (isNextAsync) {
            if (this.async) {
                this.currentHolder.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    this.async = true;
                    this.currentHolder.run();
                });
            }
        } else {
            if (this.async) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    this.async = false;
                    this.currentHolder.run();
                });
            } else {
                this.currentHolder.run();
            }
        }
    }

    /**
     * Provides foundation of a task with what the previous task type should return
     * to pass to this and what this task will return.
     * @param <R> Return Type
     * @param <A> Argument Type Expected
     */
    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private static class TaskHolder<R, A> {
        private final Tasks<?> chain;
        private final Task<R, A> task;
        public final Boolean async;

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
                if (this.task instanceof AsyncExecutingTask) {
                    ((AsyncExecutingTask<R, A>) this.task).runAsync((A) arg, this::next);
                } else {
                    next(this.task.run((A) arg));
                }
            } catch (AbortChainException ignored) {
                this.abort();
            } catch (Exception e) {
                if (this.chain.errorHandler != null) {
                    this.chain.errorHandler.accept(e, this.task);
                } else {
                    logError("Tasks Exception on " + this.task.getClass().getName());
                    logError(ExceptionUtils.getFullStackTrace(e));
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

            this.chain.async = !Bukkit.isPrimaryThread(); // We don't know where the task called this from.
            this.chain.previous = resp;
            this.chain.nextTask();
        }
    }

    @SuppressWarnings("PublicInnerClass,WeakerAccess")
    public static class AbortChainException extends Throwable {}

    /**
     * Generic task with synchronous return (but may execute on any thread)
     * @param <R>
     * @param <A>
     */
    @SuppressWarnings("WeakerAccess")
    public interface Task <R, A> {
        /**
         * Gets the current chain that is executing this task. This method should only be called on the same thread
         * that is executing the task.
         * @return
         */
        public default Tasks<?> getCurrentChain() {
            return currentChain.get();
        }

        R run(A input) throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface AsyncExecutingTask<R, A> extends Task<R, A> {
        /**
         * Gets the current chain that is executing this task. This method should only be called on the same thread
         * that is executing the task.
         *
         * Since this is an AsyncExecutingTask, You must call this method BEFORE passing control to another thread.
         * @return
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
    @SuppressWarnings("WeakerAccess")
    public interface FirstTask <R> extends Task<R, Object> {
        @Override
        default R run(Object input) throws AbortChainException {
            return run();
        }

        R run() throws AbortChainException;
    }
    @SuppressWarnings("WeakerAccess")
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
    @SuppressWarnings("WeakerAccess")
    public interface LastTask <A> extends Task<Object, A> {
        @Override
        default Object run(A input) throws AbortChainException {
            runLast(input);
            return null;
        }
        void runLast(A input) throws AbortChainException;
    }
    @SuppressWarnings("WeakerAccess")
    public interface GenericTask extends Task<Object, Object> {
        @Override
        default Object run(Object input) throws AbortChainException {
            runGeneric();
            return null;
        }
        void runGeneric() throws AbortChainException;
    }
    @SuppressWarnings("WeakerAccess")
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

    private static class SharedTasks<R>  extends Tasks<R> {
        private final Tasks<R> backingChain;
        private SharedTasks(Tasks<R> backingChain) {
            this.backingChain = backingChain;
        }

        @Override
        public void execute() {
            synchronized (backingChain) {
                // This executes SharedTasks.execute(Runnable), which says execute
                // my wrapped chains queue of events, but pass a done callback for when its done.
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
