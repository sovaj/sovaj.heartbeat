package io.sovaj.heartbeat.output;

import io.sovaj.heartbeat.output.XsltFilter;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * @author mohamed
 *
 */
public class XsltFilterTest {

    private XsltFilter xsltFilter;

    @Mock
    private FilterConfig filterConfigMock;

    @Mock
    private ServletContext servletContextMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @Mock
    private FilterChain filterChainMock;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        xsltFilter = new XsltFilter();

    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#init(javax.servlet.FilterConfig)}.
     * @throws ServletException 
     */
    @Test
    public void testInit_xsltPath_null() throws ServletException {
        // Null parameter should cause default transformer to be loaded
        xsltFilter.init(filterConfigMock);

    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#init(javax.servlet.FilterConfig)}.
     * @throws ServletException 
     */
    @Test(expected = UnavailableException.class)
    public void testInit_xsltPath_not_null_throw_UnavailableException() throws ServletException {

        when(filterConfigMock.getInitParameter("xsltPath")).thenReturn("test.xsl");
        when(filterConfigMock.getServletContext()).thenReturn(servletContextMock);
        xsltFilter.init(filterConfigMock);

    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#init(javax.servlet.FilterConfig)}.
     * @throws ServletException 
     */
    @Test
    public void testInit_xsltPath_not_null() throws ServletException {
        String fileName = "./src/main/resources/io/sovaj/heartbeat/output/application-test.xsl";
        when(filterConfigMock.getInitParameter("xsltPath")).thenReturn(fileName);
        when(filterConfigMock.getServletContext()).thenReturn(servletContextMock);
        when(servletContextMock.getRealPath(fileName)).thenReturn("./" + fileName);

        xsltFilter.init(filterConfigMock);

    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test(expected = ServletException.class)
    public void testDoFilter_null_response_throw_ServletException() throws IOException, ServletException {
        xsltFilter.doFilter(null, null, null);
    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test
    public void testDoFilter_output_null() throws IOException, ServletException {
        xsltFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);
    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test
    public void testDoFilter_output_diff_html() throws IOException, ServletException {
        when(httpServletRequestMock.getParameter("output")).thenReturn("otherOutput");

        xsltFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);
    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test
    public void testDoFilter_output_eq_html() throws IOException, ServletException {
        when(httpServletRequestMock.getParameter("output")).thenReturn("html");

        xsltFilter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);
    }

    /**
     * Test method for {@link io.sovaj.heartbeat.output.XsltFilter#destroy()}.
     * @throws Exception 
     */
    @Test
    public void testDestroy() throws Exception {

        xsltFilter = new XsltFilter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
                this.filterConfig = filterConfig;
            }
        };

        xsltFilter.init(filterConfigMock);

        assertNotNull(xsltFilter.getFilterConfig());

        xsltFilter.destroy();

        assertNull(xsltFilter.getFilterConfig());

    }

}
