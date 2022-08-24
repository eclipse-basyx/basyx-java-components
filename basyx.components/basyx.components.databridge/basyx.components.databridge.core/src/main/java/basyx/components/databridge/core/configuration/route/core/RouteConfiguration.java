package basyx.components.databridge.core.configuration.route.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteConfiguration {
	private String trigger;
	private String routeId;
	private String datasource;
	private List<String> transformers = new ArrayList<>();
	private List<String> datasinks = new ArrayList<>();

	private Map<String, Object> triggerData = new HashMap<>();

	public RouteConfiguration() {
	}

	/**
	 * @param trigger
	 * @param routeId
	 * @param datasource
	 * @param transformers
	 * @param datasinks
	 */
	public RouteConfiguration(String trigger, String datasource, List<String> transformers, List<String> datasinks) {
		this.trigger = trigger;
		this.datasource = datasource;
		this.transformers = transformers;
		this.datasinks = datasinks;
	}

	public RouteConfiguration(RouteConfiguration configuration) {
		this(configuration.getRouteTrigger(), configuration.getDatasource(), configuration.getTransformers(), configuration.getDatasinks());
		setRouteId(configuration.getRouteId());
		this.triggerData = configuration.triggerData;
	}

	protected Map<String, Object> getTriggerData() {
		return triggerData;
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

	public String getRouteTrigger() {
		return trigger;
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
