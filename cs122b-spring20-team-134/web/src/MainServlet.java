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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

import dataContainer.*;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(name = "MainServlet", urlPatterns = "/main")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;
//    private Connection connection;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // set response mime type
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
//            if(connection == null || connection.isClosed()) {
//                Connection dbcon = dataSource.getConnection();
//                connection = dbcon;
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();
            ArrayList<Pair> allGenres = getAllGenres(dbcon);

            Gson gson = new Gson();
            String json = gson.toJson(allGenres);
            out.write(json);
            response.setStatus(200);

            dbcon.close();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();

    }

    public ArrayList<Pair> getAllGenres(Connection connection) throws SQLException{
        String query = "SELECT name,id FROM genres ORDER BY name asc;";
        ResultSet rs = connection.createStatement().executeQuery(query);
        ArrayList<Pair> allGenres = new ArrayList<Pair>();

        while(rs.next()){
            allGenres.add(new Pair(rs.getString("name"), rs.getString("id")));
        }

        return allGenres;
    }

}