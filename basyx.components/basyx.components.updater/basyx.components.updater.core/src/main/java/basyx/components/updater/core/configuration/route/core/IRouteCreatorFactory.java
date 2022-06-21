package basyx.components.updater.core.configuration.route.core;

import org.apache.camel.builder.RouteBuilder;

public interface IRouteCreatorFactory {

	public IRouteCreator create(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration);

}
