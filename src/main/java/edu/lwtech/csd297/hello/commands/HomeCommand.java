package edu.lwtech.csd297.hello.commands;

import java.util.*;
import javax.servlet.http.*;
import edu.lwtech.csd297.hello.HelloServlet;

public class HomeCommand implements ServletCommand {

    @Override
    public String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData) {
        HelloServlet hello = (HelloServlet)servlet;
        hello.incrementNumPageLoads();
        templateData.put("n", hello.getNumPageLoads());
        templateData.put("ownerName", hello.getOwnerName());
        templateData.put("version", hello.getVersion());
        return "home.ftl";
    }

}
