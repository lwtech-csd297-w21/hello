package edu.lwtech.csd297.hello.commands;

import java.util.*;
import javax.servlet.http.*;
import edu.lwtech.csd297.hello.HelloServlet;

public class AboutCommand implements ServletCommand {

    @Override
    public String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData) {
        HelloServlet hello = (HelloServlet)servlet;
        templateData.put("ownerName", hello.getOwnerName());
        templateData.put("version", hello.getVersion());
        return "about.ftl";
    }

}
