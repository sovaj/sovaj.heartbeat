package com.ypg.car.monitoring.output;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * A utility class that uses the Servlet 2.3 Filtering API to apply an XSLT
 * stylesheet to a servlet response.
 * 
 * @author e19853
 */
public class XsltFilter implements Filter {
    protected FilterConfig filterConfig;

    private Transformer transformer;

    /** 
     * This method is called once when the filter is first loaded.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        StreamSource xsltStreamSource = null;

        // xsltPath should be something like "/WEB-INF/xslt/a.xslt"
        String xsltPath = filterConfig.getInitParameter("xsltPath");

        if (xsltPath == null) {
            // Get the default transformer
            /*xsltStreamSource =
                            new StreamSource(getClass().getResourceAsStream(
                                            "/com/ypg/ccar/monitoring/output/application-test.xsl"));*/
            xsltStreamSource =
                    new StreamSource(getClass().getResourceAsStream(
                            "/xsl/monitoring.xsl"));

        } else {
            // convert the context-relative path to a physical path name
            String xsltFileName = filterConfig.getServletContext().getRealPath(xsltPath);

            // verify that the file exists
            if (xsltFileName == null || !new File(xsltFileName).exists()) {
                throw new UnavailableException("Unable to locate stylesheet: " + xsltFileName, 30);
            }
            xsltStreamSource = new StreamSource(xsltFileName);
        }

        TransformerFactory tFactory = TransformerFactory.newInstance();

        try {
            transformer = tFactory.newTransformer(xsltStreamSource);
        } catch (TransformerConfigurationException e) {
            throw new UnavailableException("Init the transformer :: " + e.toString());
        }

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
                    ServletException {

        if (!(res instanceof HttpServletResponse)) {
            throw new ServletException("This filter only supports HTTP");
        }

        String output = req.getParameter("output");

        if (!"html".equalsIgnoreCase(output)) {
            chain.doFilter(req, res);
            return;
        }

        // BufferedHttpResponseWrapper responseWrapper = new
        // BufferedHttpResponseWrapper(
        // (HttpServletResponse) res);

        CharResponseWrapper responseWrapper = new CharResponseWrapper((HttpServletResponse ) res);

        chain.doFilter(req, responseWrapper);

        // Tomcat 4.0 reuses instances of its HttpServletResponse
        // implementation class in some scenarios. For instance, hitting
        // reload( ) repeatedly on a web browser will cause this to happen.
        // Unfortunately, when this occurs, output is never written to the
        // BufferedHttpResponseWrapper's OutputStream. This means that the
        // XML output array is empty when this happens. The following
        // code is a workaround:

        byte[] origXML = responseWrapper.getBuffer();
        if (origXML == null || origXML.length == 0) {
            // just let Tomcat deliver its cached data back to the client
            chain.doFilter(req, res);
            return;
        }

        try {

            ByteArrayInputStream origXMLIn = new ByteArrayInputStream(origXML);
            Source xmlSource = new StreamSource(origXMLIn);

            ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();
            transformer.transform(xmlSource, new StreamResult(resultBuf));

            res.setContentLength(resultBuf.size());
            res.setContentType("text/html");
            res.getOutputStream().write(resultBuf.toByteArray());
            res.flushBuffer();
        } catch (TransformerException te) {
            throw new ServletException(te);
        }
    }

    /**
     * The counterpart to the init( ) method.
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * @return the filterConfig
     */
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }
}
