package esa.mo.mp.impl.exec;

/**
 * TimelineExecutionCallback is called by TimelineExecutionEngine when execution starts, stops, finishes or catches an error
 */
public interface TimelineExecutionCallback {
    public default void onStart() {}
    public default void onStop() {}
    public default void onFinish() {}
    public default void onError(Throwable t) {}
}
