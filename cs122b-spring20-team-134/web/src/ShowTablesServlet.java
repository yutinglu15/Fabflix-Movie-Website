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
import java.lang.reflect.Type;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.sun.xml.internal.fastinfoset.util.StringArray;
import dataContainer.*;

@WebServlet(name = "ShowTablesServlet", urlPatterns = "/showTables")
public class ShowTablesServlet extends HttpServlet{
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
//    @Resource(name="jdbc/moviedb")
//    private DataSource dataSource;

    protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        JsonObject responseJsonObject = new JsonObject();
        String field = "";
        String type = "";
        try{
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/moviedb");

            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            String tableName = request.getParameter("tables");
            String getMAXIdQuery = "describe "+tableName+";";
            System.out.println("tableName: "+ tableName);
            ResultSet rs = statement.executeQuery(getMAXIdQuery);
            while(rs.next()){
                field = field  + " " +  rs.getString("Field");
                type =  type + " " + rs.getString("Type");
                System.out.println(field);
                System.out.println(type);
            }

        }catch(SQLException | NamingException e){
            e.printStackTrace();
        }
        responseJsonObject.addProperty("Field",field);
        responseJsonObject.addProperty("Type",type);
        response.getWriter().write(responseJsonObject.toString());
    }

}
