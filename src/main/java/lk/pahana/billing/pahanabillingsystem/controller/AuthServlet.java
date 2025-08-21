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

@WebServlet("/auth") // මේ Servlet එකට access කරන්න පුළුවන් URL path එක
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO(); // Servlet එක initiate වෙනකොට UserDAO එක හදනවා
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action"); // "action" parameter එක ගන්නවා

        if (action != null && action.equals("login")) {
            handleLogin(request, response); // Login request එක handle කරනවා
        } else if (action != null && action.equals("logout")) {
            handleLogout(request, response); // Logout request එක handle කරනවා
        } else {
            response.sendRedirect("login.jsp?error=invalid_action"); // Invalid action නම් login page එකට යවනවා
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logout request එක GET විදියට ආවොත්, ඒක doPost() එකටම handle කරන්න යවනවා.
        // AuthServlet එකේ අපි action parameter එක බලන්නේ POST එකකින් වගේම.
        doPost(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username"); // Form එකෙන් username එක ගන්නවා
        String password = request.getParameter("password"); // Form එකෙන් password එක ගන්නවා

        User user = userDAO.getUserByUsernameAndPassword(username, password); // Database එකෙන් user ව check කරනවා

        if (user != null) { // User ව හම්බුනොත්
            HttpSession session = request.getSession(); // Session එකක් හදනවා
            session.setAttribute("currentUser", user); // Session එකට user ව දානවා
            session.setMaxInactiveInterval(30 * 60); // Session timeout 30 minutes
//            response.sendRedirect("dashboard.jsp"); // Dashboard එකට redirect කරනවා
            response.sendRedirect("bill?action=dashboard"); // BillServlet එකේ dashboard action එකට redirect කරනවා
        } else { // User ව හම්බුනේ නැත්නම්
            String errorMessage = "Invalid username or password!";
            request.setAttribute("errorMessage", errorMessage); // Error message එක request එකට දානවා
            request.getRequestDispatcher("login.jsp").forward(request, response); // Login page එකට forward කරනවා
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Existing session එක ගන්නවා
        if (session != null) {
            session.invalidate(); // Session එක destroy කරනවා
        }
        response.sendRedirect("login.jsp?message=logged_out"); // Login page එකට redirect කරනවා
    }
}
