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
    private UserDAO userDAO; // UserDAO object එක

    public void init() {
        userDAO = new UserDAO(); // Servlet එක initiate වෙනකොට UserDAO එක හදනවා
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session check: User logged in ද කියලා බලනවා
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        // *** Role-Based Access Control: User Management admin ට පමණයි ***
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Access Denied: Only administrators can manage users.");
            response.sendRedirect("bill?action=dashboard"); // Dashboard එකට redirect කරනවා
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

        // Role-Based Access Control: User Management admin ට පමණයි
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            session.setAttribute("errorMessage", "Access Denied: Only administrators can manage users.");
            response.sendRedirect("bill?action=dashboard");
            return;
        }

        String action = request.getParameter("action");

        // *** මෙතනින් පහළට doPost method එකේ වෙනස්කම් ***
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

    // User Registration form එක පෙන්වන්න
    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // මේ page එකට access කරන්න admin role එකක් තියෙනවද කියලා බලමු (ප්‍රවේශ පාලනය)
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can register new users.");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response); // Dashboard එකට යවනවා
            return;
        }
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    // අලුත් User කෙනෙක් Register කරන්න
    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // මේ method එකට access කරන්න admin role එකක් තියෙනවද කියලා බලමු (ප්‍රවේශ පාලනය)
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null || !"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can register new users.");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role"); // Form එකෙන් role එක ගන්නවා

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

        // Username එක දැනටමත් තියෙනවද කියලා බලනවා
        if (userDAO.isUsernameExists(username)) {
            request.setAttribute("error", "Username '" + username + "' already exists. Please choose a different username.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // User object එක හදනවා
        User newUser = new User(username, password, role); // User Model එකේ අලුත් constructor එක භාවිතා කරනවා

        boolean success = userDAO.registerUser(newUser); // UserDAO එකෙන් user ව register කරනවා

        if (success) {
            request.setAttribute("message", "User '" + username + "' registered successfully as " + role + "!");
            request.getRequestDispatcher("register.jsp").forward(request, response); // සාර්ථක නම් register page එකටම යවනවා
        } else {
            request.setAttribute("error", "Failed to register user. Please try again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }

    // *** අලුත් method එක: සියලුම Users ලා list කරන්න ***
    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<User> listUsers = userDAO.getAllUsers(); // UserDAO එකෙන් සියලුම users ගන්නවා
        request.setAttribute("listUsers", listUsers); // User list එක request එකට දානවා
        request.getRequestDispatcher("user-list.jsp").forward(request, response); // user-list.jsp එකට forward කරනවා
    }

    // *** අලුත් method එක: User Edit Form පෙන්වන්න ***
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
            request.getRequestDispatcher("user-form.jsp").forward(request, response); // user-form.jsp එකට forward කරනවා
        } else {
            request.setAttribute("error", "User not found for editing: " + userId);
            listUsers(request, response);
        }
    }

    // *** අලුත් method එක: User Update කරන්න ***
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
        String password = request.getParameter("password"); // Password එකත් update කරන්න පුළුවන්

        // *** මේ debugging lines ටික එකතු කරන්න ***
        System.out.println("User ID to update: " + userId);
        System.out.println("Username received: '" + username + "'");
        System.out.println("Role received: '" + role + "'");
        System.out.println("Password received: '" + password + "'"); // Password එක print කරද්දී ප්‍රවේශමෙන්
        // ********************************************


        // Input Validation (Basic for now, can be enhanced)
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

        System.out.println("Attempting to update user in DB: " + userToUpdate.toString()); // User object එක print කරනවා

        boolean success = userDAO.updateUser(userToUpdate); // UserDAO එකේ updateUser method එකක් අවශ්‍යයි

        System.out.println("User update success status from DAO: " + success); // DAO එකෙන් එන success status එක print කරනවා

        if (success) {
            request.setAttribute("message", "User '" + username + "' updated successfully!");
        } else {
            request.setAttribute("error", "Failed to update user.");
        }
        listUsers(request, response);
        System.out.println("--- END DEBUGGING USER UPDATE ---");
    }

    // *** අලුත් method එක: User Delete කරන්න ***
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

        // Admin කෙනෙක් තමන්වම delete කරන එක වළක්වන්න පුළුවන්
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser != null && currentUser.getId() == userId) {
            session.setAttribute("errorMessage", "You cannot delete your own account!");
            response.sendRedirect("bill?action=dashboard"); // Dashboard එකට redirect කරනවා
            return;
        }

        boolean success = userDAO.deleteUser(userId); // UserDAO එකේ deleteUser method එකක් අවශ්‍යයි

        if (success) {
            request.setAttribute("message", "User ID " + userId + " deleted successfully!");
        } else {
            request.setAttribute("error", "Failed to delete user.");
        }
        listUsers(request, response);
    }

}
