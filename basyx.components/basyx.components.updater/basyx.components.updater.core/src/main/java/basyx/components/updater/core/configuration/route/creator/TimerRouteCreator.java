package basyx.components.updater.core.configuration.route.creator;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.TimerRouteConfiguration;

public class TimerRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	@Override
	public void addRouteToRouteBuilder(IRouteConfiguration routeConfig) {
		TimerRouteConfiguration timerRouteConfig = (TimerRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, timerRouteConfig.getDatasource());
		String[] dataSinkEndpoints = RouteCreatorHelper.getDataSinkEndpoints(routesConfiguration, timerRouteConfig.getDatasinks());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, timerRouteConfig.getTransformers());
		String routeId = timerRouteConfig.getRouteId();

		if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
			routeBuilder.from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId).to(dataSinkEndpoints[1]).to("log:" + routeId);
		} else {
			routeBuilder.from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId).to(dataTransformerEndpoints).to("log:" + routeId).to(dataSinkEndpoints[1]).to("log:" + routeId);
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
