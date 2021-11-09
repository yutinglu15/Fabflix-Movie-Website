package dataContainer;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataContainer.Movie;
import dataContainer.Star;

import javax.annotation.Resource;
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
import java.sql.ResultSet;
import java.sql.Statement;

public class formQuery{
//    private StringBuilder searchMovieQuery;
//    private StringBuilder searchStarQuery;
//    private StringBuilder searchGenreQuery;
    private String query;


    public formQuery(){
        this.query = "";
    }

//    public formQuery(String title, String director, String starName, int year){
//        StringBuilder searchMovieQuery = new StringBuilder("select m.id, m.title, m.year, m.director from movies as m where 1=1");
//        StringBuilder searchStarQuery = new StringBuilder("select m.title, m.year, m.director , s.name from movies as m , stars_in_movies as sim, stars as s where");
//        StringBuilder searchGenreQuery = new StringBuilder("select m.title, group_concat(g.name) from movies as m, genres_in_movies as gim, genres as g where m.id = gim.movieId and g.id = gim.genreId group by m.title;");
//        //4个 if statement 来完善query 1，2，3写movie query。然后先用resultSet拿到结果后，就加入明星searchStarQuery.
//
//        if(title != null){
//            //searchMovieQuery.append("  and  m.title like '%an%' or m.title like 'a_z' or m.title like 'abc%' or m.title like '%xyz';");
//            searchMovieQuery.append(" and m.title like '%");
//            searchMovieQuery.append(title);
//            searchMovieQuery.append("%'");
//        }
//
//        if(director != null){
//            searchMovieQuery.append(" and m.director like '%");
//            searchMovieQuery.append(director);
//            searchMovieQuery.append("%'");
//        }
//        if (year > 0){
//            searchMovieQuery.append(" and m.year = ");
//            searchMovieQuery.append(year);
//        }
//        if (starName != null){
//            searchStarQuery.append(" m.id = sim.movieId and s.id = sim.starId and s.name like '%");
//            searchStarQuery.append(starName);
//            searchStarQuery.append("%'");
//        }
//        else{
//            searchStarQuery.append(" m.id = sim.movieId and s.id = sim.starId");
//        }
//        searchMovieQuery.append(";");
//        searchStarQuery.append(";");
//    }
    public ArrayList<Movie> retrieveStarAndGenre(ResultSet rs, Connection dbcon) throws SQLException {
        ArrayList<Movie> movielist = new ArrayList<Movie>();

        while (rs.next()) {

            Movie m = new Movie(rs.getString("id"), rs.getString("title"), rs.getInt("year"), rs.getString("director"), rs.getFloat("rating"));

            Statement find_star_statement = dbcon.createStatement();
//                String find_star_query = "select stars.id, stars.name from movies, stars, stars_in_movies where movies.id = stars_in_movies.movieId and stars.id = st" +
//                        "ars_in_movies.starId and movies.id = '" + m.getId() +"' limit 3;";
            String find_star_query = " select starRank.starName, rank, starRank.starId, movies.id from movies, stars_in_movies as sim" +
                    ", (select stars.name as starName, stars.id as starId, count(*) as rank from (select stars.name, stars" +
                    ".id from stars, stars_in_movies where stars.id = stars_in_movies.starId and stars_in_movies.movieId ='" + m.getId() +
                    "') as stars, stars_in_movies where stars.id = stars_in_movies.starId group by stars.id) as" +
                    " starRank where movies.id = '" + m.getId() + "' and movies.Id = sim.movieId and starRank.starId = sim.starId" +
                    " order by rank desc limit 3;";

            ResultSet find_star_rs = find_star_statement.executeQuery(find_star_query);

            while (find_star_rs.next()) {
                m.insertStar(new Star(find_star_rs.getString("starId"), find_star_rs.getString("starName")));
            }
            find_star_rs.close();
            find_star_statement.close();

            Statement find_genre_statement = dbcon.createStatement();
            String find_genre_query = "select movies.title, genres.id, genres.name from movies, genres, genres_in_movies where movies.id = genr" +
                    "es_in_movies.movieId and genres.id = genres_in_movies.genreId and movies.id = '" + m.getId() + "'order by genres.name asc limit 3;";

            ResultSet find_genre_rs = find_genre_statement.executeQuery(find_genre_query);
            while (find_genre_rs.next()) {
                m.insertGenre(find_genre_rs.getString("name"));
            }
            find_genre_rs.close();
            find_genre_statement.close();

            movielist.add(m);



        }
        return movielist;
    }

    public String formByGenre(String genre){
        return "select * from movies, ratings, genres_in_movies, genres where genres.name='"+ genre +"' and genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = m" +
                "ovies.id";
    }

    public String formByTitle(String ch){
        String regexNum = "\\d";
        String regexAlpha = "[A-Z]";
        if (ch.matches(regexAlpha)){
            System.out.println("match "+ch);
            return "select * from movies, ratings, genres_in_movies, genres where movies.title like '"+ ch+"%' and genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = m" +
                    "ovies.id";
        }
        else if(ch.matches(regexNum)){
            return "select * from movies, ratings, genres_in_movies, genres where movies.title like '"+ ch +"%' and genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = m" +
                    "ovies.id";
        }
        return "select * from movies, ratings, genres_in_movies, genres where movies.title not regexp '^[0-9a-zA-Z]' and genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = m" +
                "ovies.id";
    }

    public String fullTextQuery(String q){
        String[] keys = q.trim().split(" ");
        String match = "+"+keys[0]+"*";
        for(int i=1; i < keys.length; i++){
            match += " +"+keys[i]+"*";
        }

        return match;
    }
    public String fullTextSearchSQL(){

        String result_query = "select movies.id as id, movies.title as title, movies.year as year, movies.director as director, ratings.rating as rating from movies, ratings where (ed(title, ?) <= 2 " +
                "or match(title) against(? in boolean mode)" +
                "or title like ?) and id = ratings.movieId group by id, title, year, director , rating";

//         TODO: notice to change to below query when in local machine. fuzzy search is only available in AWS machine
//        String result_query = "select movies.id as id, movies.title as title, movies.year as year, movies.director as director, ratings.rating as rating from movies, ratings, genres_in_movies, genres where match(movies.title) against(?" +
//                " in boolean mode) and genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = movies.id group by id, title, year, director , rating";

//        System.out.println(result_query);
        return result_query;
    }

    public String makeSortString(String sortOption){
        switch (sortOption) {
            case "sord":
                return "rating desc";
            case "sora":
                return "rating asc";
            case "sotd":
                return "title desc";
            case "sota":
                return "title asc";

        }
        return null;
    }

    public String getTopTen(){
        return "select * from movies, ratings, genres_in_movies, genres where genres.id = genres_in_movies.genreId and movies.id = ratings.movieId and genres_in_movies.movieId = m" +
                "ovies.id";
    }

    public String addSortOption(String query, ArrayList<String> sortOption){
            if (sortOption.size() == 0){
                return query += " order by rating desc";
            }
            else if(sortOption.size() == 1){
                return query += " order by " + makeSortString(sortOption.get(0));
            }
            else if (sortOption.size() == 2){
                return query += " order by " + makeSortString(sortOption.get(0)) + ", "+ makeSortString(sortOption.get(1));
            }
            return null;
    }


    public String addLimAndOffset(String query, int limit, int page){
        int offset = page * limit;
        query += " limit "+limit+" offset "+offset;
        return query;
    }


}
