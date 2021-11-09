package parser;

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

public class ActorSAXParser extends DefaultHandler {

    List<Star> myStars;

    private String tempVal;
    private String tempDir;

    //to maintain context
    private Star tempStar;
    private boolean tempFlag = true;
    private int incrementStarId;
//    private ArrayList<String> tempGenre;

    // to maintain utils
    private MyUtils3 myUtils;

//    private PrintWriter writer = new PrintWriter("log/success_entry/starlog.txt", "UTF-8");
    private PrintWriter inconsWriter = new PrintWriter("log/inconsistency/inconsStarLog.txt", "UTF-8");
 //   private PrintWriter weirdStarWriter = new PrintWriter("log/weirdStarLog.txt", "UTF-8");

    // data insertion txt file
    private PrintWriter insertStarWriter = new PrintWriter("log/insert_file/insert_star.txt", "UTF-8");



    public ActorSAXParser() throws FileNotFoundException, UnsupportedEncodingException {
        myStars = new ArrayList<Star>();
    }

    public ActorSAXParser(MyUtils3 utils) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        myStars = new ArrayList<Star>();
        myUtils = utils;
        incrementStarId = myUtils.getMaxId("stars");
    }

    public void runExample() {
        parseDocument();
//        writer.close();
        inconsWriter.close();

        insertStarWriter.close();
 //       weirdStarWriter.close();
//        logData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("log/stanford-movies/actors63.xml", this);

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



    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if ("actor".equals(qName)) {
            tempFlag = true;
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
        tempVal = tempVal.trim();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            if (tempFlag){
//                myStars.add(tempStar);
                try {
                    if(myUtils.checkStarExist(tempStar)){
                        inconsWriter.println("Repeat star entry " + tempVal + ", Details: " + tempStar.toString());
                        inconsWriter.flush();
//                        tempFlag = false;
                    } else {
                        incrementStarId++;
                        tempStar.setId("nm" + incrementStarId);
                        myUtils.insertStar(tempStar, insertStarWriter);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//                writer.println(tempStar.toString());
//                writer.flush();
//                System.out.println(tempMovie.toString());
            }


//        } else if (qName.equalsIgnoreCase("familyname")) {
//            tempStar.setId(tempVal);
        } else if (qName.equalsIgnoreCase("stagename")) {

            if(tempVal.equals("")){
                inconsWriter.println("Empty stage name " + tempVal + ", Details: " + tempStar.toString());
                inconsWriter.flush();
                tempFlag = false;
            }
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            if (!tempVal.equals("")) {
                try {
                    tempStar.setDob(Integer.parseInt(tempVal));
                } catch (NumberFormatException e) {
//                    inconsWriter.println("Wrong birthYear Value " + tempVal + ", Details: " + tempStar.toString());
//                    inconsWriter.flush();
//                    tempFlag = false;
                    tempStar.setDob(-1);
                }
            }


        }

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        ActorSAXParser spe = new ActorSAXParser();
        spe.runExample();
    }
}
