package esa.mo.mp.impl.api;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.api.entity.ActivityArchiveManager;
import esa.mo.mp.impl.api.entity.EventArchiveManager;
import esa.mo.mp.impl.api.entity.PlanVersionArchiveManager;
import esa.mo.mp.impl.api.entity.RequestTemplateArchiveManager;
import esa.mo.mp.impl.api.entity.RequestVersionArchiveManager;
import esa.mo.mp.impl.api.entity.ResourceArchiveManager;

/**
 * Front-end to COM Archive. It is used by MP service default implementations and App developers.
 *
 */
public class MPArchiveManager {

    public final PlanVersionArchiveManager PLAN;
    public final RequestTemplateArchiveManager REQUEST_TEMPLATE;
    public final RequestVersionArchiveManager REQUEST_VERSION;
    public final ActivityArchiveManager ACTIVITY;
    public final EventArchiveManager EVENT;
    public final ResourceArchiveManager RESOURCE;

    public MPArchiveManager(COMServicesProvider comServices) {
        MPConfiguration configuration = new MPConfiguration();
        PLAN = new PlanVersionArchiveManager(comServices, configuration);
        REQUEST_TEMPLATE = new RequestTemplateArchiveManager(comServices, configuration);
        REQUEST_VERSION = new RequestVersionArchiveManager(comServices, configuration);
        ACTIVITY = new ActivityArchiveManager(comServices, configuration);
        EVENT = new EventArchiveManager(comServices, configuration);
        RESOURCE = new ResourceArchiveManager(comServices, configuration);
    }
}
