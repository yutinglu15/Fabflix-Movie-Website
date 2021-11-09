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
import javax.xml.transform.Result;
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
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/single-movie")
public class SingleMovieServlet extends HttpServlet {
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

            String query = "select title, year, director, rating from movies, ratings where movies.id = ? and ratings.movieId = movies.id;";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

//            JsonArray jsonArray = new JsonArray();
            rs.next();
            Movie m = new Movie(id, rs.getString("title"), rs.getInt("year"), rs.getString("director"), rs.getFloat("rating"));

            Statement find_star_statement = dbcon.createStatement();
            String find_star_query = " select starRank.starName, rank, starRank.starId, movies.id from movies, stars_in_movies as sim" +
                    ", (select stars.name as starName, stars.id as starId, count(*) as rank from (select stars.name, stars" +
                    ".id from stars, stars_in_movies where stars.id = stars_in_movies.starId and stars_in_movies.movieId ='" + id +
                    "') as stars, stars_in_movies where stars.id = stars_in_movies.starId group by stars.id) as" +
                    " starRank where movies.id = '" + id + "' and movies.Id = sim.movieId and starRank.starId = sim.starId" +
                    " order by rank desc;";
            ResultSet find_star_rs = find_star_statement.executeQuery(find_star_query);
            while(find_star_rs.next()){
                m.insertStar(new Star(find_star_rs.getString("starId"), find_star_rs.getString("starName")));
            }

            Statement find_genre_statement = dbcon.createStatement();
            String find_genre_query = "select genres.name from genres, genres_in_movies as gim where gim.movieId = '" + id +"' and genres.id = gim.genreId;";
            ResultSet find_genre_rs = find_genre_statement.executeQuery(find_genre_query);
            while(find_genre_rs.next()){
                m.insertGenre(find_genre_rs.getString("name"));
            }

            // write JSON string to output
            Collection collection = new ArrayList();
            collection.add(m);
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