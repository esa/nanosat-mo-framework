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
package esa.mo.nmf;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ActionNotFoundException;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Aggregation;
import esa.mo.nmf.annotations.Aggregations;
import esa.mo.nmf.annotations.Parameter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.PaginationFilter;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The MonitorAndControlAdapter implements the Monitor and Control interfaces
 * composed by the ActionInvocationListener interface and the
 * ParameterStatusListener interface.
 *
 */
public abstract class MonitorAndControlNMFAdapter implements ActionInvocationListener, ParameterStatusListener {

    private static final Logger LOGGER = Logger.getLogger(MonitorAndControlNMFAdapter.class.getName());
    private final HashMap<Long, Field> parameterMapping = new HashMap<>();
    private final HashMap<Long, Method> actionMapping = new HashMap<>();
    private final HashMap<String, Long> actionNameMapping = new HashMap<>();

    private ArchiveProviderServiceImpl archiveService;
    /** The Parameter service used to push parameter values; set during registration. */
    protected ParameterProviderServiceImpl parameterService;

    /**
     * Default constructor.
     */
    public MonitorAndControlNMFAdapter() {
    }

    /**
     * Registers the parameters and actions declared by this adapter in the Monitor and
     * Control services. Called once by the NMF when the provider starts.
     *
     * @param registration the registration object bound to the M&amp;C services
     */
    public void initialRegistrations(MCRegistration registration) {
        // Prevent definition updates on consecutive application runs
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);
        registerParameters(registration);
        registerActions(registration);
        if (registration.comServices != null) {
            archiveService = registration.comServices.getArchiveService();
            parameterService = registration.parameterService;
        }
    }

    /**
     * Registers a Parameter for every field with the @Parameter annotation
     *
     * @param registration
     */
    private void registerParameters(MCRegistration registration) {
        LOGGER.log(Level.INFO, "Registering Parameters:");
        ParameterDefinitionList definitions = new ParameterDefinitionList();
        LinkedList<Field> parameters = new LinkedList<>();

        // get all fields
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            Parameter annotation = field.getAnnotation(Parameter.class);
            // if field has Parameter anotation
            if (annotation == null) {
                continue;
            }

            field.setAccessible(true);
            parameters.add(field);
            // if name is not set, use variable name
            String name = annotation.name().equals("") ? field.getName() : annotation.name();

            //----------------collect ParameterDefinition----------------
            String description = annotation.description();
            int rawType;
            if (annotation.malType().equals("")) {
                try {
                    Object att = Attribute.javaType2Attribute(field.get(this));
                    rawType = (Integer) ((Attribute) att).getTypeId().getSFP();
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, "Unable to register parameter! "
                            + "Please try setting malType in @Parameter. {0}", ex);
                    continue;
                }
            } else {
                rawType = HelperAttributes.attributeName2typeShortForm(annotation.malType());
            }

            String rawUnit = annotation.rawUnit();
            boolean reportingEnabled = annotation.reportingEnabled();
            Duration reportInterval = new Duration(annotation.reportIntervalSeconds());
            ParameterExpression validityExpression = null;
            String valExpress = annotation.validityExpressionFieldName();
            if (!valExpress.equals("")) {
                try {
                    Field validityField = this.getClass().getField(valExpress);
                    validityField.setAccessible(true);
                    validityExpression = (ParameterExpression) validityField.get(this);
                } catch (NoSuchFieldException
                        | SecurityException
                        | IllegalArgumentException
                        | IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, "Unable to parse validityExpression Field!"
                            + " (fallback to null) {0}", ex);
                }
            }

            ParameterConversion conversion = null;
            String convFun = annotation.conversionFunctionName();
            if (!convFun.equals("")) {
                try {
                    Field conversionField = this.getClass().getField(convFun);
                    conversionField.setAccessible(true);
                    conversion = (ParameterConversion) conversionField.get(this);
                } catch (NoSuchFieldException
                        | SecurityException
                        | IllegalArgumentException
                        | IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, "Unable to parse conversion Field!"
                            + " (fallback to null) {0}", ex);
                }
            }

            boolean readOnly = annotation.readOnly()
                    || (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
            definitions.add(new ParameterDefinition(new Identifier(name),
                    description, new AttributeType(rawType), rawUnit,
                    reportingEnabled, reportInterval, validityExpression, conversion, readOnly));
        }

        if (!definitions.isEmpty()) {
            HashMap<String, LongList> aggregationMapping = new HashMap<>();
            LongList idList = registration.registerParameters(definitions);

            // save mapping (id -> Field) in map
            for (int i = 0; i < idList.size(); i++) {
                parameterMapping.put(idList.get(i), parameters.get(i));
                for (String aggregation : parameters.get(i).getAnnotation(Parameter.class).aggregations()) {
                    if (!aggregationMapping.containsKey(aggregation)) {
                        aggregationMapping.put(aggregation, new LongList());
                    }
                    aggregationMapping.get(aggregation).add(idList.get(i));
                }
            }

            //--------------------- register Aggregations ---------------------
            LOGGER.log(Level.INFO, "Registering Aggregations:");

            Aggregations[] aggregationList = this.getClass().getAnnotationsByType(Aggregations.class);
            if (aggregationList != null && aggregationList.length > 0) {
                Aggregation[] aggregations = aggregationList[0].value();

                AggregationDefinitionList aggregationDetails = new AggregationDefinitionList();

                for (Aggregation aggregation : aggregations) {
                    if (aggregationMapping.containsKey(aggregation.id())) {
                        LongList paramList = aggregationMapping.remove(aggregation.id());

                        AggregationParameterSetList parameterSet;

                        ThresholdFilter filter = null;
                        if (!aggregation.thresholdFilterFieldName().equals("")) {
                            try {
                                Field filterField = this.getClass().getField(aggregation.thresholdFilterFieldName());
                                filter = (ThresholdFilter) filterField.get(this);
                            } catch (NoSuchFieldException ex) {
                                LOGGER.log(Level.SEVERE, "Could not find Field \"{0}\". No filter has been added!",
                                        aggregation.thresholdFilterFieldName());
                            } catch (IllegalArgumentException | IllegalAccessException ex) {
                                LOGGER.log(Level.SEVERE, ex.getMessage());
                            }
                        }

                        parameterSet = new AggregationParameterSetList();
                        parameterSet.add(new AggregationParameterSet(null, paramList,
                                new Duration(aggregation.sampleInterval()), filter));

                        // Create the Aggregation
                        AggregationDefinition aggregationDetail = new AggregationDefinition(
                                new Identifier(aggregation.id()),
                                aggregation.description(), new AggregationCategory(aggregation.category()),
                                new Duration(aggregation.reportInterval()), aggregation.sendUnchanged(),
                                aggregation.sendDefinitions(), aggregation.filterEnabled(),
                                new Duration(aggregation.filterTimeout()),
                                aggregation.reportingEnabled(), parameterSet);

                        LOGGER.log(Level.INFO, "Aggregation registered: {0}", aggregation.id());

                        aggregationDetails.add(aggregationDetail);

                    } else {
                        LOGGER.log(Level.SEVERE, "There are no Parameters assigned to "
                                + "Aggregation {0}! The Aggregation therefore will not be created!",
                                aggregation.id());
                    }
                }

                registration.registerAggregations(aggregationDetails);
            }
        }
    }

    /**
     * Restores parameters to their latest value stored in the archive. This
     * method is called by default on startup after parameters are registered.
     * To prevent parameters from being restored either override this method in
     * your MC Adapter or set the 'restored' flag to false in the @Parameter
     * annotation.
     *
     * For the parameter to be restored it has to be registered with the
     * "Parameter" annotation.
     */
    public void restoreParameterValuesFromArchive() {
        if (archiveService == null || parameterService == null) {
            return;
        }
        for (Map.Entry<Long, Field> entry : parameterMapping.entrySet()) {
            // The map key is already the parameter definition id, so there is no
            // need to resolve it again from the Parameter service.
            Long id = entry.getKey();
            Field field = entry.getValue();

            Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation == null || !annotation.restored()) {
                continue;
            }
            field.setAccessible(true);

            // Restore each parameter independently: a failure on one (missing
            // value, type mismatch, archive error) must not abort the restore
            // of the others, nor the app startup that triggers this.
            try {
                ArchiveQuery query = new ArchiveQuery(
                        archiveService.getConnection().getConnectionDetails().getDomain(),
                        null, id, null, null,
                        Time.now(), false, null);
                // sortOrder=false sorts by timestamp descending, so the single
                // returned object is the newest stored value for this parameter.
                PaginationFilter filter = new PaginationFilter(new UInteger(1), new UInteger(0));
                List<ArchivePersistenceObject> result = archiveService.getArchiveManager().query(
                        ParameterServiceInfo.PARAMETERVALUE_OBJECT_TYPE, query, filter);
                if (result.isEmpty()) {
                    continue; // No previously stored value; leave the default.
                }
                Attribute rawValue = ((ParameterValue) result.get(0).getObject()).getRawValue();
                Object value;
                if (field.getType() == double.class) {
                    value = Attribute.attribute2double(rawValue);
                } else if (field.getType() == String.class) {
                    value = Attribute.attribute2string(rawValue);
                } else {
                    value = Attribute.attribute2JavaType(rawValue);
                }
                field.set(this, value);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Could not restore parameter value for field ''{0}''", field.getName());
            }
        }
    }

    /**
     * Registers an Action for every function with the @Action annotation
     *
     * @param registration
     */
    private void registerActions(MCRegistration registration) {
        ActionDefinitionList actionDefs = new ActionDefinitionList();
        LinkedList<Method> actionFunctions = new LinkedList<>();

        // get all methods
        Method[] methods = this.getClass().getDeclaredMethods();
        LOGGER.log(Level.INFO, "Registering Actions");
        for (Method method : methods) {
            Action annotation = method.getAnnotation(Action.class);
            // if field has Parameter anotation
            if (annotation != null) {
                method.setAccessible(true);
                // check if Long executionId,
                // MALInteraction interaction is implemented. If not, don't parse the action
                java.lang.reflect.Parameter executionId = method.getParameters()[0];
                java.lang.reflect.Parameter interaction = method.getParameters()[1];
                if (!executionId.getType().equals(Long.class)) {
                    LOGGER.log(Level.SEVERE,
                            "Unable to parse argument! First argument of action has to be Long executionId!");
                    continue;
                }
                if (!interaction.getType().equals(MALInteraction.class)) {
                    LOGGER.log(Level.SEVERE,
                            "Unable to parse argument! Second argument of action has to be MALInteraction interaction!");
                    continue;
                }

                actionFunctions.add(method);

                ArgumentDefinitionList arguments = new ArgumentDefinitionList();

                java.lang.reflect.Parameter[] parameters = Arrays.copyOfRange(
                        method.getParameters(), 2, method.getParameters().length);
                for (java.lang.reflect.Parameter param : parameters) {
                    Identifier identifier = new Identifier(method.getName() + "_" + param.getName());
                    String description = null;
                    Integer rawType = getTypeShortForm(param.getType());
                    if (rawType == null) {
                        LOGGER.log(Level.SEVERE,
                                "Unable to register action Parameter of type {0}. Only MAL Types are allowed!",
                                param.getType().getSimpleName());
                        continue;
                    }
                    String rawUnit = "";
                    ActionParameter paramAnnotation = param.getAnnotation(ActionParameter.class);
                    if (paramAnnotation != null) {
                        if (!paramAnnotation.name().equals("")) { // if user given name exist, use it
                            identifier = new Identifier(paramAnnotation.name());
                        }
                        description = paramAnnotation.description();
                        if (paramAnnotation.rawType() != 0) {
                            rawType = paramAnnotation.rawType();
                        }

                        rawUnit = paramAnnotation.rawUnit();
                    }
                    arguments.add(new ArgumentDefinition(identifier, description,
                            new AttributeType(rawType), rawUnit));
                }

                // use fallback name if no name was given
                String acName = (annotation.name().equals("")) ? method.getName() : annotation.name();
                Identifier actionName = new Identifier(acName);

                LOGGER.log(Level.INFO, "Action registered: {0}", actionName);
                actionDefs.add(new ActionDefinition(actionName, annotation.description(),
                        new UShort(annotation.stepCount()), arguments));
            }
        }

        if (!actionDefs.isEmpty()) {
            LongList idList = registration.registerActions(actionDefs);

            // save mapping (id -> Field) in map
            for (int i = 0; i < idList.size(); i++) {
                actionMapping.put(idList.get(i), actionFunctions.get(i));
                actionNameMapping.put(actionDefs.get(i).getName().getValue(), idList.get(i));
            }
        }
    }

    @Override
    public void actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction)
            throws ExecutionFailedException, ActionNotFoundException {
        Method actionMethod = actionMapping.get(actionNameMapping.get(identifier.getValue()));
        if (actionMethod == null) {
            throw new ActionNotFoundException(identifier.getValue());
        }
        try {
            // add default arguments
            Object[] arguments = new Object[attributeValues.size() + 2];
            arguments[0] = executionId;
            arguments[1] = interaction;

            // add custom arguments
            int i = 2;
            for (AttributeValue attribute : attributeValues) {
                Class type = actionMethod.getParameters()[i].getType();
                if (type == double.class) {
                    arguments[i] = Attribute.attribute2double(attribute.getValue());
                } else if (type == String.class) {
                    arguments[i] = Attribute.attribute2string(attribute.getValue());
                } else {
                    arguments[i] = Attribute.attribute2JavaType(attribute.getValue());
                }
                i++;
            }

            actionMethod.invoke(this, arguments);
        } catch (IllegalAccessException ex) {
            throw new ExecutionFailedException("Cannot access action method: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ExecutionFailedException("Action arguments are incorrect: " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getTargetException();
            if (cause instanceof ExecutionFailedException) {
                throw (ExecutionFailedException) cause;
            }
            throw new ExecutionFailedException("Action method threw an exception: "
                    + (cause.getMessage() != null ? cause.getMessage() : cause.toString()));
        }
    }

    @Override
    public Attribute onGetValue(Long parameterID) throws IOException {
        Field field = parameterMapping.get(parameterID);
        if (field == null) {
            LOGGER.log(Level.SEVERE, "no parameter with ID {0} exists!", parameterID);
            return null;
        }

        Parameter param = field.getAnnotation(Parameter.class);
        if (param == null) {
            LOGGER.log(Level.SEVERE, "Parameter with ID {0} and name {1} is not Annotated!",
                    new Object[]{parameterID, field.getName()});
            return null;
        }

        try {
            String onGet = param.onGetFunction();
            if (!onGet.equals("")) {
                Method onGetMethod = this.getClass().getMethod(onGet);
                onGetMethod.setAccessible(true);
                onGetMethod.invoke(this);
            }
            return (Attribute) Attribute.javaType2Attribute(field.get(this));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new IOException("Unable to get Parameter Mapping");
        } catch (NoSuchMethodException | SecurityException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new IOException("Unable to call onGet Method");
        } catch (InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Boolean onSetValue(ParameterRawValueList newRawValues) {
        boolean result = true;
        for (ParameterRawValue newRawValue : newRawValues) {
            result = result && onSetValue(newRawValue);
        }
        return result;
    }

    /**
     * Sets a single parameter's raw value on the mapped annotated field.
     *
     * @param newRawValue the parameter id and its new raw value
     * @return {@code true} if the value was set; {@code false} if no field maps to the id
     */
    public Boolean onSetValue(ParameterRawValue newRawValue) {
        Object value;
        Field param = parameterMapping.get(newRawValue.getParameterId());

        if (param == null) {
            return false;
        }

        if (param.getType() == double.class) {
            value = Attribute.attribute2double(newRawValue.getRawValue());
        } else if (param.getType() == String.class) {
            value = Attribute.attribute2string(newRawValue.getRawValue());
        } else {
            value = Attribute.attribute2JavaType(newRawValue.getRawValue());
        }
        try {
            param.set(this, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Checks the java type and returns the equivalent MO type short form.
     * Deprecated because it should be moved to: HelperAttributes()
     *
     * @param type The java type.
     * @return The type short form in MO.
     */
    @Deprecated
    public static Integer getTypeShortForm(Class<?> type) {
        Integer helperValue = HelperAttributes.attributeName2typeShortForm(type.getSimpleName());
        if (helperValue != null) {
            return helperValue;
        }

        if (type.equals(boolean.class)) {
            return HelperAttributes.attributeName2typeShortForm("Boolean");
        } else if (type.equals(float.class)) {
            return HelperAttributes.attributeName2typeShortForm("Float");
        } else if (type.equals(double.class)) {
            return HelperAttributes.attributeName2typeShortForm("Double");
        } else if (type.equals(int.class)) {
            return HelperAttributes.attributeName2typeShortForm("Integer");
        } else if (type.equals(long.class)) {
            return HelperAttributes.attributeName2typeShortForm("Long");
        }
        return null;
    }

}
