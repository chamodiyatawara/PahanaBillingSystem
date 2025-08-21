<%-- src/main/webapp/bill-list.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
  <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
  <title>Pahana Edu - Bill List</title>
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
        <li><a href="bill?action=dashboard" class="nav-link ">Dashboard</a></li>
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
    <h2>Bill List</h2>

    <div class="search-form-container" style="margin-bottom: 20px;">
      <form action="bill" method="get">
        <input type="hidden" name="action" value="listBills"> <%-- Servlet එකේ listBills action එකට යවනවා --%>
        <div class="form-group" style="display: flex; align-items: center; gap: 10px;">
          <input type="text" id="search" name="search" placeholder="Search by Customer Account or Bill ID" value="${searchTerm}" style="flex-grow: 1; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
          <button type="submit" class="btn btn-primary">Search</button>
          <a href="bill?action=listBills" class="btn btn-secondary">Clear</a> <%-- Search clear කරන්න --%>
        </div>
      </form>
    </div>

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
        <th>Bill ID</th>
        <th>Customer Account</th>
        <th>Bill Date</th>
        <th>Total Amount</th>
        <th>User ID</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="bill" items="${bills}">
        <tr>
          <td>${bill.billId}</td>
          <td>${bill.customerAccountNumber}</td>
          <td><fmt:formatDate value="${bill.billDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
          <td><fmt:formatNumber value="${bill.totalAmount}" type="currency" currencySymbol="Rs. "/></td>
          <td>${bill.userId}</td>
          <td>
            <a href="bill?action=viewBillDetails&billId=${bill.billId}" class="btn btn-info btn-sm">View Details</a>
              <%-- Delete Bill functionality (optional, needs confirmation) --%>
              <%-- <a href="bill?action=deleteBill&billId=${bill.billId}" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete Bill ID ${bill.billId}?');">Delete</a> --%>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty bills}">
        <tr>
          <td colspan="6">No bills found.</td>
        </tr>
      </c:if>
      </tbody>
    </table>
  </div>
</div>

<footer>
  <p>Pahana Edu Billing System &copy; 2025</p>
</footer>
</body>
</html>
