package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.AbstractMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;
import javax.annotation.Resource;
import org.springframework.context.MessageSource;

/**
 *
 * @author Mickael Dubois
 */
public class PropertyMonitoring extends AbstractMonitor {

    @Resource(name = "monitoringPropertyMessageSource")
    private MessageSource myProperties;

    public PropertyMonitoring() {
        super("Property validation", Type.OTHER);
    }

    @Override
    public void doMonitor(TestElement monitoredElement) {
        String activated = myProperties.getMessage("activateNode", null, null);
        if ("true".equals(activated)) {
            monitoredElement.setTestIsOk();
        } else {
            monitoredElement.setTestIsKo("Node have been deactivated  using the cms-monitoring property file");
        }
    }

}
