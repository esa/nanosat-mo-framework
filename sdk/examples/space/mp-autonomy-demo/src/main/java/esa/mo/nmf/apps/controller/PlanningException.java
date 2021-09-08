package esa.mo.nmf.apps.controller;

/**
 * PlanningException is an exception class for planning specific exceptions
 */
public class PlanningException extends Exception {

    public PlanningException() {
        super();
    }

    public PlanningException(String message) {
        super(message);
    }

    public PlanningException(Throwable cause) {
        super(cause);
    }

    public PlanningException(String message, Throwable cause) {
        super(message, cause);
    }
}
