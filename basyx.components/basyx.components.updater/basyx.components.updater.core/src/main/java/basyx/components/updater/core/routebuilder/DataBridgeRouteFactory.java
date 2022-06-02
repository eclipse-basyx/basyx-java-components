package basyx.components.updater.core.routebuilder;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.updater.core.configuration.route.configuration.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.creator.IRouteCreator;
import basyx.components.updater.core.configuration.route.creatorfactory.IRouteCreatorFactory;

/**
 * This factory is used to create the apache camel routes for the data bridge
 * component
 *
 * @author fischer
 *
 */
public class DataBridgeRouteFactory extends RouteBuilder {
	private static final String ROUTE_ID_PREFIX = "route";
	private RoutesConfiguration routesConfiguration;
	private Map<String, IRouteCreatorFactory> routeCreatorFactoryMap;

	public DataBridgeRouteFactory(RoutesConfiguration configuration, Map<String, IRouteCreatorFactory> routeCreatorFactoryMap) {
		this.routesConfiguration = configureRouteIds(configuration);
		this.routeCreatorFactoryMap = routeCreatorFactoryMap;
	}

	@Override
	public void configure() throws Exception {
		for (IRouteConfiguration routeConfig : routesConfiguration.getRoutes()) {
			IRouteCreator routeCreator = routeCreatorFactoryMap.get(routeConfig.getRouteType()).create(this, routesConfiguration);

			routeCreator.addRouteToRouteBuilder(routeConfig);
		}
	}

	public void addRouteCreatorFactory(String key, IRouteCreatorFactory routeCreatorFactory) {
		routeCreatorFactoryMap.put(key, routeCreatorFactory);
	}

	private RoutesConfiguration configureRouteIds(RoutesConfiguration routesConfiguration) {
		long incrementalId = 1;
		for (IRouteConfiguration route : routesConfiguration.getRoutes()) {
			if (Strings.isNullOrEmpty(route.getRouteId())) {
				route.setRouteId(ROUTE_ID_PREFIX + incrementalId);
				incrementalId++;
			}
		}

		return routesConfiguration;
	}
}
