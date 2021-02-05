package edu.lwtech.csd297.hello;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

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
    private static final String RESOURCES_DIR = "/WEB-INF/classes";

    private String webPageTemplate = "";
    private final AtomicInteger numPageLoads = new AtomicInteger(0);

    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.warn("");
        logger.warn("===========================================================");
        logger.warn("       " + SERVLET_NAME + " init() started");
        logger.warn("            http://localhost:8080/" + SERVLET_NAME + "/servlet");
        logger.warn("===========================================================");
        logger.warn("");

        String resourcesDir = config.getServletContext().getRealPath(RESOURCES_DIR);
        logger.info("resourcesDir = {}", resourcesDir);

        logger.info("Reading templateFile...");
        String fullTemplateFilename = resourcesDir + "/templates/home.tpl";
        webPageTemplate = readTemplateFile(fullTemplateFilename);

        logger.warn("");
        logger.warn("Initialization completed successfully!");
        logger.warn("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logger.debug("IN - {}", logInfo);
        long startTime = System.currentTimeMillis();

        numPageLoads.incrementAndGet();

        // Insert variable values into the template
        String html = webPageTemplate.replace("{n}", "" + numPageLoads);        

        // Send the template to the user
        try (ServletOutputStream out = response.getOutputStream()) {
            out.println(html);
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

    @Override
    public void destroy() {
        logger.warn("");
        logger.warn("-----------------------------------------");
        logger.warn("  " + SERVLET_NAME + " destroy() completed!");
        logger.warn("-----------------------------------------");
    }

    // --------------------------------------------------------------------

    private String readTemplateFile(String fileName) throws UnavailableException {
        String contents = "";
        try {
            contents = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException ex) {
            String msg = "Unable to read " + fileName;
            logger.fatal(msg, ex);
            throw new UnavailableException(msg);
        }
        return contents;
    }

}
