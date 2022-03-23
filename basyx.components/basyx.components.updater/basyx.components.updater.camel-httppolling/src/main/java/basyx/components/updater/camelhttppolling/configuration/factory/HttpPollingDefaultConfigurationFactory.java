package basyx.components.updater.camelhttppolling.configuration.factory;

import basyx.components.updater.camelhttppolling.configuration.HttpPollingConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSinkConfigurationFactory;

/**
 * A default configuration factory for Http polling from a default file location
 * @author Niklas Mertens
 *
 */
public class HttpPollingDefaultConfigurationFactory extends DataSinkConfigurationFactory {
	private static final String FILE_PATH = "httpconsumer.json";
	
	public HttpPollingDefaultConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, HttpPollingConsumerConfiguration.class);
	}
	
	public HttpPollingDefaultConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, HttpPollingConsumerConfiguration.class);
	}
}
