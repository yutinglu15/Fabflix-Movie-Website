package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String fid;
    private String title;
    private int year;
    private String director;
    private float rating;

    private ArrayList<String> genres;
    private ArrayList<String> stars;

    public Movie(String id, String title, int year, String director, float rating){
        this.id = id;
        this.fid = "";
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;

        this.genres = new ArrayList<String>();
        this.stars = new ArrayList<String>();
    }

    public Movie(){
        this.id = "";
        this.fid = "";
        this.title = "";
        this.year = -1;
        this.director = "";
        this.rating = (float) -1.0;

        this.genres = new ArrayList<String>();
        this.stars = new ArrayList<String>();
    }

    public Movie(String id, String title, int year){
        this.id = id;
        this.title = title;
        this.year = year;

        this.genres = new ArrayList<String>();
        this.stars = new ArrayList<String>();
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public int getYear(){
        return year;
    }

    public String getDirector(){
        return director;
    }

    public float getRating() { return rating; }

    public ArrayList<String> getGenres(){
        return genres;
    }

    public ArrayList<String> getStars(){
        return stars;
    }

    public void insertGenre(String newGenre){
        genres.add(newGenre);
    }

    public void insertStar(String newStar){
        stars.add(newStar);
    }

    public void setGenres(ArrayList<String> newGenres) {this.genres = newGenres; }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setStars(ArrayList<String> stars) {
        this.stars = stars;
    }

    public void setYear(int year) {
        this.year = year;
    }
}