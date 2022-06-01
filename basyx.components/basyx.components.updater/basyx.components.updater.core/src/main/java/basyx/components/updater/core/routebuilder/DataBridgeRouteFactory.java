package basyx.components.updater.core.routebuilder;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.updater.core.configuration.route.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.creator.IRouteCreator;

/**
 * This factory is used to create the apache camel routes for the data bridge
 * component
 *
 * @author fischer
 *
 */
public class DataBridgeRouteFactory extends RouteBuilder {
	private static final String ROUTE_ID_PREFIX = "route";
	private RoutesConfiguration configuration;
	Map<String, IRouteCreator> routeCreatorMap = new HashMap<>();

	public DataBridgeRouteFactory(RoutesConfiguration configuration) {
		this.configuration = configureRouteIds(configuration);
	}

	@Override
	public void configure() throws Exception {
		for (IRouteConfiguration routeConfig : configuration.getRoutes()) {
			IRouteCreator routeCreator = routeCreatorMap.get(routeConfig.getRouteType());
			routeCreator.addRouteToRouteBuilder(routeConfig);
		}
	}

	public void addRouteCreator(IRouteCreator routeCreator) {
		routeCreator.setBuilder(this);
		routeCreator.setRoutesConfiguration(configuration);

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
