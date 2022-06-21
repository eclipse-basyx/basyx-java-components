package basyx.components.updater.core.configuration.route.timer;

import basyx.components.updater.core.configuration.route.sources.RouteEntity;

/**
 * A generic class of Timer Configuration
 * @author n14s
 *
 */

public abstract class TimerConfiguration extends RouteEntity {

    public TimerConfiguration() {
        super();
    }

    public TimerConfiguration(String uniqueId) {
        super(uniqueId);
    }
}

