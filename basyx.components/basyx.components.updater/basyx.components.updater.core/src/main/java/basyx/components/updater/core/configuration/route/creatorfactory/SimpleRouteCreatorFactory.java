package basyx.components.updater.core.configuration.route.creatorfactory;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.creator.IRouteCreator;
import basyx.components.updater.core.configuration.route.creator.SimpleRouteCreator;

public class SimpleRouteCreatorFactory implements IRouteCreatorFactory {

	@Override
	public IRouteCreator create(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		return new SimpleRouteCreator(routeBuilder, routesConfiguration);
	}

}
