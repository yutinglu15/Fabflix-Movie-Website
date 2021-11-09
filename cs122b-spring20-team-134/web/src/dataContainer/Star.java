package dataContainer;

import java.util.ArrayList;
//import java.util.Date;

public class Star {
    private String id;
    private String name;
    private String birthYear;
    private int dob;

    private ArrayList<Movie> movies;

    public Star(String id, String name){
        this.id = id;
        this.name = name;

        this.movies = new ArrayList<Movie>();
//        this.birthYear = birthYear;
    }

    public Star(){
        this.id = "";
        this.name = "";
        this.birthYear = "";
        this.dob = -1;
        this.movies = new ArrayList<Movie>();
    }

    public Star(String id, String name, String birthYear){
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;

        this.movies = new ArrayList<Movie>();
    }


    public void insert_movie(Movie newMovie){
        movies.add(newMovie);
    }

    public int getDob() {
        return dob;
    }

    public void setDob(int dob) {
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public String getBirthYear(){
        return birthYear;
    }

    public ArrayList<Movie> getMovies(){
        return movies;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public String toString() {
//        StringBuilder sb = new StringBuilder();
//        for (String s : genres){
//            sb.append(s);
//            sb.append("\t");
//        }
        String sb = "";
        for (Movie s : movies){
            sb = sb + ", " + s;
        }

        return "Star: id=" + id + ", name=" + name + ", dob=" + dob + ", movies=" + sb;
    }

}
