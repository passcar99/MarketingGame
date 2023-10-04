package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.ExpertiseLevel;
import it.polimi.db2.marketing.entities.Sex;
import it.polimi.db2.marketing.entities.User;
import it.polimi.db2.marketing.exception.AnswerException;
import it.polimi.db2.marketing.exception.IncompleteQuestionnaireException;
import it.polimi.db2.marketing.exception.OffensiveWordException;
import it.polimi.db2.marketing.exception.QuestionnaireException;
import it.polimi.db2.marketing.services.AnswerService;
import it.polimi.db2.marketing.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
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

@WebServlet("/AddAnswer")
public class AddAnswer extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.marketing.services/UserService")
    private UserService usrService;
    public AddAnswer() {
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
        int questionId = Integer.parseInt(request.getParameter("questId"));
        User user = ((User)request.getSession().getAttribute("user"));
        if(questionId != -1){
            String text = StringEscapeUtils.escapeJava(request.getParameter("answer"));
            AnswerService answerService = (AnswerService) request.getSession().getAttribute("answerService");
            try {
                answerService.addNewAnswer(questionId, user.getId(), text);
            } catch (AnswerException | QuestionnaireException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add answer");
                return;
            }
        }else{
            try {
                Integer age = null;
                if(request.getParameter("age") != null && !request.getParameter("age").isEmpty())
                    age = Integer.parseInt(request.getParameter("age"));
                Sex sex = Sex.getSexFromChar(StringEscapeUtils.escapeJava(request.getParameter("sex")).charAt(0));
                ExpertiseLevel expertiseLevel = ExpertiseLevel.getExpertiseLevelFromString(StringEscapeUtils.escapeJava(request.getParameter("expertise")));
                usrService.addStatisticalInfo(age, sex, expertiseLevel, user.getId());
            }catch (Exception e){
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add answer");
                return;
            }
        }
        response.sendRedirect(request.getHeader("referer"));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AnswerService answerService = (AnswerService) request.getSession().getAttribute("answerService");
        Boolean submit = Boolean.parseBoolean(request.getParameter("submit"));
        if(submit){
            try {
                answerService.confirmAnswers();
            } catch (IncompleteQuestionnaireException e) {
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
                ctx.setVariable("errorMsg", "Incomplete questionnaire");
                String path = "/WEB-INF/Home.html";
                templateEngine.process(path, ctx, response.getWriter());
                return;
            } catch (OffensiveWordException ex){
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
                ctx.setVariable("errorMsg", "An offensive word was detected. You are banned off the game.");
                String path = "/index.html";
                templateEngine.process(path, ctx, response.getWriter());
                return;
            }catch (AnswerException exc){
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
                ctx.setVariable("errorMsg", "Error in adding answers. Probably the questionnaire has been already filled.");
                String path = "/WEB-INF/Home.html";
                templateEngine.process(path, ctx, response.getWriter());
                return;
            }
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            String path = "/WEB-INF/Greetings.html";
            templateEngine.process(path, ctx, response.getWriter());
        }else{
            answerService.cancelAnswers();
            String path = getServletContext().getContextPath() + "/Home";
            response.sendRedirect(path);
        }
    }
}
