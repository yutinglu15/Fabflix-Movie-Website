import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

//import com.sun.deploy.net.HttpRequest;
//import com.sun.deploy.net.HttpResponse;
import dataContainer.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/shoppingCart")
public class ShoppingCartServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        HttpSession session = request.getSession();
        UrlContainer sesOpt = (UrlContainer) session.getAttribute("sesOpt");
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // write JSON string to output

            Collection collection = new ArrayList<>();
            collection.add(cart);
            collection.add(sesOpt);

            Gson gson = new Gson();
            String json = gson.toJson(collection);
            out.write(json);
            // set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Random rn = new Random();

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String title = request.getParameter("title");
        String movieId = request.getParameter("id");
        String opt = request.getParameter("opt");

        HttpSession session = request.getSession();
        UrlContainer sesOpt = (UrlContainer) session.getAttribute("sesOpt");

        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if(cart == null){
            cart = new ShoppingCart();
        }

        switch (opt) {
            case "add":
                if (title != null && cart.getCart().get(movieId) == null) {
                    cart.addToCart(new ShoppingItem(movieId, title, 1, rn.nextInt(10) + 1));
                } else {
                    cart.increaseQuantity(movieId);
                }
                break;
            case "plus":
                cart.increaseQuantity(movieId);
                break;
            case "minus":
                cart.decreaseQuantity(movieId);
                break;
            case "del":
                cart.deleteFromCart(movieId);
                break;
        }

        Gson gson = new Gson();
        String json = gson.toJson(cart);
        System.out.println(json);
        session.setAttribute("cart", cart);
        System.out.println(cart);

    }


}