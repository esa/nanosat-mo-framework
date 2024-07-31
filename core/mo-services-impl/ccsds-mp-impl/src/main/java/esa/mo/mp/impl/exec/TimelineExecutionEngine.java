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
package esa.mo.mp.impl.exec;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import esa.mo.helpertools.clock.SystemClock;

/**
 * TimelineExecutionEngine polls every tickInterval for any items to execute from submitted timeline.
 */
public class TimelineExecutionEngine {

    private static final Logger LOGGER = Logger.getLogger(TimelineExecutionEngine.class.getName());

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;

    private int tickInterval = 1000; // In milliseconds
    private TimelineExecutionCallback callback;
    private Deque<TimelineItem> timelineStack;

    public TimelineExecutionEngine() {
        super();
    }

    /**
     * Submits a timeline to be executed
     * @param timeline to be executed
     */
    public void submitTimeline(List<TimelineItem> timeline) {
        TimelineItem[] sortedTimeline = timeline.toArray(new TimelineItem[0]);
        Arrays.sort(sortedTimeline);
        Deque<TimelineItem> stack = new ArrayDeque<>();
        for (TimelineItem item : sortedTimeline) {
            stack.push(item);
        }

        if (stack.isEmpty()) {
            LOGGER.warning("Empty timeline submitted for execution");
        }

        this.timelineStack = stack;
        LOGGER.info(getFormattedTimeline());
    }

    /**
     * Sets a callback for execution start, stop, finish and error
     */
    public void setCallback(TimelineExecutionCallback callback) {
        this.callback = callback;
    }

    /**
     * Starts timeline execution engine that does not block executing thread
     */
    public void start() {
        this.schedule();
        this.onStart();
    }

    /**
     * Starts timeline execution engine that blocks executing thread until timeline execution has finished or stopped
     */
    public void startBlocking() throws ExecutionException {
        this.schedule();
        this.onStart();
        try {
            this.scheduledFuture.get();
        } catch (InterruptedException e) {
            LOGGER.info("Timeline execution interrupted");
        } catch (CancellationException e) {
            LOGGER.info("Timeline execution cancelled");
        }
    }

    /**
     * Stops timeline execution engine when it is still running
     */
    public void stop() {
        if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
            this.scheduledFuture.cancel(false);
            onStop();
        }
    }

    /**
     * Sets desired polling rate for engine
     */
    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    /**
     * Gets submitted timeline in formatted string
     * @return formatted timeline
     */
    public String getFormattedTimeline() {
        String lineSeparator = System.lineSeparator();
        String timelineSeparator = String.join("", Collections.nCopies(80, "-"));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Submitted timeline");
        stringBuilder.append(lineSeparator);
        stringBuilder.append(timelineSeparator);
        stringBuilder.append(lineSeparator);
        Iterator<TimelineItem> iterator = this.timelineStack.iterator();
        while (iterator.hasNext()) {
            TimelineItem item = iterator.next();
            stringBuilder.append(item.toString());
            stringBuilder.append(lineSeparator);
        }
        stringBuilder.append(timelineSeparator);
        return stringBuilder.toString();
    }

    private void schedule() {
        if (this.scheduler != null && !this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
        }
        this.scheduler = Executors.newScheduledThreadPool(1);

        if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
            this.scheduledFuture.cancel(false);
        }

        this.scheduledFuture = this.scheduler.scheduleAtFixedRate(() -> {
            try {
                tick();
            } catch (Throwable t) {
                this.onError(t);
            }
        }, 0, this.tickInterval, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        long timestamp = SystemClock.getTime().getValue();
        // Log tick timestamps for easier debugging
        // LOGGER.info("Timestamp: " + HelperTime.time2readableString(new Time(timestamp)));

        if (timelineStack.isEmpty()) {
            finish();
            return;
        }

        while (timelineStack.peek() != null && timelineStack.peek().getLatestStartTime() < timestamp) {
            // Item latest start time has passed
            TimelineItem item = timelineStack.pop();
            if (item.getLatestStartTime() + tickInterval >= timestamp) {
                // If it passed less than tickInterval ago, still execute the item
                LOGGER.info("Executing item " + item.getItemId());
                item.getCallback().execute();
            } else {
                // If it passed more then tickInterval ago, call missed
                LOGGER.info("Missed item " + item.getItemId());
                item.getCallback().missed();
            }
        }
        while (timelineStack.peek() != null && timelineStack.peek().getEarliestStartTime() < timestamp) {
            TimelineItem item = timelineStack.pop();
            LOGGER.info("Executing item " + item.getItemId());
            item.getCallback().execute();
        }

        if (timelineStack.isEmpty()) {
            finish();
        }
    }

    private void finish() {
        LOGGER.info("Timeline finished");
        if (this.scheduler != null) {
            this.scheduler.shutdownNow();
        }
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }
        this.onFinish();
    }

    private void onStart() {
        if (this.callback != null) {
            this.callback.onStart();
        }
    }

    private void onStop() {
        if (this.callback != null) {
            this.callback.onStop();
        }
    }

    private void onFinish() {
        if (this.callback != null) {
            this.callback.onFinish();
        }
    }

    private void onError(Throwable t) {
        if (this.callback != null) {
            this.callback.onError(t);
        }
        LOGGER.log(Level.SEVERE, null, t);
    }
}
