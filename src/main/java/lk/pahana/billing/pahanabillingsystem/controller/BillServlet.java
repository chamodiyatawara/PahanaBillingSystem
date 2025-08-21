package lk.pahana.billing.pahanabillingsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.pahana.billing.pahanabillingsystem.dao.BillDAO;
import lk.pahana.billing.pahanabillingsystem.dao.BillItemDAO;
import lk.pahana.billing.pahanabillingsystem.dao.CustomerDAO;
import lk.pahana.billing.pahanabillingsystem.dao.ItemDAO;
import lk.pahana.billing.pahanabillingsystem.dao.UserDAO;

import lk.pahana.billing.pahanabillingsystem.model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

// @WebServlet annotation එකෙන් මේ Servlet එකට access කරන්න පුළුවන් URL path එක කියනවා.
// උදා: http://localhost:8080/PahanaBillingSystem_war_exploded/bill
@WebServlet("/bill")
public class BillServlet extends HttpServlet {
    private BillDAO billDAO;
    private BillItemDAO billItemDAO;
    private CustomerDAO customerDAO;
    private ItemDAO itemDAO;
    private UserDAO userDAO;

    public void init() {
        billDAO = new BillDAO();
        billItemDAO = new BillItemDAO();
        customerDAO = new CustomerDAO();
        itemDAO = new ItemDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session check: User logged in ද කියලා බලනවා
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "newBill"; // Default action: New Bill generate කරන්න
        }

        switch (action) {
            case "dashboard":
                loadDashboard(request, response);
                break;
            case "newBill":
                showNewBillForm(request, response);
                break;
            case "addTempItem": // AJAX request for adding item to temporary bill list
                addTemporaryItem(request, response);
                break;
            case "removeItem": // AJAX request for removing item from temporary bill list
                removeTemporaryItem(request, response);
                break;
            case "finalizeBill":
                finalizeBill(request, response);
                break;
            case "listBills":
                listBills(request, response);
                break;
            case "viewBillDetails":
                viewBillDetails(request, response);
                break;
            case "printBill":
                printBill(request, response);
                break;
            default:
                showNewBillForm(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // POST requests ටිකත් doGet එකට යවනවා
    }

    // අලුත් බිලක් generate කරන්න form එක පෙන්වන්න
    private void showNewBillForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Customer list එක සහ Item list එක JSP එකට යවනවා
        List<Customer> customers = customerDAO.getAllCustomers();
        List<Item> items = itemDAO.getAllItems();
        request.setAttribute("customers", customers);
        request.setAttribute("items", items);

        // Session එකේ temporary bill items list එකක් තියාගන්නවා
        // මේකෙන් තමයි user bill එකට add කරන items ටික තාවකාලිකව තියාගන්නේ.
        List<BillItem> tempBillItems = (List<BillItem>) request.getSession().getAttribute("tempBillItems");
        if (tempBillItems == null) {
            tempBillItems = new ArrayList<>();
            request.getSession().setAttribute("tempBillItems", tempBillItems);
        }
        request.setAttribute("tempBillItems", tempBillItems);

// tempBillItems list එක JSON string එකකට convert කරලා JSP එකට යවනවා
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String tempBillItemsJson = objectMapper.writeValueAsString(tempBillItems);
            request.setAttribute("tempBillItemsJson", tempBillItemsJson);
        } catch (Exception e) {
            System.err.println("Error converting tempBillItems to JSON: " + e.getMessage());
            request.setAttribute("tempBillItemsJson", "[]"); // Default to empty array on error
        }
        request.getRequestDispatcher("bill-generator.jsp").forward(request, response);
    }

    // Temporary Bill Item එකක් Add කරන්න (AJAX request එකක් විදියට)
    private void addTemporaryItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (existing code for validation and adding item to tempBillItems list)

        String itemId = request.getParameter("itemId");
        int quantity = 0;

        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            // response.getWriter().write("Error: Invalid quantity."); // කලින් තිබ්බ response එක
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid quantity.\"}");
            return;
        }

        Item item = itemDAO.getItemById(itemId);
        if (item == null) {
            // response.getWriter().write("Error: Item not found.");
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Item not found.\"}");
            return;
        }

        if (quantity <= 0) {
            // response.getWriter().write("Error: Quantity must be positive.");
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Quantity must be positive.\"}");
            return;
        }

        // තොගය ප්‍රමාණවත්ද කියලා බලනවා
        if (item.getQuantityInStock() < quantity) {
            // response.getWriter().write("Error: Not enough stock for " + item.getItemName() + ". Available: " + item.getQuantityInStock());
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Not enough stock for " + item.getItemName() + ". Available: " + item.getQuantityInStock() + "\"}");
            return;
        }

        System.out.println("--- DEBUGGING ADD TEMPORARY ITEM ---");
        System.out.println("Item ID received in Servlet: '" + itemId + "'");
        System.out.println("Quantity received in Servlet: " + quantity);

        List<BillItem> tempBillItems = (List<BillItem>) request.getSession().getAttribute("tempBillItems");
        if (tempBillItems == null) {
            tempBillItems = new ArrayList<>();
            request.getSession().setAttribute("tempBillItems", tempBillItems);
        }

        // දැනටමත් මේ item එක bill එකට add කරලා නම්, quantity එක update කරනවා
        boolean itemExists = false;
        for (BillItem bi : tempBillItems) {
            if (bi.getItemId().equals(itemId)) {
                // අලුත් quantity එකෙන් තොගය ප්‍රමාණවත්ද කියලා නැවත බලනවා
                if (item.getQuantityInStock() < (bi.getQuantity() + quantity)) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"Not enough stock for " + item.getItemName() + ". Available: " + item.getQuantityInStock() + "\"}");
                    return;
                }
                bi.setQuantity(bi.getQuantity() + quantity);
                bi.setSubTotal(bi.getQuantity() * bi.getUnitPriceAtSale());

                System.out.println("Updating existing item in temp list: " + itemId + ", New Quantity: " + bi.getQuantity());

                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            double subTotal = quantity * item.getUnitPrice();
            BillItem billItem = new BillItem(0, itemId, quantity, item.getUnitPrice(), subTotal); // billId දැනට 0, finalize කරද්දී update වෙනවා
            tempBillItems.add(billItem);

            System.out.println("Added new item to temp list: " + billItem.toString()); // *** BillItem object එක print කරනවා ***

        }
        System.out.println("--- END DEBUGGING ADD TEMPORARY ITEM ---");

        // *** මෙතනින් පහළට වෙනස්කම් ***
        // Response එක JSON විදියට යවනවා, අලුත් tempBillItems list එකයි total amount එකයි එක්ක
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", "success");
        responseData.put("message", "Item added to bill.");
        responseData.put("tempBillItems", tempBillItems); // අලුත් tempBillItems list එක

        // අලුත් total amount එක calculate කරනවා
        double currentTotal = 0;
        for(BillItem bi : tempBillItems) {
            currentTotal += bi.getSubTotal();
        }
        responseData.put("totalAmount", currentTotal);

        // Item Names Map එකත් යවනවා, මොකද JSP එකේදී Item Name එක පෙන්වන්න අවශ්‍ය නිසා
        Map<String, String> itemNamesMap = new HashMap<>();
        for(BillItem bi : tempBillItems) {
            Item currentItem = itemDAO.getItemById(bi.getItemId());
            if(currentItem != null) {
                itemNamesMap.put(currentItem.getItemId(), currentItem.getItemName());
            }
        }
        responseData.put("itemNamesMap", itemNamesMap);

        objectMapper.writeValue(response.getWriter(), responseData); // JSON response එක යවනවා
    }

    // Temporary Bill Item එකක් Remove කරන්න (AJAX request එකක් විදියට)
    private void removeTemporaryItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemId = request.getParameter("itemId");
        List<BillItem> tempBillItems = (List<BillItem>) request.getSession().getAttribute("tempBillItems");

        System.out.println("--- DEBUGGING REMOVE ITEM ---");
        System.out.println("Item ID to remove (from request): '" + itemId + "'");
        System.out.println("Initial tempBillItems size: " + (tempBillItems != null ? tempBillItems.size() : "null"));

        if (tempBillItems != null) {
            boolean removed = false; // Item එක remove වුණාද කියලා බලන්න
            Iterator<BillItem> iterator = tempBillItems.iterator();
            while (iterator.hasNext()) {
                BillItem bi = iterator.next();
                System.out.println("  Comparing: Current BillItem ID in list: '" + bi.getItemId() + "' with Request Item ID: '" + itemId + "'");
                if (bi.getItemId().equals(itemId)) { // *** මෙතන equals() method එකේ ගැටලුවක් වෙන්න පුළුවන් ***
                    iterator.remove();
                    removed = true;
                    System.out.println("  SUCCESS: Item " + itemId + " removed from temporary list.");
                    break;
                }
            }
            if (!removed) {
                System.out.println("  WARNING: Item " + itemId + " not found in temporary list for removal.");
            }
        } else {
            System.out.println("  WARNING: tempBillItems list is null in session.");
        }
        System.out.println("Final tempBillItems size: " + (tempBillItems != null ? tempBillItems.size() : "null"));
        System.out.println("--- END DEBUGGING REMOVE ITEM ---");

        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", "success");
        responseData.put("message", "Item removed from bill.");
        responseData.put("tempBillItems", tempBillItems); // අලුත් tempBillItems list එක (item එක නැතුව)

        double currentTotal = 0;
        if (tempBillItems != null) {
            for(BillItem bi : tempBillItems) {
                currentTotal += bi.getSubTotal();
            }
        }
        responseData.put("totalAmount", currentTotal);

        Map<String, String> itemNamesMap = new HashMap<>();
        if (tempBillItems != null) {
            for(BillItem bi : tempBillItems) {
                Item currentItem = itemDAO.getItemById(bi.getItemId());
                if(currentItem != null) {
                    itemNamesMap.put(currentItem.getItemId(), currentItem.getItemName());
                }
            }
        }
        responseData.put("itemNamesMap", itemNamesMap);

        objectMapper.writeValue(response.getWriter(), responseData);
    }

    // Bill එක Finalize කරන්න
    private void finalizeBill(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customerAccountNumber = request.getParameter("customerAccountNumber");
        List<BillItem> tempBillItems = (List<BillItem>) request.getSession().getAttribute("tempBillItems");

        if (customerAccountNumber == null || customerAccountNumber.isEmpty()) {
            request.setAttribute("error", "Please select a customer.");
            showNewBillForm(request, response);
            return;
        }
        if (tempBillItems == null || tempBillItems.isEmpty()) {
            request.setAttribute("error", "Please add items to the bill before finalizing.");
            showNewBillForm(request, response);
            return;
        }

        Customer customer = customerDAO.getCustomerByAccountNumber(customerAccountNumber);
        if (customer == null) {
            request.setAttribute("error", "Selected customer not found.");
            showNewBillForm(request, response);
            return;
        }

        double totalAmount = 0;
        for (BillItem bi : tempBillItems) {
            totalAmount += bi.getSubTotal();
        }

        // Get current logged in user ID
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        int userId = currentUser != null ? currentUser.getId() : 0; // If user ID is not tracked, use 0 or a default

        // 1. Bill එක database එකට Add කරන්න
        Bill bill = new Bill(customerAccountNumber, new Date(), totalAmount, userId);
        System.out.println("Attempting to add Bill to DB: " + bill.toString());
        int billId = billDAO.addBill(bill);

        if (billId > 0) {
            boolean allItemsAdded = true;
            boolean allQuantitiesUpdated = true;

            // 2. Bill Items ටික database එකට Add කරන්න සහ Item Quantities අඩු කරන්න
            for (BillItem bi : tempBillItems) {
                bi.setBillId(billId); // Generate වුණ billId එක BillItem එකට set කරනවා
                int billItemId = billItemDAO.addBillItem(bi); // Bill Item එක database එකට දානවා

                if (billItemId > 0) {
                    // Item එකේ තොගය අඩු කරන්න
                    Item itemInStock = itemDAO.getItemById(bi.getItemId());
                    if (itemInStock != null && itemInStock.getQuantityInStock() >= bi.getQuantity()) {
                        int newQuantity = itemInStock.getQuantityInStock() - bi.getQuantity();
                        if (!itemDAO.updateItemQuantity(bi.getItemId(), newQuantity)) {
                            allQuantitiesUpdated = false; // Quantity update failed
                        }
                    } else {
                        allQuantitiesUpdated = false; // Item not found or insufficient stock (should be caught earlier)
                    }
                } else {
                    allItemsAdded = false; // Bill Item add failed
                }
            }

            if (allItemsAdded && allQuantitiesUpdated) {
                request.getSession().removeAttribute("tempBillItems"); // Temporary bill items session එකෙන් අයින් කරනවා
//                request.setAttribute("message", "Bill generated successfully! Bill ID: " + billId);
//                request.setAttribute("finalBillId", billId); // Final bill ID එක JSP එකට යවනවා
                System.out.println("Bill Finalized successfully. Redirecting to bill-details.jsp");
//                request.getRequestDispatcher("bill-details.jsp").forward(request, response); // Bill Details page එකට යවනවා
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Bill generated successfully! Bill ID: " + billId);
                response.sendRedirect("bill?action=viewBillDetails&billId=" + billId); // BillServlet එකේ viewBillDetails action එකට redirect කරනවා
            } else {
                // If something went wrong, try to rollback (more complex for this demo)
                // For simplicity, just show an error.
                request.setAttribute("error", "Error generating bill. Some items or quantities failed to update.");
                // Optionally delete the bill and bill items if rollback is needed
                // billItemDAO.deleteBillItemsByBillId(billId);
                // billDAO.deleteBill(billId);
                showNewBillForm(request, response);
            }

        } else {
            request.setAttribute("error", "Failed to create bill record in database.");
            showNewBillForm(request, response);
        }
    }

    // සියලුම Bills list කරන්න (Search functionality එකත් එක්ක)
    private void listBills(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("search"); // Search box එකෙන් එන "search" parameter එක ගන්නවා
        List<Bill> bills;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Search term එකක් තියෙනවා නම්, searchBills method එක call කරනවා
            bills = billDAO.searchBills(searchTerm.trim());
            request.setAttribute("searchTerm", searchTerm); // Search term එක JSP එකට යවනවා, search box එකේ value එක retain කරන්න
        } else {
            // Search term එකක් නැත්නම්, සියලුම bills ලා ගන්නවා
            bills = billDAO.getAllBills();
        }

        request.setAttribute("bills", bills); // Bill list එක request එකට දානවා
        request.getRequestDispatcher("bill-list.jsp").forward(request, response); // bill-list.jsp එකට forward කරනවා
    }

    // Bill එකක details view කරන්න
    private void viewBillDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int billId = 0;
        try {
            billId = Integer.parseInt(request.getParameter("billId"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Bill ID.");
            listBills(request, response);
            return;
        }

        Bill bill = billDAO.getBillById(billId);
        if (bill == null) {
            request.setAttribute("error", "Bill not found for ID: " + billId);
            listBills(request, response);
            return;
        }

        List<BillItem> billItems = billItemDAO.getBillItemsByBillId(billId);

        // *** මේ කොටස අලුතින් එකතු කරන්න ***
        // Item ID එකට අදාළ Item Name එක තියාගන්න Map එකක් හදනවා
        Map<String, String> itemNamesMap = new HashMap<>();
        for(BillItem bi : billItems) {
            Item item = itemDAO.getItemById(bi.getItemId()); // Item ID එකෙන් Item object එක ගන්නවා
            if(item != null) {
                itemNamesMap.put(item.getItemId(), item.getItemName()); // Map එකට Item ID එකයි Item Name එකයි දානවා
            }
        }
        request.setAttribute("itemNamesMap", itemNamesMap); // Map එක JSP එකට යවනවා
        // ********************************************

        // Customer Account Number එකට අදාළ Customer Name එක තියාගන්න Map එකක්
        Map<String, String> customerNamesMap = new HashMap<>();
        Customer customer = customerDAO.getCustomerByAccountNumber(bill.getCustomerAccountNumber());
        if (customer != null) {
            customerNamesMap.put(customer.getAccountNumber(), customer.getName());
        }
        request.setAttribute("customerNamesMap", customerNamesMap);

        // User ID එකට අදාළ User Name එක තියාගන්න Map එකක්
        Map<Integer, String> userNamesMap = new HashMap<>();
        // User ID එක 0 නම් (default), ඒක skip කරනවා
        if (bill.getUserId() != 0) {
            User user = userDAO.getUserById(bill.getUserId()); // UserDAO එකේ getItemById වගේ getUserById method එකක් අවශ්‍යයි
            if (user != null) {
                userNamesMap.put(user.getId(), user.getUsername());
            }
        }
        request.setAttribute("userNamesMap", userNamesMap);

        request.setAttribute("bill", bill);
        request.setAttribute("billItems", billItems);
        request.getRequestDispatcher("bill-details.jsp").forward(request, response);
    }

    // *** Bill එක Print කරන්න ***
    private void printBill(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int billId = 0;
        try {
            billId = Integer.parseInt(request.getParameter("billId"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Bill ID for printing.");
            listBills(request, response); // Error නම් Bill List එකට යවනවා
            return;
        }

        Bill bill = billDAO.getBillById(billId);
        if (bill == null) {
            request.setAttribute("error", "Bill not found for printing: " + billId);
            listBills(request, response); // Error නම් Bill List එකට යවනවා
            return;
        }

        List<BillItem> billItems = billItemDAO.getBillItemsByBillId(billId);

        // Item Names Map එක (viewBillDetails වගේම)
        Map<String, String> itemNamesMap = new HashMap<>();
        for(BillItem bi : billItems) {
            Item item = itemDAO.getItemById(bi.getItemId());
            if(item != null) {
                itemNamesMap.put(item.getItemId(), item.getItemName());
            }
        }
        request.setAttribute("itemNamesMap", itemNamesMap);

        // Customer Names Map එක (viewBillDetails වගේම)
        Map<String, String> customerNamesMap = new HashMap<>();
        Customer customer = customerDAO.getCustomerByAccountNumber(bill.getCustomerAccountNumber());
        if (customer != null) {
            customerNamesMap.put(customer.getAccountNumber(), customer.getName());
        }
        request.setAttribute("customerNamesMap", customerNamesMap);

        // User Names Map එක (viewBillDetails වගේම)
        Map<Integer, String> userNamesMap = new HashMap<>();
        if (bill.getUserId() != 0) {
            User user = userDAO.getUserById(bill.getUserId());
            if (user != null) {
                userNamesMap.put(user.getId(), user.getUsername());
            }
        }
        request.setAttribute("userNamesMap", userNamesMap);

        request.setAttribute("bill", bill);
        request.setAttribute("billItems", billItems);
        request.getRequestDispatcher("bill-print.jsp").forward(request, response); // bill-print.jsp එකට forward කරනවා
    }

    // Dashboard එකට අවශ්‍ය data load කරන්න ***
    private void loadDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("--- DEBUGGING LOAD DASHBOARD ---");
//        System.out.println("Request URL: " + request.getRequestURL());
//        System.out.println("Request URI: " + request.getRequestURI());
//        System.out.println("Query String: " + request.getQueryString());
//        System.out.println("Action parameter: " + request.getParameter("action"));

        int totalCustomers = customerDAO.getTotalCustomers();
        int totalItems = itemDAO.getTotalItems();
        double totalSalesAmount = billDAO.getTotalSalesAmount();

//        System.out.println("Fetched Total Customers from DB: " + totalCustomers);
//        System.out.println("Fetched Total Items from DB: " + totalItems);
//        System.out.println("Fetched Total Sales Amount from DB: " + totalSalesAmount);

        // *** මෙතන වෙනස් කරන්න (forward එකට ආයෙත් යමු) ***
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("totalSalesAmount", totalSalesAmount);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        // ********************************************
//        System.out.println("--- END DEBUGGING LOAD DASHBOARD ---");
    }
}
