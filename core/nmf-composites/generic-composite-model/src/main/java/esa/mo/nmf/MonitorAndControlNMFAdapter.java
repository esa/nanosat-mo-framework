/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Parameter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

/**
 * The MonitorAndControlAdapter implements the Monitor and Control interfaces composed by the
 * ActionInvocationListener interface and the ParameterStatusListener interface.
 *
 */
public abstract class MonitorAndControlNMFAdapter implements ActionInvocationListener,
    ParameterStatusListener
{

  private final HashMap<Long, Field> parameterMapping = new HashMap<>();
  private final HashMap<Long, Method> actionMapping = new HashMap<>();

  public void initialRegistrations(MCRegistration registration)
  {
    // Prevent definition updates on consecutive application runs
    registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);
    registerParameters(registration);
    registerActions(registration);
  }

  /**
   * Registers a Parameter for every field with the @Parameter annotation
   *
   * @param registration
   */
  private void registerParameters(MCRegistration registration)
  {
    IdentifierList parameterNames = new IdentifierList();
    ParameterDefinitionDetailsList definitions = new ParameterDefinitionDetailsList();
    LinkedList<Field> parameters = new LinkedList<>();

    // get all fields
    Field[] fields = this.getClass().getDeclaredFields();

    for (Field field : fields) {

      Parameter annotation = field.getAnnotation(Parameter.class);
      // if field has Parameter anotation
      if (annotation != null) {
        field.setAccessible(true);
        parameters.add(field);
        if (annotation.name().equals("")) {
          // if name is not set, use variable name
          parameterNames.add(new Identifier(field.getName()));
          System.out.println(field.getName());
        } else {
          parameterNames.add(new Identifier(annotation.name()));
          System.out.println(annotation.name());
        }

        //-------------------------collect ParameterDefinitionDetails-------------------------------
        String description = annotation.description();
        byte rawType;
        if (annotation.malType().equals("")) {
          try {
            rawType = ((Attribute) HelperAttributes.javaType2Attribute(field.get(this)))
                .getTypeShortForm().byteValue();

          } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE,
                "Unable to register parameter! Please try setting malType in @Parameter. {0}", ex);
            break;
          }

        } else {
          rawType = HelperAttributes.attributeName2typeShortForm(annotation.malType()).byteValue();
        }

        String rawUnit = annotation.rawUnit();
        boolean generationEnabled = annotation.generationEnabled();
        Duration reportInterval = new Duration(annotation.reportIntervalSeconds());
        ParameterExpression validityExpression = null;
        if (!annotation.validityExpressionFieldName().equals("")) {
          try {
            Field validityField = this.getClass().getField(annotation.validityExpressionFieldName());
            validityField.setAccessible(true);
            validityExpression = (ParameterExpression) validityField.get(this);
          } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
              | IllegalAccessException ex) {
            Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE,
                "Unable to parse validityExpression Field! (fallback to null) {0}", ex);
          }
        }
        ParameterConversion conversion = null;
        if (!annotation.conversionFieldName().equals("")) {
          try {
            Field conversionField = this.getClass().getField(annotation.conversionFieldName());
            conversionField.setAccessible(true);
            conversion = (ParameterConversion) conversionField.get(this);
          } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
              | IllegalAccessException ex) {
            Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE,
                "Unable to parse conversion Field! (fallback to null) {0}", ex);
          }
        }
        //------------------------------------------------------------------------------------------

        definitions.add(new ParameterDefinitionDetails(description, rawType, rawUnit,
            generationEnabled, reportInterval, validityExpression, conversion));
      }
    }

    if (parameterNames.size() > 0) {
      LongList idList = registration.registerParameters(parameterNames, definitions);

      // save mapping (id -> Field) in map
      for (int i = 0; i < idList.size(); i++) {
        parameterMapping.put(idList.get(i), parameters.get(i));
      }
    }
  }

  /**
   * Registers an Action for every function with the @Action annotation
   *
   * @param registration
   */
  private void registerActions(MCRegistration registration)
  {
    ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
    IdentifierList actionNames = new IdentifierList();
    LinkedList<Method> actionFunctions = new LinkedList<>();

    // get all methods
    Method[] methods = this.getClass().getDeclaredMethods();
    for (Method method : methods) {
      Action annotation = method.getAnnotation(Action.class);
      // if field has Parameter anotation
      if (annotation != null) {
        // TODO check if Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction is implemented
        method.setAccessible(true);
        actionFunctions.add(method);

        ArgumentDefinitionDetailsList arguments = new ArgumentDefinitionDetailsList();

        for (java.lang.reflect.Parameter param : method.getParameters()) {
          Identifier identifier = new Identifier(param.getName());
          String description = null;
          byte rawType =
              ((Attribute) HelperAttributes.javaType2Attribute(param.getType().cast(null)))
                  .getTypeShortForm().byteValue();
          String rawUnit = "";
          ConditionalConversionList conditionalConversions = null;// TODO
          Byte convertedType = null;// TODO
          String convertedUnit = null;// TOOD
          ActionParameter paramAnnotation = param.getAnnotation(ActionParameter.class);
          if (paramAnnotation != null) {
            identifier = new Identifier(paramAnnotation.name());
            description = paramAnnotation.description();
            rawType = paramAnnotation.rawType();
            rawUnit = paramAnnotation.rawUnit();
          }

          arguments.add(new ArgumentDefinitionDetails(identifier, description,
              rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
        }

        actionNames.add(new Identifier(annotation.name()));
        actionDefs.add(new ActionDefinitionDetails(
            annotation.description(),
            new UOctet(annotation.category()),
            new UShort(annotation.stepCount()),
            arguments
        ));
      }
    }

    if (actionNames.size() > 0) {
      LongList idList = registration.registerActions(actionNames, actionDefs);

      // save mapping (id -> Field) in map
      for (int i = 0; i < idList.size(); i++) {
        actionMapping.put(idList.get(i), actionFunctions.get(i));
      }
    }
  }

  @Override
  public UInteger actionArrived(Identifier identifier, AttributeValueList attributeValues,
      Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction)
  {
    //TODO
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Attribute onGetValue(Long parameterID) throws IOException
  {
    try {
      Field field = parameterMapping.get(parameterID);
      String onGet = field.getAnnotation(Parameter.class).onGetFunction();
      if (!onGet.equals("")) {
        System.out.println(onGet);
        Method onGetMethod = this.getClass().getMethod(onGet);
        onGetMethod.setAccessible(true);
        onGetMethod.invoke(this);

      }
      return (Attribute) HelperAttributes.javaType2Attribute(field.get(
          this));
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE, null,
          ex);
      throw new IOException("Unable to get Parameter Mapping");
    } catch (NoSuchMethodException | SecurityException ex) {
      Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE, null,
          ex);
      throw new IOException("Unable to call onGet Method");
    } catch (InvocationTargetException ex) {
      Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE, null,
          ex);
    }
    return null;
  }

  @Override
  public Boolean onSetValue(ParameterRawValueList newRawValues)
  {
    boolean result = true;
    for (ParameterRawValue newRawValue : newRawValues) {

      Object value;
      if (isReadOnly(newRawValue.getParamInstId())) {
        result = false;
        continue;
      }
      Field param = parameterMapping.get(newRawValue.getParamInstId());

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
        Logger.getLogger(MonitorAndControlNMFAdapter.class.getName()).log(Level.SEVERE,
            null, ex);
        result = false;
      }
    }
    return result;
  }

  @Override
  public boolean isReadOnly(Long parameterID)
  {
    Field field = parameterMapping.get(parameterID);
    return field.getAnnotation(Parameter.class).readOnly() || (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
  }

  @Override
  public boolean isReadOnly(Identifier name)
  {
    return false;
  }

  @Override
  public boolean preCheck(ActionDefinitionDetails defDetails, ActionInstanceDetails instDetails,
      UIntegerList errorList)
  {
    return true;
  }

  @Override
  public ParameterValue getValueWithCustomValidityState(Attribute rawValue,
      ParameterDefinitionDetails pDef)
  {
    return null; // Return null to work normally...
  }

}
