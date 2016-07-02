package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;
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
