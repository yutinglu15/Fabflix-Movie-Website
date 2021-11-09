package parser;

import dataContainer.Movie;
import dataContainer.Star;

import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;


public class MyUtils3 {

//    @Resource(name="jdbc/moviedbtest")
//    private DataSource dataSource;

    private Connection dbcon;
    Statement stmt;
    PreparedStatement pstmt;
    ResultSet rs;

    int maxGenreId;

    private HashMap<String, String> genreMap = new HashMap<String, String>();
    private HashMap<String, String> movieIdMap = new HashMap<String, String>();
    private HashMap<String, String> starIdMap = new HashMap<String, String>();

    private HashMap<String, Integer> exsitGenreMap = new HashMap<String, Integer>();
    private HashMap<String, String> exsitMovieMap = new HashMap<String, String>();
    private HashMap<String, String> exsitStarMap = new HashMap<String, String>();


    public MyUtils3() throws SQLException {
        dbcon =  DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false",
                "mytestuser", "mypassword");
        stmt = dbcon.createStatement();
        generateGenreMap();
        getExistGenres();
        getExistMovie();
        getExistStar();

    }


    public void insertMovie(Movie m, PrintWriter movieWriter, PrintWriter genreWriter, PrintWriter gimWriter, PrintWriter ratingWriter) throws SQLException {
//        Statement stmt = dbcon.createStatement();
        movieIdMap.put(m.getFid(), m.getId());
        exsitMovieMap.put(m.getTitle()+m.getYear()+m.getDirector(), m.getId());

        movieWriter.print("\"" + m.getId() + "\",\"" + m.getTitle() + "\"," + m.getYear() + ",\"" + m.getDirector()+"\"" + "\r\n");
//        String insertMovieQuery = "insert into movies(id, title, year, director) values(?, ?, ?, ?);";
//        pstmt = dbcon.prepareStatement(insertMovieQuery);
//
//        pstmt.setString(1, m.getId());
//        pstmt.setString(2, m.getTitle());
//        pstmt.setInt(3, m.getYear());
//        pstmt.setString(4, m.getDirector());
//
//        pstmt.executeUpdate();
        ratingWriter.print("\""+ m.getId() + "\"," + 0 + "," + 0+"\r\n");

//        String insertRatingQuery = "insert into ratings(movieId, rating, numVotes) values(?, ?, ?);";
//        pstmt = dbcon.prepareStatement(insertRatingQuery);
//
//        pstmt.setString(1, m.getId());
//        pstmt.setFloat(2, 0);
//        pstmt.setInt(3, 0);
//
//        pstmt.executeUpdate();


        for(String g : m.getGenres()){
            if(exsitGenreMap.get(g) == null){
                insertGenre(g, genreWriter);
            }
            insertGenreInMovie(exsitGenreMap.get(g), m.getId(), gimWriter);
        }
    }

    public int getMaxId(String choice) throws SQLException {
        int intMaxId = 1;
//        Statement stmt = dbcon.createStatement();
        ResultSet rs = stmt.executeQuery("select max(id) from " + choice + ";");
        while(rs.next()){
            String maxId = rs.getString(1);
            intMaxId = Integer.parseInt(maxId.substring(maxId.length() - 7));
            System.out.println(intMaxId);
        }
        return intMaxId;

    }

    public void getExistGenres() throws SQLException {
//        Statement stmt = dbcon.createStatement();
        ResultSet rs = stmt.executeQuery("select * from genres;");
        while(rs.next()){
            Integer genreId = rs.getInt(1);
            String genreName = rs.getString(2);
            exsitGenreMap.put(genreName,genreId);
        }
        maxGenreId = exsitGenreMap.size();
    }

    public void getExistMovie() throws SQLException {
//        Statement stmt = dbcon.createStatement();
        ResultSet rs = stmt.executeQuery("select * from movies;");
        while(rs.next()){
            String movieId = rs.getString(1);
            String title = rs.getString(2);
            int year = rs.getInt(3);
            String director = rs.getString(4);

            exsitMovieMap.put(title+year+director, movieId);
        }
    }

    public void getExistStar() throws SQLException {
//        Statement stmt = dbcon.createStatement();
        ResultSet rs = stmt.executeQuery("select * from stars;");
        while(rs.next()){
            String starId = rs.getString(1);
            String name = rs.getString(2);
            int dob = rs.getInt(3);
            starIdMap.put(name, starId);
            exsitStarMap.put(name+dob, starId);
        }
    }

    public void addExistGenre(String genre, Integer genreId){
        exsitGenreMap.put(genre, genreId);
    }

    public void insertGenre(String genre, PrintWriter genreWriter) throws SQLException {
//        String insertGenreQuery = "insert into genres(name) values(?);";
//
//        pstmt = dbcon.prepareStatement(insertGenreQuery);
//        pstmt.setString(1, genre);
//        pstmt.executeUpdate();
        genreWriter.print("\""+genre+"\""+"\r\n");

//        stmt.executeUpdate("insert into genres(name) value('"+ genre + "');");
        maxGenreId++;
        addExistGenre(genre, maxGenreId);
    }

    public void insertStar(Star s, PrintWriter starWriter) throws SQLException {

        starWriter.print("\"" + s.getId() + "\",\"" + s.getName() + "\","+s.getDob()+"\r\n");

//        if(s.getDob() == -1) {
//            String insertStarQuery = "insert into stars(id, name) values(?, ?);";
//            pstmt = dbcon.prepareStatement(insertStarQuery);
//
//            pstmt.setString(1, s.getId());
//            pstmt.setString(2, s.getName());
//
//            pstmt.executeUpdate();
////            stmt.executeUpdate("insert into stars(id, name) values('" + s.getId() + "', '"+s.getName()+"');");
//        } else {
//            String insertStarQuery = "insert into stars(id, name, birthYear) values(?, ?, ?);";
//            pstmt = dbcon.prepareStatement(insertStarQuery);
//
//            pstmt.setString(1, s.getId());
//            pstmt.setString(2, s.getName());
//            pstmt.setInt(3, s.getDob());
//
//            pstmt.executeUpdate();
////            stmt.executeUpdate("insert into stars(id, name, birthYear) values('"
////                    + s.getId() + "', '"+s.getName()+"', "+s.getDob()+");");
//
//        }
        starIdMap.put(s.getName(), s.getId());
//        exsitStarMap.put(s.getName(), s.getId());
    }

    public void insertGenreInMovie(Integer genreId, String movieId, PrintWriter gimWriter) throws SQLException {
//        String insertGenreQuery = "insert into genres_in_movies(genreId, movieId) values(?, ?);";
//        pstmt = dbcon.prepareStatement(insertGenreQuery);
//
//        pstmt.setInt(1, genreId);
//        pstmt.setString(2, movieId);
//
//        pstmt.executeUpdate();
        gimWriter.print("\""+genreId + "\",\"" + movieId+"\"\r\n");
//        stmt.executeUpdate("insert into genres_in_movies(genreId, movieId) values("+ genreId
//                + ", '"+ movieId + "');");
    }

    public boolean checkMovieExist(Movie m){
        String key = m.getTitle()+m.getYear()+m.getDirector();
        String movieId = exsitMovieMap.get(key);
        if(movieId==null){
            return false;
        } else {
            movieIdMap.put(m.getFid(), movieId);
            return true;
        }
    }

    public boolean checkStarExist(Star s){
        String key = s.getName();
        int dob = s.getDob();
        if(s.getDob() == -1){
            key += 0;
        } else {
            key += dob;
        }

        String starId = exsitStarMap.get(key);
        if(starId==null){
            return false;
        } else {
//            starIdMap.put(s.getName(), starId);
            return true;
        }
    }

    public void insertCast(String starId, String movieId, PrintWriter simWriter) throws SQLException {
        simWriter.print("\""+starId + "\",\"" + movieId+"\"\r\n");
        //        String insertCastQuery = "insert into stars_in_movies(starId, movieId) values(?, ?);";
//        pstmt = dbcon.prepareStatement(insertCastQuery);
//
//        pstmt.setString(1, starId);
//        pstmt.setString(2, movieId);
//
//        pstmt.executeUpdate();
    }

        public String getStarIdFromName(String starName){
        String starId = starIdMap.get(starName);
        if(starId == null){
            return "InvalidStarName";
        } else {
            return starId;
        }
    }

    public String getMovieIdFromFid(String fid){
        String movieId = movieIdMap.get(fid);
        if(movieId==null){
            return "InvalidMovieFid";
        }
        return movieId;
    }

    public void generateGenreMap(){
        genreMap.put("susp", "Thriller");
        genreMap.put("cnr", "Crime");
        genreMap.put("dram", "Drama");
        genreMap.put("drama", "Drama");
        genreMap.put("anti-dram", "Drama");
        genreMap.put("romt dram", "Drama");
        genreMap.put("west", "Western");
        genreMap.put("myst", "Mystery");
        genreMap.put("s.f.", "Sci-Fi");
        genreMap.put("advt", "Adventure");
        genreMap.put("horr", "Horror");
        genreMap.put("hor", "Horror");
        genreMap.put("romt", "Romance");
        genreMap.put("comd", "Comedy");
        genreMap.put("musc", "Musical");
        genreMap.put("docu", "Documentary");
        genreMap.put("porn", "Adult");
        genreMap.put("noir", "Black");
        genreMap.put("noir comd", "Black");
        genreMap.put("biop", "Biography");
        genreMap.put("tv", "Reality-TV");
        genreMap.put("tvs", "TV series");
        genreMap.put("tvm", "TV miniseries");
        genreMap.put("actn", "Action");
        genreMap.put("act", "Action");
        genreMap.put("fant", "Fantasy");
        genreMap.put("scfi", "Sci-Fi");
        genreMap.put("cart", "Animation");
        genreMap.put("hist", "History");
        genreMap.put("epic", "Epic");
        genreMap.put("crim", "Crime");
        genreMap.put("cnrb", "Crime");
        genreMap.put("cnrbb", "Crime");
        genreMap.put("homo", "Homo");
        genreMap.put("biob", "Biography");
        genreMap.put("biopp", "Biography");
        genreMap.put("biog", "Biography");
        genreMap.put("bio", "Biography");
        genreMap.put("ducu", "Documentary");
        genreMap.put("romt advt", "Romantic Adventure");
        genreMap.put("ctxx", "Uncategorized");
        genreMap.put("surr", "Surreal");
        genreMap.put("surl", "Surreal");
        genreMap.put("disa", "Disaster");
        genreMap.put("dist", "Disaster");
        genreMap.put("scat", "Scat");
        genreMap.put("romt comd", "Romantic Comedy");
        genreMap.put("romt. comd", "Romantic Comedy");
        genreMap.put("tvmini", "TV miniseries");
        genreMap.put("scif", "Sci-Fi");
        genreMap.put("muscl", "Musical");
        genreMap.put("stage musical", "Musical");
        genreMap.put("surreal", "Surreal");
        genreMap.put("cond", "Comedy");
        genreMap.put("west1", "Western");
        genreMap.put("adctx", "Action");
        genreMap.put("txx", "Uncategorized");
        genreMap.put("camp", "Camp");
        genreMap.put("romtadvt", "Romantic Adventure");
        genreMap.put("psych dram", "Drama");
        genreMap.put("faml", "Family");
        genreMap.put("ctxxx", "Uncategorized");
        genreMap.put("ctcxx", "Uncategorized");
        genreMap.put("road", "Road");
        genreMap.put("sports", "Sport");

    }


    public HashMap<String, String> getGenreMap() {
        return genreMap;
    }

    public HashMap<String, String> getMovieIdMap() {
        return movieIdMap;
    }

    public HashMap<String, String> getStarIdMap() {
        return starIdMap;
    }

    public HashMap<String, String> getExsitStarMap() {
        return exsitStarMap;
    }


}
