package edu.lwtech.csd297.hello.commands;

import java.util.*;
import javax.servlet.http.*;
import edu.lwtech.csd297.hello.HelloServlet;

public class SessionCommand implements ServletCommand {

    @Override
    public String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData) {
        HelloServlet hello = (HelloServlet)servlet;

        HttpSession session = request.getSession();
        Integer numPageLoads = (Integer)session.getAttribute("numPageLoads");
        if (numPageLoads == null)
            numPageLoads = 0;
        
        numPageLoads++;

        session.setAttribute("numPageLoads", numPageLoads);

        templateData.put("n", numPageLoads);
        templateData.put("ownerName", hello.getOwnerName());
        templateData.put("version", hello.getVersion());
        return "session.ftl";
    }

}
