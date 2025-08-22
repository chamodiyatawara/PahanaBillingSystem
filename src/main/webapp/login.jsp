<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Pahana Edu - Login</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
    <style>

        :root{
            --primary:#337ab7;
            --bg:#f4f6f9;
            --card:#ffffff;
            --muted:#6c7a86;
        }
        html,body{height:100%;margin:0;font-family:Inter,Segoe UI,Roboto,"Helvetica Neue",Arial,sans-serif;background:var(--bg);color:#24313a;}
        .login-viewport {
            min-height:100vh;
            display:flex;
            align-items:center;
            justify-content:center;
            padding:32px;
        }

        .login-shell {
            width:100%;
            max-width:980px;
            background:linear-gradient(180deg, rgba(255,255,255,0.95), rgba(255,255,255,0.92));
            border-radius:12px;
            box-shadow:0 20px 50px rgba(33,47,60,0.12);
            overflow:hidden;
            display:flex;
            gap:0;
        }

        /* Left visual area */
        .login-visual {
            flex:1 1 420px;
            min-height:420px;
            background-image: url('images/login-side.jpg');
            background-size:cover;
            background-position: center;
            position:relative;
            display:flex;
            align-items:flex-end;
            padding:28px;
        }
        .login-visual::after{
            content:"";
            position:absolute;
            inset:0;
            background:linear-gradient(180deg, rgba(26,40,54,0.28), rgba(26,40,54,0.5));
            pointer-events:none;
        }
        .visual-caption{
            position:relative;
            color:#fff;
            z-index:2;
            max-width:84%;
            font-weight:600;
            font-size:1.05rem;
            line-height:1.25;
            text-shadow:0 6px 18px rgba(0,0,0,0.28);
        }
        .visual-caption small{display:block; font-weight:400; opacity:0.95; margin-top:6px; font-size:0.86rem; color:#e9f3ff;}

        /* Right form area */
        .login-panel {
            flex:0 0 420px;
            background:var(--card);
            padding:36px 36px;
            display:flex;
            flex-direction:column;
            justify-content:center;
            gap:12px;
        }
        .brand-row{
            display:flex;
            gap:12px;
            align-items:center;
            margin-bottom:6px;
        }
        .brand-logo{
            width:46px;height:46px;object-fit:contain;border-radius:8px;background:#f5f8fb;padding:6px;border:1px solid #eef5fb;
        }
        .brand-title{
            font-size:1.15rem;
            font-weight:700;
            color:#21323b;
            line-height:1;
        }
        .brand-sub{font-size:0.82rem;color:var(--muted);margin-top:2px;}

        .login-card {
            width:100%;
        }
        .login-card h2{
            margin:8px 0 6px 0;
            font-size:1.25rem;
            color:#17222a;
        }
        .login-card p.hint{
            margin:0 0 16px 0;color:var(--muted);font-size:0.9rem;
        }

        .form-group{margin-bottom:14px;}
        .form-control{
            width:100%;
            padding:12px 14px;
            border-radius:10px;
            border:1px solid #e1e7ec;
            background:#fbfdff;
            font-size:0.96rem;
            color:#20303a;
            box-shadow:inset 0 1px 0 rgba(255,255,255,0.6);
        }
        .form-control:focus{outline:none;border-color:var(--primary);box-shadow:0 8px 24px rgba(51,122,183,0.12);transform:translateY(-1px);}

        .btn-primary{
            display:inline-block;
            width:100%;
            padding:12px 14px;
            background:linear-gradient(180deg,var(--primary),#2a6ea3);
            color:#fff;
            border:none;
            border-radius:10px;
            font-weight:700;
            cursor:pointer;
            font-size:1rem;
            box-shadow:0 10px 26px rgba(51,122,183,0.16);
        }
        .btn-primary:hover{transform:translateY(-2px);}

        .message {
            padding:10px 12px;border-radius:8px;font-weight:600;
        }
        .error-message{background:#ffecec;color:#a52222;border:1px solid #f5c9c9;}
        .success-message{background:#eef9f0;color:#266a35;border:1px solid #cdeac9;}

        .foot-note{font-size:0.85rem;color:var(--muted);text-align:center;margin-top:10px;}

        /* Small screens: stack */
        @media (max-width:880px){
            .login-shell{flex-direction:column; border-radius:10px;}
            .login-visual{min-height:180px;order:1;}
            .login-panel{order:2;padding:24px;}
        }
    </style>
</head>
<body>
<div class="login-viewport">
    <div class="login-shell">
        <!-- Left: visual (image) -->
        <div class="login-visual" role="img" aria-label="Education illustration" >
            <div class="visual-caption">
                Pahana Edu Billing System

            </div>
        </div>

        <!-- Right: login form (logic unchanged) -->
        <div class="login-panel">
<%--            <div class="brand-row">--%>
<%--                <img src="images/logo.png" alt="Pahana Logo" class="brand-logo" onerror="this.style.display='none'"/>--%>
<%--                <div>--%>
<%--                    <div class="brand-title">Pahana Edu</div>--%>
<%--                    <div class="brand-sub">Billing System</div>--%>
<%--                </div>--%>
<%--            </div>--%>

            <div class="login-card">
                <h2>Pahana Edu - Login</h2>
                <p class="hint">Sign in to manage bills, customers and items.</p>

                <%-- Error message if login fails --%>
                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="message error-message"><%= request.getAttribute("errorMessage") %></div>
                <% } %>

                <%-- Success message if logged out --%>
                <% if (request.getParameter("message") != null && request.getParameter("message").equals("logged_out")) { %>
                <div class="message success-message">You have been logged out successfully!</div>
                <% } %>

                <form action="auth" method="post" style="margin-top:12px;">
                    <input type="hidden" name="action" value="login">
                    <div class="form-group">
                        <input class="form-control" type="text" name="username" placeholder="Username" required>
                    </div>
                    <div class="form-group">
                        <input class="form-control" type="password" name="password" placeholder="Password" required>
                    </div>
                    <button type="submit" class="btn-primary">Login</button>
                </form>

                <div class="foot-note">Forgot password? Contact your administrator.</div>
            </div>
        </div>
    </div>
</div>
</body>
</html>