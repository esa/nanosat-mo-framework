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
    ADD_REQUEST_DEF, UPDATE_REQUEST_DEF,
    // LIST_REQUEST_DEF,     // Not used
    // GET_REQUEST_DEF,      // Not used
    REMOVE_REQUEST_DEF,

    // Activity definition CRUD operations
    ADD_ACTIVITY_DEF, UPDATE_ACTIVITY_DEF,
    // LIST_ACTIVITY_DEF,    // Not used
    // GET_ACTIVITY_DEF,     // Not used
    REMOVE_ACTIVITY_DEF,

    // Event definition CRUD operations
    ADD_EVENT_DEF, UPDATE_EVENT_DEF,
    // LIST_EVENT_DEF,       // Not used
    // GET_EVENT_DEF,        // Not used
    REMOVE_EVENT_DEF,

    // Resource definition CRUD operations
    ADD_RESOURCE_DEF, UPDATE_RESOURCE_DEF,
    // LIST_RESOURCE_DEF,    // Not used
    // GET_RESOURCE_DEF,     // Not used
    REMOVE_RESOURCE_DEF,

    /**
     * Planning Request Service
     */

    SUBMIT_REQUEST, UPDATE_REQUEST, CANCEL_REQUEST,
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

    INSERT_ACTIVITY, UPDATE_ACTIVITY, DELETE_ACTIVITY, INSERT_EVENT, UPDATE_EVENT, DELETE_EVENT, UPDATE_PLAN_STATUS,

    /**
     * Plan Execution Control Service
     */
    SUBMIT_PLAN
}
