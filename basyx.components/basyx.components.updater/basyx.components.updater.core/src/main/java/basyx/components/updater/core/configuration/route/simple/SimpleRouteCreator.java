package basyx.components.updater.core.configuration.route.simple;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import basyx.components.updater.core.configuration.route.core.IRouteCreator;
import basyx.components.updater.core.configuration.route.core.RouteConfiguration;
import basyx.components.updater.core.configuration.route.core.RouteCreatorHelper;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class SimpleRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	public SimpleRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		this.routeBuilder = routeBuilder;
		this.routesConfiguration = routesConfiguration;
	}

	@Override
	public void addRouteToRouteBuilder(RouteConfiguration routeConfig) {
		SimpleRouteConfiguration simpleRouteConfig = (SimpleRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, simpleRouteConfig.getDatasource());
		String dataSinkEndpoints = RouteCreatorHelper.getDataSinkEndpoint(routesConfiguration, simpleRouteConfig.getDatasink());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, simpleRouteConfig.getTransformers());
		String routeId = simpleRouteConfig.getRouteId();

		RouteDefinition routeDefinition = routeBuilder.from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId);

		if (!(dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0)) {
			routeDefinition.to(dataTransformerEndpoints).to("log:" + routeId);
		}

		routeDefinition.to(dataSinkEndpoints).to("log:" + routeId);
	}

}
