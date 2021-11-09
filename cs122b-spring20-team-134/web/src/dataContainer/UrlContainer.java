package dataContainer;

public class UrlContainer {
    private String browseOpt;
    private String genre;
    private String title;
    private String year;
    private String director;
    private String starName;
    //
    private Integer page;
    private Integer limit;
    //
    private String sortOption1;
    private String sortOption2;

    private String full_query;


    public UrlContainer(){
        this.browseOpt="null";
        this.genre="null";
        //
        this.year="null";
        this.title="null";
        this.director="null";
        this.starName="null";
        //
        this.page=0;
        this.limit=20;
        //
        this.sortOption1 = "null";
        this.sortOption2 = "null";
        this.full_query = "null";
    }

    public UrlContainer(String genreOpt, String genre, Integer page, Integer limit, String sortOpt1, String sortOpt2){
        this.browseOpt = genreOpt;
        this.genre = genre;
        this.page = page;
        this.limit = limit;
        this.sortOption1 = sortOpt1;
        this.sortOption2 = sortOpt2;
    }
//
    public UrlContainer(String searchOpt, String title, String year, String director, String starName, Integer page, Integer limit, String sortOpt1, String sortOpt2){
        this.browseOpt = searchOpt;
        this.title = title;
        this.year = year;
        this.director = director;
        this.starName=starName;
        this.page = page;
        this.limit = limit;
        this.sortOption1 = sortOpt1;
        this.sortOption2 = sortOpt2;
    }
//
    public String getBrowseOpt() {
        return browseOpt;
    }

    public String getSortOpt1() {
        return sortOption1;
    }

    public String getSortOpt2() {
        return sortOption2;
    }

    public void setSortOpt1(String sortOpt1) {
        this.sortOption1 = sortOpt1;
    }

    public void setSortOpt2(String sortOpt2) {
        this.sortOption2 = sortOpt2;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setBrowseOpt(String genreOpt) {
        this.browseOpt = genreOpt;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
    //
    public void setTitle(String title) {
        this.title = title;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    public void setStarName(String starName) {
        this.starName = starName;
    }
    public String getTitle(String title){ return title;}
    public String getYear(String year){ return year;}
    public String getDirector(String director){ return director;}
    public String getStarName(String starName){ return starName;}
    //


    public String getFull_query() {
        return full_query;
    }

    public void setFull_query(String full_query) {
        this.full_query = full_query;
    }
}
