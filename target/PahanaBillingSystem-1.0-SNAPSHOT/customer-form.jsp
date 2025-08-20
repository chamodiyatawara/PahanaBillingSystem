<%-- src/main/webapp/customer-form.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Session check to ensure user is logged in and unauthorized access is prevented --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - <c:if test="${customer != null}">Edit</c:if><c:if test="${customer == null}">Add</c:if> Customer</title>

    <%-- Cache Control Headers to prevent browser caching after logout --%>
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
        <h2><c:if test="${customer != null}">Edit</c:if><c:if test="${customer == null}">Add New</c:if> Customer</h2>

        <form action="customers" method="post">
            <%-- Hidden field to indicate the action (insert or update) --%>
            <input type="hidden" name="action" value="<c:if test="${customer != null}">update</c:if><c:if test="${customer == null}">insert</c:if>">
            <c:if test="${customer != null}">
                <input type="hidden" name="originalAccountNumber" value="<c:out value="${customer.accountNumber}"/>">
            </c:if>

            <div class="form-group">
                <label for="accountNumber">Account Number:</label>
                <%--
                    This is the primary input field for Account Number.
                    It's readonly during an 'edit' operation.
                --%>
                <input type="text" id="accountNumber" name="accountNumber" value="<c:out value="${customer.accountNumber}"/>" <c:if test="${customer != null}">readonly</c:if> required>
                <c:if test="${customer != null}"><small> (Account Number cannot be changed)</small></c:if>
            </div>

            <div class="form-group">
                <label for="name">Name:</label>
                <input type="text" id="name" name="name" value="<c:out value="${customer.name}"/>" required>
            </div>
            <div class="form-group">
                <label for="address">Address:</label>
                <input type="text" id="address" name="address" value="<c:out value="${customer.address}"/>">
            </div>
            <div class="form-group">
                <label for="telephoneNumber">Telephone Number:</label>
                <input type="text" id="telephoneNumber" name="telephoneNumber" value="<c:out value="${customer.telephoneNumber}"/>">
            </div>
            <div class="form-group">
                <label for="unitsConsumed">Units Consumed:</label>
                <input type="number" id="unitsConsumed" name="unitsConsumed" value="<c:out value="${customer.unitsConsumed}"/>" step="0.01" min="0" required>
            </div>
            <button type="submit" class="btn btn-primary">
                <c:if test="${customer != null}">Update</c:if><c:if test="${customer == null}">Add</c:if> Customer
            </button>
            <a href="customers?action=list" class="btn btn-secondary">Cancel</a>
        </form>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2024</p>
</footer>
</body>
</html>
