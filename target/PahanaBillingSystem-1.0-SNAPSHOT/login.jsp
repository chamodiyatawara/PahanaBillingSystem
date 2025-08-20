<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pahana Edu - Login</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f4f4f4;
        }
        .login-container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 350px;
            text-align: center;
        }
        .login-container h2 {
            margin-bottom: 20px;
            color: #333;
        }
        .login-container input[type="text"],
        .login-container input[type="password"] {
            width: calc(100% - 22px);
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .login-container button {
            width: 100%;
            padding: 10px;
            background-color: #337ab7;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        .login-container button:hover {
            background-color: #286090;
        }
        .error-message {
            color: #d9534f;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success-message {
            color: #5cb85c;
            margin-bottom: 15px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>Pahana Edu - Login</h2>
    <%-- Error message if login fails --%>
    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="error-message"><%= request.getAttribute("errorMessage") %></div>
    <% } %>
    <%-- Success message if logged out --%>
    <% if (request.getParameter("message") != null && request.getParameter("message").equals("logged_out")) { %>
    <div class="success-message">You have been logged out successfully!</div>
    <% } %>
    <form action="auth" method="post">
        <input type="hidden" name="action" value="login">
        <div class="form-group">
            <input type="text" name="username" placeholder="Username" required>
        </div>
        <div class="form-group">
            <input type="password" name="password" placeholder="Password" required>
        </div>
        <button type="submit">Login</button>
    </form>
</div>
</body>
</html>