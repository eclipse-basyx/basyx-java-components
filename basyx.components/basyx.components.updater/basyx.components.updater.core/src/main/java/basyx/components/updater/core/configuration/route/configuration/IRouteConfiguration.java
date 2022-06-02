package basyx.components.updater.core.configuration.route.configuration;

import java.util.List;

public interface IRouteConfiguration {
	public String getRouteId();

	public void setRouteId(String routeId);

	public String getRouteType();

	public List<String> getTransformers();

	public void setTransformers(List<String> transformers);

	public String getDelegator();
}
