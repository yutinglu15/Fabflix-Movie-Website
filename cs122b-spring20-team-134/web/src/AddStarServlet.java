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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dataContainer.*;


@WebServlet(name = "AddStarServlet", urlPatterns = "/addStar")
public class AddStarServlet extends HttpServlet{
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
//    @Resource(name="jdbc/moviedb")
//    private DataSource dataSource;

    protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        JsonObject responseJsonObject = new JsonObject();
        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb_write");

            Connection dbcon = ds.getConnection();
            Statement statement = dbcon.createStatement();
            Statement statement2 = dbcon.createStatement();
            Integer starDOB = 0;
            String insertQuery = "";
            String starName = request.getParameter("name");
            String tempStarDOB = request.getParameter("dob");
            if(tempStarDOB != null && tempStarDOB != "null"&&tempStarDOB!=""){
                starDOB = Integer.parseInt(tempStarDOB);
            }
            String getMAXIdQuery = "select max(id) from stars;";
            ResultSet rs = statement.executeQuery(getMAXIdQuery);
            if(rs.next()){
                String maxID = rs.getString(1);
                System.out.println(maxID);
//                System.out.println(rs.getString(1));
//                System.out.println(substring(rs.getString("max(id)"),2));
                String subid = maxID.substring(maxID.length() - 7);
                System.out.println(subid);
                int id = Integer.parseInt(subid);
                id++;
                String maxId = "nm" + id;
                System.out.println("starDOB:"+starDOB);
                String popUpMessage = "";
                if(starDOB.equals(0)){
                    insertQuery = "insert into stars(id, name) values('"+ maxId +"' , '"+ starName+"');";
                    popUpMessage = "new star name: " + starName + " DOB: NULL";
                }
                else{
                    insertQuery = "insert into stars(id, name, birthYear) values('"+ maxId +"' , '"+ starName+"', "+ starDOB + ");";
                    popUpMessage = "new star name: " + starName + " DOB: " + starDOB;
                }
//                JOptionPane.showMessageDialog(null, popUpMessage, "Success",1);
                statement2.executeUpdate(insertQuery);
                System.out.println("in addStar servlet");
                responseJsonObject.addProperty("id",maxId);
                responseJsonObject.addProperty("starName",starName);
                responseJsonObject.addProperty("starDOB",starDOB);
            }

        }catch(SQLException | NamingException e){
            e.printStackTrace();
        }
        response.getWriter().write(responseJsonObject.toString());
    }

}
