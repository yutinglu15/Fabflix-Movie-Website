import com.google.gson.Gson;
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
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dataContainer.*;

@WebServlet(name = "PaymentServlet", urlPatterns = "/payment")
public class PaymentServlet extends HttpServlet {
//    @Resource(name="jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");

        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        String customerId = (String) session.getAttribute("customerId");

        if(cart == null){
            cart = new ShoppingCart();
        }
        Integer total = 0;

        for (ShoppingItem item : cart.getCart().values()){
            total +=  item.getPrice() * item.getQuantity();
        }
        Gson gson = new Gson();
        String json = gson.toJson(total);
        out.write(json);
        // set response status to 200 (OK)
        response.setStatus(200);

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();
            // 用preparedstatement
//            Statement statement = dbcon.createStatement();

            String fname = request.getParameter("firstName");
            String lname = request.getParameter("lastName");
            String userInputCC = request.getParameter("cc-number");
            String userInputExp = request.getParameter("cc-expiration");

            // parse string to date
            Date day=new Date();

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf1.parse(userInputExp);
            java.sql.Date sqlExp = new java.sql.Date(date.getTime());


            response.setContentType("application/json");

            HttpSession session = request.getSession();
            PrintWriter out = response.getWriter();

            PreparedStatement formPaymentQuery = null;

//            String paymentQuery = "select * from creditcards as c where c.firstName='"+fname+"' and " +
//                    "c.lastName='"+lname+"' and c.id = '"+userInputCC+"' and c.expiration='"+sqlExp+"';";
            String paymentQuery = "select * from creditcards as c where c.firstName = ? and c.lastName= ? and c.id = ? and c.expiration= ?;";
            formPaymentQuery = dbcon.prepareStatement(paymentQuery);
            formPaymentQuery.setString(1,fname);
            formPaymentQuery.setString(2,lname);
            formPaymentQuery.setString(3,userInputCC);
            formPaymentQuery.setString(4,userInputExp);
            ResultSet rs = formPaymentQuery.executeQuery();
//            ResultSet rs = statement.executeQuery(paymentQuery);



            if (!rs.next()){
                responseJsonObject.addProperty("status", "failed");
                responseJsonObject.addProperty("message","invalid credit cart information");
            }
            else{

                ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
                String customerId = (String) session.getAttribute("customerId");
                if(cart == null){
                    cart = new ShoppingCart();
                }
                int total = 0;
                ArrayList<Integer> confirmId = new ArrayList<Integer>();
                ArrayList<String> confirmItem = new ArrayList<String>();
                ArrayList<Integer> confirmQuantity = new ArrayList<Integer>();

                for (ShoppingItem item : cart.getCart().values()){

                    Statement statement2 = dbcon.createStatement();
                    String d = sdf1.format(day);
                    String insertSales = "insert into sales(customerId, movieId, saleDate) values('"+customerId
                            +"', '"+item.getId()+"', '"+d + "');";
                    statement2.executeUpdate(insertSales);

                    Statement statement3 = dbcon.createStatement();
                    String getSaleId = " select id from sales where CustomerId = '"+ customerId +"' and movieId='"+
                            item.getId() + "' and saleDate='"+d+"';";

                    ResultSet rs2 = statement3.executeQuery(getSaleId);
                    while (rs2.next()){
                        int saleId = rs2.getInt("id");
                        confirmId.add(saleId);
                    }
                    confirmItem.add(item.getTitle());
                    confirmQuantity.add(item.getQuantity());


                    total = total + (item.getPrice() * item.getQuantity());
                }

                Gson gson = new Gson();

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message","success");
                responseJsonObject.addProperty("confirmId", gson.toJson(confirmId.get(confirmId.size() - 1)));
                responseJsonObject.addProperty("confirmItem", gson.toJson(confirmItem));
                responseJsonObject.addProperty("total", total);
                responseJsonObject.addProperty("quantity", gson.toJson(confirmQuantity));


            }

//            while(rs.next()){
//                String dbCC = rs.getString("id");
//                String dbExp = rs.getString("expiration");
//                if(dbCC.equals(userInputCC) && dbExp.equals(userInputExp)){
////                    request.getSession().setAttribute("user", new User(username));
//
//                    responseJsonObject.addProperty("status", "success");
//                    responseJsonObject.addProperty("message","success");
//                    break;
//                }
//            }
//            if (responseJsonObject.size() ==  0){
//                responseJsonObject.addProperty("status","fail");
//                responseJsonObject.addProperty("message", "plesae enter a valid credit card");
//            }


        } catch (SQLException | ParseException | NamingException e) {
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}