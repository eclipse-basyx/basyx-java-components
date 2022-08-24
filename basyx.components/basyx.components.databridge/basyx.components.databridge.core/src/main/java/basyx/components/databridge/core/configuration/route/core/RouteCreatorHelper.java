package basyx.components.databridge.core.configuration.route.core;

import java.util.ArrayList;
import java.util.List;

public class RouteCreatorHelper {
	private RouteCreatorHelper() {
	}

	public static String getDataSourceEndpoint(RoutesConfiguration routesConfiguration, String dataSourceId) {
		return routesConfiguration.getDatasources().get(dataSourceId).getConnectionURI();
	}

	public static String getDataSinkEndpoint(RoutesConfiguration routesConfiguration, String dataSinkId) {
		return routesConfiguration.getDatasinks().get(dataSinkId).getConnectionURI();
	}

	public static String[] getDataSinkEndpoints(RoutesConfiguration routesConfiguration, List<String> dataSinkIdList) {
		List<String> endpoints = new ArrayList<>();
		for (String dataSinkId : dataSinkIdList) {
			endpoints.add(routesConfiguration.getDatasinks().get(dataSinkId).getConnectionURI());
		}

		return endpoints.toArray(new String[0]);
	}

	public static String[] getDataTransformerEndpoints(RoutesConfiguration routesConfiguration, List<String> transformerIdList) {
		List<String> endpoints = new ArrayList<>();
		for (String transformerId : transformerIdList) {
			endpoints.add(routesConfiguration.getTransformers().get(transformerId).getConnectionURI());
		}
		return endpoints.toArray(new String[0]);
	}
}
