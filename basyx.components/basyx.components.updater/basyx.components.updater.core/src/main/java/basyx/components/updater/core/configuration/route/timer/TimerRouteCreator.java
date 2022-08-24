package basyx.components.updater.core.configuration.route.timer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import basyx.components.updater.core.configuration.route.core.AbstractRouteCreator;
import basyx.components.updater.core.configuration.route.core.RouteConfiguration;
import basyx.components.updater.core.configuration.route.core.RouteCreatorHelper;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class TimerRouteCreator extends AbstractRouteCreator {

	public TimerRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		super(routeBuilder, routesConfiguration);
	}

	@Override
	protected void configureRoute(RouteConfiguration routeConfig, String dataSourceEndpoint, String[] dataSinkEndpoints, String[] dataTransformerEndpoints, String routeId) {
		TimerRouteConfiguration timerConfig = (TimerRouteConfiguration) routeConfig;
		String timerEndpoint = RouteCreatorHelper.getDataSourceEndpoint(getRoutesConfiguration(), timerConfig.getTimerName());
		RouteDefinition routeDefinition = getRouteBuilder().from(timerEndpoint).to(dataSourceEndpoint).routeId(routeId).to("log:" + routeId);

		if (!(dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0)) {
			routeDefinition.to(dataTransformerEndpoints).to("log:" + routeId);
		}

		routeDefinition.to(dataSinkEndpoints[0]).to("log:" + routeId);
	}

}
