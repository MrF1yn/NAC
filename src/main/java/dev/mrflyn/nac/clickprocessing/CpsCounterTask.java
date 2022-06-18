package dev.mrflyn.nac.clickprocessing;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public class CpsCounterTask {
    private TimerTask task;
    private Timer timer;
    private ReentrantLock lock = new ReentrantLock();

    public CpsCounterTask() {
        timer = new Timer("CpsCounterGlobal");
    }

    public void start(){
        if (task!=null)task.cancel();
        task = null;
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    for (CpsCounter c : CpsCounter.cpsCounters.values()) {
                        if (c.isRunning())
                            c.calculate();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public void stop(){
        if (task!=null)task.cancel();
        task = null;
    }

}
