import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.ResultSetImpl;
import dataContainer.*;

// this annotation maps this Java Servlet Class to a URL
@WebServlet(name = "MovieListServlet", urlPatterns = "/aglr-movie-list")
public class MovielistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // counting time spend - TS

        String contextPath = getServletContext().getRealPath("/");
//        String contextPath = getServletContext().getRealPath("/WEB-INF");
        FileWriter file = new FileWriter(contextPath);
        long TJstartTime = 0;
        long TJendTime = 0;
        long TSstartTime = System.nanoTime();
        // set response mime type
        response.setContentType("application/json");
//        log(response.toString());


        // get url parameters

        String browse_by_genre = request.getParameter("browse_by_genre");
        String browse_by_title = request.getParameter("browse_by_title");
        String ch = request.getParameter("ch");

        String search_by_filter = request.getParameter("search_by_filter");
        String title= request.getParameter("title");
        String year = request.getParameter("year");
        String movieStar= request.getParameter("star");
        String director=request.getParameter("director");

        // get url parameters of full text search
        String full_search = request.getParameter("fullSearch");
        System.out.println("fetching full search: " + full_search);
        String full_search_query = request.getParameter("q");
        System.out.println("fetching search query: " + full_search_query);
        //....fe

        String genreName = request.getParameter("genre");
        String sortOption1 = request.getParameter("sortoption1");
        if (sortOption1 != null && sortOption1.equals("null")){sortOption1 = null;}

        String sortOption2 = request.getParameter("sortoption2");
        if (sortOption2 != null && sortOption2.equals("null")){sortOption2 = null;}

        String limit = request.getParameter("limit");
        System.out.println(limit);
        String page = request.getParameter("page");
        System.out.println(page);

        // create HTTP session and insert url paramters
        HttpSession session = request.getSession(true);

        UrlContainer sesOpt = (UrlContainer) session.getAttribute("sesOpt");
        if(sesOpt == null){
            sesOpt= new UrlContainer();
        }


        System.out.println(request);


        PrintWriter out = response.getWriter();

        try {

            // count time cost - TJ
            TJstartTime = System.nanoTime();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            formQuery qr = new formQuery();
            String query = "";


            // check url parameter and generate corresponding query
            // check if url browse by genre
            if (browse_by_genre != null && browse_by_genre.equals("True")){
                // handle session
                sesOpt.setBrowseOpt("genre");
                sesOpt.setGenre(genreName);

                query = qr.formByGenre(genreName);
            }
            else if(browse_by_title != null && browse_by_title.equals("True")){
                query = qr.formByTitle(ch);
            }
            // handle full text search
            else if(full_search!=null && full_search.equals("True")){
                sesOpt.setBrowseOpt("full");
                sesOpt.setFull_query(full_search_query);

                System.out.println("start full text search");
                query = qr.fullTextSearchSQL();
                System.out.println(query);

            }
            else if(search_by_filter != null && search_by_filter.equals("True")){

                StringBuilder buildMovieQuery = new StringBuilder("select movies.id, movies.title, movies.year, movies.director, ratings.rating from movies, stars_in_movies, stars, ratings where movies.id = ratings.movieId and stars_in_movies.movieId = movies.id and stars.id = stars_in_movies.starId");
                System.out.println("buildMoverQuery success");
                System.out.println("enter search equal True");
//
                sesOpt.setBrowseOpt("search");
                sesOpt.setTitle(title);
                sesOpt.setYear(year);
                sesOpt.setDirector(director);
                sesOpt.setStarName(movieStar);

                buildMovieQuery.append(" and movies.title like ? and movies.director like ? and movies.year like ? and stars.name like ?");
                buildMovieQuery.append(" group by movies.id, movies.title, movies.year, movies.director, ratings.rating ");
                query = buildMovieQuery.toString();
                System.out.println(query);
            }
//            else{
//
//                query = qr.getTopTen();
//            }


            //  check sort option
            // make sort option array
            ArrayList<String> sortOptions = new ArrayList<>();
            if(!(sortOption1 == null)){
                sesOpt.setSortOpt1(sortOption1);
                sortOptions.add(sortOption1);
//                query = qr.addSortOption1(query, sortOption1);

            }

            if(!(sortOption2 == null)){
                sesOpt.setSortOpt2(sortOption2);
                sortOptions.add(sortOption2);
            }

            query = qr.addSortOption(query, sortOptions);

            // handle limit and pages
            if(!(limit == null || page==null)){
                int limit_num = Integer.parseInt(limit);
                int page_num = Integer.parseInt(page);
                // handle http session here
                sesOpt.setPage(page_num);
                sesOpt.setLimit(limit_num);
//                session.setAttribute("sesPage", page_num);
//                session.setAttribute("sesLim", limit_num);

               query = qr.addLimAndOffset(query, limit_num, page_num);
            }
            else{
                query = query + " limit 20";
            }

            query = query + ";";

            System.out.println(query);



            // excute query - if search by filter, use preparedstatement else, use regular excute
            ResultSet rs = null;
            if((search_by_filter !=null && search_by_filter.equals("True"))){
//                Integer getYear = Integer.parseInt(year);
                PreparedStatement formSearchByFilter =  dbcon.prepareStatement(query);
                formSearchByFilter.setString(1,"%"+title+"%");
                formSearchByFilter.setString(2,"%"+director+"%");
                formSearchByFilter.setString(3,"%"+year+"%");
                if(movieStar != null && !movieStar.equals("null") && !movieStar.equals("")){
                    formSearchByFilter.setString(4,"%"+movieStar+"%");
                }
                else {
                    formSearchByFilter.setString(4,"%%");
                }
//                System.out.println(formSearchByFilter);
                rs = formSearchByFilter.executeQuery();
            }
            else if(full_search!=null && full_search.equals("True")){
                String match = qr.fullTextQuery(full_search_query);
                PreparedStatement formFullSearch = dbcon.prepareStatement(query);
                // formFullSearch.setString(1, match);

                // TODO: uncomment in aws
   //             formFullSearch.setString(2, "%"+full_search_query+"%");
 //               formFullSearch.setString(3, full_search_query);
                formFullSearch.setString(1, full_search_query);
		formFullSearch.setString(2, match);
                formFullSearch.setString(3, "%"+full_search_query+"%");
                rs = formFullSearch.executeQuery();
            }
            else{
                rs = statement.executeQuery(query);
            }

//            ResultSet rs = statement.executeQuery(query);

            ArrayList<Movie> movielist = qr.retrieveStarAndGenre(rs, dbcon);
            Gson gson = new Gson();

            session.setAttribute("sesOpt", sesOpt);
            System.out.println(gson.toJson(movielist).toString());
            out.write(gson.toJson(movielist).toString());
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();

            TJendTime = System.nanoTime();

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        out.close();

        long TSendTime = System.nanoTime();
        long TSelapsedTime = TSendTime - TSstartTime; // elapsed time in nano seconds. Note: print the values in nano seconds
        long TJelapsedTime = TJendTime - TJstartTime; // elapsed time in nano seconds. Note: print the values in nano seconds

        synchronized (file){
            JsonObject timeObject = new JsonObject();
            file.write("search servlet total execution time: " + TSelapsedTime + ",JDBC execution time: " + TJelapsedTime);
        }
        file.close();


    }


}
