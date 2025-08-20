<%-- src/main/webapp/help.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Help</title>

    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>

    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<header>
    <div class="container">
        <h1>Pahana Edu Billing System</h1>
        <nav>
            <ul>
                <li><a href="dashboard.jsp">Dashboard</a></li>
                <li><a href="customers?action=list">Manage Customers</a></li>
                <li><a href="bill-calculator.jsp">Calculate Bill</a></li>
                <li><a href="help.jsp">Help</a></li>
                <li><a href="auth?action=logout">Logout</a></li>
            </ul>
        </nav>
    </div>
</header>

<div class="container">
    <div class="main-content">
        <h2>Help Section - System Usage Guidelines</h2>
        <p>Welcome to the help section of Pahana Edu Online Billing System. Here are some guidelines to help you use the system effectively:</p>

        <h3>1. User Authentication (Login)</h3>
        <ul>
            <li>To access the system, you must log in with a valid username and password.</li>
            <li>Default Login: <strong>Username:</strong> <code>admin</code>, <strong>Password:</strong> <code>admin123</code> (For demonstration purposes. In a real system, passwords should be hashed and secured.)</li>
            <li>If you encounter login issues, please contact the system administrator.</li>
        </ul>

        <h3>2. Manage Customers</h3>
        <ul>
            <li>Go to the "Manage Customers" section from the navigation bar.</li>
            <li><strong>Add New Customer:</strong> Click "Add New Customer" button to register new customer details (Account Number, Name, Address, Telephone Number, Units Consumed).</li>
            <li><strong>Edit Customer Information:</strong> Click the "Edit" button next to a customer's entry in the list to update their details.</li>
            <li><strong>View Customer Information:</strong> Click the "View" button to see specific details of a customer.</li>
            <li><strong>Delete Customer:</strong> Click the "Delete" button to remove a customer from the system. Be careful, this action is permanent.</li>
        </ul>

        <h3>3. Calculate and Print Bill</h3>
        <ul>
            <li>Navigate to the "Calculate Bill" section.</li>
            <li>Enter the customer's Account Number and the Units Consumed.</li>
            <li>Click "Calculate Bill" to see the total bill amount.</li>
            <li>You can use the "Print Bill" button to print the generated bill.</li>
            <li><small>Note: Bill calculation logic is simplified for this demo (e.g., flat rate per unit). In a real system, this would be more complex based on tariffs.</small></li>
        </ul>

        <h3>4. Exit System</h3>
        <ul>
            <li>To securely log out from the system, click the "Logout" link in the navigation bar.</li>
        </ul>

        <h3>Troubleshooting:</h3>
        <ul>
            <li>If the page does not load correctly, check if the Tomcat server is running.</li>
            <li>If data is not saving, check your MySQL server status and database connection details in <code>DBConnection.java</code>.</li>
            <li>For any unhandled errors, please report to the developer with a screenshot.</li>
        </ul>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2024</p>
</footer>
</body>
</html>