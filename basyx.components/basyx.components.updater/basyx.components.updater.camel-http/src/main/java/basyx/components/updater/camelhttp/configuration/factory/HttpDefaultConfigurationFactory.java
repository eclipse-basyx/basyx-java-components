package basyx.components.updater.camelhttp.configuration.factory;

import basyx.components.updater.camelhttp.configuration.HttpConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSinkConfigurationFactory;

/**
 * A default configuration factory for Http polling from a default file location
 * @author n14s - Niklas Mertens
 *
 */
public class HttpDefaultConfigurationFactory extends DataSinkConfigurationFactory {
	private static final String FILE_PATH = "httpconsumer.json";
	
	public HttpDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, HttpConsumerConfiguration.class);
	}
	
	public HttpDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, HttpConsumerConfiguration.class);
	}
}
