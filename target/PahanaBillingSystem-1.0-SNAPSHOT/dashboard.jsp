<%-- src/main/webapp/dashboard.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Session check to ensure user is logged in --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Dashboard</title>


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
        <h2>Welcome, ${sessionScope.currentUser.username}!</h2>
        <p>This is the dashboard for the Online Billing System. You can manage customer accounts, calculate bills, and access other features from here.</p>

        <h3>Quick Links:</h3>
        <ul>
            <li><a href="customers?action=new">Add New Customer</a></li>
            <li><a href="customers?action=list">View All Customers</a></li>
            <li><a href="bill-calculator.jsp">Generate a new Bill</a></li>
        </ul>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2024</p>
</footer>
</body>
</html>