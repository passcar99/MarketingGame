package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.User;
import it.polimi.db2.marketing.exception.CredentialsException;
import it.polimi.db2.marketing.services.AnswerService;
import it.polimi.db2.marketing.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.marketing.services/UserService")
    private UserService usrService;

    public CheckLogin() {
        super();
    }

    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // obtain and escape params
        String usrn = null;
        String pwd = null;
        try {
            usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
            pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
            if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
                throw new Exception("Missing or empty credential value");
            }

        } catch (Exception e) {
            // for debugging only e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
            return;
        }
        User user;
        try {
            // query db to authenticate for user
            user = usrService.checkCredentials(usrn, pwd);
        } catch (CredentialsException | NonUniqueResultException e) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", e.getMessage());
            String path = "/index.html";
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }

        // If the user exists, add info to the session and go to home page, otherwise
        // show login page with error message

        String path;
        if (user == null || user.isOffensive()) {
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", "Incorrect username or password");
            path = "/index.html";
            templateEngine.process(path, ctx, response.getWriter());
        } else {
            if(user.getUsername().compareTo("admin") == 0){
                request.getSession().setAttribute("user", user);
                path = getServletContext().getContextPath() + "/GoToAdminPage";
                response.sendRedirect(path);
                return;
            }
            AnswerService answerService = null;
            try {
                // Get the Initial Context for the JNDI lookup for a local EJB
                InitialContext ic = new InitialContext();
                // Retrieve the EJB using JNDI lookup
                answerService = (AnswerService) ic.lookup("java:module/AnswerService"); //original "java:openejb/local/QueryServiceLocalBean"
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getSession().setAttribute("answerService", answerService);
            request.getSession().setAttribute("user", user);
            path = getServletContext().getContextPath() + "/Home";
            response.sendRedirect(path);
        }

    }

    public void destroy() {
    }
}
