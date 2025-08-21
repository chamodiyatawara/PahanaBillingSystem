<%-- src/main/webapp/bill-generator.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Session check --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Generate Bill</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <%-- Cache Control Headers --%>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <style>
        /* Specific styles for bill-generator */
        .bill-section {
            display: flex;
            gap: 20px;
            margin-top: 20px;
        }
        .bill-section > div {
            flex: 1;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 8px;
            background-color: #f9f9f9;
        }
        .bill-section h3 {
            margin-top: 0;
            color: #337ab7;
        }
        .item-selector select, .item-selector input[type="number"] {
            width: calc(100% - 22px);
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .item-selector button {
            width: 100%;
            padding: 10px;
        }
        .temp-items-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .temp-items-table th, .temp-items-table td {
            padding: 8px;
            border: 1px solid #eee;
            text-align: left;
        }
        .temp-items-table th {
            background-color: #e0e0e0;
        }
        .temp-items-table tr:nth-child(even) {
            background-color: #f5f5f5;
        }
        .temp-items-table .remove-btn {
            background-color: #f44336;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
        }
        .temp-items-table .remove-btn:hover {
            background-color: #d32f2f;
        }
        .bill-summary {
            text-align: right;
            margin-top: 20px;
        }
        .bill-summary h3 {
            font-size: 1.5em;
            color: #333;
        }
        .bill-summary button {
            padding: 12px 25px;
            font-size: 1.1em;
            margin-top: 10px;
        }

        /* Improved select visuals (customer & item selects) */
        .select-wrap { position: relative; display: block; width: 100%; }
        .styled-select {
            -webkit-appearance: none;
            appearance: none;
            width: 100%;
            padding: 10px 42px 10px 12px;
            border-radius: 8px;
            border: 1px solid #d6dde6;
            background-color: #ffffff;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24'%3E%3Cpath fill='%233a4954' d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 12px center;
            background-size: 12px;
            box-shadow: 0 6px 18px rgba(34,50,66,0.06);
            font-weight: 600;
            color: #2c3e50;
            transition: border-color .15s ease, box-shadow .15s ease, transform .08s ease;
            cursor: pointer;
        }
        .styled-select:focus {
            outline: none;
            border-color: #4aa3ff;
            box-shadow: 0 8px 28px rgba(52,152,219,0.12);
            transform: translateY(-1px);
        }
        .select-hint {
            display: block;
            font-size: 0.85rem;
            color: #63737f;
            margin-top: 6px;
            padding-left: 4px;
        }
        /* compact option info for item select (visual only) */
        .item-info {
            font-size: 0.9rem;
            color: #51606a;
            margin-top: 6px;
        }

        /* keep existing responsive behavior */
        @media (max-width: 768px) {
            .bill-section { flex-direction: column; }
            .bill-section > div { width:100%; }
        }
    </style>
    <script>
        // Global variables for current bill items and total
        let currentBillItems = [];
        let currentTotalAmount = 0;
        let itemNamesMap = {}; // To store item names for display

        document.addEventListener('DOMContentLoaded', function() {
            // Initialize currentBillItems from session data (if any)
            // This is for when the page reloads after an error or navigation.
            // We need to pass tempBillItemsJson from servlet to initialize this.
            // <c:if test="${not empty tempBillItemsJson}">
            //     currentBillItems = JSON.parse('<c:out value="${tempBillItemsJson}" escapeXml="false"/>');
            //     itemNamesMap = JSON.parse('<c:out value="${itemNamesMapJson}" escapeXml="false"/>'); // Initialize itemNamesMap
            //     updateBillDisplay(currentBillItems, itemNamesMap); // Update display based on reloaded items
            // </c:if>

            // Re-render the initial items from the JSP context (if page reloaded)
            // This is crucial for the "Finalize Bill" validation to work correctly.
            const initialTempItemsData = [];
            <c:forEach var="tempBillItem" items="${tempBillItems}">
            initialTempItemsData.push({
                billId: ${tempBillItem.billId},
                itemId: '${tempBillItem.itemId}',
                quantity: ${tempBillItem.quantity},
                unitPriceAtSale: ${tempBillItem.unitPriceAtSale},
                subTotal: ${tempBillItem.subTotal}
            });
            </c:forEach>
            currentBillItems = initialTempItemsData;

            const initialItemNamesMap = {};
            <c:forEach var="item" items="${items}">
            initialItemNamesMap['${item.itemId}'] = '${item.itemName}';
            </c:forEach>
            itemNamesMap = initialItemNamesMap;

            updateBillDisplay(currentBillItems, itemNamesMap); // Initial display update

            // Event listener for adding items
            const addItemBtn = document.getElementById('addItemBtn');
            if (addItemBtn) {
                addItemBtn.addEventListener('click', function() {
                    const itemId = document.getElementById('itemSelect').value;
                    const quantity = parseInt(document.getElementById('itemQuantity').value);

                    if (!itemId || itemId === "0") {
                        alert("Please select an item.");
                        return;
                    }
                    if (isNaN(quantity) || quantity <= 0) {
                        alert("Please enter a valid quantity.");
                        return;
                    }

                    // Send AJAX request to add item to session
                    fetch('bill?action=addTempItem&itemId=' + itemId + '&quantity=' + quantity, {
                        method: 'GET' // Using GET for simplicity, POST is better for data submission
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.status === 'success') {
                                alert(data.message); // Show success message
                                // Update UI dynamically instead of reloading
                                currentBillItems = data.tempBillItems; // Update global list
                                itemNamesMap = data.itemNamesMap; // Update global item names map
                                updateBillDisplay(currentBillItems, itemNamesMap); // Re-render table and total
                                // Clear item selection fields
                                document.getElementById('itemSelect').value = "0";
                                document.getElementById('itemQuantity').value = "1";
                            } else {
                                alert(data.message); // Show error message from servlet
                            }
                        })
                        .catch(error => {
                            console.error('Error adding item:', error);
                            alert('An error occurred while adding item.');
                        });
                });
            }

            // Event listener for removing items
            document.getElementById('tempItemsTableBody').addEventListener('click', function(event) {
                if (event.target.classList.contains('remove-btn')) {
                    const itemIdToRemove = event.target.dataset.itemId;
                    if (confirm('Are you sure you want to remove this item from the bill?')) {
                        fetch('bill?action=removeItem&itemId=' + itemIdToRemove, {
                            method: 'GET' // Using GET for simplicity
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.status === 'success') {
                                    alert(data.message);
                                    // Update UI dynamically instead of reloading
                                    currentBillItems = data.tempBillItems; // Update global list
                                    itemNamesMap = data.itemNamesMap; // Update global item names map
                                    updateBillDisplay(currentBillItems, itemNamesMap); // Re-render table and total
                                } else {
                                    alert(data.message);
                                }
                            })
                            .catch(error => {
                                console.error('Error removing item:', error);
                                alert('An error occurred while removing item.');
                            });
                    }
                }
            });

            // Finalize Bill button listener
            const finalizeBillBtn = document.getElementById('finalizeBillBtn');
            if (finalizeBillBtn) {
                finalizeBillBtn.addEventListener('click', function() {
                    const customerAccountNumber = document.getElementById('customerSelect').value;
                    if (!customerAccountNumber || customerAccountNumber === "0") {
                        alert("Please select a customer before finalizing the bill.");
                        return;
                    }
                    if (currentBillItems.length === 0) { // Now currentBillItems will be correctly updated
                        alert("Please add items to the bill before finalizing.");
                        return;
                    }

                    // Submit the form to finalize the bill
                    document.getElementById('finalizeBillForm').submit();
                });
            }

            // Function to update the bill summary and table
            function updateBillDisplay(items, namesMap) {
                const tableBody = document.getElementById('tempItemsTableBody');
                tableBody.innerHTML = ''; // Clear existing rows
                currentTotalAmount = 0;

                if (items.length === 0) {
                    tableBody.innerHTML = '<tr><td colspan="6">No items added to the bill yet.</td></tr>';
                } else {
                    items.forEach(item => {
                        const row = tableBody.insertRow();
                        row.insertCell().textContent = item.itemId;
                        row.insertCell().textContent = namesMap[item.itemId] || 'N/A'; // Get name from map
                        row.insertCell().textContent = item.quantity;
                        row.insertCell().textContent = parseFloat(item.unitPriceAtSale).toFixed(2);
                        row.insertCell().textContent = parseFloat(item.subTotal).toFixed(2);

                        const actionCell = row.insertCell();
                        actionCell.innerHTML = `<button class="remove-btn" data-item-id="${item.itemId}">Remove</button>`;

                        currentTotalAmount += item.subTotal;
                    });
                }
                document.getElementById('totalBillAmountDisplay').textContent = currentTotalAmount.toFixed(2);
            }
        });
    </script>
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
                <li><a href="bill?action=newBill" class="nav-link active">New Bill</a></li>
                <li><a href="bill?action=listBills" class="nav-link">Bills</a></li>
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
        <h2>Generate New Bill</h2>

        <%-- Display messages --%>
        <c:if test="${not empty requestScope.message}">
            <div class="message success">${requestScope.message}</div>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <div class="message error">${requestScope.error}</div>
        </c:if>

        <div class="bill-section">
            <!-- Customer Selection -->
            <div>
                <h3>1. Select Customer</h3>
                <form id="finalizeBillForm" action="bill" method="post">
                    <input type="hidden" name="action" value="finalizeBill">
                    <div class="form-group">
                        <label for="customerSelect">Customer Account:</label>
                        <div class="select-wrap">
                            <select id="customerSelect" name="customerAccountNumber" class="form-control styled-select" required>
                                <option value="0">-- Select Customer --</option>
                                <c:forEach var="customer" items="${customers}">
                                    <option value="${customer.accountNumber}">
                                            ${customer.name} (${customer.accountNumber})
                                    </option>
                                </c:forEach>
                            </select>
                            <span class="select-hint">Select by customer name or account number</span>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Item Selection and Add -->
            <div>
                <h3>2. Add Items to Bill</h3>
                <div class="form-group">
                    <label for="itemSelect">Select Item:</label>
                    <div class="select-wrap">
                        <select id="itemSelect" class="form-control styled-select" required>
                            <option value="0">-- Select Item --</option>
                            <c:forEach var="item" items="${items}">
                                <option value="${item.itemId}" data-unit-price="${item.unitPrice}" data-quantity-in-stock="${item.quantityInStock}">
                                        ${item.itemName} (Rs. <fmt:formatNumber value="${item.unitPrice}" pattern="0.00"/>) - Stock: ${item.quantityInStock}
                                </option>
                            </c:forEach>
                        </select>
                        <span class="select-hint">Price and stock shown in the list</span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="itemQuantity">Quantity:</label>
                    <input type="number" id="itemQuantity" value="1" min="1" required>
                </div>
                <button id="addItemBtn" class="btn btn-primary">Add Item to Bill</button>
            </div>
        </div>

        <!-- Temporary Bill Items List -->
        <div class="main-content" style="margin-top: 20px;">
            <h3>3. Items in Current Bill</h3>
            <table class="temp-items-table">
                <thead>
                <tr>
                    <th>Item ID</th>
                    <th>Item Name</th>
                    <th>Quantity</th>
                    <th>Unit Price</th>
                    <th>Sub Total</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody id="tempItemsTableBody">
                <c:set var="totalItemsValue" value="0"/>
                <c:forEach var="tempBillItem" items="${tempBillItems}">
                    <tr>
                        <td>${tempBillItem.itemId}</td>
                        <td>
                            <c:set var="currentItem" value="${null}"/>
                            <c:forEach var="item" items="${items}">
                                <c:if test="${item.itemId == tempBillItem.itemId}">
                                    <c:set var="currentItem" value="${item}"/>
                                </c:if>
                            </c:forEach>
                            <c:if test="${currentItem != null}">
                                ${currentItem.itemName}
                            </c:if>
                        </td>
                        <td>${tempBillItem.quantity}</td>
                        <td><fmt:formatNumber value="${tempBillItem.unitPriceAtSale}" pattern="0.00"/></td>
                        <td><fmt:formatNumber value="${tempBillItem.subTotal}" pattern="0.00"/></td>
                        <td>
                            <button class="remove-btn" data-item-id="${tempBillItem.itemId}">Remove</button>
                        </td>
                    </tr>
                    <c:set var="totalItemsValue" value="${totalItemsValue + tempBillItem.subTotal}"/>
                </c:forEach>
                <c:if test="${empty tempBillItems}">
                    <tr>
                        <td colspan="6">No items added to the bill yet.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <!-- Bill Summary and Finalize -->
        <div class="bill-summary">
            <h3>Total Bill Amount: Rs. <span id="totalBillAmountDisplay"><fmt:formatNumber value="${totalItemsValue}" pattern="0.00"/></span></h3>
            <button id="finalizeBillBtn" class="btn btn-success">Finalize Bill</button>
        </div>
    </div>
</div>

<footer>
    <p>Pahana Edu Billing System &copy; 2025</p>
</footer>
</body>
</html>