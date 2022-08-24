package basyx.components.databridge.core.routebuilder;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.databridge.core.configuration.route.core.IRouteCreator;
import basyx.components.databridge.core.configuration.route.core.IRouteCreatorFactory;
import basyx.components.databridge.core.configuration.route.core.RouteConfiguration;
import basyx.components.databridge.core.configuration.route.core.RoutesConfiguration;

/**
 * This factory is used to create the apache camel routes for the data bridge
 * component
 *
 * @author fischer
 *
 */
public class DataBridgeRouteBuilder extends RouteBuilder {
	private static final String ROUTE_ID_PREFIX = "route";
	private RoutesConfiguration routesConfiguration;
	private Map<String, IRouteCreatorFactory> routeCreatorFactoryMap;

	public DataBridgeRouteBuilder(RoutesConfiguration configuration, Map<String, IRouteCreatorFactory> routeCreatorFactoryMap) {
		this.routesConfiguration = configureRouteIds(configuration);
		this.routeCreatorFactoryMap = routeCreatorFactoryMap;
	}

	@Override
	public void configure() throws Exception {
		for (RouteConfiguration routeConfig : routesConfiguration.getRoutes()) {
			IRouteCreator routeCreator = routeCreatorFactoryMap.get(routeConfig.getRouteTrigger()).create(this, routesConfiguration);

			routeCreator.addRouteToRouteBuilder(routeConfig);
		}
	}

	private RoutesConfiguration configureRouteIds(RoutesConfiguration routesConfiguration) {
		long incrementalId = 1;
		for (RouteConfiguration route : routesConfiguration.getRoutes()) {
			if (Strings.isNullOrEmpty(route.getRouteId())) {
				route.setRouteId(ROUTE_ID_PREFIX + incrementalId);
				incrementalId++;
			}
		}

		return routesConfiguration;
	}
}
