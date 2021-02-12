package edu.lwtech.csd297.hello;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.atomic.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import freemarker.template.*;
import org.apache.logging.log4j.*;

import edu.lwtech.csd297.hello.commands.*;

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
    private static final String SAVED_STATE_FILENAME = "/var/local/config/" + SERVLET_NAME + ".state";
    private static final Configuration freeMarkerConfig = new Configuration(Configuration.getVersion());

    private static final Map<String, ServletCommand> commandMap = new HashMap<>();

    private String ownerName = "";
    private String version = "";
    private AtomicInteger numPageLoads;

    // --------------------------------------------------------------------

    public String getOwnerName() {
        return ownerName;
    }

    public String getVersion() {
        return version;
    }

    public int getNumPageLoads() {
        return numPageLoads.get();
    }

    public void incrementNumPageLoads() {
        numPageLoads.incrementAndGet();
    }

    public void resetPageLoads() {
        numPageLoads.set(0);
    }

    // --------------------------------------------------------------------

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

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

        // Read in saved state (if any)
        logger.info("Reading state file (if any)...");
        int pageLoads = 0;
        String savedStateString = "0";
        try {
            savedStateString = new String(Files.readAllBytes(Paths.get(SAVED_STATE_FILENAME)));
            pageLoads = Integer.parseInt(savedStateString.trim());
        } catch (IOException | NumberFormatException e) {
            // If there are problems reading the state file, set numPageLoads to zero
            pageLoads = 0;
        }
        logger.info("Setting numPageLoads to {}", pageLoads);
        numPageLoads = new AtomicInteger(pageLoads);

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

        // Initialize the ServletCommand map
        commandMap.put("home", new HomeCommand());
        commandMap.put("about", new AboutCommand());
        commandMap.put("health", new HealthCommand());
        commandMap.put("resetcount", new ResetCountCommand());
        commandMap.put("setloglevel", new SetLogLevelCommand());
        commandMap.put("session", new SessionCommand());

        logger.warn("");
        logger.warn("Initialization completed successfully!");
        logger.warn("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logInfo += getSanitizedQueryString(request);
        logger.debug("IN - {}", logInfo);
        long startTime = System.currentTimeMillis();

        String cmd = request.getParameter("cmd");
        if (cmd == null)
            cmd = "home";

        String fmTemplateName = "";
        Map<String, Object> fmTemplateData = new HashMap<>();

        try {
            // Run the appropriate ServletCommand
            ServletCommand command = commandMap.get(cmd);
            if (command != null) {
                fmTemplateName = command.initTemplate(this, request, response, fmTemplateData);
                if (fmTemplateName == null)
                    return;
            } else {
                logger.info("Unknown GET command received: {}", cmd);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Process the template and send the results back to the user
            processTemplate(response, fmTemplateName, fmTemplateData);

        } catch (IOException e) {
            // Typically, this is because the connection was closed prematurely
            logger.debug("Unexpected I/O exception: ", e);
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
        logger.info("Writing out state to {}", SAVED_STATE_FILENAME);
        try (PrintWriter out = new PrintWriter(SAVED_STATE_FILENAME)) {
            out.println(numPageLoads.get());
        } catch (IOException e) {
            logger.error("Unable to save state to {}", SAVED_STATE_FILENAME, e);
        }
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

    private void processTemplate(HttpServletResponse response, String templateName, Map<String, Object> dataModel) throws TemplateException, IOException {
        logger.debug("Processing Template: {}", templateName);
        try (PrintWriter out = response.getWriter()) {

            Template template = freeMarkerConfig.getTemplate(templateName);
            template.process(dataModel, out);

        } catch (MalformedTemplateNameException e) {
            // This should never happen.
            logger.fatal(e);
            throw new IllegalStateException(e);
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

    private String getSanitizedQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return "";

        try { 
            queryString = URLDecoder.decode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen
            throw new IllegalStateException(e);
        }
        queryString = queryString.replaceAll("[\n|\t]", "_");
        return queryString;
    }

}
