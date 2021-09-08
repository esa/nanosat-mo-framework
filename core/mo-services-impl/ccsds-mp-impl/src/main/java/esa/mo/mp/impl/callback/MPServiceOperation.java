package esa.mo.mp.impl.callback;

/**
 * MPServiceOperation holds operation names, that can be used to create callbacks
 *
 * @see MPServiceOperationManager
 */
public enum MPServiceOperation {

    /**
     * Plan Information Management Service
     */

    // Request definition CRUD operations
    ADD_REQUEST_DEF,
    UPDATE_REQUEST_DEF,
    // LIST_REQUEST_DEF,     // Not used
    // GET_REQUEST_DEF,      // Not used
    REMOVE_REQUEST_DEF,

    // Activity definition CRUD operations
    ADD_ACTIVITY_DEF,
    UPDATE_ACTIVITY_DEF,
    // LIST_ACTIVITY_DEF,    // Not used
    // GET_ACTIVITY_DEF,     // Not used
    REMOVE_ACTIVITY_DEF,

    // Event definition CRUD operations
    ADD_EVENT_DEF,
    UPDATE_EVENT_DEF,
    // LIST_EVENT_DEF,       // Not used
    // GET_EVENT_DEF,        // Not used
    REMOVE_EVENT_DEF,

    // Resource definition CRUD operations
    ADD_RESOURCE_DEF,
    UPDATE_RESOURCE_DEF,
    // LIST_RESOURCE_DEF,    // Not used
    // GET_RESOURCE_DEF,     // Not used
    REMOVE_RESOURCE_DEF,

    /**
     * Planning Request Service
     */

    SUBMIT_REQUEST,
    UPDATE_REQUEST,
    CANCEL_REQUEST,
    // GET_REQUEST,          // Not used
    // GET_REQUEST_STATUS,   // Not used
    // MONITOR_REQUESTS,     // Not used

    /**
     * Plan Distribution Service
     */

     // LIST_PLANS,          // Not used
     // GET_PLAN,            // Not used
     // GET_PLAN_STATUS,     // Not used
     // MONITOR_PLAN,        // Not used
     // MONITOR_PLAN_STATUS, // Not used
     // QUERY_PLAN,          // Not used

    /**
     * Plan Edit Service
     */

    INSERT_ACTIVITY,
    UPDATE_ACTIVITY,
    DELETE_ACTIVITY,
    INSERT_EVENT,
    UPDATE_EVENT,
    DELETE_EVENT,
    UPDATE_PLAN_STATUS,

     /**
      * Plan Execution Control Service
      */
    SUBMIT_PLAN
}
