package basyx.components.updater.core.configuration.route.timer;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.core.IRouteCreator;
import basyx.components.updater.core.configuration.route.core.IRouteCreatorFactory;
import basyx.components.updater.core.configuration.route.core.RoutesConfiguration;

public class TimerRouteCreatorFactory implements IRouteCreatorFactory {

	@Override
	public IRouteCreator create(RouteBuilder routeBuilder, RoutesConfiguration routesConfiguration) {
		return new TimerRouteCreator(routeBuilder, routesConfiguration);
	}

}
