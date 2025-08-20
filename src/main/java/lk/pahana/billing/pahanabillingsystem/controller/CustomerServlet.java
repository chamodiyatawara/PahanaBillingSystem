package lk.pahana.billing.pahanabillingsystem.controller;


import lk.pahana.billing.pahanabillingsystem.dao.CustomerDAO;
import lk.pahana.billing.pahanabillingsystem.model.Customer;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/customers") // url path that can access the servlet
public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO;

    public void init() {
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list"; // Default action if no action parameter is provided
        }

        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "insert":
                insertCustomer(request, response);
                break;
            case "delete":
                deleteCustomer(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "update":
                updateCustomer(request, response);
                break;
            case "view":
                viewCustomer(request, response);
                break;
            case "list":
            default:
                listCustomers(request, response); // Default action: list all customers
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // POST requests sent to doGet
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Customer> listCustomers = customerDAO.getAllCustomers();
        request.setAttribute("listCustomers", listCustomers); // Customer list
        request.getRequestDispatcher("customer-list.jsp").forward(request, response); // forward customer-list.jsp
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("customer-form.jsp").forward(request, response); // forward New customer form
    }

    private void insertCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephoneNumber = request.getParameter("telephoneNumber");
        double unitsConsumed = 0; // Default value in case parsing fails
        try {
            unitsConsumed = Double.parseDouble(request.getParameter("unitsConsumed"));
        } catch (NumberFormatException e) {
            System.err.println("Error parsing unitsConsumed: " + request.getParameter("unitsConsumed"));
            request.setAttribute("error", "Invalid Units Consumed. Please enter a number.");
            showNewForm(request, response); // Redirect back to form with error
            return;
        }

        //debugging
        System.out.println("--- DEBUGGING CUSTOMER INSERT ---");
        System.out.println("Account Number received from form: '" + accountNumber + "'");
        System.out.println("Name received from form: '" + name + "'");
        System.out.println("Address received from form: '" + address + "'");
        System.out.println("Telephone Number received from form: '" + telephoneNumber + "'");
        System.out.println("Units Consumed received from form: '" + unitsConsumed + "'");
        System.out.println("---------------------------------");


        Customer newCustomer = new Customer(accountNumber, name, address, telephoneNumber, unitsConsumed);
        boolean success = customerDAO.addCustomer(newCustomer);
        if (success) {
            request.setAttribute("message", "Customer added successfully!");
        } else {
            // Error adding - could be a duplicate account number if primary key constraint violated
            Customer existingCustomer = customerDAO.getCustomerByAccountNumber(accountNumber);
            if (existingCustomer != null) {
                request.setAttribute("error", "Error adding customer. Account number '" + accountNumber + "' already exists.");
            } else {
                request.setAttribute("error", "Error adding customer. Please check server logs for database issues.");
            }
        }
        listCustomers(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        Customer existingCustomer = customerDAO.getCustomerByAccountNumber(accountNumber);
        request.setAttribute("customer", existingCustomer);
        request.getRequestDispatcher("customer-form.jsp").forward(request, response); // forward to Edit form
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber"); // does not change the Account number (primary key)
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String telephoneNumber = request.getParameter("telephoneNumber");
        double unitsConsumed = Double.parseDouble(request.getParameter("unitsConsumed"));

        Customer customer = new Customer(accountNumber, name, address, telephoneNumber, unitsConsumed);
        boolean success = customerDAO.updateCustomer(customer);
        if (success) {
            request.setAttribute("message", "Customer updated successfully!");
        } else {
            request.setAttribute("error", "Error updating customer.");
        }
        listCustomers(request, response);
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        boolean success = customerDAO.deleteCustomer(accountNumber);
        if (success) {
            request.setAttribute("message", "Customer deleted successfully!");
        } else {
            request.setAttribute("error", "Error deleting customer.");
        }
        listCustomers(request, response);
    }

    private void viewCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accountNumber = request.getParameter("accountNumber");
        Customer customer = customerDAO.getCustomerByAccountNumber(accountNumber);
        if (customer != null) {
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("customer-details.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Customer not found!");
            listCustomers(request, response);
        }
    }
}
