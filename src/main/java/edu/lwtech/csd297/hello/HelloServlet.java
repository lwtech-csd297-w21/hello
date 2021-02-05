package edu.lwtech.csd297.hello;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import org.apache.logging.log4j.*;

// World's Simplest Hello World Servlet -
//      http://server:8080/hello/servlet
//
// Chip Anderson
// LWTech CSD297

@WebServlet(name = "hello", urlPatterns = {"/servlet"}, loadOnStartup = 0)
public class HelloServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;        // Unused
    private static final Logger logger = LogManager.getLogger(HelloServlet.class);

    private static final String SERVLET_NAME = "hello";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logger.debug("IN - {}", logInfo);
        long startTime = System.currentTimeMillis();

        try (ServletOutputStream out = response.getOutputStream()) {
            out.println("<html><body><h1>Hello World!</h1></body></html>");
        } catch (IOException e) {
            logger.error("I/O Exception writing out the web page", e);
        }

        long time = System.currentTimeMillis() - startTime;
        logger.info("OUT- {} {}ms", logInfo, time);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return SERVLET_NAME + " Servlet";
    }

}
