/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.aas;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.factory.aasx.FileLoaderHelper;
import org.eclipse.basyx.aas.factory.aasx.SubmodelFileEndpointLoader;
import org.eclipse.basyx.aas.factory.json.JSONAASBundleFactory;
import org.eclipse.basyx.aas.factory.xml.XMLAASBundleFactory;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.components.aas.aascomponent.InMemoryAASServerComponentFactory;
import org.eclipse.basyx.components.aas.aascomponent.MongoDBAASServerComponentFactory;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.components.aas.authorization.AuthorizedAASServerFeature;
import org.eclipse.basyx.components.aas.authorization.AuthorizedDefaultServlet;
import org.eclipse.basyx.components.aas.authorization.AuthorizedDefaultServletParams;
import org.eclipse.basyx.components.aas.configuration.AASEventBackend;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerFeature;
import org.eclipse.basyx.components.aas.mqtt.MqttAASServerFeature;
import org.eclipse.basyx.components.aas.mqtt.MqttV2AASServerFeature;
import org.eclipse.basyx.components.aas.servlet.AASAggregatorAASXUploadServlet;
import org.eclipse.basyx.components.aas.servlet.AASAggregatorServlet;
import org.eclipse.basyx.components.configuration.BaSyxConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.AASAggregatorAASXUpload;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.encoding.Base64URLEncoder;
import org.eclipse.basyx.extensions.shared.encoding.URLEncoder;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Component providing an empty AAS server that is able to receive AAS/SMs from
 * remote. It uses the Aggregator API, i.e. AAS should be pushed to
 * ${URL}/shells
 *
 * @author schnicke, espen, fried, fischer, danish, wege
 *
 */
@SuppressWarnings("deprecation")
public class AASServerComponent implements IComponent {
	private static Logger logger = LoggerFactory.getLogger(AASServerComponent.class);

	// The server with the servlet that will be created
	private BaSyxHTTPServer server;
	private IAASRegistry registry;

	// Configurations
	private BaSyxContextConfiguration contextConfig;
	private BaSyxAASServerConfiguration aasConfig;
	private BaSyxMongoDBConfiguration mongoDBConfig;
	private BaSyxSecurityConfiguration securityConfig;

	private List<IAASServerFeature> aasServerFeatureList = new ArrayList<IAASServerFeature>();

	// Initial AASBundle
	protected Collection<AASBundle> aasBundles;

	private IAASAggregator aggregator;
	// Watcher for AAS Aggregator functionality
	private boolean isAASXUploadEnabled = false;
	
	private static final String PREFIX_SUBMODEL_PATH = "/aas/submodels/";

	/**
	 * Constructs an empty AAS server using the passed context
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = new BaSyxAASServerConfiguration();
	}

	/**
	 * Constructs an empty AAS server using the passed configuration
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = aasConfig;
	}

	/**
	 * Constructs an empty AAS server using the passed configuration
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig, BaSyxMongoDBConfiguration mongoDBConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = aasConfig;
		this.aasConfig.setAASBackend(AASServerBackend.MONGODB);
		this.mongoDBConfig = mongoDBConfig;
	}

	/**
	 * Sets and enables mqtt connection configuration for this component. Has to be
	 * called before the component is started. Currently only works for InMemory
	 * backend.
	 *
	 * @param configuration
	 * 
	 * @deprecated Add MQTT via {@link MqttAASServerFeature} instead.
	 */
	@Deprecated
	public void enableMQTT(BaSyxMqttConfiguration configuration) {
		aasServerFeatureList.add(new MqttAASServerFeature(configuration, getMqttSubmodelClientId()));
	}

	/**
	 * Disables mqtt configuration. Has to be called before the component is
	 * started.
	 * 
	 * @deprecated remove MQTT from the feature list instead.
	 */
	@Deprecated
	public void disableMQTT() {
		aasServerFeatureList.forEach(f -> {
			if (f instanceof MqttAASServerFeature) {
				aasServerFeatureList.remove(f);
			}
		});
	}

	/**
	 * Enables AASX upload functionality
	 */
	public void enableAASXUpload() {
		this.isAASXUploadEnabled = true;
	}

	/**
	 * Sets a registry service for registering AAS that are created during startup
	 *
	 * @param registry
	 */
	public void setRegistry(IAASRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Explicitly sets AAS bundles that should be loaded during startup
	 *
	 * @param aasBundles
	 *            The bundles that will be loaded during startup
	 */
	public void setAASBundles(Collection<AASBundle> aasBundles) {
		this.aasBundles = aasBundles;
	}

	/**
	 * Explicitly sets an AAS bundle that should be loaded during startup
	 *
	 * @param aasBundle
	 *            The bundle that will be loaded during startup
	 */
	public void setAASBundle(AASBundle aasBundle) {
		this.aasBundles = Collections.singleton(aasBundle);
	}

	/**
	 * Starts the AASX component at http://${hostName}:${port}/${path}
	 */
	@Override
	public void startComponent() {
		logger.info("Create the server...");
		registry = createRegistryFromConfig(aasConfig);

		IConnectorFactory connectorFactory = new HTTPConnectorFactory();

		loadAASServerFeaturesFromConfig();
		initializeAASServerFeatures();

		BaSyxContext context = contextConfig.createBaSyxContext();
		context.addServletMapping("/*", createAggregatorServlet());
		addAASServerFeaturesToContext(context);

		// An initial AAS has been loaded from the drive?
		if (aasBundles != null) {
			// 1. Also provide the files
			context.addServletMapping("/files/*", createDefaultServlet());

			// 2. Fix the file paths according to the servlet configuration
			modifyFilePaths(contextConfig.getHostname(), contextConfig.getPort(), contextConfig.getContextPath());

			// 3. Register the initial AAS
			registerEnvironment();
		}

		logger.info("Start the server");
		server = new BaSyxHTTPServer(context);
		server.start();
		
		registerPreexistingAASAndSMIfPossible();
	}

	private DefaultServlet createDefaultServlet() {
		if (securityConfig.isAuthorizationEnabled()) {
			return new AuthorizedDefaultServlet<>(getAuthorizedDefaultServletParams());
		}
		return new DefaultServlet();
	}

	private AuthorizedDefaultServletParams<?> getAuthorizedDefaultServletParams() {
		final AuthorizedAASServerFeature authorizedAASServerFeature = new AuthorizedAASServerFeature(securityConfig);

		return authorizedAASServerFeature.getFilesAuthorizerParams();
	}

	private void registerPreexistingAASAndSMIfPossible() {
		if (!shouldRegisterPreexistingAASAndSM()) {
			return;
		}
		
		aggregator.getAASList().stream().forEach(this::registerAASAndSubmodels);
	}

	private boolean shouldRegisterPreexistingAASAndSM() {
		return isMongoDBBackend() && registry != null;
	}
	
	private void registerAASAndSubmodels(IAssetAdministrationShell aas) {
		registerAAS(aas);
		
		registerSubmodels(aas);
	}

	private void registerAAS(IAssetAdministrationShell aas) {
		try {
			String combinedEndpoint = getAASAccessPath(aas);
			registry.register(new AASDescriptor(aas, combinedEndpoint));
			logger.info("The AAS " + aas.getIdShort() + " is Successfully Registered from DB");
		} catch(Exception e) {
			logger.info("The AAS " + aas.getIdShort() + " could not be Registered from DB" + e);
		}
	}

	private String getAASAccessPath(IAssetAdministrationShell aas) {
		return VABPathTools.concatenatePaths(getURL(), AASAggregatorAPIHelper.getAASAccessPath(aas.getIdentification()));
	}

	private void registerSubmodels(IAssetAdministrationShell aas) {
		List<ISubmodel> submodels = getSubmodelFromAggregator(aggregator, aas.getIdentification());
		try {
			submodels.stream().forEach(submodel -> registerSubmodel(aas, submodel));
			logger.info("The submodels from AAS " + aas.getIdShort() + " are Successfully Registered from DB");
		} catch(Exception e) {
			logger.info("The submodel from AAS " + aas.getIdShort() + " could not be Registered from DB " + e);
		}
	}
	
	private void registerSubmodel(IAssetAdministrationShell aas, ISubmodel submodel) {
		String smEndpoint = VABPathTools.concatenatePaths(getAASAccessPath(aas), AssetAdministrationShell.SUBMODELS, submodel.getIdShort(), SubmodelProvider.SUBMODEL);
		registry.register(aas.getIdentification(), new SubmodelDescriptor(submodel, smEndpoint));
	}

	private List<ISubmodel> getSubmodelFromAggregator(IAASAggregator aggregator, IIdentifier iIdentifier) {
		MultiSubmodelProvider aasProvider = (MultiSubmodelProvider) aggregator.getAASProvider(iIdentifier);

		@SuppressWarnings("unchecked")
		List<Object> submodelObject = (List<Object>) aasProvider.getValue(PREFIX_SUBMODEL_PATH);
		
		List<ISubmodel> persistentSubmodelList = new ArrayList<>();
		
		submodelObject.stream().map(this::getSubmodel).forEach(persistentSubmodelList::add);		

		return persistentSubmodelList;
	}
	
	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodel(Object submodelObject) {
		return Submodel.createAsFacade((Map<String, Object>) submodelObject);	
	}

	private void loadAASServerFeaturesFromConfig() {
		if (isEventingEnabled()) {
			configureMqttFeature();
		}

		if(aasConfig.isPropertyDelegationEnabled()) {
			addAASServerFeature(new DelegationAASServerFeature());
		}

		configureAuthorization();

		if (aasConfig.isAASXUploadEnabled()) {
			enableAASXUpload();
		}
	}

	private void configureAuthorization() {
		securityConfig = new BaSyxSecurityConfiguration();
		securityConfig.loadFromDefaultSource();
		addAASServerFeature(new AuthorizedAASServerFeature(securityConfig));
	}

	private boolean isEventingEnabled() {
		return !aasConfig.getAASEvents().equals(AASEventBackend.NONE);
	}

	private void configureMqttFeature() {
		BaSyxMqttConfiguration mqttConfig = new BaSyxMqttConfiguration();
		mqttConfig.loadFromDefaultSource();
		if (aasConfig.getAASEvents().equals(AASEventBackend.MQTT)) {
			addAASServerFeature(new MqttAASServerFeature(mqttConfig, mqttConfig.getClientId()));
		} else if (aasConfig.getAASEvents().equals(AASEventBackend.MQTTV2)) {
			addAASServerFeature(new MqttV2AASServerFeature(mqttConfig, mqttConfig.getClientId(), aasConfig.getAASId(), new Base64URLEncoder()));
		} else if (aasConfig.getAASEvents().equals(AASEventBackend.MQTTV2_SIMPLE_ENCODING)) {
			addAASServerFeature(new MqttV2AASServerFeature(mqttConfig, mqttConfig.getClientId(), aasConfig.getAASId(), new URLEncoder()));
		}
	}

	/**
	 * Retrieves the URL on which the component is providing its HTTP server
	 *
	 * @return
	 */
	public String getURL() {
		String basePath = aasConfig.getHostpath();
		if (basePath.isEmpty()) {
			return contextConfig.getUrl();
		}
		return basePath;
	}

	@Override
	public void stopComponent() {
		deregisterAASAndSmAddedDuringRuntime();
		
		cleanUpAASServerFeatures();

		server.shutdown();
	}
	
	private void deregisterAASAndSmAddedDuringRuntime() {
		if(registry == null) {
			return;
		}
		
		try {
			aggregator.getAASList().stream().forEach(this::deregisterAASAndAccompanyingSM);
		} catch(RuntimeException e) {
			logger.info("The resource could not be found in the aggregator " + e);
		}
		
	}
	
	private void deregisterAASAndAccompanyingSM(IAssetAdministrationShell aas) {	
		getSubmodelDescriptors(aas.getIdentification()).stream().forEach(submodelDescriptor -> deregisterSubmodel(aas.getIdentification(), submodelDescriptor));
		
		deregisterAAS(aas.getIdentification());
	}

	private List<SubmodelDescriptor> getSubmodelDescriptors(IIdentifier aasIdentifier) {
		try {
			return registry.lookupSubmodels(aasIdentifier);
		} catch(ResourceNotFoundException e) {
			return Collections.emptyList();
		}
	}
	
	private void deregisterSubmodel(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) {
		try {
			registry.delete(aasIdentifier, submodelDescriptor.getIdentifier());
			logger.info("The SM '" + submodelDescriptor.getIdShort() + "' successfully deregistered.");
		} catch (ProviderException e) {
			logger.info("The SM '" + submodelDescriptor.getIdShort() + "' can't be deregistered. It was not found in registry.");
		}
	}

	private void deregisterAAS(IIdentifier aasIdentifier) {
		try {
			registry.delete(aasIdentifier);
			logger.info("The AAS '" + aasIdentifier.getId() + "' successfully deregistered.");
		} catch (ProviderException e) {
			logger.info("The AAS '" + aasIdentifier.getId() + "' can't be deregistered. It was not found in registry.");
		}
	}

	public void addAASServerFeature(IAASServerFeature aasServerFeature) {
		aasServerFeatureList.add(aasServerFeature);
	}

	private void initializeAASServerFeatures() {
		for (IAASServerFeature aasServerFeature : aasServerFeatureList) {
			aasServerFeature.initialize();
		}
	}

	private void cleanUpAASServerFeatures() {
		for (IAASServerFeature aasServerFeature : aasServerFeatureList) {
			aasServerFeature.cleanUp();
		}
	}

	private String loadBundleString(String filePath) throws IOException {
		String content;
		try {
			content = IOUtils.toString(FileLoaderHelper.getInputStream(filePath), StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			logger.info("Could not find a corresponding file. Loading from default resource.");
			content = BaSyxConfiguration.getResourceString(filePath);
		}

		return content;
	}

	private Set<AASBundle> loadBundleFromXML(String xmlPath) throws IOException, ParserConfigurationException, SAXException {
		logger.info("Loading aas from xml \"" + xmlPath + "\"");
		String xmlContent = loadBundleString(xmlPath);

		return new XMLAASBundleFactory(xmlContent).create();
	}

	private Set<AASBundle> loadBundleFromJSON(String jsonPath) throws IOException {
		logger.info("Loading aas from json \"" + jsonPath + "\"");
		String jsonContent = loadBundleString(jsonPath);

		return new JSONAASBundleFactory(jsonContent).create();
	}

	private static Set<AASBundle> loadBundleFromAASX(String aasxPath) throws IOException, ParserConfigurationException, SAXException, InvalidFormatException, URISyntaxException {
		logger.info("Loading aas from aasx \"" + aasxPath + "\"");

		// Instantiate the aasx package manager
		AASXToMetamodelConverter packageManager = new AASXPackageManager(aasxPath);

		// Unpack the files referenced by the aas
		packageManager.unzipRelatedFiles();

		// Retrieve the aas from the package
		return packageManager.retrieveAASBundles();
	}

	private void addAASServerFeaturesToContext(BaSyxContext context) {
		for (IAASServerFeature aasServerFeature : aasServerFeatureList) {
			aasServerFeature.addToContext(context);
		}
	}

	private VABHTTPInterface<?> createAggregatorServlet() {
		aggregator = createAASAggregator();
		loadAASBundles();
		
		if (aasBundles != null) {
			try (final var ignored = ElevatedCodeAuthentication.enterElevatedCodeAuthenticationArea()) {
				AASBundleHelper.integrate(aggregator, aasBundles);
			}
		}

		if (isAASXUploadEnabled) {
			return new AASAggregatorAASXUploadServlet(new AASAggregatorAASXUpload(aggregator));
		} else {
			return new AASAggregatorServlet(aggregator);
		}
	}

	private IAASAggregator createAASAggregator() {
		if (isMongoDBBackend()) {
			return new MongoDBAASServerComponentFactory(createMongoDbConfiguration(), createAASServerDecoratorList(), registry).create();
		}
		return new InMemoryAASServerComponentFactory(createAASServerDecoratorList(), registry).create();
	}

	private boolean isMongoDBBackend() {
		return aasConfig.getAASBackend().equals(AASServerBackend.MONGODB);
	}
	
	private BaSyxMongoDBConfiguration createMongoDbConfiguration() {
		BaSyxMongoDBConfiguration config;
		if (this.mongoDBConfig == null) {
			config = new BaSyxMongoDBConfiguration();
			config.loadFromDefaultSource();
		} else {
			config = this.mongoDBConfig;
		}
		return config;
	}

	private List<IAASServerDecorator> createAASServerDecoratorList() {
		List<IAASServerDecorator> aasServerDecoratorList = new ArrayList<>();

		for (IAASServerFeature aasServerFeature : aasServerFeatureList) {
			aasServerDecoratorList.add(aasServerFeature.getDecorator());
		}

		return aasServerDecoratorList;
	}

	private void loadAASBundles() {
		if (aasBundles != null) {
			return;
		}

		List<String> aasSources = aasConfig.getAASSourceAsList();
		aasBundles = loadAASFromSource(aasSources);
	}

	private Set<AASBundle> loadAASFromSource(List<String> aasSources) {
		if (aasSources.isEmpty()) {
			return Collections.emptySet();
		}

		Set<AASBundle> aasBundlesSet = new HashSet<>();

		aasSources.stream().map(this::loadBundleFromFile).forEach(aasBundlesSet::addAll);

		return aasBundlesSet;
	}

	private Set<AASBundle> loadBundleFromFile(String aasSource) {
		try {
			if (aasSource.endsWith(".aasx")) {
				return loadBundleFromAASX(aasSource);
			} else if (aasSource.endsWith(".json")) {
				return loadBundleFromJSON(aasSource);
			} else if (aasSource.endsWith(".xml")) {
				return loadBundleFromXML(aasSource);
			}
		} catch (IOException | ParserConfigurationException | SAXException | URISyntaxException | InvalidFormatException e) {
			logger.error("Could not load initial AAS from source '" + aasSource + "'");
		}

		return Collections.emptySet();
	}

	/**
	 * Only creates the registry, if it hasn't been set explicitly before
	 */
	private IAASRegistry createRegistryFromConfig(BaSyxAASServerConfiguration aasConfig) {
		if (this.registry != null) {
			// Do not overwrite an explicitly set registry
			return this.registry;
		}
		String registryUrl = aasConfig.getRegistry();
		if (registryUrl == null || registryUrl.isEmpty()) {
			return null;
		}

		// Load registry url from config
		logger.info("Registry loaded at \"" + registryUrl + "\"");

		if (shouldUseSecuredRegistryConnection(aasConfig)) {
			return new AuthorizedAASRegistryProxy(registryUrl, aasConfig.configureAndGetAuthorizationSupplier());
		} else {
			return new AASRegistryProxy(registryUrl);
		}
	}

	private boolean shouldUseSecuredRegistryConnection(BaSyxAASServerConfiguration aasConfig) {
		return aasConfig.isAuthorizationCredentialsForSecuredRegistryConfigured();
	}

	private void registerEnvironment() {
		if (aasConfig.getSubmodels().isEmpty()) {
			registerFullAAS();
		} else {
			registerSubmodelsFromWhitelist();
		}
	}

	private void registerSubmodelsFromWhitelist() {
		logger.info("Register from whitelist");
		List<AASDescriptor> descriptors = registry.lookupAll();
		List<String> smWhitelist = aasConfig.getSubmodels();
		for (String s : smWhitelist) {
			updateSMEndpoint(s, descriptors);
		}
	}

	private void registerFullAAS() {
		if (registry == null) {
			logger.info("No registry specified, skipped registration");
			return;
		}

		String baseUrl = getURL();
		String aggregatorPath = VABPathTools.concatenatePaths(baseUrl, AASAggregatorProvider.PREFIX);
		AASBundleHelper.register(registry, aasBundles, aggregatorPath);
	}

	private void updateSMEndpoint(String smId, List<AASDescriptor> descriptors) {
		descriptors.forEach(desc -> {
			Collection<SubmodelDescriptor> smDescriptors = desc.getSubmodelDescriptors();
			SubmodelDescriptor smDescriptor = findSMDescriptor(smId, smDescriptors);
			updateSMEndpoint(smDescriptor);
			registry.register(desc.getIdentifier(), smDescriptor);
		});
	}

	private void updateSMEndpoint(SubmodelDescriptor smDescriptor) {
		String smEndpoint = getSMEndpoint(smDescriptor.getIdentifier());
		String firstEndpoint = smDescriptor.getFirstEndpoint();
		if (firstEndpoint.isEmpty()) {
			smDescriptor.removeEndpoint("");
		} else if (firstEndpoint.equals("/submodel")) {
			smDescriptor.removeEndpoint("/submodel");
		}
		smDescriptor.addEndpoint(smEndpoint);
	}

	private SubmodelDescriptor findSMDescriptor(String smId, Collection<SubmodelDescriptor> smDescriptors) {
		for (SubmodelDescriptor smDesc : smDescriptors) {
			if (smDesc.getIdentifier().getId().equals(smId)) {
				return smDesc;
			}
		}
		return null;
	}

	private String getSMEndpoint(IIdentifier smId) {
		String aasId = getAASIdFromSMId(smId);
		String encodedAASId = VABPathTools.encodePathElement(aasId);
		String aasBasePath = VABPathTools.concatenatePaths(getURL(), encodedAASId, "aas");
		String smIdShort = getSMIdShortFromSMId(smId);
		return VABPathTools.concatenatePaths(aasBasePath, "submodels", smIdShort, "submodel");
	}

	private String getSMIdShortFromSMId(IIdentifier smId) {
		for (AASBundle bundle : aasBundles) {
			for (ISubmodel sm : bundle.getSubmodels()) {
				if (smId.getId().equals(sm.getIdentification().getId())) {
					return sm.getIdShort();
				}
			}
		}
		throw new ResourceNotFoundException("Submodel in registry whitelist not found in AASBundle");
	}

	private String getAASIdFromSMId(IIdentifier smId) {
		for (AASBundle bundle : aasBundles) {
			for (ISubmodel sm : bundle.getSubmodels()) {
				if (smId.getId().equals(sm.getIdentification().getId())) {
					return bundle.getAAS().getIdentification().getId();
				}
			}
		}
		throw new ResourceNotFoundException("Submodel in registry whitelist does not belong to any AAS in AASBundle");
	}

	/**
	 * Fixes the File submodel element value paths according to the given endpoint
	 * configuration
	 */
	private void modifyFilePaths(String hostName, int port, String rootPath) {
		rootPath = rootPath + "/files";
		for (AASBundle bundle : aasBundles) {
			Set<ISubmodel> submodels = bundle.getSubmodels();
			for (ISubmodel sm : submodels) {
				SubmodelFileEndpointLoader.setRelativeFileEndpoints(sm, hostName, port, rootPath);
			}
		}
	}

	private String getMqttAASClientId() {
		if (aasBundles == null || aasBundles.isEmpty()) {
			return "defaultNoShellId";
		}
		return aasBundles.stream().findFirst().get().getAAS().getIdShort();
	}

	private String getMqttSubmodelClientId() {
		return getMqttAASClientId() + "/submodelAggregator";
	}
}
