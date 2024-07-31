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
