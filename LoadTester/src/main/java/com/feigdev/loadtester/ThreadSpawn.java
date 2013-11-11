package com.feigdev.loadtester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ejf3 on 11/4/13.
 */
public class ThreadSpawn extends Service {
    private static final String TAG = "ThreadSpawn";
    private static final LinkedBlockingQueue<Runnable> writerQueue = new LinkedBlockingQueue<Runnable>();
    private static ExecutorService executorService;
    private static final ConcurrentHashMap<Integer, Future<?>> tasks = new ConcurrentHashMap<Integer, Future<?>>();
    private static boolean running = true;
    static Context appContext;
    static Service srv;
    static Watcher watcher;
    static TimedKillTask killer;
    static StartTask startTask;
    static KillTask killTask;

    private static boolean isWorking = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        appContext = getApplicationContext();
        srv = this;
        BusProvider.INSTANCE.bus().register(this);

        startTask = new StartTask();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            startTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            startTask.execute((Void[]) null);
    }

    @Override
    public void onDestroy() {
        BusProvider.INSTANCE.bus().unregister(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // is executor alive?
    static boolean isRunning() {
        return !(null == executorService || executorService.isShutdown() || executorService.isTerminated());
    }

    // is service alive?
    static boolean isStarted() {
        return (null != srv);
    }

    static void startSpawner() {
        if (isRunning())
            return;
        if (isWorking) {
            BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("wait until previous task completed"));
            BusProvider.INSTANCE.bus().post(new MessageTypes.RamStatus(""));
            return;
        }

        Log.d(TAG, "startSpawner");
        startTask = new StartTask();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            startTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            startTask.execute((Void[]) null);
    }

    static void stopSpawner() {
        if (!isRunning())
            return;
        if (isWorking) {
            BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("wait until previous task completed"));
            BusProvider.INSTANCE.bus().post(new MessageTypes.RamStatus(""));
            return;
        }

        Log.d(TAG, "stopSpawner");
        killTask = new KillTask();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            killTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            killTask.execute((Void[]) null);
    }

    static void killSpawner() {
        srv.stopSelf();
    }

    static void switchModes(Constants mode) {
        if (isWorking) {
            BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("wait until previous task completed"));
            BusProvider.INSTANCE.bus().post(new MessageTypes.RamStatus(""));
            return;
        }
        if (Constants.MODE == mode) {
            BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("already in that mode, ignoring"));
            BusProvider.INSTANCE.bus().post(new MessageTypes.RamStatus(""));
            return;
        }

        SwitchModes switchMode = new SwitchModes();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            switchMode.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mode);
        else
            switchMode.execute(mode);
    }

    static void enqueue() {
        if (isRunning())
            return;

        if (!isRunning())
            executorService
                    = new ThreadPoolExecutor(Constants.MODE.NUM_THREADS, Constants.MODE.NUM_THREADS, 30, TimeUnit.SECONDS, writerQueue, new ThreadPoolExecutor.CallerRunsPolicy());
        else if (!executorService.isShutdown()) {
            Log.e(TAG, "This should never happen, enqueue called with an active executorService");
            return;
        }
        BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("starting..."));

        BusProvider.INSTANCE.bus().post(new MessageTypes.RunningStatus());

        running = true;
        for (int i = 0; i < Constants.MODE.NUM_CPU_THREADS; i++) {
            Log.d(TAG, "enqueue #" + i);
            Future<?> task = executorService.submit(new PiCalc.CalcTask());
            tasks.put(i, task);
        }
        for (int i = Constants.MODE.NUM_CPU_THREADS; i < (Constants.MODE.NUM_CPU_THREADS + Constants.MODE.NUM_RAM_THREADS); i++) {
            Log.d(TAG, "enqueue #" + i);
            Future<?> task = executorService.submit(new ImgLoader.ImgLoaderTask());
            tasks.put(i, task);
        }

    }

    static void killSwitch() {
        Log.w(TAG, "killing all");
        running = false;
        BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus("killing..."));

        removeAllTasks();
        if (null != killer)
            killer.cancel(true);
        if (null != watcher)
            watcher.cancel(true);

        if (null != executorService) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(20, TimeUnit.SECONDS); // or what ever
            } catch (InterruptedException e) {
            }
            executorService.shutdownNow();
        }

        BusProvider.INSTANCE.bus().post(new MessageTypes.CpuStatus(""));
        BusProvider.INSTANCE.bus().post(new MessageTypes.RunningStatus());
    }

    private static void removeAllTasks() {
        if (null == tasks)
            return;

        for (Integer i : tasks.keySet()) {
            Future<?> task = tasks.get(i);
            if (null != task)
                task.cancel(true);
            tasks.remove(i);
        }
    }

    static class SwitchModes extends AsyncTask<Constants, Void, Void> {
        @Override
        protected Void doInBackground(Constants... params) {
            Log.w(TAG, "StartTask");
            if (null == params[0])
                return null;

            isWorking = true;
            Constants.MODE = params[0];
            if (null != killer)
                killer.cancel(true);
            if (null != watcher)
                watcher.cancel(true);

            killSwitch();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            enqueue();

            isWorking = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            killer = new TimedKillTask();
            watcher = new Watcher();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                killer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                watcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            } else {
                killer.execute((Void[]) null);
                watcher.execute((Void[]) null);
            }
        }

    }

    static class StartTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.w(TAG, "StartTask");
            isWorking = true;
            enqueue();
            isWorking = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            killer = new TimedKillTask();
            watcher = new Watcher();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                killer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                watcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            } else {
                killer.execute((Void[]) null);
                watcher.execute((Void[]) null);
            }
        }
    }

    static class KillTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.w(TAG, "KillTask");
            isWorking = true;
            killSwitch();
            isWorking = false;
            return null;
        }
    }

    static class Watcher extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            while (!tasks.isEmpty()) {

                if (Thread.currentThread().isInterrupted()) {
                    running = false;
                }

                for (Integer i : tasks.keySet()) {
                    if (!running) {
                        killSwitch();
                        return null;
                    }

                    Future<?> task = tasks.get(i);
                    try {
                        if (null != task.get())
                            continue;

                        if (i < Constants.MODE.NUM_CPU_THREADS) {
                            Future<?> newTask = executorService.submit(new PiCalc.CalcTask());
                            tasks.put(i, newTask);
                        } else if (i < Constants.MODE.NUM_CPU_THREADS + Constants.MODE.NUM_RAM_THREADS) {
                            Future<?> newTask = executorService.submit(new ImgLoader.ImgLoaderTask());
                            tasks.put(i, newTask);
                        } else {
                            tasks.remove(i);
                        }
                    } catch (InterruptedException e) {
                        killSwitch();
                        return null;
                    } catch (CancellationException e) {
                        killSwitch();
                        return null;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }
            killSwitch();
            return null;
        }
    }

    static class TimedKillTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (!Constants.TIMED_KILL)
                return null;
            try {
                Thread.sleep(Constants.KEEP_ALIVE);
            } catch (InterruptedException e) {
            }
            running = false;
            return null;
        }
    }
}
