package it.polimi.db2.marketing.filter;

import it.polimi.db2.marketing.entities.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/Admin")
public class Admin implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.print("Admin checker marketing.filter executing ...\n");


        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginpath = req.getServletContext().getContextPath() + "/index.html";

        HttpSession s = req.getSession();
        if (s.isNew() || s.getAttribute("user") == null) {
            res.sendRedirect(loginpath);
            return;
        }
        String userHomePath = req.getServletContext().getContextPath() +"/Home";
        if(((User)s.getAttribute("user")).getUsername().compareTo("admin") != 0){
            res.sendRedirect(userHomePath);
            return;
        }
        // pass the request along the marketing.filter chain
        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
