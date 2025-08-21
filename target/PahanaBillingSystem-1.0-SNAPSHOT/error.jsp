<%-- src/main/webapp/error.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %> <%-- isErrorPage="true" වැදගත් --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Pahana Edu - Error</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <style>
        /* Specific styles for error page */
        .error-page-container {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            min-height: 100vh; /* Full height of the viewport */
            background-color: #f0f2f5;
            text-align: center;
            padding: 20px;
        }
        .error-card {
            background-color: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 100%;
        }
        .error-card h1 {
            font-size: 3em;
            color: #d9534f; /* Danger color */
            margin-bottom: 10px;
        }
        .error-card h2 {
            font-size: 1.8em;
            color: #333;
            margin-bottom: 20px;
        }
        .error-card p {
            font-size: 1.1em;
            color: #666;
            margin-bottom: 30px;
        }
        .error-card .btn {
            padding: 12px 25px;
            font-size: 1.1em;
        }
    </style>
</head>
<body>
<div class="error-page-container">
    <div class="error-card">
        <h1>Oops!</h1>
        <h2>Something went wrong.</h2>
        <p>
            We apologize for the inconvenience. An unexpected error has occurred.
            Please try again later or contact support if the problem persists.
        </p>
        <%-- Display specific error message if available (from exception object) --%>
        <c:if test="${not empty pageContext.exception}">
            <p style="color: #a94442; font-size: 0.9em;">
                Error Details: ${pageContext.exception.message}
            </p>
        </c:if>
        <a href="bill?action=dashboard" class="btn btn-primary">Go to Dashboard</a>
    </div>
</div>
</body>
</html>