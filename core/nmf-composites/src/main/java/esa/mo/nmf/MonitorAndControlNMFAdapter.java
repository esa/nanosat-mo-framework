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
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
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
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
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
    protected ParameterProviderServiceImpl parameterService;

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
        IdentifierList parameterNames = new IdentifierList();
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
            if (annotation.name().equals("")) {
                // if name is not set, use variable name
                parameterNames.add(new Identifier(field.getName()));
                LOGGER.log(Level.INFO, "Parameter registered: {0}", field.getName());
            } else {
                parameterNames.add(new Identifier(annotation.name()));
                LOGGER.log(Level.INFO, "Parameter registered: {0}", annotation.name());
            }

            //----------------collect ParameterDefinition----------------
            String description = annotation.description();
            int rawType;
            if (annotation.malType().equals("")) {
                try {
                    Object att = HelperAttributes.javaType2Attribute(field.get(this));
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
            boolean generationEnabled = annotation.generationEnabled();
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

            definitions.add(new ParameterDefinition(description, new AttributeType(rawType), rawUnit,
                    generationEnabled, reportInterval, validityExpression, conversion));
        }

        if (!parameterNames.isEmpty()) {
            HashMap<String, LongList> aggregationMapping = new HashMap<>();
            LongList idList = registration.registerParameters(parameterNames, definitions);

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

                IdentifierList aggregationNames = new IdentifierList();
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
                                aggregation.description(), new AggregationCategory(aggregation.category()),
                                new Duration(aggregation.reportInterval()), aggregation.sendUnchanged(),
                                aggregation.sendDefinitions(), aggregation.filterEnabled(),
                                new Duration(aggregation.filterTimeout()),
                                aggregation.generationEnabled(), parameterSet);

                        LOGGER.log(Level.INFO, "Aggregation registered: {0}", aggregation.id());

                        aggregationNames.add(new Identifier(aggregation.id()));
                        aggregationDetails.add(aggregationDetail);

                    } else {
                        LOGGER.log(Level.SEVERE, "There are no Parameters assigned to "
                                + "Aggregation {0}! The Aggregation therefore will not be created!",
                                aggregation.id());
                    }
                }

                registration.registerAggregations(aggregationNames, aggregationDetails);
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
        if (archiveService != null && parameterService != null) {
            for (Map.Entry<Long, Field> entry : parameterMapping.entrySet()) {
                Field field = entry.getValue();
                String parameterName = null;
                Parameter annotation = field.getAnnotation(Parameter.class);
                if (annotation != null) {
                    if (!annotation.restored()) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (annotation.name().equals("")) {
                        parameterName = field.getName();
                    } else {
                        parameterName = annotation.name();
                    }
                }

                if (parameterName == null) {
                    continue;
                }

                IdentifierList names = new IdentifierList();
                names.add(new Identifier(parameterName));
                try {
                    ObjectInstancePairList ids = parameterService.listDefinition(names, null);
                    ObjectInstancePair id = ids.get(0);
                    ArchiveQuery query = new ArchiveQuery(
                            archiveService.getConnection().getConnectionDetails().getDomain(),
                            null, null, id.getObjDefInstanceId(), null, null,
                            FineTime.now(), false, null);
                    PaginationFilter filter = new PaginationFilter(new UInteger(1), new UInteger(0));
                    List<ArchivePersistenceObject> result = archiveService.getArchiveManager().query(
                            ParameterServiceInfo.PARAMETERVALUEINSTANCE_OBJECT_TYPE, query, filter);
                    if (!result.isEmpty()) {
                        // assume the first one is the newest because of the query sort order
                        ArchivePersistenceObject newestParameter = result.get(0);
                        Attribute rawValue = ((ParameterValue) newestParameter.getObject()).getRawValue();
                        Object value;
                        if (field.getType() == double.class) {
                            value = HelperAttributes.attribute2double(rawValue);
                        } else if (field.getType() == String.class) {
                            value = HelperAttributes.attribute2string(rawValue);
                        } else {
                            value = HelperAttributes.attribute2JavaType(rawValue);
                        }
                        field.set(this, value);
                    }
                } catch (MALException | MALInteractionException | IllegalAccessException e) {
                    LOGGER.log(Level.SEVERE, "Error during restoring parameter value!", e);
                }
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
        IdentifierList actionNames = new IdentifierList();
        LinkedList<Method> actionFunctions = new LinkedList<>();

        // get all methods
        Method[] methods = this.getClass().getDeclaredMethods();
        LOGGER.log(Level.INFO, "Registering Actions");
        for (Method method : methods) {
            Action annotation = method.getAnnotation(Action.class);
            // if field has Parameter anotation
            if (annotation != null) {
                method.setAccessible(true);
                // check if Long actionInstanceObjId, boolean reportProgress,
                // MALInteraction interaction is implemented. If not, don't parse the action
                java.lang.reflect.Parameter actionInstanceObjId = method.getParameters()[0];
                java.lang.reflect.Parameter reportProgress = method.getParameters()[1];
                java.lang.reflect.Parameter interaction = method.getParameters()[2];
                if (!actionInstanceObjId.getType().equals(Long.class)) {
                    LOGGER.log(Level.SEVERE,
                            "Unable to parse action! First argument of action has to be Long actionInstanceObjId!");
                    continue;
                }
                if (!reportProgress.getType().equals(boolean.class)) {
                    LOGGER.log(Level.SEVERE,
                            "Unable to parse action! Second argument of action has to be boolean reportProgress!");
                    continue;
                }
                if (!interaction.getType().equals(MALInteraction.class)) {
                    LOGGER.log(Level.SEVERE,
                            "Unable to parse action! Third argument of action has to be MALInteraction interaction!");
                    continue;
                }

                actionFunctions.add(method);

                ArgumentDefinitionList arguments = new ArgumentDefinitionList();

                java.lang.reflect.Parameter[] parameters = Arrays.copyOfRange(
                        method.getParameters(), 3, method.getParameters().length);
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
                Identifier actionId = new Identifier(acName);

                LOGGER.log(Level.INFO, "Action registered: {0}", actionId);
                actionNames.add(actionId);
                actionDefs.add(new ActionDefinition(annotation.description(),
                        new ActionCategory(annotation.category()),
                        new UShort(annotation.stepCount()), arguments));
            }
        }

        if (!actionNames.isEmpty()) {
            LongList idList = registration.registerActions(actionNames, actionDefs);

            // save mapping (id -> Field) in map
            for (int i = 0; i < idList.size(); i++) {
                actionMapping.put(idList.get(i), actionFunctions.get(i));
                actionNameMapping.put(actionNames.get(i).getValue(), idList.get(i));
            }
        }
    }

    @Override
    public UInteger actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        Method actionMethod = actionMapping.get(actionNameMapping.get(identifier.getValue()));
        try {
            // add default arguments
            Object[] arguments = new Object[attributeValues.size() + 3];
            arguments[0] = actionInstanceObjId;
            arguments[1] = reportProgress;
            arguments[2] = interaction;

            // add custom arguments
            int i = 3;
            for (AttributeValue attribute : attributeValues) {
                Class type = actionMethod.getParameters()[i].getType();
                if (type == double.class) {
                    arguments[i] = HelperAttributes.attribute2double(attribute.getValue());
                } else if (type == String.class) {
                    arguments[i] = HelperAttributes.attribute2string(attribute.getValue());
                } else {
                    arguments[i] = HelperAttributes.attribute2JavaType(attribute.getValue());
                }
                i++;
            }

            Object result = actionMethod.invoke(this, arguments);
            return (result == null) ? null : (UInteger) result;
        } catch (IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, "Cannot access Method! {0}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Arguments for action are incorrect! {0}", ex.getMessage());
        } catch (InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, "The action Method threw an invocation exception! {0}",
                    (ex.getMessage() != null ? ex.getMessage() : ex.getTargetException().getMessage()));
        }
        return new UInteger(0);
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
            return (Attribute) HelperAttributes.javaType2Attribute(field.get(this));
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

    public Boolean onSetValue(ParameterRawValue newRawValue) {
        Object value;
        if (isReadOnly(newRawValue.getParamInstId())) {
            return false;
        }
        Field param = parameterMapping.get(newRawValue.getParamInstId());

        if (param == null) {
            return false;
        }

        if (param.getType() == double.class) {
            value = HelperAttributes.attribute2double(newRawValue.getRawValue());
        } else if (param.getType() == String.class) {
            value = HelperAttributes.attribute2string(newRawValue.getRawValue());
        } else {
            value = HelperAttributes.attribute2JavaType(newRawValue.getRawValue());
        }
        try {
            param.set(this, value);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean isReadOnly(Long parameterID) {
        Field field = parameterMapping.get(parameterID);
        if (field == null) {
            return false;
        }
        return field.getAnnotation(Parameter.class).readOnly()
                || (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
    }

    @Override
    public boolean isReadOnly(Identifier name) {
        return false;
    }

    @Override
    public boolean preCheck(ActionDefinition defDetails,
            ActionInstanceDetails instDetails, UIntegerList errorList) {
        return true;
    }

    @Override
    public ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinition pDef) {
        return null; // Return null to work normally...
    }

    private Integer getTypeShortForm(Class<?> type) {
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
