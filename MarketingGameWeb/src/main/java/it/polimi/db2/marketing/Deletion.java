package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.Question;
import it.polimi.db2.marketing.entities.Questionnaire;
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
import java.util.Date;
import java.util.List;

@WebServlet("/Deletion")
public class Deletion extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.marketing.services/QuestionnaireService")
    QuestionnaireService questionnaireService;

    public Deletion() {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Questionnaire questionnaire = null;
        List<Question> questions = null;
        int questid = Integer.parseInt(request.getParameter("questid"));
        try {
            questionnaire = questionnaireService.getQuestionnaire(questid);
            questions = new ArrayList<>(questionnaire.getQuestions());
            if(questionnaire.getDate().compareTo(new Date(System.currentTimeMillis())) == 0){
                String path = getServletContext().getContextPath() + "/GoToAdminPage";
                response.sendRedirect(path);
                return;
            }
        } catch (QuestionnaireException |QuestionnaireNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data. " + e.getMessage());
            return;
        }

        //Redirect to DeletionPage
        String path = "/WEB-INF/DeletionPage.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("questionnaire", questionnaire);
        ctx.setVariable("questions", questions);
        templateEngine.process(path, ctx, response.getWriter());
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}
