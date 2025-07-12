package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

@WebFilter("/*")  // run on every request
public class AuthFilter implements Filter {
    // URLs that don’t require login
    private static final Set<String> PUBLIC = Set.of(
            "/index.jsp",
            "/create_account.jsp",
            "/LoginServlet",
            "/RegisterServlet",
            "/test-session",
            "/css/", "/js/", "/images/" // if you serve static under those paths
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        // Debug logging for challenge requests
        if (path.equals("/challenge") && "POST".equals(request.getMethod())) {
            System.out.println("=== AuthFilter Debug ===");
            System.out.println("Request path: " + path);
            System.out.println("Request method: " + request.getMethod());
            System.out.println("Context path: " + request.getContextPath());
            System.out.println("Full URI: " + request.getRequestURI());
            
            HttpSession session = request.getSession(false);
            System.out.println("Session exists: " + (session != null));
            if (session != null) {
                System.out.println("User in session: " + (session.getAttribute("user") != null));
                if (session.getAttribute("user") != null) {
                    System.out.println("User ID: " + ((Bean.User)session.getAttribute("user")).getUserId());
                }
            }
        }

        boolean isPublic = PUBLIC.stream().anyMatch(path::startsWith);
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (!loggedIn && !isPublic) {
            // not logged in & trying to hit a protected page → back to login
            System.out.println("AuthFilter: Redirecting to login - path: " + path + ", loggedIn: " + loggedIn);
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        // Prevent caching of protected pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
        
        // Debug logging for challenge requests
        if (path.equals("/challenge") && "POST".equals(request.getMethod())) {
            System.out.println("AuthFilter: Allowing request to proceed to ChallengeServlet");
        }
        
        // otherwise, just continue on
        chain.doFilter(req, res);
    }


}
