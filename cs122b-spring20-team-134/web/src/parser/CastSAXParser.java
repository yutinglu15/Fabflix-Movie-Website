package parser;

import dataContainer.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dataContainer.*;

public class CastSAXParser extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;
    private String tempFid;

    //to maintain context
//    private Movie tempMovie;
//    private Star tempStar;
    private String tempMovie;
    private String tempStar;

    private boolean tempFlag = true;
    private boolean tempStarFlag = true;
//    private ArrayList<String> tempGenre;

    // to maintain utils
    private MyUtils3 myUtils;


//    private PrintWriter writer = new PrintWriter("log/success_entry/castslog.txt", "UTF-8");
    private PrintWriter inconsWriter = new PrintWriter("log/inconsistency/inconsCastsLog.txt", "UTF-8");
//    private PrintWriter weirdCastWriter = new PrintWriter("log/weirdCastsLog.txt", "UTF-8");

    // data insertion file
    private PrintWriter insertSimWriter = new PrintWriter("log/insert_file/insert_sim.txt", "UTF-8");



    public CastSAXParser() throws FileNotFoundException, UnsupportedEncodingException {
        myMovies = new ArrayList<Movie>();
    }

    public CastSAXParser(MyUtils3 utils) throws FileNotFoundException, UnsupportedEncodingException {
        myMovies = new ArrayList<Movie>();
        myUtils = utils;
    }

    public void runExample() {
        parseDocument();
//        writer.close();
        inconsWriter.close();

        insertSimWriter.close();
 //       weirdCastWriter.close();
//        logData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("log/stanford-movies/casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        switch (qName){
//            case "filmc":
//                tempFid = "";
//                tempFlag = true;
//                tempMovie = new Movie();
//                break;
            case "m":
                tempStarFlag = true;
                tempFlag = true;
                tempMovie = "";
                tempStar = "";
//                tempStar = new Star();
//                tempStarFlag = true;

        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

//        if (qName.equalsIgnoreCase("filmc")) {
            //add it to the list
//            if (tempFlag){
////                myMovies.add(tempMovie);
//                writer.println(tempMovie.toString());
//                writer.flush();
////                System.out.println(tempMovie.toString());
//            }
//            inconsLogger.info(tempMovie.toString());

//        } else
        if (qName.equalsIgnoreCase("f")) {
//            tempMovie.setFid(tempVal);
            tempMovie = tempVal;
        } else if (qName.equalsIgnoreCase("a")) {
            if(tempVal.toLowerCase().equals("s a")){
                inconsWriter.println("s a name " + tempVal + ", Details: " + tempMovie.toString());
                inconsWriter.flush();
                tempStarFlag = false;
            }
            tempStar = tempVal;

//            tempStar.setName(tempVal);
        }  else if (qName.equalsIgnoreCase("m")){
            if(tempStarFlag) {
//                tempMovie.insertStar(tempStar);
                try {
                    String movieId = myUtils.getMovieIdFromFid(tempMovie);
                    if(movieId.equals("InvalidMovieFid")){
                        inconsWriter.println("None Exsit Movie " + tempVal + ", Details: " + tempMovie.toString());
                        inconsWriter.flush();
                        tempFlag = false;
                    }

                    String starId = myUtils.getStarIdFromName(tempStar);
                    if(starId.equals("InvalidStarName")){
                        inconsWriter.println("None Exsit Star " + tempStar + ", Details: " + tempMovie.toString());
                        inconsWriter.flush();
                        tempFlag = false;

                    }
                    if(tempFlag){
 //                           writer.println(starId + " " + movieId);
                            myUtils.insertCast(starId, movieId, insertSimWriter);
//                            tempFlag = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        CastSAXParser spe = new CastSAXParser();
        spe.runExample();
    }
}
