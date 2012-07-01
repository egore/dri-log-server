
package de.egore911.drilog.server;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Dri_log_serverServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");
    }
}
