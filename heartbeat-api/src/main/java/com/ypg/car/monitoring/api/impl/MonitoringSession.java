
package com.ypg.car.monitoring.api.impl;

import com.ypg.car.monitoring.api.IMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;
import com.ypg.car.monitoring.api.jaxb2.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Permet d'ex�cuter un certain nombre de tests, de collecter les r�sultats de
 * l'ex�cution et de produire ce r�sultat sous forme XML. Le flux XML correspond
 * � un sch�ma XSD pr�cis. Cette classe est � usage interne, elle ne doit pas
 * �tre utilis�e directement par les applications utilisatrices.
 */
public class MonitoringSession {

    private final JAXBContext jaxbContext;

    /**
     * Message d'erreur.
     */
    private static final String MSG_NON_EXECUTED_TEST = "Non-executed test";

    /**
     * Niveau d'indentation du XML.
     */
    private static final int INDENT = 4;

    /**
     * Les stack traces remont�es dans le message XML ne d�passeront pas
     * MAX_LINE_ALLOWED_STACK_TRACE lignes.
     */
    private static final int MAX_LINE_ALLOWED_STACK_TRACE = 20;

    /**
     * Dur�e sp�ciale servant � signifier "dur�e invalide".
     */
    private static final long INVALID_DURATION = -9999L;

    /**
     * El�ment XML Application associ� � la session.
     */
    private ApplicationTest application;

    /**
     * Cr�� une instance de session de monitoring
     *
     * @param webAppName nom de l'application � monitorer
     */
    public MonitoringSession(String webAppName) {
        this(webAppName, null, null);
    }

    /**
     * Cr�� une instance de session de monitoring
     *
     * @param webAppName nom de l'application � monitorer
     * @param nodeName   nom du noeud WAS o� se trouve l'application � monitorer
     */
    public MonitoringSession(String webAppName, String nodeName) {
        this(webAppName, nodeName, null);
    }

    /**
     * Cr�� une instance de session de monitoring
     *
     * @param webAppName    nom de l'application � monitorer
     * @param nodeName      nom du noeud WAS o� se trouve l'application � monitorer
     * @param appServerName nom du serveur physique sur lequel se trouve le WAS
     */
    public MonitoringSession(String webAppName, String nodeName, String appServerName) {

        application = new ApplicationTest();
        application.setWebAppName(webAppName);
        final Calendar now = Calendar.getInstance(Locale.US);
        now.setTime(new Date());
        application.setStartMonitoring(now);
        application.setNodeName(nodeName);
        application.setAppServerName(appServerName);
        application.setSchemaVersion(1.0);

        try {
            jaxbContext = JAXBContext.newInstance(ApplicationTest.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Could not initialize JaxBContext : " + e.getMessage(), e);
        }
    }

    /**
     * Ex�cution d'un test technique.
     *
     * @param mon impl�mentation du test � ex�cuter.
     */
    public void doTechnicalTest(IMonitor mon) {
        checkMandatory("mon", mon);
        final TestElement testResult = mon.monitor();
        addTechnicalTest(testResult);
    }

    /**
     * Ex�cution d'un test de d�pendance.
     *
     * @param mon impl�mentation du test � ex�cuter.
     */
    public void doDependencyTest(IMonitor mon) {
        checkMandatory("mon", mon);
        final TestElement testResult = mon.monitor();

        addDependencyTest(testResult);
    }

    /**
     * Rajoute un test technique � la session de monitoring.
     *
     * @param monitoredElement �l�ment � monitorer
     */
    private void addTechnicalTest(TestElement monitoredElement) {
        if (application.getTechnicalTests() == null) {
            application.setTechnicalTests(newGroup());
        }
        addTestToGroup(monitoredElement, application.getTechnicalTests());
    }

    /**
     * Rajoute un test de d�pendance � la session de monitoring.
     *
     * @param monitoredElement �l�ment � monitorer
     */
    private void addDependencyTest(TestElement monitoredElement) {
        if (application.getDependencyTests() == null) {
            application.setDependencyTests(newGroup());
        }
        addTestToGroup(monitoredElement, application.getDependencyTests());
    }

    /**
     * Retourn le r�sultat des tests de monitoring au format XML. Dans le cas
     * d'une servlet de monitoring, c'est cette m�thode qui doit �tre utilis�e
     * pour retourner le flux XML.
     *
     * @return r�sultat des tests de monitoring au format XML
     */
    public String toXML(boolean technical) {
        verify(technical);
        String resultXml = null;
        try {
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            m.marshal(application, result);
            resultXml = writer.getBuffer().toString();
        } catch (Exception e) {
            resultXml = getStackTrace(e);
        }

        return resultXml;
    }

    public boolean isAllTestsOK(boolean technical, boolean functionnal) {
        verify(technical);
        boolean isOk = true;

        if (technical && application.getTechnicalTests()!=null)
            isOk = isOk && application.getTechnicalTests().getStatus() == GroupStatus.OK;
        if (functionnal && application.getDependencyTests()!=null)
            isOk = isOk && application.getDependencyTests().getStatus() == GroupStatus.OK;
      return isOk;
    }

    /**
     * V�rification de la coh�rence du mod�le associ� � la session de
     * monitoring.
     */
    private void verify(boolean verifyTechnical) {
        // Must have a webapp name
        if (application.getWebAppName() == null || application.getWebAppName().trim().length() == 0) {
            application.setWebAppName("NEEDS A WEBAPP NAME !!!");
        }
        // Must have at least one technical test

        if (verifyTechnical && application.getTechnicalTests() == null) {
            final Group technicalTests = new Group();
            application.setTechnicalTests(technicalTests);
            technicalTests.setStatus(GroupStatus.KO);
            final Test newTest = new Test();
            technicalTests.getTests().add(newTest);
            newTest.setName("no-technical-tests");
            newTest.setErrorMessage("This application has no technical tests.");
            newTest.setStatus(Status.KO);
            newTest.setType(Type.OTHER.toString());
            newTest.setDuration(0);
        }
    }

    /**
     * Cr�ation d'un noeud XML "Test" � partir d'un TestElement.
     *
     * @param testedElement TestElement
     * @param xmlTest       noeud XML de type Test, � cr�er vide via les m�thodes de
     *                      XmlBeans.
     * @return noeud XML
     */
    private Test initTestXmlElement(TestElement testedElement, Test xmlTest) {

        xmlTest.setName(testedElement.getName());

        // Si j'ai un type, j'affecte le type.
        // On ne doit pas faire set(null) sinon XmlBeans produira un �l�ment de
        // type xsi:nil qui est ind�sirable : on pr�f�re l'absence de
        // l'�l�ment.
        if (testedElement.getType() != null) {
            xmlTest.setType(testedElement.getType().toString());
        }

        // M�me principe pour "description" :
        if (testedElement.getDescription() != null) {
            xmlTest.setDescription(testedElement.getDescription());
        }

        // Calcul du status :

        Status status;
        String errorMessage = testedElement.getErrorMessage();
        if (testedElement.getStatus() == null) {
            // Il faut un status. Si on n'en a pas, on passe le test en KO.
            status = Status.KO;
            errorMessage = MSG_NON_EXECUTED_TEST;
        } else {
            // Lecture du status :
            status = Status.valueOf(testedElement.getStatus().toString());
        }

        // Calcul de la dur�e d'ex�cution :

        long duration;
        if (testedElement.getStart() == null || testedElement.getStop() == null) {
            // Il faut une date de d�but et de fin d'ex�uction du test.
            // Ici, on n'en a pas : on passe donc le test en KO.
            status = Status.KO;
            errorMessage = MSG_NON_EXECUTED_TEST;
            duration = INVALID_DURATION;
        } else {
            // Calcul de la dur�e d'ex�cution du test :
            duration = testedElement.getStop().getTime() - testedElement.getStart().getTime();
        }

        // Affectation des noeuds status, duration et error-message :

        xmlTest.setStatus(status);

        xmlTest.setDuration(duration);

        if (errorMessage == null) {
            errorMessage = testedElement.getErrorMessage();
        }
        if (errorMessage != null) {
            xmlTest.setErrorMessage(errorMessage);
        }

        // Calcul de la stack trace :

        if (testedElement.getException() != null) {
            final String stack = getStackTrace(testedElement.getException());
            xmlTest.setStackTrace(stack);
        }

        return xmlTest;
    }

    /**
     * Cr�ation d'un nouveau groupe.
     *
     * @return groupe.
     */
    private Group newGroup() {
        final Group newGroup = new Group();
        newGroup.setStatus(GroupStatus.OK);
        return newGroup;
    }

    /**
     * Ajout d'un test � un groupe de tests et met � jour le status du groupe.
     *
     * @param monitoredElement test � ajouter.
     * @param group            groupe cible
     */
    protected void addTestToGroup(TestElement monitoredElement, Group group) {
        Test testXml = new Test();
        group.getTests().add(testXml);
        initTestXmlElement(monitoredElement, testXml);

        // Mise � jour du status :
        int severity = 0;
        final GroupStatus[] status = new GroupStatus[]{GroupStatus.OK, GroupStatus.KO};
        final List<Test> tests = group.getTests();
        for (Test test : tests) {
            // Association entre niveau de s�v�rit� et status, afin
            // de permettre les comparaisons :
            int curSeverity;
            switch (test.getStatus()) {
                case OK:
                case SKIP:
                    curSeverity = 0;
                    break;
                case KO:
                    curSeverity = 1;
                    break;
                default:
                    // Cas impossible dans la pratique, sauf si on ajoute un nouveau
                    // status
                    // pas encore compris de ce code.
                    throw new IllegalStateException("Unknown Status enum value: " + test.getStatus().toString());
            }

            // Comparaison de la s�v�rit� et adaptation de la s�v�rit� du
            // groupe :
            if (curSeverity > severity) {
                severity = curSeverity;
            }
        }
        group.setStatus(status[severity]);
    }

    /**
     * Only gets the 20 first rows of the stack trace.
     *
     * @param anException exception
     * @return stacktrace
     */
    private String getStackTrace(Throwable anException) {
        final StringBuffer buffer = new StringBuffer();
        StackTraceElement[] stackTraceElements;

        stackTraceElements = anException.getStackTrace();
        for (int i = 0; i < stackTraceElements.length && i < MAX_LINE_ALLOWED_STACK_TRACE; i++) {
            buffer.append(stackTraceElements[i].toString()).append('\n');
        }

        if (anException.getCause() != null) {
            buffer.append("(...)\n");
            stackTraceElements = anException.getCause().getStackTrace();
            for (int i = 0; i < stackTraceElements.length && i < MAX_LINE_ALLOWED_STACK_TRACE; i++) {
                buffer.append(stackTraceElements[i].toString()).append('\n');
            }
        }

        return buffer.toString();
    }

    /**
     * V�rifie si une cha�ne est null ou vide. On n'utilise pas commons-lang car
     * une des contraintes de cette API est d'avoir le moins possible de
     * d�pendances.
     *
     * @param value cha�ne � v�rifier
     * @return true si cha�ne vide
     */
    protected boolean isBlank(String value) {
        boolean isBlank;
        if (value == null) {
            isBlank = true;
        } else {
            isBlank = true;
            for (int i = 0; i < value.length() && isBlank; i++) {
                isBlank = Character.isWhitespace(value.charAt(i));
            }
        }
        return isBlank;
    }

    /**
     * V�rifie si un param�tre obligatoire est bien pr�sent. Lance une exception
     * le cas �ch�ant.
     *
     * @param paramName nom du param�tre
     * @param value     valeur du param�tre
     */
    private void checkMandatory(String paramName, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " is mandatory");
        }
    }

    /**
     * @return the application
     */
    public ApplicationTest getApplication() {
        return application;
    }

}
