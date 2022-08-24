package basyx.components.updater.core.configuration.route.timer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import basyx.components.updater.core.configuration.route.core.AbstractRouteCreator;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class TimerRouteCreator extends AbstractRouteCreator {

	public TimerRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		super(routeBuilder, routesConfiguration);
	}

	@Override
	protected void configureRoute(String dataSourceEndpoint, String[] dataSinkEndpoints, String[] dataTransformerEndpoints, String routeId) {
		RouteDefinition routeDefinition = getRouteBuilder().from(dataSourceEndpoint).to(dataSinkEndpoints[0]).routeId(routeId).to("log:" + routeId);

		if (!(dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0)) {
			routeDefinition.to(dataTransformerEndpoints).to("log:" + routeId);
		}

		routeDefinition.to(dataSinkEndpoints[1]).to("log:" + routeId);
	}

}
