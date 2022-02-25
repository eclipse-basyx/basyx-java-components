package basyx.components.updater.camelprometheus.configuration.factory;

import basyx.components.updater.camelprometheus.configuration.PrometheusConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSinkConfigurationFactory;

/**
 * A default configuration factory for polling Prometheus data from a default file location
 * @author n14s - Niklas Mertens
 *
 */
public class PrometheusDefaultConfigurationFactory extends DataSinkConfigurationFactory {
	private static final String FILE_PATH = "prometheus.json";
	
	public PrometheusDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, PrometheusConsumerConfiguration.class);
	}
	
	public PrometheusDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, PrometheusConsumerConfiguration.class);
	}
}
