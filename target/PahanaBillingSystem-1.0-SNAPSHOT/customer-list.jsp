<%-- src/main/webapp/customer-list.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Customer List</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">

    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>

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
        <h2>Customer List</h2>
        <p>
            <a href="customers?action=new" class="btn btn-success">Add New Customer</a>
        </p>

        <%-- Display messages --%>
        <c:if test="${not empty requestScope.message}">
            <div class="message success">${requestScope.message}</div>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div class="message error">${requestScope.error}</div>
        </c:if>

        <table border="1">
            <thead>
            <tr>
                <th>Account Number</th>
                <th>Name</th>
                <th>Address</th>
                <th>Telephone</th>
                <th>Units Consumed</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="customer" items="${listCustomers}">
                <tr>
                    <td>${customer.accountNumber}</td>
                    <td>${customer.name}</td>
                    <td>${customer.address}</td>
                    <td>${customer.telephoneNumber}</td>
                    <td>${customer.unitsConsumed}</td>
                    <td>
                        <a href="customers?action=view&accountNumber=${customer.accountNumber}" class="btn btn-info btn-sm">View</a>
                        <a href="customers?action=edit&accountNumber=${customer.accountNumber}" class="btn btn-primary btn-sm">Edit</a>
                        <a href="customers?action=delete&accountNumber=${customer.accountNumber}" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this customer?');">Delete</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty listCustomers}">
                <tr>
                    <td colspan="6">No customers found.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2024</p>
</footer>
</body>
</html>