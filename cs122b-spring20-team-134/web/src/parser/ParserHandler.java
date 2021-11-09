package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ParserHandler {
//    private MyUtils myUtils = new MyUtils();

    static void runAllParser() throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        MyUtils3 myUtils = new MyUtils3();

        // Timer to count the running time
        long starTime = System.currentTimeMillis();

        // parsing main file
        MySAXParser mspe = new MySAXParser(myUtils);
        mspe.runExample();
        // write movieId map to txt
        HashMap<String, String> movieIdMap = myUtils.getMovieIdMap();
        System.out.println("MovieId Map size: " + movieIdMap.size());
//        PrintWriter movieIdMapWriter = new PrintWriter("log/map/movieIdMap.txt", "UTF-8");
//        for(Map.Entry<String, String> entry: movieIdMap.entrySet()){
//            movieIdMapWriter.println(entry.getKey() + ": " + entry.getValue());
//        }
//        movieIdMapWriter.flush();
//        movieIdMapWriter.close();

        long endTime1 = System.currentTimeMillis();
        System.out.println("running time: " + (endTime1 - starTime)/1000 + "s");
        System.out.println("finish parsing Main File\n");

        // parsing actor file
        ActorSAXParser aspe = new ActorSAXParser(myUtils);
        aspe.runExample();
        HashMap<String, String> starIdMap = myUtils.getStarIdMap();
        System.out.println("starId Map size: " + starIdMap.size());
//        PrintWriter starIdMapWriter = new PrintWriter("log/map/starIdMap.txt", "UTF-8");
//        for(Map.Entry<String, String> entry: starIdMap.entrySet()){
//            starIdMapWriter.println(entry.getKey() + ": " + entry.getValue());
//        }
//        starIdMapWriter.flush();
//        starIdMapWriter.close();

        long endTime2 = System.currentTimeMillis();
        System.out.println("total running time: " + (endTime2 - starTime)/1000 + "s");
        System.out.println("task running time: " + (endTime2 - endTime1)/1000 + "s");
        System.out.println("finish parsing Actor File\n");

        // parsing cast file
        CastSAXParser cspe = new CastSAXParser(myUtils);
        cspe.runExample();
        long endTime3 = System.currentTimeMillis();
        System.out.println("total running time: " + (endTime3 - starTime)/1000 + "s");
        System.out.println("task running time: " + (endTime3 - endTime2)/1000 + "s");
        System.out.println("finish parsing Cast File\n");

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        System.out.println("main");

        File directory = new File("log/inconsistency/");
        if (!directory.exists()){
            directory.mkdirs();
        }
        File directory2 = new File("log/insert_file/");
        if (!directory2.exists()){
            directory2.mkdirs();
        }



        // Calling the mainCalller() method
        // so that main() methiod is called externally
        runAllParser();
    }

}
