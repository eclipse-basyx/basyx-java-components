package basyx.components.updater.camelhttp.configuration.factory;

import basyx.components.updater.camelhttp.configuration.HttpConsumerConfiguration;
import basyx.components.updater.core.configuration.factory.DataSinkConfigurationFactory;
import basyx.components.updater.core.configuration.factory.DataSourceConfigurationFactory;

/**
 * A default configuration factory for Http polling from a default file location
 * @author Niklas Mertens
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
