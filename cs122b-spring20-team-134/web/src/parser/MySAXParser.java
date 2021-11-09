package parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dataContainer.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MySAXParser extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;
    private String tempDir;
    private int incrementMovieId;

    //to maintain context
    private Movie tempMovie;
    private boolean tempFlag = true;
//    private ArrayList<String> tempGenre;

    // to maintain utils
    private MyUtils3 myUtils;
//    private HashMap<String, String> movieIdMap = new HashMap<String, String>();

    // logging settings

//    private PrintWriter writer = new PrintWriter("log/success_entry/movielog.txt", "UTF-8");
    private PrintWriter inconsWriter = new PrintWriter("log/inconsistency/inconsistentLog.txt", "UTF-8");
    private PrintWriter weirdGenreWriter = new PrintWriter("log/weirdGenresLog.txt", "UTF-8");

    // write to txt to use dataload in the future
    private PrintWriter insertMovieWriter = new PrintWriter("log/insert_file/insert_movie.txt", "UTF-8");
    private PrintWriter insertGenreWriter = new PrintWriter("log/insert_file/insert_genre.txt", "UTF-8");
    private PrintWriter insertGimWriter = new PrintWriter("log/insert_file/insert_gim.txt", "UTF-8");
    private PrintWriter insertRatingWriter = new PrintWriter("log/insert_file/insert_rating.txt", "UTF-8");




    public MySAXParser() throws FileNotFoundException, UnsupportedEncodingException {
        myMovies = new ArrayList<Movie>();
    }

    public MySAXParser(MyUtils3 utils) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        myMovies = new ArrayList<Movie>();
        myUtils = utils;
        incrementMovieId = utils.getMaxId("movies");
    }

    public void runExample() {
        parseDocument();
 //       writer.close();
        inconsWriter.close();
        weirdGenreWriter.close();

        insertRatingWriter.close();
        insertGenreWriter.close();
        insertRatingWriter.close();
        insertGimWriter.close();
//        logData();
    }

//    public HashMap<String, String> getMovieIdMap() {
//        return movieIdMap;
//    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("log/stanford-movies/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void logData() {
        for (Movie m : myMovies){
 //           writer.println(tempMovie.toString());
            System.out.println();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        switch (qName){
            case "director":
                tempDir = "";
                break;
            case "film":
                tempFlag = true;
                tempMovie = new Movie();

                tempMovie.setDirector(tempDir);

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            if (tempFlag){
//                myMovies.add(tempMovie);
//                movieIdMap.put(tempMovie.getFid(), tempMovie.getId());

                try {
                    if(myUtils.checkMovieExist(tempMovie))
                    {
                        inconsWriter.println("Repeat Movie Entry " + ", Details: " + tempMovie.toString());
                        inconsWriter.flush();
                    } else {
                        incrementMovieId++;
//                        movieIdMap.put("tt0"+incrementMovieId, tempMovie.getFid());
                        tempMovie.setId("tt0"+ incrementMovieId);
                        myUtils.insertMovie(tempMovie, insertMovieWriter, insertGenreWriter, insertGimWriter, insertRatingWriter);
                    };
                } catch (SQLException e) {
                    e.printStackTrace();
                }
 //               writer.println(tempMovie.toString());
 //               writer.flush();
            }
//            inconsLogger.info(tempMovie.toString());

        } else if (qName.equalsIgnoreCase("fid")) {
            if(tempVal.equals("")){
                inconsWriter.println("Empty movie film id " + tempVal + ", Details: " + tempMovie.toString());
                inconsWriter.flush();
                tempFlag = false;
            }
            else{
                tempMovie.setFid(tempVal);
            }
        } else if (qName.equalsIgnoreCase("t")) {
            if(tempVal.equals("")) {
                inconsWriter.println("Empty movie title " + tempVal + ", Details: " + tempMovie.toString());
                inconsWriter.flush();
                tempFlag = false;
            }
            else{
                tempMovie.setTitle(tempVal);
            }
        } else if (qName.equalsIgnoreCase("year")) {

            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e){
                inconsWriter.println("Wrong Year Value "  + tempVal + ", Details: " + tempMovie.toString());
                inconsWriter.flush();
//                System.out.println("Wrong Year Value "  + tempVal + ", Details: " + tempMovie.toString());
                tempFlag = false;
            }


        } else if (qName.equalsIgnoreCase("cat")){
            String genre = myUtils.getGenreMap().get(tempVal.toLowerCase());
            if(tempVal.equals(""))
            {
                inconsWriter.println("Empty genres "  + tempVal + ", Details: " + tempMovie.toString());
                inconsWriter.flush();
                tempFlag = false;
            }else if(genre == null){
                weirdGenreWriter.println(tempVal.toLowerCase());
                weirdGenreWriter.flush();
            } else{
                tempMovie.insertGenre(genre);
            }
        } else if (qName.equalsIgnoreCase("dirname")){
            tempDir = tempVal;
        }

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        MySAXParser spe = new MySAXParser();
        spe.runExample();
    }
}
