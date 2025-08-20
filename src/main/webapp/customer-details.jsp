<%-- src/main/webapp/customer-details.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Customer Details</title>

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
        <h2>Customer Details</h2>

        <c:if test="${customer != null}">
            <p><strong>Account Number:</strong> ${customer.accountNumber}</p>
            <p><strong>Name:</strong> ${customer.name}</p>
            <p><strong>Address:</strong> ${customer.address}</p>
            <p><strong>Telephone Number:</strong> ${customer.telephoneNumber}</p>
            <p><strong>Units Consumed:</strong> ${customer.unitsConsumed}</p>
            <p>
                <a href="customers?action=edit&accountNumber=${customer.accountNumber}" class="btn btn-primary">Edit Customer</a>
                <a href="customers?action=list" class="btn btn-secondary">Back to List</a>
            </p>
        </c:if>
        <c:if test="${customer == null}">
            <div class="message error">Customer not found.</div>
            <p><a href="customers?action=list" class="btn btn-secondary">Back to Customer List</a></p>
        </c:if>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2024</p>
</footer>
</body>
</html>