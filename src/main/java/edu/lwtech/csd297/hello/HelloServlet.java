package edu.lwtech.csd297.hello;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import freemarker.template.*;
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
    private static final String INTERNAL_PROPS_FILENAME = "servlet.properties";
    private static final String EXTERNAL_PROPS_FILENAME = "/var/local/config/" + SERVLET_NAME + ".props";
    private static final Configuration freeMarkerConfig = new Configuration(Configuration.getVersion());

    private String ownerName = "";
    private String version = "";
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

        // Initialize internal properties
        String fullInternalPropsFilename = resourcesDir + "/" + INTERNAL_PROPS_FILENAME;
        logger.info("Reading internal properties from {}", fullInternalPropsFilename);
        Properties props = loadProperties(fullInternalPropsFilename);
        version = props.getProperty("version");
        logger.info("version = {}", version);
        logger.info("");

        // Initialize external properties
        Properties externalProps = loadProperties(EXTERNAL_PROPS_FILENAME);
        logger.info("Reading external properties from {}", fullInternalPropsFilename);
        ownerName = getProperty(externalProps, "ownerName");
        logger.info("ownerName = {}", ownerName);
        logger.info("");

        logger.info("Initializing FreeMarker...");
        String templateDir = resourcesDir + "/templates";
        try {
            freeMarkerConfig.setDirectoryForTemplateLoading(new File(templateDir));
        } catch (IOException e) {
            String msg = "Template directory not found: " + templateDir;
            logger.fatal(msg, e);
            throw new UnavailableException(msg);
        }
        logger.info("Successfully initialized FreeMarker");

        logger.warn("");
        logger.warn("Initialization completed successfully!");
        logger.warn("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logger.debug("IN - {}", logInfo);
        long startTime = System.currentTimeMillis();

        String fmTemplateName = "";
        Map<String, Object> fmTemplateData = new HashMap<>();

        try {
            // Prepare the appropriate Freemarker template
            fmTemplateName = "home.ftl";
            fmTemplateData.put("n", numPageLoads.incrementAndGet());
            fmTemplateData.put("ownerName", ownerName);
            fmTemplateData.put("version", version);

            // Process the template and send the results back to the user
            processTemplate(response, fmTemplateName, fmTemplateData);

        } catch (TemplateException e) {
            // Somehow bad data got into the template model...
            logger.error("Template exception processing {}", fmTemplateName);
            sendServerError(response);
        } catch (RuntimeException e) {
            // Something unexpected happened...
            logger.error("Unexpected runtime exception: ", e);
            sendServerError(response);
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

    private Properties loadProperties(String propsFilename) throws UnavailableException {
        Properties props = new Properties();
        try (InputStream inputStream = new FileInputStream(propsFilename)) {
            props.load(inputStream);
        } catch (IOException e) {
            String msg = "Unable to find properties file at " + propsFilename;
            logger.fatal(msg, e);
            throw new UnavailableException(msg);
        }
        return props;
    }

    private String getProperty(Properties props, String propertyName) throws UnavailableException {
        String property = props.getProperty(propertyName);
        if (property == null) {
            String msg = "Unable to get " + propertyName + " property from props.";
            logger.fatal(msg);
            throw new UnavailableException(msg);
        }
        return property;
    }

    private void processTemplate(HttpServletResponse response, String templateName, Map<String, Object> dataModel) throws TemplateException {
        logger.debug("Processing Template: {}", templateName);
        try (PrintWriter out = response.getWriter()) {

            Template template = freeMarkerConfig.getTemplate(templateName);
            template.process(dataModel, out);

        } catch (MalformedTemplateNameException e) {
            // This should never happen.
            logger.fatal(e);
            throw new IllegalStateException(e);
        } catch (IOException e) {
            // Typically, this means the browser connection dropped before we could send our response. Ignore.
            logger.debug(e);
        }
    }

    private void sendServerError(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Oh no! Something went wrong. The appropriate authorities have been alerted.");
        } catch (IOException e) {
            logger.error(e);
        }
    }    
}
