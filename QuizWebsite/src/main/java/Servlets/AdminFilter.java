package Servlets;

import Bean.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return;
        }
        
        // Check if user is admin
        User user = (User) session.getAttribute("user");
        if (!user.isAdmin()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/access_denied.jsp");
            return;
        }
        
        // User is admin, continue to the requested resource
        chain.doFilter(request, response);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }
    
    @Override
    public void destroy() {
        // Cleanup code if needed
    }
} 