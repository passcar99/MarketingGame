package it.polimi.db2.marketing;

import it.polimi.db2.marketing.entities.Product;
import it.polimi.db2.marketing.entities.Review;
import it.polimi.db2.marketing.exception.ProductException;
import it.polimi.db2.marketing.exception.ProductNotFoundException;
import it.polimi.db2.marketing.exception.QuestionnaireException;
import it.polimi.db2.marketing.services.ProductService;
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
import java.util.List;

@WebServlet("/Home")
public class GoToHome extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    @EJB(name = "it.polimi.db2.mission.services/ProductService")
    private ProductService productService;


    public GoToHome() {
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
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product;
        List<Review> reviews;
        try {
            product = productService.getProductOfTheDay(true);
            reviews = productService.getReviewsByProduct(product.getId());

        } catch (QuestionnaireException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data");
            return;
        } catch (ProductException | ProductNotFoundException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get product or reviews");
            return;
        }

        // Redirect to the Home page and add missions to the parameters
        String path = "/WEB-INF/Home.html";
        ServletContext servletContext = getServletContext();
        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("product", product);
        ctx.setVariable("reviews", reviews);

        templateEngine.process(path, ctx, response.getWriter());
    }
}
