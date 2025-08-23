package lk.pahana.billing.pahanabillingsystem.controller;

import lk.pahana.billing.pahanabillingsystem.dao.ItemDAO;
import lk.pahana.billing.pahanabillingsystem.model.Item;
import lk.pahana.billing.pahanabillingsystem.model.User;

import javax.servlet.ServletException; // Tomcat 9 / Java EE 8 නිසා javax.* imports
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Session check එකට

import java.io.IOException;
import java.util.List;


@WebServlet("/items")
public class ItemServlet extends HttpServlet {
    private ItemDAO itemDAO;

    public void init() {
        itemDAO = new ItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }


        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can manage items.");
            request.getRequestDispatcher("bill?action=dashboard").forward(request, response);
            return;
        }
        // ***************************************************************

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "insert":
                insertItem(request, response);
                break;
            case "delete":
                deleteItem(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "update":
                updateItem(request, response);
                break;
            case "view":
                viewItem(request, response);
                break;
            case "list":
            default:
                listItems(request, response);
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

        // *** Role-Based Access Control: Item Management only admin
        User currentUser = (User) session.getAttribute("currentUser");
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("error", "Access Denied: Only administrators can manage items.");
            request.getRequestDispatcher("bill?action=dashboard").forward(request, response);
            return;
        }


        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "insert":
                insertItem(request, response);
                break;
            case "update":
                updateItem(request, response);
                break;
            case "delete":
                deleteItem(request, response);
                break;
            default:
                listItems(request, response);
                break;
        }
    }


    private void listItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("search"); // Search box එකෙන් එන "search" parameter එක ගන්නවා
        List<Item> listItem;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {

            listItem = itemDAO.searchItems(searchTerm.trim());
            request.setAttribute("searchTerm", searchTerm);
        } else {

            listItem = itemDAO.getAllItems();
        }

        request.setAttribute("listItem", listItem);
        request.getRequestDispatcher("item-list.jsp").forward(request, response);
    }


    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("item-form.jsp").forward(request, response);
    }


    private void insertItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        String itemName = request.getParameter("itemName");
        double unitPrice = 0.0;
        int quantityInStock = 0;

        // Input validation and parsing
        try {
            unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
            quantityInStock = Integer.parseInt(request.getParameter("quantityInStock"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price or quantity. Please enter valid numbers.");
            showNewForm(request, response);
            return;
        }

        Item newItem = new Item(itemId, itemName, unitPrice, quantityInStock);
        boolean success = itemDAO.addItem(newItem);

        if (success) {
            request.setAttribute("message", "Item added successfully!");
        } else {

            if (itemDAO.getItemById(itemId) != null) {
                request.setAttribute("error", "Error adding item. Item ID '" + itemId + "' already exists.");
            } else {
                request.setAttribute("error", "Error adding item. Please check server logs for database issues.");
            }
        }
        listItems(request, response);
    }


    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        Item existingItem = itemDAO.getItemById(itemId);

        if (existingItem != null) {
            request.setAttribute("item", existingItem);
            request.getRequestDispatcher("item-form.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Item not found for editing: " + itemId);
            listItems(request, response);
        }
    }


    private void updateItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        String itemName = request.getParameter("itemName");
        double unitPrice = 0.0;
        int quantityInStock = 0;

        // Input validation and parsing
        try {
            unitPrice = Double.parseDouble(request.getParameter("unitPrice"));
            quantityInStock = Integer.parseInt(request.getParameter("quantityInStock"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid price or quantity. Please enter valid numbers.");
            // Re-show form with existing data and error
            Item currentItem = itemDAO.getItemById(itemId);
            request.setAttribute("item", currentItem);
            request.getRequestDispatcher("item-form.jsp").forward(request, response);
            return;
        }

        Item item = new Item(itemId, itemName, unitPrice, quantityInStock);
        boolean success = itemDAO.updateItem(item);

        if (success) {
            request.setAttribute("message", "Item updated successfully!");
        } else {
            request.setAttribute("error", "Error updating item: " + itemId);
        }
        listItems(request, response);
    }


    private void deleteItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        boolean success = itemDAO.deleteItem(itemId);

        if (success) {
            request.setAttribute("message", "Item deleted successfully!");
        } else {
            request.setAttribute("error", "Error deleting item: " + itemId);
        }
        listItems(request, response);
    }


    private void viewItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        Item item = itemDAO.getItemById(itemId);

        if (item != null) {
            request.setAttribute("item", item);
            request.getRequestDispatcher("item-details.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Item not found: " + itemId);
            listItems(request, response);
        }
    }
}
