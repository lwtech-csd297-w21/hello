package edu.lwtech.csd297.hello.commands;

import java.util.*;
import javax.servlet.http.*;

public interface ServletCommand {

    // Returns template name or null if response has already been sent
    String initTemplate(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, Map<String, Object> templateData);

}
