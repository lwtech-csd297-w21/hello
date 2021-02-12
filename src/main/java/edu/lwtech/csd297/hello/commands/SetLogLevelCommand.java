package edu.lwtech.csd297.hello.commands;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.Configurator;

import edu.lwtech.csd297.hello.HelloServlet;

public class SetLogLevelCommand implements ServletCommand {

    private static final Logger logger = LogManager.getLogger(SetLogLevelCommand.class);

    @Override
    public String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData) {
        HelloServlet hello = (HelloServlet)servlet;
        String level = request.getParameter("level");
        if (level == null)
            level = "INFO";
        if (!"|DEBUG|INFO|WARN|".contains("|"+level+"|")) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                logger.error("Unable to send error response code.", e);
            }
            return null;
        }
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.getLevel(level));
        templateData.put("bannerMessage", "Logging level set to " + level + ".");
        templateData.put("n", hello.getNumPageLoads());
        templateData.put("ownerName", hello.getOwnerName());
        templateData.put("version", hello.getVersion());
        return "home.ftl";
    }

}
