package edu.lwtech.csd297.hello.commands;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;
import org.apache.logging.log4j.*;

public class HealthCommand implements ServletCommand {

    private static final Logger logger = LogManager.getLogger(HealthCommand.class);
    
    @Override
    public String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData) {
        try {
            response.sendError(HttpServletResponse.SC_OK);
        } catch (IOException | IllegalStateException e) {
            logger.error("Unable to send error response code.", e);
        }
        return null;
    }

}
