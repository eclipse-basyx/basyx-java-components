package basyx.components.updater.core.configuration.route.creatorfactory;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.creator.IRouteCreator;

public interface IRouteCreatorFactory {

	public IRouteCreator create(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration);

}
