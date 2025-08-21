<%--&lt;%&ndash; src/main/webapp/bill-calculator.jsp &ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>

<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>

<%--&lt;%&ndash; Session check &ndash;%&gt;--%>
<%--<c:if test="${empty sessionScope.currentUser}">--%>
<%--    <c:redirect url="login.jsp?error=unauthorized"/>--%>
<%--</c:if>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Pahana Edu - Calculate Bill</title>--%>

<%--    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>--%>
<%--    <meta http-equiv="Pragma" content="no-cache"/>--%>
<%--    <meta http-equiv="Expires" content="0"/>--%>

<%--    <link rel="stylesheet" type="text/css" href="css/style.css">--%>
<%--</head>--%>
<%--<body>--%>
<%--<header>--%>
<%--    <div class="container">--%>
<%--        <h1>Pahana Edu Billing System</h1>--%>
<%--        <nav>--%>
<%--            <ul>--%>
<%--                <li><a href="dashboard.jsp">Dashboard</a></li>--%>
<%--                <li><a href="customers?action=list">Manage Customers</a></li>--%>
<%--                <li><a href="items?action=list">Manage Items</a></li>--%>
<%--                <li><a href="bill-calculator.jsp">Calculate Bill</a></li>--%>
<%--                <li><a href="help.jsp">Help</a></li>--%>
<%--                <li><a href="auth?action=logout">Logout</a></li>--%>
<%--            </ul>--%>
<%--        </nav>--%>
<%--    </div>--%>
<%--</header>--%>

<%--<div class="container">--%>
<%--    <div class="main-content">--%>
<%--        <h2>Calculate and Print Bill</h2>--%>

<%--        <form id="billForm">--%>
<%--            <div class="form-group">--%>
<%--                <label for="accountNumber">Account Number:</label>--%>
<%--                <input type="text" id="accountNumber" name="accountNumber" required>--%>
<%--            </div>--%>
<%--            <div class="form-group">--%>
<%--                <label for="unitsConsumed">Units Consumed:</label>--%>
<%--                <input type="number" id="unitsConsumed" name="unitsConsumed" step="0.01" min="0" required>--%>
<%--            </div>--%>
<%--            <button type="submit" class="btn btn-primary" id="calculateBtn">Calculate Bill</button>--%>
<%--        </form>--%>

<%--        <div id="billResult" style="--%>
<%--            margin-top: 30px;--%>
<%--            padding: 15px;--%>
<%--            border: 1px solid #d6e9c6; /* Success border color */--%>
<%--            border-radius: 8px;--%>
<%--            background-color: #dff0d8; /* Success background color */--%>
<%--            color: #3c763d; /* Success text color */--%>
<%--            box-shadow: 0 2px 5px rgba(0,0,0,0.1); /* පොඩි shadow එකක් */--%>
<%--            display: none; /* මුලින් hidden, JavaScript එකෙන් block කරනවා */--%>
<%--        ">--%>
<%--            &lt;%&ndash; Bill calculation result will be displayed here by JavaScript &ndash;%&gt;--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</div>--%>

<%--<footer>--%>
<%--    <p>Pahana Edu Billing System &copy; 2025</p>--%>
<%--</footer>--%>

<%--<script>--%>
<%--    // Simple client-side validation--%>
<%--    function validateBillForm() {--%>
<%--        const accountNumber = document.getElementById('accountNumber').value;--%>
<%--        const unitsConsumed = document.getElementById('unitsConsumed').value;--%>

<%--        if (accountNumber === "") {--%>
<%--            alert("Please enter an Account Number.");--%>
<%--            return false;--%>
<%--        }--%>
<%--        if (unitsConsumed === "" || isNaN(unitsConsumed) || parseFloat(unitsConsumed) < 0) {--%>
<%--            alert("Please enter a valid number for Units Consumed.");--%>
<%--            return false;--%>
<%--        }--%>
<%--        return true;--%>
<%--    }--%>


<%--    function calculateBillAmount(units) {--%>
<%--        let totalBill = 0;--%>
<%--        const ratePerUnit = 10.0; // Rs. 10 per unit--%>
<%--        totalBill = units * ratePerUnit;--%>
<%--        console.log("Total calculated bill: " + totalBill); // console.log for debugging--%>
<%--        return totalBill.toFixed(2); // Format to 2 decimal places--%>
<%--    }--%>

<%--    // JavaScript to handle calculation on the page--%>
<%--    document.addEventListener('DOMContentLoaded', function() {--%>
<%--        const calculateBtn = document.getElementById('calculateBtn');--%>

<%--        // ** Diagnostic Test **--%>
<%--        // මේ කොටසෙන් JavaScript template literals හරියටම වැඩ කරනවද කියලා බලමු.--%>
<%--        // Page එක load වෙද්දී Console එකේ මෙහි output පෙන්වනවද බලන්න.--%>
<%--        try {--%>
<%--            const diagnosticVar = "Success!";--%>
<%--            const diagnosticString = `Diagnostic Test: Variables work -> ${diagnosticVar}. This should not be blank!`;--%>
<%--            console.log(diagnosticString); // Console එකේ මේ output එක පෙන්වනවද බලන්න--%>
<%--        } catch (e) {--%>
<%--            console.error("Diagnostic Test Failed (Template Literal Issue):", e);--%>
<%--        }--%>
<%--        // ** End Diagnostic Test **--%>

<%--        if (calculateBtn) {--%>
<%--            calculateBtn.addEventListener('click', function(event) {--%>
<%--                event.preventDefault();--%>

<%--                console.log("Calculate Button Clicked!");--%>

<%--                if (validateBillForm()) {--%>
<%--                    console.log("Validation Passed! Proceeding with calculation...");--%>

<%--                    const accountNumber = document.getElementById('accountNumber').value;--%>
<%--                    console.log("Account Number (from input): " + accountNumber);--%>

<%--                    const units = parseFloat(document.getElementById('unitsConsumed').value);--%>
<%--                    console.log("Units Consumed (from input): " + units);--%>

<%--                    const billAmount = calculateBillAmount(units);--%>
<%--                    console.log("Calculated Bill Amount: " + billAmount);--%>

<%--                    // Note: This customerName retrieval is a client-side simulation.--%>
<%--                    // In a real application, Customer Name should be fetched from the database or server-side.--%>
<%--                    const dummyCustomers = JSON.parse(localStorage.getItem('customers')) || [];--%>
<%--                    const customer = dummyCustomers.find(c => c.accountNumber === accountNumber);--%>
<%--                    const customerName = customer ? customer.name : "N/A (Customer not found)";--%>

<%--                    // *** මෙතන backticks (`) හරියටම භාවිතා කර ඇත. මෙය ගැටලුවට හේතුව විය හැකිය ***--%>
<%--                    const contentToRender = `--%>
<%--                        <h3>Bill Calculation Result:</h3>--%>
<%--                        <p><strong>Account Number:</strong> ${accountNumber}</p>--%>
<%--                        <p><strong>Customer Name:</strong> ${customerName}</p>--%>
<%--                        <p><strong>Units Consumed:</strong> ${units}</p>--%>
<%--                        <p><strong>Total Bill Amount:</strong> Rs. ${billAmount}</p>--%>
<%--                        <button class="btn btn-primary" onclick="window.print()">Print Bill</button>--%>
<%--                    `;--%>
<%--                    console.log("Attempting to render this HTML content:", contentToRender);--%>

<%--                    document.getElementById('billResult').innerHTML = contentToRender;--%>
<%--                    document.getElementById('billResult').className = 'message success';--%>
<%--                    document.getElementById('billResult').style.display = 'block';--%>

<%--                } else {--%>
<%--                    console.log("Validation Failed! Calculation stopped.");--%>
<%--                }--%>
<%--            });--%>
<%--        }--%>
<%--    });--%>
<%--</script>--%>

<%--</body>--%>
<%--</html>--%>

<%-- src/main/webapp/bill-calculator.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Redirecting to Bill Generator...</title>
    <meta http-equiv="refresh" content="0; url=bill?action=newBill" />
</head>
<body>
<p>Redirecting to <a href="bill?action=newBill">Bill Generator Page</a>...</p>
</body>
</html>
