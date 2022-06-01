package basyx.components.updater.core.configuration.route.creator;

import org.apache.camel.builder.RouteBuilder;

import basyx.components.updater.core.configuration.route.IRouteConfiguration;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;

public interface IRouteCreator {

	public void addRouteToRouteBuilder(IRouteConfiguration routeConfig);

	public RouteBuilder getBuilder();

	public void setBuilder(RouteBuilder routeBuilder);

	public RoutesConfiguration getRoutesConfiguration();

	public void setRoutesConfiguration(RoutesConfiguration routesConfiguration);
}
