<%-- src/main/webapp/bill-print.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Session check (optional for print page, but good practice) --%>
<c:if test="${empty sessionScope.currentUser}">
    <c:redirect url="login.jsp?error=unauthorized"/>
</c:if>
<html>
<head>
    <title>Pahana Edu - Print Bill #${bill.billId}</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <%-- Cache Control Headers --%>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <style>
        /* General print styles */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; /* Modern font */
            margin: 0; /* Remove default margins */
            padding: 0;
            background-color: #fff; /* Ensure white background for print */
            color: #000; /* Ensure black text for print */
            font-size: 10pt; /* Smaller base font for print readability */
        }

        .bill-container {
            width: 210mm; /* A4 width for print, adjust if using receipt paper */
            min-height: 297mm; /* A4 height */
            margin: 0 auto; /* Center content */
            padding: 20mm; /* A4 margins */
            box-sizing: border-box; /* Include padding in width/height */
            display: block; /* Ensure it's a block element */
        }

        .company-header {
            text-align: center;
            margin-bottom: 20mm;
        }
        .company-header h1 {
            font-size: 24pt;
            margin: 0;
            color: #333; /* Darker color for headings */
        }
        .company-header p {
            font-size: 10pt;
            margin: 2pt 0;
        }
        .section-divider {
            border: none;
            border-top: 1px dashed #999;
            margin: 8mm 0;
        }

        .bill-details, .item-details {
            margin-bottom: 8mm;
            font-size: 10pt;
            line-height: 1.5;
        }
        .bill-details p, .item-details p {
            margin: 0;
            padding: 2pt 0;
        }
        .bill-details strong {
            display: inline-block;
            width: 120px; /* Align labels */
        }

        .item-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 15mm;
        }
        .item-table th, .item-table td {
            border-bottom: 1px solid #ddd; /* Subtle lines */
            padding: 5pt;
            text-align: left;
            font-size: 9pt;
        }
        .item-table th {
            font-weight: bold;
            background-color: #f0f0f0; /* Light background for header */
        }
        .item-table .qty { width: 10%; text-align: center; }
        .item-table .price { width: 20%; text-align: right; }
        .item-table .subtotal { width: 20%; text-align: right; }
        .item-table .item-name { width: 50%; } /* Allocate remaining width */

        .total-section {
            text-align: right;
            margin-top: 15mm;
            font-size: 14pt;
            font-weight: bold;
            padding-top: 5mm;
            border-top: 2px solid #333; /* Stronger line for total */
        }
        .total-section p {
            margin: 0;
        }

        .footer-text {
            text-align: center;
            margin-top: 20mm;
            font-size: 9pt;
            color: #555;
        }

        /* Hide elements not needed for print */
        @media print {
            body {
                margin: 0 !important;
                padding: 0 !important;
                -webkit-print-color-adjust: exact; /* For background colors on print */
                color-adjust: exact;
            }
            .bill-container {
                width: 100% !important;
                min-height: auto !important; /* Let content define height */
                border: none !important;
                box-shadow: none !important;
                padding: 10mm !important; /* Adjust padding for print */
            }
            /* Hide non-print elements */
            header, nav, footer, .btn, .main-content p:last-child, script, style {
                display: none !important;
            }
            /* Ensure text is black for print */
            * {
                color: #000 !important;
                background: none !important;
            }
            table, th, td {
                border-color: #ccc !important; /* Ensure borders are visible */
            }
            .section-divider, hr {
                border-top: 1px dashed #000 !important;
            }
            .total-section {
                border-top: 2px solid #000 !important;
            }
        }
    </style>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Automatically trigger print dialog when page loads
            window.print();
            // Optionally, redirect back after printing/closing print dialog
            window.onafterprint = function() {
                window.location.href = 'bill?action=viewBillDetails&billId=${bill.billId}'; // Redirect back to bill details page
            };
        });
    </script>
</head>
<body>
<c:if test="${bill != null}">
    <div class="bill-container">
        <div class="company-header">
            <h1>Pahana Edu Bookshop</h1>
            <p>123 Main Street, Colombo 00100</p>
            <p>Phone: +94 11 2345678 | Email: info@pahanaedu.lk</p>
            <hr class="section-divider">
        </div>

        <div class="bill-details">
            <p><strong>Bill ID:</strong> #${bill.billId}</p>
            <p><strong>Date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
            <p><strong>Customer:</strong> ${customerNamesMap[bill.customerAccountNumber]} (${bill.customerAccountNumber})</p>
            <p><strong>Cashier:</strong> ${userNamesMap[bill.userId]}</p>
            <hr class="section-divider">
        </div>

        <div class="item-details">
            <table class="item-table">
                <thead>
                <tr>
                    <th>Item</th>
                    <th class="qty">Qty</th>
                    <th class="price">Unit Price</th>
                    <th class="subtotal">Amount</th>
                </tr>
                </thead>
                <tbody>
                <c:set var="totalItemsValue" value="0"/>
                <c:forEach var="billItem" items="${billItems}">
                    <tr>
                        <td>${itemNamesMap[billItem.itemId]}</td>
                        <td class="qty">${billItem.quantity}</td>
                        <td class="price"><fmt:formatNumber value="${billItem.unitPriceAtSale}" pattern="0.00"/></td>
                        <td class="subtotal"><fmt:formatNumber value="${billItem.subTotal}" pattern="0.00"/></td>
                    </tr>
                    <c:set var="totalItemsValue" value="${totalItemsValue + billItem.subTotal}"/>
                </c:forEach>
                </tbody>
            </table>
            <hr class="section-divider">
        </div>

        <div class="total-section">
            <p><strong>Total: Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="0.00"/></strong></p>
        </div>

        <div class="footer-text">
            <p>Thank you for your purchase!</p>
            <p>Visit us again.</p>
        </div>
    </div>
</c:if>
<c:if test="${bill == null}">
    <p style="text-align: center; color: red;">Error: Bill details not found for printing.</p>
</c:if>
</body>
</html>