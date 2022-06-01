package basyx.components.updater.core.configuration.route.creator;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.SimpleRouteConfiguration;

public class SimpleRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	@Override
	public void addRouteToRouteBuilder(IRouteConfiguration routeConfig) {
		SimpleRouteConfiguration simpleRouteConfig = (SimpleRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, simpleRouteConfig.getDatasource());
		String dataSinkEndpoint = RouteCreatorHelper.getDataSinkEndpoint(routesConfiguration, simpleRouteConfig.getDatasink());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, simpleRouteConfig.getTransformers());
		String routeId = simpleRouteConfig.getRouteId();

		if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
			routeBuilder.from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId).to(dataSinkEndpoint).to("log:" + routeId);
		} else {
			routeBuilder.from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId).to(dataTransformerEndpoints).to("log:" + routeId).to(dataSinkEndpoint).to("log:" + routeId);
		}
	}

	@Override
	public RouteBuilder getBuilder() {
		return routeBuilder;
	}

	@Override
	public void setBuilder(RouteBuilder routeBuilder) {
		this.routeBuilder = routeBuilder;
	}

	@Override
	public RoutesConfiguration getRoutesConfiguration() {
		return routesConfiguration;
	}

	@Override
	public void setRoutesConfiguration(RoutesConfiguration routesConfiguration) {
		this.routesConfiguration = routesConfiguration;
	}
}
