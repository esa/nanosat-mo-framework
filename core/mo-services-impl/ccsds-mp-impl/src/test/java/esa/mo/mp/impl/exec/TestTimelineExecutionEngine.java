package esa.mo.mp.impl.exec;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.mockito.Mockito;
import esa.mo.helpertools.clock.SystemClock;

public class TestTimelineExecutionEngine {

    @Test
    public void testOnFinishCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);
        List<TimelineItem> timeline = new ArrayList<>();
        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow + 1000;
        long endWindow = timeNow + 2000;
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.start();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onStop();
        Mockito.verify(executionCallback, Mockito.times(0)).onError(any());
        Mockito.verify(itemCallback, Mockito.times(1)).execute();
        Mockito.verify(itemCallback, Mockito.times(0)).missed();
    }

    @Test
    public void testOnErrorCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);
        TestMockException exception = new TestMockException();
        Mockito.doAnswer(invocation -> {
            throw exception;
        }).when(itemCallback).execute();

        List<TimelineItem> timeline = new ArrayList<>();
        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow + 1000;
        long endWindow = timeNow + 2000;
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.start();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onStop();
        Mockito.verify(executionCallback, Mockito.times(1)).onError(exception);
        Mockito.verify(itemCallback, Mockito.times(1)).execute();
        Mockito.verify(itemCallback, Mockito.times(0)).missed();
    }

    @Test
    public void testOnStopCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);
        List<TimelineItem> timeline = new ArrayList<>();
        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow + 1000;
        long endWindow = timeNow + 2000;
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.start();

        engine.stop();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onStop();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(0)).onError(any());
    }

    @Test
    public void testItemExecuteCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow + 1000;
        long endWindow = timeNow + 2000;
        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);
        Mockito.doAnswer(invocation -> {
            long executionTime = SystemClock.getTime().getValue();
            assertTrue("Executed too early", executionTime > startWindow);
            assertTrue("Executed too late", executionTime < endWindow);
            return null;
        }).when(itemCallback).execute();

        List<TimelineItem> timeline = new ArrayList<>();
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.startBlocking();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onStop();
        Mockito.verify(executionCallback, Mockito.times(0)).onError(any());
        Mockito.verify(itemCallback, Mockito.times(1)).execute();
        Mockito.verify(itemCallback, Mockito.times(0)).missed();
    }

    @Test
    public void testInstantItemExecuteCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow + 2000;
        long endWindow = timeNow + 2000;
        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);

        List<TimelineItem> timeline = new ArrayList<>();
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.startBlocking();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onStop();
        Mockito.verify(executionCallback, Mockito.times(0)).onError(any());
        Mockito.verify(itemCallback, Mockito.times(1)).execute();
        Mockito.verify(itemCallback, Mockito.times(0)).missed();
    }

    @Test
    public void testItemMissedCallback() throws ExecutionException {
        TimelineExecutionCallback executionCallback = Mockito.mock(TimelineExecutionCallback.class);

        long timeNow = SystemClock.getTime().getValue();
        long startWindow = timeNow - 3000;
        long endWindow = timeNow - 2500;
        ItemCallback itemCallback = Mockito.mock(ItemCallback.class);
        List<TimelineItem> timeline = new ArrayList<>();
        timeline.add(new TimelineItem(startWindow, endWindow, "testItem", itemCallback));

        TimelineExecutionEngine engine = new TimelineExecutionEngine();
        engine.setTickInterval(1000);
        engine.setCallback(executionCallback);
        engine.submitTimeline(timeline);

        engine.startBlocking();

        Mockito.verify(executionCallback, Mockito.timeout(5000).times(1)).onFinish();
        Mockito.verify(executionCallback, Mockito.times(1)).onStart();
        Mockito.verify(executionCallback, Mockito.times(0)).onStop();
        Mockito.verify(executionCallback, Mockito.times(0)).onError(any());
        Mockito.verify(itemCallback, Mockito.times(1)).missed();
        Mockito.verify(itemCallback, Mockito.times(0)).execute();
    }
}

class TestMockException extends RuntimeException {
}
