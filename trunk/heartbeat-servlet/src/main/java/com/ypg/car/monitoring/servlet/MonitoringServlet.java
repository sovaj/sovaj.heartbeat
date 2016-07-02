package com.ypg.car.monitoring.servlet;

import com.ypg.car.monitoring.api.IMonitoringService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet de monitoring standard. <a href=
 * "http://wiki.groupe.generali.fr/confluence/display/JEE/Servlet+de+monitoring"
 * >Voir doc ici</a>.
 */
public class MonitoringServlet extends HttpServlet {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -939986690782688346L;

    /**
     * Contexte Spring.
     */
    private transient ApplicationContext springContext;


    /**
     *
     */
    private boolean includeTechnicalTests = true;
    /**
     *
     */
    private boolean includeFunctionalTests = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        // Récupération du contexte Spring associé à la webapp :
        if (springContext == null) {
            springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        }


    }

    protected IMonitoringService getMonitoringServiceBean() throws ServletException {
        final Map<String, IMonitoringService> foundBeans = springContext.getBeansOfType(IMonitoringService.class);
        if (foundBeans.size() > 1) {
            // Plusieurs beans correspondants ont �t� trouv�s : comment les
            // d�partager ?
            final String msg =
                    String
                            .format(
                                    "failed to initialize MonitoringServlet: more than one bean of type %s found: %s",
                                    IMonitoringService.class.getName(), foundBeans.keySet()
                                    .toString());
            throw new ServletException(msg);
        } else if (foundBeans.isEmpty()) {
            // Aucun bean correspondant n'a �t� trouv�.
            final String msg =
                    String.format("failed to initialize MonitoringServlet: no bean of type %s found.",
                            IMonitoringService.class.getName());
            throw new ServletException(msg);
        } else {
            return foundBeans.values().iterator().next();
        }

    }

    /**
     * Lib�ration du contexte et du service, pour lib�rer les ressources.<br />
     * <b>Note</b> : ceci provoque un warning FindBugs et PMD. Ce warning a du
     * sens pour pr�venir de bugs en environenemnt multi-thread�. Il est
     * cependant non-pertinent dans ce cas : la m�thode destroy n'est pas
     * appel�e en multi-thread.
     */
    @Override
    @SuppressWarnings("PMD.NullAssignment")
    public void destroy() {
        springContext = null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
            IOException {

        final IMonitoringService service = getMonitoringServiceBean();
        // Met le bon MIME-type et le bon encoding :
        httpResponse.setContentType("text/xml; charset=utf-8");

        String technicalReqParam = httpRequest.getParameter("technical");
        String functionalReqParam = httpRequest.getParameter("functional");

        this.includeFunctionalTests = !"false".equalsIgnoreCase(functionalReqParam);
        this.includeTechnicalTests = !"false".equalsIgnoreCase(technicalReqParam);
        // Envoi du flux XML :
        httpResponse.getWriter().print(service.runTests(includeTechnicalTests,includeFunctionalTests));

        if (!service.getMonitoringSession().isAllTestsOK(includeTechnicalTests,includeFunctionalTests)) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * @return the springContext
     */
    public ApplicationContext getSpringContext() {
        return springContext;
    }

    /**
     * @param mySpringContext the springContext to set
     */
    public void setSpringContext(ApplicationContext mySpringContext) {
        this.springContext = mySpringContext;
        // Note : ceci provoque un warning FindBugs. Ce warning a du sens pour
        // pr�venir de bugs
        // en environenemnt multi-thread�. Il est cependant non-pertinent dans
        // ce cas : la m�thode
        // setSpringContext n'est pas appel�e en multi-thread mais lors de
        // l'init de la servlet
        // par Spring au d�marrage de l'appli.
    }


}
