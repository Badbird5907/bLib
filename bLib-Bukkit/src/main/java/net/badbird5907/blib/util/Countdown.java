package net.badbird5907.blib.util;

import net.badbird5907.blib.bLib;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {

    private int time;

    protected BukkitTask task;
    protected final Plugin plugin = bLib.getPlugin();
    public Countdown(int time) {
        this.time = time;
        start();
    }
    public abstract void count(int current);
    public final void start() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                time--;
                if (time <= -1){
                    cancel();
                    return;
                }
                count(time);
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }

}