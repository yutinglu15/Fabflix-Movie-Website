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
import javax.swing.*;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dataContainer.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/addMovie")
public class AddMovieServlet extends HttpServlet{
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
//    @Resource(name="jdbc/moviedb")
//    private DataSource dataSource;

    protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
        JsonObject responseObject = new JsonObject();
        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb_write");

            Connection dbcon = ds.getConnection();
            Statement statement = dbcon.createStatement();
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star_name");
            String genre = request.getParameter("genre");

            //check if title in database
            String checkTitle = "select count(*) as flag from movies where title = '"+title+"' and year = '"+year+"' and director = '"+director+"';";
            ResultSet rs = statement.executeQuery(checkTitle);
            while(rs.next()){
                String flagchar = rs.getString("flag");
                if(flagchar.equals("1")){
                    String Message = "Movie exist, failed to add";
                    responseObject.addProperty("message", Message);
           //         JOptionPane.showMessageDialog(null,Message,"Fail",1);
                }
                else{
                    String Message2 = "Added title: " + title + "   year: " + year + "  director: " + director + " star: " + star + "  genre:  " + genre;
                    responseObject.addProperty("message", Message2);
                    //           JOptionPane.showMessageDialog(null,Message,"Success",1);
                    String addMovieQuery = "call add_movie('"+title+"','"+ year +"','"+director+"','"+star+"','"+genre+"');";
                    statement.executeQuery(addMovieQuery);
                }
            }
        }catch(SQLException | NamingException e){
            e.printStackTrace();
        }
        response.getWriter().write(responseObject.toString());
    }

}
