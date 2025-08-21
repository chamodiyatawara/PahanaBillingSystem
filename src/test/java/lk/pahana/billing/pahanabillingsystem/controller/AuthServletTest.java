//package lk.pahana.billing.pahanabillingsystem.controller;
//
//import lk.pahana.billing.pahanabillingsystem.dao.UserDAO;
//import lk.pahana.billing.pahanabillingsystem.model.User;
//import org.junit.jupiter.api.BeforeEach; // Test එක run වෙන්න කලින්
//import org.junit.jupiter.api.Test;    // Test Method එකක්
//import org.mockito.InjectMocks;     // Mock objects inject කරන්න
//import org.mockito.Mock;         // Mock object එකක්
//import org.mockito.MockitoAnnotations; // Mockito initialize කරන්න
//
//import javax.servlet.RequestDispatcher; // RequestDispatcher mock කරන්න
//import javax.servlet.http.HttpServletRequest; // Request mock කරන්න
//import javax.servlet.http.HttpServletResponse; // Response mock කරන්න
//import javax.servlet.http.HttpSession;     // Session mock කරන්න
//
//import java.io.PrintWriter; // PrintWriter mock කරන්න
//import java.io.StringWriter; // StringWriter mock කරන්න
//
//import static org.junit.jupiter.api.Assertions.*; // Assertions (assertEquals, assertTrue)
//import static org.mockito.Mockito.*; // Mockito methods (when, verify)
//
//public class AuthServletTest {
//
//    @Mock // HttpServletRequest එක simulate කරන්න
//    HttpServletRequest request;
//    @Mock // HttpServletResponse එක simulate කරන්න
//    HttpServletResponse response;
//    @Mock // HttpSession එක simulate කරන්න
//    HttpSession session;
//    @Mock // RequestDispatcher එක simulate කරන්න
//    RequestDispatcher requestDispatcher;
//    @Mock // UserDAO එක simulate කරන්න
//    UserDAO userDAO;
//
//    @InjectMocks // AuthServlet එකට Mock objects inject කරනවා
//    AuthServlet authServlet;
//
//    // හැම Test එකක්ම run වෙන්න කලින් මේ method එක run වෙනවා
//    @BeforeEach
//    public void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this); // Mock objects initialize කරනවා
//        authServlet.init(); // AuthServlet එකේ init() method එක call කරනවා (userDAO එක set වෙන්න)
//        // userDAO mock එක AuthServlet එකට inject කරන්න
//        // MockitoAnnotations.openMocks(this) මගින් InjectMocks annotation එක භාවිතා කරමින් userDAO mock එක authServlet එකට ස්වයංක්‍රීයව inject කරයි.
//        // කෙසේ වෙතත්, init() method එක userDAO = new UserDAO() ලෙස නැවත assign කරන බැවින්,
//        // අපට userDAO mock එක නැවත සකස් කිරීමට අවශ්‍ය විය හැකිය.
//        // මෙය සාමාන්‍යයෙන් Mockito.when(authServlet.getUserDAO()).thenReturn(userDAO); වැනි දෙයකින් සිදු කරයි,
//        // නමුත් අපගේ AuthServlet හි getUserDAO() public නොවන බැවින්, අපි එය මෙලෙස සකස් කරමු.
//        // මෙය InjectMocks හි ස්වයංක්‍රීය ක්‍රියාකාරීත්වය මඟින් සිදු කළ යුතුය.
//        // යම් ගැටලුවක් ඇති වුවහොත්, AuthServlet හි init() method එකේ userDAO = new UserDAO() වෙනුවට
//        // userDAO = userDAO; (mock එක භාවිතා කිරීමට) ලෙස වෙනස් කළ හැකිය.
//        // නමුත් දැනට එය එලෙසම තබාගෙන ඉදිරියට යමු.
//    }
//
//    @Test // Valid Login Test Case
//    public void testHandleLogin_ValidUser() throws Exception {
//        // Test Data
//        String username = "testuser";
//        String password = "testpassword";
//        User validUser = new User(1, username, password, "cashier"); // Valid User object එකක්
//
//        // Mocking behavior: request.getParameter() call කළාම data return කරන්න
//        when(request.getParameter("username")).thenReturn(username);
//        when(request.getParameter("password")).thenReturn(password);
//
//        // Mocking behavior: userDAO.getUserByUsernameAndPassword() call කළාම validUser return කරන්න
//        when(userDAO.getUserByUsernameAndPassword(username, password)).thenReturn(validUser);
//
//        // Mocking behavior: request.getSession() call කළාම session object එක return කරන්න
//        when(request.getSession()).thenReturn(session);
//
//        // Actual method call
//        authServlet.doPost(request, response); // Login requests handle කරන්නේ doPost එකෙන්
//
//        // Verifications: Methods call වුණාද කියලා බලනවා
//        // 1. userDAO.getUserByUsernameAndPassword() call වුණාද?
//        verify(userDAO).getUserByUsernameAndPassword(username, password);
//        // 2. request.getSession() call වුණාද?
//        verify(request).getSession();
//        // 3. session.setAttribute("currentUser", validUser) call වුණාද?
//        verify(session).setAttribute("currentUser", validUser);
//        // 4. response.sendRedirect("bill?action=dashboard") call වුණාද?
//        verify(response).sendRedirect("bill?action=dashboard");
//
//        // Assertions: ප්‍රතිඵල අපේක්ෂිතද කියලා බලනවා
//        // මේ Test එක සාර්ථක නම්, කිසිම exception එකක් throw වෙන්නේ නැහැ.
//        // Mockito verify() calls මගින් අපේක්ෂිත හැසිරීම් සිදුවී ඇති බව තහවුරු කරයි.
//    }
//
//    @Test // Invalid Login Test Case
//    public void testHandleLogin_InvalidUser() throws Exception {
//        // Test Data
//        String username = "invaliduser";
//        String password = "wrongpassword";
//
//        // Mocking behavior: request.getParameter() call කළාම data return කරන්න
//        when(request.getParameter("username")).thenReturn(username);
//        when(request.getParameter("password")).thenReturn(password);
//
//        // Mocking behavior: userDAO.getUserByUsernameAndPassword() call කළාම null return කරන්න (invalid user)
//        when(userDAO.getUserByUsernameAndPassword(username, password)).thenReturn(null);
//
//        // Mocking behavior: request.getRequestDispatcher() call කළාම requestDispatcher object එක return කරන්න
//        when(request.getRequestDispatcher("login.jsp")).thenReturn(requestDispatcher);
//
//        // Actual method call
//        authServlet.doPost(request, response);
//
//        // Verifications: Methods call වුණාද කියලා බලනවා
//        // 1. userDAO.getUserByUsernameAndPassword() call වුණාද?
//        verify(userDAO).getUserByUsernameAndPassword(username, password);
//        // 2. request.setAttribute("errorMessage", "Invalid username or password!") call වුණාද?
//        verify(request).setAttribute("errorMessage", "Invalid username or password!");
//        // 3. request.getRequestDispatcher("login.jsp") call වුණාද?
//        verify(request).getRequestDispatcher("login.jsp");
//        // 4. requestDispatcher.forward(request, response) call වුණාද?
//        verify(requestDispatcher).forward(request, response);
//
//        // Assertions: ප්‍රතිඵල අපේක්ෂිතද කියලා බලනවා
//        // මේ Test එක සාර්ථක නම්, කිසිම exception එකක් throw වෙන්නේ නැහැ.
//        // Mockito verify() calls මගින් අපේක්ෂිත හැසිරීම් සිදුවී ඇති බව තහවුරු කරයි.
//    }
//}
