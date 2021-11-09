import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

import org.jasypt.util.password.StrongPasswordEncryptor;

import dataContainer.*;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
//    @Resource(name="jdbc/moviedb")
//    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        JsonObject responseJsonObject = new JsonObject();

        //  TODO: uncomment while submit

        String recaptchaFlah = request.getParameter("recaptchaFlah");
        System.out.println("gRecaptchaFlag=" + recaptchaFlah);
        if(recaptchaFlah!=null && recaptchaFlah.equals("yes")){
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "u r robot");
                response.getWriter().write(responseJsonObject.toString());
                return;
            }
        }


        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
//            String emailQuery = "select c.id, c.email, c.password from customers as c;";

            String username = request.getParameter("username");
            String password = request.getParameter("password");
//            String emailQuery = "select id, email, password from customers where email = '"+ username + "';";
//            System.out.println(emailQuery);
//            ResultSet rs = statement.executeQuery(emailQuery);

            PreparedStatement formEmailQuery = null;
            String emailQuery = "select id, email, password from customers where email = ?;";
            formEmailQuery = dbcon.prepareStatement(emailQuery);
            formEmailQuery.setString(1,username);
            ResultSet rs = formEmailQuery.executeQuery();

            while(rs.next()){
                String loginEmail = rs.getString("email");
                String loginPassword = rs.getString("password");
                String customerId = rs.getString("id");
                boolean matchPassword = false;
                matchPassword = new StrongPasswordEncryptor().checkPassword(password,loginPassword);
                if(username.equals(loginEmail) && matchPassword){
                    System.out.println("user input: " + password + "system password: " + loginPassword);
                    request.getSession().setAttribute("user", new User(username));

                    session.setAttribute("customerId", customerId);

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message","success");
                    break;
                }
                else if (username.equals(loginEmail) && !matchPassword){
                    System.out.println("user input: " + password + "system password: " + loginPassword);
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            if (responseJsonObject.size() ==  0){
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            }


        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
