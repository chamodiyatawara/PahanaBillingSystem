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

@WebServlet("/auth") // URL path that can be accessed by the servlet
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO(); // when initiate the Servlet build the  UserDAO
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action"); // get "action" parameter

        if (action != null && action.equals("login")) {
            handleLogin(request, response); // handle Login request
        } else if (action != null && action.equals("logout")) {
            handleLogout(request, response); // handle Logout request
        } else {
            response.sendRedirect("login.jsp?error=invalid_action"); // if Invalid action go to  login page
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // when Logout request come with GET , it send to  doPost() to handle

        doPost(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username"); // get  username from form
        String password = request.getParameter("password"); // get password from form

        User user = userDAO.getUserByUsernameAndPassword(username, password); // check the user in Database

        if (user != null) { // if find the user
            HttpSession session = request.getSession(); // make a Session
            session.setAttribute("currentUser", user); // set the user in Session
            session.setMaxInactiveInterval(30 * 60); // Session timeout 30 minutes
            response.sendRedirect("dashboard.jsp"); // redirect to the Dashboard
        } else { // if not find the User
            String errorMessage = "Invalid username or password!";
            request.setAttribute("errorMessage", errorMessage); // Error message
            request.getRequestDispatcher("login.jsp").forward(request, response); // forward to the Login page
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Existing session
        if (session != null) {
            session.invalidate(); // destroy Session
        }
        response.sendRedirect("login.jsp?message=logged_out"); // redirect  Login page
    }
}
