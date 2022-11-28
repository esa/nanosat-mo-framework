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
package esa.mo.nmf.cliconsumer;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationStub;
import org.ccsds.moims.mo.mc.aggregation.structures.*;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import picocli.CommandLine.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marcel.mikolajko
 */
public class MCCommands
{
    static Logger LOGGER = Logger.getLogger(MCCommands.class.getName());

    public static Identifier parameterSubscription;
    public static Identifier aggregationSubscription;

    @Command(name = "parameter",
             subcommands = {MCCommands.ParameterMonitorValue.class,
                            MCCommands.ParameterEnableGeneration.class,
                            MCCommands.ParameterDisableGeneration.class})
    public static class Parameter
    {

    }

    @Command(name = "aggregation",
            subcommands = {MCCommands.AggregationMonitorValue.class,
                           MCCommands.AggregationEnableGeneration.class,
                           MCCommands.AggregationDisableGeneration.class})
    public static class Aggregation
    {

    }

    public static void enableOrDisableParameterGeneration(ParameterStub parameterService, List<String> parameters, boolean enable)
    {
        IdentifierList request = new IdentifierList();
        if(parameters == null || parameters.isEmpty())
        {
            request.add(new Identifier("*"));
        }
        else
        {
            for(String name : parameters)
            {
                request.add(new Identifier(name));
            }
        }

        try
        {
            ObjectInstancePairList ids = parameterService.listDefinition(request);
            InstanceBooleanPairList enableInstances = new InstanceBooleanPairList();

            for(ObjectInstancePair pair : ids)
            {
                enableInstances.add(new InstanceBooleanPair(pair.getObjIdentityInstanceId(), enable));
            }

            parameterService.enableGeneration(false, enableInstances);
            System.out.println((enable ? "Enable " : "Disable ") + "successful.");
        }
        catch (MALInteractionException e)
        {
            MALStandardError error = e.getStandardError();
            if(error.getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER))
            {
                System.out.println("Provided parameters don't exist in the provider:");
                for(UInteger id : (UIntegerList) error.getExtraInformation())
                {
                    System.out.println("- " + request.get((int) id.getValue()));
                }
            }
            else
            {
                LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
            }
        }
        catch (MALException e)
        {
            LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
        }
    }

    @Command(name = "enable", description = "Enables generation of specified parameters")
    public static class ParameterEnableGeneration extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                    description = "Names of the parameters to enable. If non are specified enable all")
        List<String> parameterNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getParameterService() == null)
            {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(consumer.getMCServices().getParameterService().getParameterStub(), parameterNames, true);
        }
    }

    @Command(name = "disable", description = "Disables generation of specified parameters")
    public static class ParameterDisableGeneration extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                description = "Names of the parameters to disable. If non are specified disable all")
        List<String> parameterNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getParameterService() == null)
            {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }
            enableOrDisableParameterGeneration(consumer.getMCServices().getParameterService().getParameterStub(), parameterNames, false);
        }
    }

    public static void enableOrDisableAggregationGeneration(AggregationStub aggregationService, List<String> aggregations, boolean enable)
    {
        IdentifierList request = new IdentifierList();
        if(aggregations == null || aggregations.isEmpty())
        {
            request.add(new Identifier("*"));
        }
        else
        {
            for(String name : aggregations)
            {
                request.add(new Identifier(name));
            }
        }

        try
        {
            ObjectInstancePairList ids = aggregationService.listDefinition(request);
            InstanceBooleanPairList enableInstances = new InstanceBooleanPairList();

            for(ObjectInstancePair pair : ids)
            {
                enableInstances.add(new InstanceBooleanPair(pair.getObjIdentityInstanceId(), enable));
            }

            aggregationService.enableGeneration(false, enableInstances);
            System.out.println((enable ? "Enable " : "Disable ") + "successful.");
        }
        catch (MALInteractionException e)
        {
            MALStandardError error = e.getStandardError();
            if(error.getErrorNumber().equals(MALHelper.UNKNOWN_ERROR_NUMBER))
            {
                System.out.println("Provided aggregations don't exist in the provider:");
                for(UInteger id : (UIntegerList) error.getExtraInformation())
                {
                    System.out.println("- " + request.get((int) id.getValue()));
                }
            }
            else
            {
                LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
            }
        }
        catch (MALException e)
        {
            LOGGER.log(Level.SEVERE, "Error during enableGeneration!", e);
        }
    }

    @Command(name = "enable", description = "Enables generation of specified aggregations")
    public static class AggregationEnableGeneration extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<aggregationNames>", index = "0",
                description = "Names of the aggregations to enable. If non are specified enable all")
        List<String> aggregationNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getAggregationService() == null)
            {
                System.out.println("Aggregation service is not available for this provider!");
                return;
            }
            enableOrDisableAggregationGeneration(consumer.getMCServices().getAggregationService().getAggregationStub(), aggregationNames, true);
        }
    }

    @Command(name = "disable", description = "Disables generation of specified aggregations")
    public static class AggregationDisableGeneration extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<aggregationNames>", index = "0",
                description = "Names of the aggregations to disable. If non are specified disable all")
        List<String> aggregationNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getAggregationService() == null)
            {
                System.out.println("Aggregation service is not available for this provider!");
                return;
            }
            enableOrDisableAggregationGeneration(consumer.getMCServices().getAggregationService().getAggregationStub(), aggregationNames, false);
        }
    }
    @Command(name = "subscribe", description = "Subscribes to specified parameters")
    public static class ParameterMonitorValue extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                    description = "Names of the parameters to subscribe to. If non are specified subscribe to all.\n"
                                  + " - examples: param1 or param1 param2")
        List<String> parameterNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getParameterService() == null)
            {
                System.out.println("Parameter service is not available for this provider!");
                return;
            }

            Identifier subscriptionId = new Identifier("CLI-Consumer-ParameterSubscription");
            EntityKeyList entityKeys = new EntityKeyList();
            if(parameterNames == null || parameterNames.isEmpty())
            {
                EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L, 0L);
                entityKeys.add(entitykey);
            }
            else
            {
                for(String parameter : parameterNames)
                {
                    EntityKey entitykey = new EntityKey(new Identifier(parameter), 0L, 0L, 0L);
                    entityKeys.add(entitykey);
                }
            }
            EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
            EntityRequestList entities = new EntityRequestList();
            entities.add(entity);

            ParameterStub stub = consumer.getMCServices().getParameterService().getParameterStub();
            Subscription subscription = new Subscription(subscriptionId, entities);
            parameterSubscription = subscriptionId;
            final Object lock = new Object();
            try
            {
                stub.monitorValueRegister(subscription, new ParameterAdapter()
                {
                    @Override
                    public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList updateHeaderList,
                                                           ObjectIdList objectIdList, ParameterValueList parameterValueList, Map qosProperties)
                    {
                        String parameterName = updateHeaderList.get(0).getKey().getFirstSubKey().getValue().toLowerCase();
                        long timestamp = updateHeaderList.get(0).getTimestamp().getValue();
                        String value = parameterValueList.get(0).getRawValue().toString();

                        System.out.println("[" + timestamp + "] - " + parameterName + ": " + value);
                    }

                    @Override
                    public void monitorValueRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });
            }
            catch (MALInteractionException | MALException e)
            {
                LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", e);
            }

            try
            {
                synchronized (lock)
                {
                    lock.wait();
                }
            }
            catch (InterruptedException e)
            {
                LOGGER.log(Level.SEVERE, "Interrupted exception!", e);
            }
        }
    }

    @Command(name = "subscribe", description = "Subscribes to specified aggregations")
    public static class AggregationMonitorValue extends BaseCommand implements Runnable
    {
        @Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "0",
                    description = "Names of the aggregations to subscribe to. If non are specified subscribe to all.\n"
                                  + " - examples: aggregation1 or aggregation1 aggregation2")
        List<String> aggregationNames;

        @Override
        public void run()
        {
            if(!super.initConsumer())
            {
                return;
            }

            if(consumer.getMCServices().getAggregationService() == null)
            {
                System.out.println("Aggregation service is not available for this provider!");
                return;
            }

            AggregationStub stub = consumer.getMCServices().getAggregationService().getAggregationStub();
            try
            {
                IdentifierList names = new IdentifierList();
                if(aggregationNames == null || aggregationNames.isEmpty())
                {
                    names.add(new Identifier("*"));
                }
                else
                {
                    for(String name : aggregationNames)
                    {
                        names.add(new Identifier(name));
                    }
                }



                ObjectInstancePairList result = stub.listDefinition(names);

                ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                LongList aggregationDefinitionsIds = new LongList();
                LongList aggregationIdentitiesIds = new LongList();
                LongList parameterIds = new LongList();

                for (ObjectInstancePair pair : result)
                {
                    long aggregationIdentityId = pair.getObjIdentityInstanceId();
                    long aggregationDefinitionId = pair.getObjDefInstanceId();

                    aggregationIdentitiesIds.add(aggregationIdentityId);
                    aggregationDefinitionsIds.add(aggregationDefinitionId);
                }

                final Object lock = new Object();

                Map<Long, String> aggregationIdentities = new HashMap<>();
                archive.retrieve(AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE, domain, aggregationIdentitiesIds, new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(int i = 0; i < objDetails.size(); ++i)
                        {
                            aggregationIdentities.put(objDetails.get(i).getInstId(), ((Identifier)objBodies.get(i)).getValue());
                        }

                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during archive retrieve!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock)
                {
                    lock.wait();
                }

//                System.out.println("Aggregation ids");
//                System.out.println(aggregationDefinitionsIds.stream().map(Object::toString).collect(Collectors.joining(", ")));
                archive.retrieve(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE, domain, aggregationDefinitionsIds, new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(int i = 0; i < objDetails.size(); ++i)
                        {
                            AggregationDefinitionDetails details = (AggregationDefinitionDetails) objBodies.get(i);
                            if(details.getGenerationEnabled())
                            {
                                for (AggregationParameterSet set : details.getParameterSets())
                                {
                                    parameterIds.addAll(set.getParameters());
                                }
                            }
                            else
                            {
                                System.out.println("Aggregation " + aggregationIdentities.get(objDetails.get(i).getDetails().getRelated()) + " is disabled!");
                            }

                            if(!details.getSendDefinitions())
                            {
                                System.out.println("sendDefinitions is set to false for aggregation: " + aggregationIdentities.get(objDetails.get(i).getDetails().getRelated()) + ". " +
                                                   "Parameter names will not be available.");
                            }

                        }

                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during archive retrieve!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock)
                {
                    lock.wait();
                }

                if(parameterIds.isEmpty())
                {
                    return;
                }

//                System.out.println("Parameter ids");
//                System.out.println(parameterIds.stream().map(Object::toString).collect(Collectors.joining(", ")));
                Map<Long, String> identityIdToName = new HashMap<>();
                archive.retrieve(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE, domain, parameterIds, new ArchiveAdapter() {
                    @Override
                    public void retrieveResponseReceived(MALMessageHeader msgHeader, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(int i = 0; i < objDetails.size(); ++i)
                        {
                            identityIdToName.put(objDetails.get(i).getInstId(), ((Identifier)objBodies.get(i)).getValue());
                        }

                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void retrieveResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during archive retrieve!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock)
                {
                    lock.wait();
                }

                ArchiveQueryList queries = new ArchiveQueryList();
                for (Long id : parameterIds)
                {
                    queries.add(new ArchiveQuery(domain, null, null, id, null, null, null, null, null));
                }
                Map<Long, String> definitionIdToIdentity = new HashMap<>();

                archive.query(false, ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE, queries,null, new ArchiveAdapter(){
                    @Override
                    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(ArchiveDetails details : objDetails)
                        {
                            definitionIdToIdentity.put(details.getInstId(), identityIdToName.get(details.getDetails().getRelated()));
                        }
                    }

                    @Override
                    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain, ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties)
                    {
                        for(ArchiveDetails details : objDetails)
                        {
                            definitionIdToIdentity.put(details.getInstId(), identityIdToName.get(details.getDetails().getRelated()));
                        }

                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock)
                {
                    lock.wait();
                }

                Identifier subscriptionId = new Identifier("CLI-Consumer-AggregationSubscription");
                EntityKeyList entityKeys = new EntityKeyList();
                if(aggregationNames == null || aggregationNames.isEmpty())
                {
                    EntityKey entitykey = new EntityKey(new Identifier("*"), 0L, 0L, 0L);
                    entityKeys.add(entitykey);
                }
                else
                {
                    for(String aggregation : aggregationNames)
                    {
                        EntityKey entitykey = new EntityKey(new Identifier(aggregation), 0L, 0L, 0L);
                        entityKeys.add(entitykey);
                    }
                }
                EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
                EntityRequestList entities = new EntityRequestList();
                entities.add(entity);
                Subscription subscription = new Subscription(subscriptionId, entities);
                aggregationSubscription = subscriptionId;
                stub.monitorValueRegister(subscription, new AggregationAdapter()
                {
                    @Override
                    public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList updateHeaderList,
                                                           ObjectIdList objectIdList, AggregationValueList aggregationValueList, Map qosProperties)
                    {
                        String aggregationName = updateHeaderList.get(0).getKey().getFirstSubKey().getValue().toLowerCase();
                        long timestamp = updateHeaderList.get(0).getTimestamp().getValue();
                        AggregationParameterValueList values = aggregationValueList.get(0).getParameterSetValues().get(0).getValues();
                        System.out.println("[" + timestamp + "] - " + aggregationName + ": ");
                        int index = 1;
                        for(AggregationParameterValue value : values)
                        {
                            String name = definitionIdToIdentity.get(value.getParamDefInstId());
                            System.out.println("  " + (name == null ? "parameter " + index : name) + ": " + value.getValue().getRawValue().toString());
                            index += 1;
                        }
                        System.out.println();
                    }

                    @Override
                    public void monitorValueRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties)
                    {
                        LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", error);
                        synchronized (lock)
                        {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock)
                {
                    lock.wait();
                }

            }
            catch (MALInteractionException | MALException | InterruptedException e)
            {
                LOGGER.log(Level.SEVERE, "Error during monitorValueRegister!", e);
            }
        }
    }
}
//------------------------------------------------------------------------------