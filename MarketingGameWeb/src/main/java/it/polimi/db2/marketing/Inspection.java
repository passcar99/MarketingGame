package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.Answer;
import it.polimi.db2.marketing.entities.User;
import it.polimi.db2.marketing.exception.QuestionnaireException;
import it.polimi.db2.marketing.exception.QuestionnaireNotFoundException;
import it.polimi.db2.marketing.services.QuestionnaireService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Inspection")
public class Inspection extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.mission.services/QuestionnaireService")
    private QuestionnaireService questionnaireService;


    public Inspection() {
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Answer> answers = new ArrayList<>();
        List<User> submitUsers;
        List<User> cancelUsers;
        int questid = Integer.parseInt(request.getParameter("questid"));
        try {
            submitUsers = questionnaireService.getSubmittedUsersByQuestionnaire(questid);
            cancelUsers = questionnaireService.getCancelledUsersByQuestionnaire(questid);
            for (User u : submitUsers) {
                answers.addAll(questionnaireService.getQuestionnaireAnswerByUser(questid, u.getId()));
            }
        } catch (QuestionnaireException | QuestionnaireNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data. " + e.getMessage());
            return;
        }

        //Redirect to InspectionPage
        String path = "/WEB-INF/InspectionPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("cancelUsers", cancelUsers);
        ctx.setVariable("submitUsers", submitUsers);
        ctx.setVariable("answers", answers);
        templateEngine.process(path, ctx, response.getWriter());
    }
}