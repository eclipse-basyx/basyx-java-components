package basyx.components.updater.core.configuration.route.timer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import basyx.components.updater.core.configuration.route.core.IRouteCreator;
import basyx.components.updater.core.configuration.route.core.RouteConfiguration;
import basyx.components.updater.core.configuration.route.core.RouteCreatorHelper;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class TimerRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	public TimerRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		this.routeBuilder = routeBuilder;
		this.routesConfiguration = routesConfiguration;
	}

	@Override
	public void addRouteToRouteBuilder(RouteConfiguration routeConfig) {
		TimerRouteConfiguration timerRouteConfig = (TimerRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, timerRouteConfig.getDatasource());
		String[] dataSinkEndpoints = RouteCreatorHelper.getDataSinkEndpoints(routesConfiguration, timerRouteConfig.getDatasinks());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, timerRouteConfig.getTransformers());
		String routeId = timerRouteConfig.getRouteId();

		RouteDefinition routeDefinition = routeBuilder.from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId);

		if (!(dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0)) {
			routeDefinition.to(dataTransformerEndpoints).to("log:" + routeId);
		}

		routeDefinition.to(dataSinkEndpoints[1]).to("log:" + routeId);
	}

}
