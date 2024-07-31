/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.helpertools.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class wraps the functionality of the ScheduledThreadPoolExecutor.
 * @author Yannick Lavan
 */
public class TaskScheduler {
    private final HashMap<Integer, ScheduledFuture<?>> scheduled;
    private int nextId;
    private final ScheduledThreadPoolExecutor scheduler;
    private final ArrayList<Integer> ids;
    private boolean daemon;

    public TaskScheduler(int corePoolSize) {
        scheduler = new ScheduledThreadPoolExecutor(corePoolSize);
        scheduled = new HashMap<>();
        ids = new ArrayList<>();
        nextId = 0;
        daemon = false;
    }

    public TaskScheduler(int corePoolSize, boolean isDaemon) {
        scheduler = new ScheduledThreadPoolExecutor(corePoolSize);
        scheduled = new HashMap<>();
        ids = new ArrayList<>();
        nextId = 0;
        daemon = isDaemon;
    }

    /**
     * This method wraps the methods scheduleAtFixedRate and scheduleWithFixedDelay.
     * @param command The command (e.g. a Thread) to run.
     * @param startDelay The delay until the task is executed for the first time.
     * @param taskDelay The delay between starts of tasks for fixedRate == true and the delay between
     * the end of the prior execution and the start of the next one, otherwise.
     * @param unit The unit used for startDelay and taskDelay.
     * @param fixedRate Determines to use scheduleAtFixedRate when set to true, and
     * scheduleWithFixedDelay otherwise.
     * @return The ID of the scheduled task.
     * @throws java.lang.IllegalArgumentException If command == null OR unit == null.
     * @see ScheduledThreadPoolExecutor#scheduleAtFixedRate(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit)
     * @see ScheduledThreadPoolExecutor#scheduleWithFixedDelay(java.lang.Runnable, long, long, java.util.concurrent.TimeUnit) 
     */
    public int scheduleTask(Thread command, long startDelay, long taskDelay, TimeUnit unit, boolean fixedRate)
        throws IllegalArgumentException {
        if (command == null || unit == null) {
            throw new IllegalArgumentException("The provided thread must not be null.");
        }
        command.setDaemon(daemon);
        ScheduledFuture<?> task;
        if (fixedRate) {
            task = this.scheduler.scheduleAtFixedRate(command, startDelay, taskDelay, unit);
        } else {
            task = this.scheduler.scheduleWithFixedDelay(command, startDelay, taskDelay, unit);
        }
        scheduled.put(nextId, task);
        ids.add(nextId);
        return nextId++;
    }

    /**
     * Immediately stops the task with the latest used ID. If there are no scheduled tasks, nothing happens.
     */
    public void stopLast() {
        if (!scheduled.isEmpty()) {
            int last = ids.size() - 1;
            int index = ids.get(last);
            scheduled.get(index).cancel(true);
            scheduled.remove(index);
            ids.remove(last);
        }
    }

    /**
     * Immediately stops the task with the provided ID. If there are no scheduled tasks matching the id,
     * nothing happens.
     * @param id The ID of the task which shall be canceled. The ID is obtained when scheduleTask is called.
     */
    public void stopTask(int id) {
        if (id >= 0 && id < nextId && scheduled.containsKey(id)) {
            scheduled.get(id).cancel(true);
            scheduled.remove(id);
            ids.remove(id);
        }
    }

    /**
     * All running threads of this scheduler are stopped and dequeued. The scheduler is reset to its
     * initial state.
     */
    public void resetScheduler() {
        Set<Entry<Integer, ScheduledFuture<?>>> entries = scheduled.entrySet();
        for (Entry<Integer, ScheduledFuture<?>> e : entries) {
            e.getValue().cancel(true);
        }
        scheduled.clear();
        ids.clear();
        nextId = 0;
        daemon = false;
    }

}
