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
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Access Denied: Only administrators can manage users.");
            response.sendRedirect("bill?action=dashboard");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "registerForm"; // Default action: Registration form එක පෙන්වන්න
        }

//        switch (action) {
//            case "registerForm":
//                showRegisterForm(request, response);
//                break;
//            case "listUsers": // අනාගතයේදී user list එකක් පෙන්වන්න
//                // listUsers(request, response);
//                break;
//            default:
//                showRegisterForm(request, response);
//                break;
//        }

        switch (action) {
            case "registerForm":
                showRegisterForm(request, response);
                break;
            case "listUsers":
                listUsers(request, response);
                break;
            case "editUserForm":
                showEditUserForm(request, response);
                break;
            case "deleteUser":
                deleteUser(request, response);
                break;
            default:
                listUsers(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session check for POST requests as well
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        // Role-Based Access Control: User Management only  admin
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Access Denied: Only administrators can manage users.");
            response.sendRedirect("bill?action=dashboard");
            return;
        }

        String action = request.getParameter("action");


        if (action != null) {
            switch (action) {
                case "register":
                    registerUser(request, response);
                    break;
                case "updateUser":
                    updateUser(request, response);
                    break;
                default: // If action is not "register" or "updateUser"
                    response.sendRedirect("user?action=listUsers&error=invalid_action"); // Invalid action නම් user list එකට යවනවා
                    break;
            }
        } else { // If action is null
            response.sendRedirect("user?action=listUsers&error=invalid_action"); // Invalid action නම් user list එකට යවනවා
        }
        // **************************************************
    }


    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can register new users.");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }


    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can register new users.");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        // Input Validation
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Password and Confirm Password do not match.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }


        if (userDAO.isUsernameExists(username)) {
            request.setAttribute("error", "Username '" + username + "' already exists. Please choose a different username.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }


        User newUser = new User(username, password, role);

        boolean success = userDAO.registerUser(newUser);

        if (success) {
            request.setAttribute("message", "User '" + username + "' registered successfully as " + role + "!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to register user. Please try again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }


    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> listUsers = userDAO.getAllUsers();
        request.setAttribute("listUsers", listUsers);
        request.getRequestDispatcher("user-list.jsp").forward(request, response);
    }


    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = 0;
        try {
            userId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid User ID.");
            listUsers(request, response);
            return;
        }

        User existingUser = userDAO.getUserById(userId);
        if (existingUser != null) {
            request.setAttribute("user", existingUser);
            request.getRequestDispatcher("user-form.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "User not found for editing: " + userId);
            listUsers(request, response);
        }
    }


    private void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("--- DEBUGGING USER UPDATE ---");
        int userId = 0;
        try {
            userId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid User ID.");
            listUsers(request, response);
            return;
        }

        String username = request.getParameter("username");
        String role = request.getParameter("role");
        String password = request.getParameter("password");


        System.out.println("User ID to update: " + userId);
        System.out.println("Username received: '" + username + "'");
        System.out.println("Role received: '" + role + "'");
        System.out.println("Password received: '" + password + "'");
        // ********************************************



        if (username == null || username.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
            request.setAttribute("error", "Username and Role are required.");
            User currentUser = userDAO.getUserById(userId); // Re-fetch for form
            request.setAttribute("user", currentUser);
            request.getRequestDispatcher("user-form.jsp").forward(request, response);
            return;
        }

        // Check if username already exists for another user
        User existingUserWithUsername = userDAO.getUserByUsernameAndPassword(username, null); // Password null, just check username
        if (existingUserWithUsername != null && existingUserWithUsername.getId() != userId) {
            request.setAttribute("error", "Username '" + username + "' already exists for another user.");
            User currentUser = userDAO.getUserById(userId);
            request.setAttribute("user", currentUser);
            request.getRequestDispatcher("user-form.jsp").forward(request, response);
            return;
        }

        User userToUpdate = new User(userId, username, password, role); // Use the constructor with ID and password

        System.out.println("Attempting to update user in DB: " + userToUpdate.toString());

        boolean success = userDAO.updateUser(userToUpdate);

        System.out.println("User update success status from DAO: " + success);

        if (success) {
            request.setAttribute("message", "User '" + username + "' updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update user.");
        }
        listUsers(request, response);
        System.out.println("--- END DEBUGGING USER UPDATE ---");
    }


    private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = 0;
        try {
            userId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid User ID.");
            listUsers(request, response);
            return;
        }

        // can not delete the Admin
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser != null && currentUser.getId() == userId) {
            session.setAttribute("errorMessage", "You cannot delete your own account!");
            response.sendRedirect("bill?action=dashboard"); // Dashboard එකට redirect කරනවා
            return;
        }

        boolean success = userDAO.deleteUser(userId);

        if (success) {
            request.setAttribute("message", "User ID " + userId + " deleted successfully!");
        } else {
            request.setAttribute("error", "Failed to delete user.");
        }
        listUsers(request, response);
    }

}
