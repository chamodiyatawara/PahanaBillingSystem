package lk.pahana.billing.pahanabillingsystem.controller;



import lk.pahana.billing.pahanabillingsystem.dao.UserDAO;
import lk.pahana.billing.pahanabillingsystem.model.User;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth") // The URL path that can be accessed to the servlet

public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO(); // When the Servlet is initiated, it creates the UserDAO.

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action != null && action.equals("login")) {
            handleLogin(request, response);
        } else if (action != null && action.equals("logout")) {
            handleLogout(request, response);
        } else {
            response.sendRedirect("login.jsp?error=invalid_action");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // If the logout request comes as a GET method, it is sent to handle the doPost() function.
        // In the AuthServlet, we check the action parameter in the same way as with a POST.
        doPost(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.getUserByUsernameAndPassword(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60);
//            response.sendRedirect("dashboard.jsp");
            response.sendRedirect("bill?action=dashboard");
        } else {
            String errorMessage = "Invalid username or password!";
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("login.jsp?message=logged_out");
    }
}
