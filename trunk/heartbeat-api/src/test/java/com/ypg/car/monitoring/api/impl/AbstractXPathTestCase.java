
package com.ypg.car.monitoring.api.impl;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Classe offrant de sservices d'assertions sur un DOM, via XPath. La plupart
 * des m�thodes utiles sont d�j� en standard dans XMLTestCase de la libraire
 * XMLUnit, dont cette classe h�rite. Cette pr�sente classe apporte quelques
 * compl�ments utiles.
 */
public abstract class AbstractXPathTestCase extends XMLTestCase {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractXPathTestCase.class);

    /**
     * Verbosit�. Mettre � true pour activer des logs, notamment des logs des
     * messages XML.
     */
    private boolean verbose = true;

    /**
     * En mode Verbose
     * 
     * @return oui ou non
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Definition du mode verbode
     * @param verbose oui/non
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Teste si le r�sultat d'une expression XPath, appliqu�e sur le DOM
     * sp�cifi�, contient une cha�ne attendue. Fait �chouer le test JUnit le cas
     * �ch�ant.
     * 
     * @param document DOM sur lequel appliquer l'assertion
     * @param xpathExpression expression XPath 1
     * @param contains valeur attendue
     * @throws XpathException en cas de pb avec l'expression XPath.
     */
    protected void assertXpathContains(Document document, String xpathExpression, String contains)
                    throws XpathException {
        if (document != null && xpathExpression != null && contains != null) {
            final String containsXPath = "contains(" + xpathExpression + ",'" + contains + "')";
            assertXpathEvaluatesTo("true", containsXPath, document);
        }
    }

    /**
     * Teste si le r�sultat d'une expression XPath, appliqu�e sur le DOM
     * sp�cifi�, n'est pas vide. Sert en fait � tester si :
     * <ul>
     * <li>L'�l�ment XML d�sign� par l'expression est pr�sent</li>
     * <li>Et : le contenu de l'�l�ment n'est pas vide.</li>
     * </ul>
     * Fait �chouer le test JUnit le cas �ch�ant.
     * 
     * @param document DOM sur lequel appliquer l'assertion
     * @param xpathExpression expression XPath 1
     * @throws XpathException en cas de pb avec l'expression XPath.
     */
    protected void assertNotBlank(Document document, String xpathExpression) throws XpathException {
        assertXpathExists(xpathExpression, document);
        assertXpathEvaluatesTo("true", "string-length(" + xpathExpression + ")>0", document);
    }

    /**
     * R�cup�re un DOM, en ayant v�rifi� sa conformit� au sch�ma.
     * 
     * @return DOM
     * @param xml flux XML dont il faut obtenir le DOM
     * @throws ParserConfigurationException pb de config de JAXP
     * @throws SAXException pb de parsing XML
     * @throws IOException pb d'I/O
     */
    protected Document getValidatedDOM(String xml) throws ParserConfigurationException, SAXException, IOException {
        if (verbose) {
            final String separator = "--------------------------------------------------------";
            LOGGER.info(separator);
            LOGGER.info("{}.{} XML : ", this.getClass().getName(), getName());
            LOGGER.info(separator);
            LOGGER.info(xml);
        }

        // Validation du XML :

        final Validator validator = new Validator(xml);
        validator.useXMLSchema(true);


        validator.setJAXP12SchemaSource(this.getClass().getClassLoader().getResourceAsStream("xsd/monitoring.xsd"));
       // validator.setJAXP12SchemaSource(new File("xsd/monitoring.xsd"));
        assertXMLValid(validator);

        // Construction d'un DOM :

        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try {
            return documentBuilder.parse(new InputSource(new StringReader(xml)));
        } catch (SAXParseException ex) {
            fail("Erreur de parsing. Le document XML ne respecte peut-�tre pas le sch�ma.\nException d'origine : "
                            + ex.toString() + "\nXML :\n" + xml + "\n");
            // On ne passe jamais ici :
            return null;
        }
    }

}
