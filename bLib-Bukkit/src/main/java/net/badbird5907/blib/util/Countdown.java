package net.badbird5907.blib.util;

import net.badbird5907.blib.bLib;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {

    private int time;

    private BukkitTask task;
    private int count = 0;
    protected final Plugin plugin = bLib.getPlugin();
    private boolean async = false;
    public Countdown(int time, boolean... async) {
        this.time = time;
        this.count = 10;
        if (async.length > 0) {
            this.async = async[0];
        }
        start();
    }
    public abstract void count(int current);
    public abstract void done();
    public final void start() {
        time = 0;
        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {
                time = time <= count ? time + 1 : -1;
                if (time >= count || time == -1) {
                    cancel();
                    done();
                    return;
                }
                int left = count - time;
                count(left);
            }
        };
        if (async) {
            task = runnable.runTaskTimerAsynchronously(plugin, 0L, 20L);
        } else {
            task = runnable.runTaskTimer(plugin, 0L, 20L);
        }
    }

}
