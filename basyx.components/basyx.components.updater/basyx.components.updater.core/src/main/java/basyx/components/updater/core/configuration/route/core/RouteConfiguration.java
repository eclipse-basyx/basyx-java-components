package basyx.components.updater.core.configuration.route.core;

import java.util.ArrayList;
import java.util.List;

public abstract class RouteConfiguration {
	protected String routeType;
	protected String routeId;
	protected String datasource;
	protected List<String> transformers = new ArrayList<>();

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteType() {
		return routeType;
	}

	public List<String> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<String> transformers) {
		this.transformers = transformers;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

}
