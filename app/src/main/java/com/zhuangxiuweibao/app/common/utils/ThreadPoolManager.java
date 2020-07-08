package com.zhuangxiuweibao.app.common.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by haoruigang on 2018/4/13 9:55.
 * 实现高性能，高并发，可延时线程池管理
 */

public class ThreadPoolManager {

    private static ThreadPoolManager instance;

    //在服务器，或者多线程访问
    //服务器并发
    public static synchronized ThreadPoolManager getInstance() {
        //假设：3个线程－－－并发线程
        //场景：同时调用getInstace方法
        //线程一：
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    public ThreadPoolExecutor threadPoolExecutor;
    private LinkedBlockingQueue<Future<?>> service = new LinkedBlockingQueue<>();

    private ThreadPoolManager() {
        threadPoolExecutor = new ThreadPoolExecutor(4, 10, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4), handler);
        threadPoolExecutor.execute(runnable);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            while (true) {

                FutureTask futureTask = null;

                try {
                    Log.e("myThreadPook", "service size " + service.size());
                    futureTask = (FutureTask) service.take();
                    Log.e("myThreadPook", "池  " + threadPoolExecutor.getPoolSize());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (futureTask != null) {
                    threadPoolExecutor.execute(futureTask);
                }
            }
        }
    };

    public <T> void execute(final FutureTask<T> futureTask, Object delayed) {


        if (futureTask != null) {
            try {

                if (delayed != null) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            try {
                                service.put(futureTask);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }, (long) delayed);
                } else {
                    service.put(futureTask);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    private RejectedExecutionHandler handler = (runnable, threadPoolExecutor) -> {
        try {
            service.put(new FutureTask<Object>(runnable, null));//
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };


    public <T> void removeThread() {
        service.poll();
    }


}
