package basyx.components.updater.core.configuration.route.creator;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tooling.model.Strings;

import basyx.components.updater.core.configuration.route.configuration.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.configuration.TimerRouteConfiguration;

public class TimerRouteCreator implements IRouteCreator {
	private RouteBuilder routeBuilder;
	private RoutesConfiguration routesConfiguration;

	public TimerRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		this.routeBuilder = routeBuilder;
		this.routesConfiguration = routesConfiguration;
	}

	@Override
	public void addRouteToRouteBuilder(IRouteConfiguration routeConfig) {
		TimerRouteConfiguration timerRouteConfig = (TimerRouteConfiguration) routeConfig;

		String dataSourceEndpoint = RouteCreatorHelper.getDataSourceEndpoint(routesConfiguration, timerRouteConfig.getDatasource());
		String[] dataSinkEndpoints = RouteCreatorHelper.getDataSinkEndpoints(routesConfiguration, timerRouteConfig.getDatasinks());
		String[] dataTransformerEndpoints = RouteCreatorHelper.getDataTransformerEndpoints(routesConfiguration, timerRouteConfig.getTransformers());
		String routeId = timerRouteConfig.getRouteId();

		if (!Strings.isNullOrEmpty(routeConfig.getDelegator())) {
			throw new RuntimeException("Delegator is currently not supported");
		}

		if (dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0) {
			routeBuilder.from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId).to(dataSinkEndpoints[1]).to("log:" + routeId);
		} else {
			routeBuilder.from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId).to(dataTransformerEndpoints).to("log:" + routeId).to(dataSinkEndpoints[1]).to("log:" + routeId);
		}
	}
}
