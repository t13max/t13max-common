package com.t13max.common.action;

import com.t13max.common.util.Log;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Action implements Runnable {

    protected ActionQueue queue;
    protected IJobName name;
    protected long createTime;

    public Action(ActionQueue queue, IJobName name) {
        this.queue = queue;
        this.name = name;
        this.createTime = System.currentTimeMillis();
    }

    public ActionQueue getActionQueue() {
        return queue;
    }

    public void checkIn() {
        this.queue.checkIn(this);
    }

    @Override
    public final void run() {
        try {
            if (runnable()) {
                exec();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.action.error("Execute exception: " + getClazz().getName(), e);
        } finally {
            queue.checkout(this);
            done();
        }
    }

    protected boolean runnable() {
        return true;
    }

    protected void done() {
        //do nothing
    }

    protected abstract void exec();

    protected Class<?> getClazz() {
        return this.getClass();
    }

    public final int waitingSize() {
        return queue.size();
    }

    @Override
    public String toString() {
        return getClazz().getName() + " [" + DateTimeFormatter.ofPattern("MM-dd HH:mm:ss").format(ZonedDateTime.now())
                + "]";
    }

}
