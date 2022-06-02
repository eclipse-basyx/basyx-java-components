package basyx.components.updater.core.configuration.route.creator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.updater.core.configuration.route.configuration.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.configuration.SimpleRouteConfiguration;

public class SimpleRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	public SimpleRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		this.routeBuilder = routeBuilder;
		this.routesConfiguration = routesConfiguration;
	}

	@Override
	public void addRouteToRouteBuilder(IRouteConfiguration routeConfig) {
		SimpleRouteConfiguration simpleRouteConfig = (SimpleRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, simpleRouteConfig.getDatasource());
		String dataSinkEndpoints = RouteCreatorHelper.getDataSinkEndpoint(routesConfiguration, simpleRouteConfig.getDatasink());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, simpleRouteConfig.getTransformers());
		String routeId = simpleRouteConfig.getRouteId();

		if (!Strings.isNullOrEmpty(routeConfig.getDelegator())) {
			throw new RuntimeException("Delegator is currently not supported");
		}

		if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
			routeBuilder.from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId).to(dataSinkEndpoints).to("log:" + routeId);
		} else {
			routeBuilder.from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId).to(dataTransformerEndpoints).to("log:" + routeId).to(dataSinkEndpoints).to("log:" + routeId);
		}
	}

}
