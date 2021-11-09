package hiddenServlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import dataContainer.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SuggestionServlet", urlPatterns = "/suggestion")
public class SuggestionServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String q = request.getParameter("query");

        HashMap<String, String> movieMap = new HashMap<>();

        // Output stream to STDOUT
//        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        try {
            // handle null
            if (q == null || q.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // Get a connection from dataSource
            int suggest_lim = 10;
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = ds.getConnection();
            Statement statement = dbcon.createStatement();

            String[] keys = q.trim().split(" ");
            String match = "+"+keys[0]+"*";
            for(int i=1; i < keys.length; i++){
                match += " +"+keys[i]+"*";
            }

            // AWS machine fuzzy search query
            String query = "select distinct movies.id, movies.title, year director  from movies where ed(movies.title, ?) <= 2 " +
                    "or match(movies.title) against(? in boolean mode)" +
                    "or movies.title like ? group by movies.id limit ?;";

            PreparedStatement pstmt = dbcon.prepareStatement(query);

            pstmt.setString(1, "%"+q+"%");
            pstmt.setString(2, match);
            pstmt.setString(3, "%"+q+"%");
            pstmt.setInt(4, suggest_lim);

            // TODO: local machine query
//            String query = "select id, title from movies where match(movies.title) against(? in boolean mode) limit ?;";
//            PreparedStatement pstmt = dbcon.prepareStatement(query);
//            pstmt.setString(1, match);
//            pstmt.setInt(2, suggest_lim);



            // Perform the query
            ResultSet rs = pstmt.executeQuery();

            // Iterate through each row of rs
            while (rs.next()) {

                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                movieMap.put(movie_id, movie_title);

//                s.insert_movie(new Movie(rs.getString("movieId"), rs.getString("title"), rs.getInt("year")));
            }

            for (String mid: movieMap.keySet()){
                String mtitle = movieMap.get(mid);
                jsonArray.add(generateJsonObject(mid, mtitle));
            }
            // write JSON string to output


//            Gson gson = new Gson();
//            String json = gson.toJson(collection);
//            out.write(json);
            response.getWriter().write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            System.out.println(e);
            response.sendError(500, e.getMessage());

            // set reponse status to 500 (Internal Server Error)
//            response.setStatus(500);
        }

    }

    private static JsonObject generateJsonObject(String mID, String mName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", mName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", mID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

}