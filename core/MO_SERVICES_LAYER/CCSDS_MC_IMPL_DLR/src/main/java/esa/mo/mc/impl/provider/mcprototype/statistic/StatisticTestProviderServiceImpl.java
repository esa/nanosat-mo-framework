/* 
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2016 Deutsches Zentrum fuer Luft- und Raumfahrt e.V. (DLR).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package esa.mo.mc.impl.provider.mcprototype.statistic;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import esa.mo.mc.impl.provider.StatisticProviderServiceImpl;
import esa.mo.mc.impl.util.MCServicesHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequest;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequestList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mcprototype.MCPrototypeHelper;
import org.ccsds.moims.mo.mcprototype.statistictest.StatisticTestHelper;
import org.ccsds.moims.mo.mcprototype.statistictest.provider.StatisticTestInheritanceSkeleton;

public class StatisticTestProviderServiceImpl extends StatisticTestInheritanceSkeleton {

	private MALProvider statisticTestServiceProvider;
	private boolean initialiased = false;
	private final ConnectionProvider connection = new ConnectionProvider();
	private StatisticProviderServiceImpl statisticService;
	private COMServicesProvider comServices;
	private ParameterProviderServiceImpl paramService;
	private final Map<String, ObjectInstancePair> parameters = new HashMap<String, ObjectInstancePair>();
	private final Map<String, Timer> parameterTimers = new HashMap<String, Timer>();
	private static final Logger LOGGER = Logger.getLogger(StatisticTestProviderServiceImpl.class.getName());

	/**
	 * creates the MAL objects
	 *
	 * @param statisticService
	 * @param comServices
	 * @param paramService
	 * @throws MALException On initialisation error.
	 */
	public synchronized void init(StatisticProviderServiceImpl statisticService, COMServicesProvider comServices, ParameterProviderServiceImpl paramService) throws MALException {
		this.statisticService = statisticService;
		this.comServices = comServices;
		this.paramService = paramService;
		if (!initialiased) {
			if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
				MALHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
				COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
				MCHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			if (MALContextFactory.lookupArea(MCPrototypeHelper.MCPROTOTYPE_AREA_NAME, MCPrototypeHelper.MCPROTOTYPE_AREA_VERSION) == null) {
				MCPrototypeHelper.init(MALContextFactory.getElementFactoryRegistry());
			}

			try {
				StatisticTestHelper.init(MALContextFactory.getElementFactoryRegistry());
			} catch (MALException ex) {
				// nothing to be done..
			}
		}

		// shut down old service transport
		if (null != statisticTestServiceProvider) {
			connection.close();
		}

		statisticTestServiceProvider = connection.startService(StatisticTestHelper.STATISTICTEST_SERVICE_NAME.toString(), StatisticTestHelper.STATISTICTEST_SERVICE, this);

		initialiased = true;
		Logger.getLogger(StatisticTestProviderServiceImpl.class.getName()).info("StatisticTest service READY");

	}

	/**
	 * Closes all running threads and releases the MAL resources.
	 */
	public void close() {
		try {
			if (null != statisticTestServiceProvider) {
				statisticTestServiceProvider.close();
			}

			connection.close();
		} catch (MALException ex) {
			Logger.getLogger(StatisticTestProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
		}
	}

	@Override
	public void resetTest(String statisticDomain, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "StatisticTestProvider: resetTest called.");
		// stop parameter update generation and clear internal parameter list
		for (Timer timer : parameterTimers.values()) {
			timer.cancel();
		}
		parameterTimers.clear();
		parameters.clear();

		// remove all parameter evaluations and parameters from provider
		LongList wildcardIds = new LongList();
		wildcardIds.add(0L);
		statisticService.removeParameterEvaluation(wildcardIds, interaction);
		paramService.removeParameter(wildcardIds, interaction);
		// and remove all parameter evaluation identities even from the COM archive- otherwise reuse of identities depends on former test runs
		//comServices.getArchiveService().delete(MCServicesHelper.getObjectType(), connection.getConnectionDetails().getDomain(), wildcardIds, interaction);
		
		// it is required to create all parameters after resetting the test
		addParameter("par1", interaction);
		addParameter("par2", interaction);
		addParameter("par3", interaction);
		addParameter("par4", interaction);
		addParameter("par5", interaction);
		addParameter("par6", interaction);
	}

	@Override
	public void setParameterValue(Integer id, Integer value, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "StatisticTestProvider: setParameterValue called.");
		ParameterRawValueList rawValueList = new ParameterRawValueList();
		rawValueList.add(new ParameterRawValue(id.longValue(), new Union(value)));
		paramService.setValue(rawValueList, interaction);
	}

	@Override
	public synchronized ObjectInstancePair addParameter(String parName, MALInteraction interaction) throws MALInteractionException, MALException {
		LOGGER.log(Level.INFO, "StatisticTestProvider: addParameter called.");
		ObjectInstancePair parInst = parameters.get(parName);
		if (null == parInst) {
			Long conversionDefId = getConversionDefId("DOUBLE_CONVERSION", interaction);
			ObjectKey conversionId = new ObjectKey(connection.getConnectionDetails().getDomain(), conversionDefId);
			Byte intType = Attribute.INTEGER_TYPE_SHORT_FORM.byteValue();
//			switch (parName) {
				if("par1".equals(parName)){
					parInst = addParameter(parName, intType, intType, conversionId, new Union(0), true, interaction);
                                }else if("par2".equals(parName)){
					parInst = addParameter(parName, intType, intType, conversionId, new Union(1), true, interaction);
                                }else if("par3".equals(parName)){
					parInst = addParameter(parName, intType, intType, conversionId, new Union(200), true, interaction);
                                }else if("par4".equals(parName)){
					parInst = addParameter(parName, intType, intType, conversionId, new Union(1), false, interaction);
                                }else if("par5".equals(parName)){
					parInst = addParameter(parName, intType, intType, conversionId, new Union(10), false, interaction);
                                }else if("par6".equals(parName)){
					parInst = addParameter(parName, Attribute.STRING_TYPE_SHORT_FORM.byteValue(), null, null, null, false, interaction);
                                }else{
					LOGGER.log(Level.SEVERE, "StatisticTestProvider: Parameter with unknown identity ''{0}'' requested.", parName);
					return null;
			}
			parameters.put(parName, parInst);
		}
		return parInst;
	}

	private Long getConversionDefId(String name, MALInteraction interaction) throws MALInteractionException, MALException {
		if (!name.equals("DOUBLE_CONVERSION")) {
			LOGGER.log(Level.SEVERE, "Unknown conversion requested.");
			return null;
		}

		// check, if conversion identity of name is in archive; if not in archive, create identity
		Long identityId = retrieveIdentityIdByNameFromArchive(connection.getConnectionDetails().getDomain(), 
                        new Identifier(name), ConversionHelper.CONVERSIONIDENTITY_OBJECT_TYPE);
                
		if (null == identityId) {
			// add conversion identity to archive
			IdentifierList identifierList = new IdentifierList();
			identifierList.add(new Identifier(name));
			LongList conversionIdentityIds = comServices.getArchiveService().store(
					true,
					ConversionHelper.CONVERSIONIDENTITY_OBJECT_TYPE,
					connection.getConnectionDetails().getDomain(),
					HelperArchive.generateArchiveDetailsList(null, null, interaction),
					identifierList,
					interaction);
			identityId = conversionIdentityIds.get(0);
			if (null == identityId) {
				LOGGER.log(Level.SEVERE, "Could not create new Conversion Identity.");
				return null;
			}
		}

		// retrieve the associated definition id; if not in archive, create definition
		Long definitionId = retrieveDefinitionIdByIdentitiyIdFromArchive(connection.getConnectionDetails().getDomain(), 
                        identityId, ConversionHelper.LINECONVERSION_OBJECT_TYPE);
		if (null == definitionId) {
			// add conversion definition to archive, related pointing to conversion identity
			PairList points = new PairList();
			points.add(new Pair(new Union(0), new Union(0))); // first is raw, second is converted value
			points.add(new Pair(new Union(1), new Union(2)));
			LineConversionDetailsList lineConversionList = new LineConversionDetailsList();
			lineConversionList.add(new LineConversionDetails(true, points));
			LongList conversionDefinitionIds = comServices.getArchiveService().store(
					true,
					ConversionHelper.LINECONVERSION_OBJECT_TYPE,
					connection.getConnectionDetails().getDomain(),
					HelperArchive.generateArchiveDetailsList(identityId, null, interaction),
					lineConversionList,
					interaction);
			definitionId = conversionDefinitionIds.get(0);
		}
		return definitionId;
	}

	private ObjectInstancePair addParameter(String name, Byte rawType, Byte convType, 
                ObjectKey conversionId, final Attribute initValue, boolean autoIncrement, 
                final MALInteraction interaction) throws MALInteractionException, MALException {
		ParameterConversion conversion = null;
		if (null != convType) {
			ConditionalConversionList ccl = new ConditionalConversionList();
			ccl.add(new ConditionalConversion(null, conversionId));
			conversion = new ParameterConversion(convType, null, ccl);
		}
		ParameterCreationRequest pcr = new ParameterCreationRequest(
				new Identifier(name),
				new ParameterDefinitionDetails(
						"description for " + name,
						rawType,
						null,
						false,
						new Duration(0),
						null,
						conversion)
		);

		// add parameter
		ParameterCreationRequestList pcrl = new ParameterCreationRequestList();
		pcrl.add(pcr);
		ObjectInstancePairList oipl = paramService.addParameter(pcrl, interaction);
		final Long paramIdentityId = oipl.get(0).getObjIdentityInstanceId();

		if (null != initValue) {
			ParameterRawValueList prvl = new ParameterRawValueList();
			prvl.add(new ParameterRawValue(paramIdentityId, initValue));
			paramService.setValue(prvl, interaction);
		}

		if (autoIncrement) {
			Timer timer = new Timer(true);
			TimerTask task = new TimerTask() {
				private Integer prevVal = ((Union) initValue).getIntegerValue();

				@Override
				public void run() {
					prevVal++;
					ParameterRawValueList prvl = new ParameterRawValueList();
					prvl.add(new ParameterRawValue(paramIdentityId, new Union(prevVal)));
					try {
						paramService.setValue(prvl, interaction);
					} catch (Exception ex) {
						LOGGER.log(Level.WARNING, "Cannot increment Statistics Test parameter value. Stopping parameter updates.");
						this.cancel();
					}
				}
			};
			// start timer with delay of 1s and repetition rate of 1s
			timer.schedule(task, 1000L, 1000L);
			parameterTimers.put(name, timer);
		}
		return oipl.get(0);
	}

	private Long retrieveIdentityIdByNameFromArchive(IdentifierList domain, Identifier name, ObjectType identitysObjectType) {
		final ArchiveProviderServiceImpl archive = comServices.getArchiveService();
		if (archive == null) { // If there's no archive...
			return null;
		}
		//get all identity-objects with the given objectType
		LongList identityIds = new LongList();
		identityIds.add(0L);
		final List<ArchivePersistenceObject> identityArchiveObjs = HelperArchive.getArchiveCOMObjectList(archive, identitysObjectType, domain, identityIds);
		if (identityArchiveObjs == null) {
			return null;
		}
		//get the  Identity with the given name
		for (ArchivePersistenceObject identityArchiveObj : identityArchiveObjs) {
			final Identifier objArchiveName = (Identifier) identityArchiveObj.getObject();
			if (objArchiveName.equals(name)) //return the id of the  Identity with the given name
			{
				return identityArchiveObj.getObjectId();
			}
		}
		return null;
	}

	private Long retrieveDefinitionIdByIdentitiyIdFromArchive(IdentifierList domain, Long identityId, ObjectType definitionObjectType) {
        //retrieve all existing conversion-objects
        LongList defIds = new LongList();
        defIds.add(0L);
        final ArchiveDetailsList defarchiveDetailsListFromArchive = HelperArchive.getArchiveDetailsListFromArchive(comServices.getArchiveService(), definitionObjectType, domain, defIds);
        //look if there are conversionDetails, which reference the identity
        Long defId = null;
        Long maxTimeStamp = 0L;
        if (defarchiveDetailsListFromArchive == null) {
            return null;
		}
        //iterate through all entries to check for the given identity as the source object
        for (ArchiveDetails defArchiveDetails : defarchiveDetailsListFromArchive) {
            if (defArchiveDetails.getDetails().getRelated() == null) {
                continue;
			}
            if (defArchiveDetails.getDetails().getRelated().equals(identityId)) {
                //and filter for the latest one
                final long itemTimestamp = defArchiveDetails.getTimestamp().getValue();
                if (itemTimestamp > maxTimeStamp) {
                    defId = defArchiveDetails.getInstId();
                    maxTimeStamp = itemTimestamp;
                }
            }
        }
		return defId;
	}

}
