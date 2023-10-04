package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.Product;
import it.polimi.db2.marketing.entities.Questionnaire;
import it.polimi.db2.marketing.exception.QuestionnaireException;
import it.polimi.db2.marketing.services.ProductService;
import it.polimi.db2.marketing.services.QuestionnaireService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/AddProduct")
@MultipartConfig
public class AddProduct extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.marketing.services/QuestionnaireService")
    QuestionnaireService questionnaireService;
    @EJB(name = "it.polimi.db2.marketing.services/ProductService")
    ProductService productService;

    public AddProduct() {
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
        //Redirect to CreationPage
        String path = getServletContext().getContextPath() + "/Creation";
        response.sendRedirect(path);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");

        Part imgFile = request.getPart("picture");
        InputStream imgContent = imgFile.getInputStream();
        byte[] imgByteArray = ImageUtils.readImage(imgContent);

        if (name == null | name.isEmpty() | imgByteArray.length == 0 | imgByteArray.length == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid question");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = (Date) sdf.parse(request.getParameter("date"));
        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid question");
            e.printStackTrace();
            return;
        }
        Product product = productService.createNewProduct(name, imgByteArray);
        Questionnaire questionnaire;
        try {
            questionnaire = questionnaireService.createQuestionnaire(product.getId(), date);
        }catch (QuestionnaireException ex){
            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("errorMsg", "Incorrect username or password");
            String path = "/Creation";
            templateEngine.process(path, ctx, response.getWriter());
            return;
        }
        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/Creation";
        path = path + "?questId=" + questionnaire.getId();
        response.sendRedirect(path);
    }
}
