package io.sovaj.heartbeat.api.impl;

import io.sovaj.heartbeat.api.impl.MonitoringSession;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Socle commun aux tests de {@link MonitoringSessionImpl}.
 */
public abstract class AbstractMonitoringSessionTestCase extends AbstractXPathTestCase {

    /**
     * Objet test�.
     */
    protected MonitoringSession monitoringSession;

    /**
     * R�cup�re un DOM, en ayant v�rifi� sa conformit� au sch�ma.
     * 
     * @return DOM
     * @throws ParserConfigurationException pb de config de JAXP
     * @throws SAXException pb de parsing XML
     * @throws IOException pb d'I/O
     */
    protected Document getValidatedDOM() throws ParserConfigurationException, SAXException, IOException {
        return getValidatedDOM(monitoringSession.toXML(true));
    }

}
