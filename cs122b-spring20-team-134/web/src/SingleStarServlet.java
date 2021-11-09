import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
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

import dataContainer.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/single-star")
public class SingleStarServlet extends HttpServlet {
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

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
             String query = "SELECT s.id AS starId, s.name as starName" +
                     ",m.id as movieId, m.title as title, m.year as year, s.birthYear as dob" +
                     " from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.id = ? order by m.year asc, m.title asc";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

//            JsonArray jsonArray = new JsonArray();
            rs.next();
            String dob = rs.getString("dob");
            Star s;
            if (rs.wasNull()) {
                s = new Star(rs.getString("starId"), rs.getString("starName"), " ");
            }
            else{
                s = new Star(rs.getString("starId"), rs.getString("starName"), dob);
            }

            Movie m = new Movie(rs.getString("movieId"), rs.getString("title"), rs.getInt("year"));
            s.insert_movie(m);

            // Iterate through each row of rs
            while (rs.next()) {
                s.insert_movie(new Movie(rs.getString("movieId"), rs.getString("title"), rs.getInt("year")));
            }

            // write JSON string to output

            Collection collection = new ArrayList<>();
            collection.add(s);
            collection.add(sesOpt);

            Gson gson = new Gson();
            String json = gson.toJson(collection);
            out.write(json);
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
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

}