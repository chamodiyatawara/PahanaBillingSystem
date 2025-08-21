<%-- src/main/webapp/bill-details.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Formatting numbers and dates --%>
<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Bill Details</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <%-- Cache Control Headers --%>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
</head>
<body>
<header>
    <div class="header-content">
        <a href="bill?action=dashboard" class="brand" aria-label="Pahana Edu Home">
            <img src="images/logo.png" alt="Pahana Logo" class="brand-logo" onerror="this.style.display='none'"/>
            <div class="brand-identity">
                <span class="brand-name">Pahana Edu</span>
                <span class="brand-tag">Billing System</span>
            </div>
        </a>

        <nav class="main-nav" role="navigation" aria-label="Main Navigation">
            <button class="nav-toggle" aria-expanded="false" aria-controls="nav-menu" title="Menu">☰</button>
            <ul id="nav-menu" class="nav-list">
                <li><a href="bill?action=dashboard" class="nav-link">Dashboard</a></li>
                <li><a href="customers?action=list" class="nav-link">Customers</a></li>
                <c:if test="${sessionScope.currentUser.role == 'admin'}">
                    <li><a href="items?action=list" class="nav-link">Items</a></li>
                </c:if>
                <li><a href="bill?action=newBill" class="nav-link">New Bill</a></li>
                <li><a href="bill?action=listBills" class="nav-link active">Bills</a></li>
                <li><a href="help.jsp" class="nav-link">Help</a></li>
                <c:if test="${sessionScope.currentUser.role == 'admin'}">
                    <li><a href="user?action=registerForm" class="nav-link">Register User</a></li>
                    <li><a href="user?action=listUsers" class="nav-link">Manage Users</a></li>
                </c:if>
            </ul>
        </nav>

        <div class="user-actions" role="group" aria-label="User actions">
            <div class="user-info-inline" title="${sessionScope.currentUser.username}">
                <strong class="user-name">${sessionScope.currentUser.username} (${sessionScope.currentUser.role})</strong>
            </div>
            <a href="auth?action=logout" class="logout-btn" title="Logout">Logout</a>
        </div>
    </div>

    <style>
        /* Scoped navbar improvements */
        header { background:#2c3e50; color:#fff; border-bottom:3px solid #3498db; }
        .header-content { width:95%; margin:0 auto; display:flex; align-items:center; justify-content:space-between; gap:18px; padding:12px 0; flex-wrap:wrap; }
        .brand { display:flex; align-items:center; gap:12px; text-decoration:none; color:inherit; }
        .brand-logo { width:44px; height:44px; object-fit:contain; border-radius:6px; background:#fff; padding:4px; }
        .brand-identity { display:flex; flex-direction:column; line-height:1; }
        .brand-name { font-size:1.15rem; font-weight:700; color:#ecf0f1; }
        .brand-tag { font-size:0.75rem; color:#bcd6ee; margin-top:2px; }

        .main-nav { flex:1 1 480px; display:flex; justify-content:center; position:relative; }
        .nav-list { display:flex; gap:2px; align-items:center; list-style:none; padding:0; margin:0; flex-wrap:wrap; justify-content:center; }
        .nav-link { padding:8px 10px; background:transparent; border:1px solid rgba(236,240,241,0.08); color:#ecf0f1; text-decoration:none; padding:8px 12px; border-radius:6px; transition:all .18s ease; font-weight:600; font-size:0.80rem; }
        .nav-link:hover { background:rgba(255,255,255,0.06); transform:translateY(-1px); }
        .nav-link.active { background:#34495e; color:#fff; box-shadow:0 6px 18px rgba(0,0,0,0.12); }

        .user-actions { display:flex; align-items:center; gap:10px; flex-shrink:0; }
        .user-name { color:#dfeffb; font-weight:600; font-size:0.95rem; padding:6px 10px; background:transparent; border-radius:6px; }
        .logout-btn { padding:8px 12px; background:transparent; border:1px solid rgba(236,240,241,0.08); color:#ecf0f1; border-radius:6px; text-decoration:none; font-weight:700; }
        .logout-btn:hover { background:#b22222; color:white; border-color:transparent; }

        .nav-toggle { display:none; background:transparent; border:1px solid rgba(236,240,241,0.08); color:#ecf0f1; padding:8px 10px; border-radius:6px; cursor:pointer; }

        /* Responsive: collapse nav to toggle on smaller screens */
        @media (max-width:880px) {
            .main-nav { order:3; width:100%; }
            .user-actions { order:2; width:100%; justify-content:flex-end; padding-top:6px; }
            .brand { order:1; }
            .nav-toggle { display:inline-flex; }
            .nav-list {
                position:absolute; top:100%; right:12px; background:linear-gradient(180deg, rgba(44,62,80,0.98), rgba(34,50,66,0.98));
                flex-direction:column; gap:6px; padding:10px; border-radius:8px; box-shadow:0 8px 30px rgba(0,0,0,0.18);
                display:none; min-width:200px; z-index:50;
            }
            .nav-list.open { display:flex; }
        }
    </style>

    <script>
        (function(){
            var btn = document.querySelector('.nav-toggle');
            var menu = document.getElementById('nav-menu');
            if (!btn || !menu) return;
            btn.addEventListener('click', function(e){
                var expanded = this.getAttribute('aria-expanded') === 'true';
                this.setAttribute('aria-expanded', String(!expanded));
                menu.classList.toggle('open');
                e.stopPropagation();
            });
            document.addEventListener('click', function(e){
                if (!menu.classList.contains('open')) return;
                if (!menu.contains(e.target) && !btn.contains(e.target)) {
                    menu.classList.remove('open');
                    btn.setAttribute('aria-expanded','false');
                }
            });
        })();
    </script>
</header>

<div class="container">
    <div class="main-content">
        <h2>Bill Details</h2>

        <%-- Display messages --%>
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="message success">${sessionScope.successMessage}</div>
            <c:remove var="successMessage" scope="session"/> <%-- Message එක පෙන්වූ පසු session එකෙන් ඉවත් කරන්න --%>
        </c:if>
        <c:if test="${not empty requestScope.error}"> <%-- Error messages request scope එකේම තියෙන්න පුළුවන් --%>
            <div class="message error">${requestScope.error}</div>
        </c:if>

        <c:if test="${bill != null}">
            <p><strong>Bill ID:</strong> ${bill.billId}</p>
            <p><strong>Customer Account Number:</strong> ${bill.customerAccountNumber}</p>
            <p><strong>Bill Date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
            <p><strong>Total Amount:</strong> <fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="Rs. "/></p>
            <p><strong>Generated By User ID:</strong> ${bill.userId}</p>

            <h3>Items in this Bill:</h3>
            <table border="1">
                <thead>
                <tr>
                    <th>Item ID</th>
                    <th>Item Name</th> <%-- අලුත් Item Name Column එක --%>
                    <th>Quantity</th>
                    <th>Unit Price (at Sale)</th>
                    <th>Sub Total</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="billItem" items="${billItems}">
                    <tr>
                        <td>${billItem.itemId}</td>
                        <td>${itemNamesMap[billItem.itemId]}</td> <%-- Map එකෙන් Item Name එක ගන්නවා --%>
                        <td>${billItem.quantity}</td>
                        <td><fmt:formatNumber value="${billItem.unitPriceAtSale}" type="currency" currencySymbol="Rs. "/></td> <%-- මේක නිවැරදි කළා --%>
                        <td><fmt:formatNumber value="${billItem.subTotal}" type="currency" currencySymbol="Rs. "/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty billItems}">
                    <tr>
                        <td colspan="5">No items found for this bill.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
            <p>
                <a href="bill?action=listBills" class="btn btn-secondary">Back to Bill List</a>
                <a href="bill?action=printBill&billId=${bill.billId}" class="btn btn-info">Print Bill</a>
            </p>
        </c:if>
        <c:if test="${bill == null}">
            <div class="message error">Bill not found.</div>
            <p><a href="bill?action=listBills" class="btn btn-secondary">Back to Bill List</a></p>
        </c:if>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2025</p>
</footer>
</body>
</html>
