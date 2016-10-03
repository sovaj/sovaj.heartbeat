package io.sovaj.heartbeat.monitors.mock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet fake pour les tests.
 */
public class FakeServlet extends HttpServlet {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -496687948236557742L;

    /**
     * {@inheritDoc}
     */
    protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
                    IOException {
        // Met le bon MIME-type et le bon encoding :
        httpResponse.setContentType("text/xml; charset=utf-8");
        // Envoi du flux XML :
        httpResponse.getWriter().print("<xml>test</xml>");
    }

}
