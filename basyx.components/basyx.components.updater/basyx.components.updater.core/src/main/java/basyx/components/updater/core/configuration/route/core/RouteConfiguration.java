package basyx.components.updater.core.configuration.route.core;

import java.util.ArrayList;
import java.util.List;

public abstract class RouteConfiguration {
	private String routeType;
	private String routeId;
	private String datasource;
	private List<String> transformers = new ArrayList<>();
	private List<String> datasinks = new ArrayList<>();

	/**
	 * @param routeType
	 * @param routeId
	 * @param datasource
	 * @param transformers
	 * @param datasinks
	 */
	public RouteConfiguration(String routeType, String datasource, List<String> transformers, List<String> datasinks) {
		this.routeType = routeType;
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
	}

	public List<String> getDatasinks() {
		return datasinks;
	}

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
