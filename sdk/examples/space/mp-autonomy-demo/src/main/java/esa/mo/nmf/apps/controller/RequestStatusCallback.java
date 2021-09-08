package esa.mo.nmf.apps.controller;

import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;

/**
 * RequestStatusCallback is called by RequestStatusMonitor when a Request status is updated
 */
public abstract class RequestStatusCallback {
    public void requested(RequestUpdateDetails update) {}
}
