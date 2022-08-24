package basyx.components.updater.core.configuration.route.event;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import basyx.components.updater.core.configuration.route.core.AbstractRouteCreator;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class EventRouteCreator extends AbstractRouteCreator {

	public EventRouteCreator(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		super(routeBuilder, routesConfiguration);
	}

	@Override
	protected void configureRoute(String dataSourceEndpoint, String[] dataSinkEndpoints, String[] dataTransformerEndpoints, String routeId) {
		RouteDefinition routeDefinition = getRouteBuilder().from(dataSourceEndpoint).routeId(routeId).to("log:" + routeId);

		if (!(dataTransformerEndpoints == null || dataTransformerEndpoints.length == 0)) {
			routeDefinition.to(dataTransformerEndpoints).to("log:" + routeId);
		}

		routeDefinition.to(dataSinkEndpoints[0]).to("log:" + routeId);
	}

}
