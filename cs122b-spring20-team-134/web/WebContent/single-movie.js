/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let sesInfo = null;
let movieTitle= null;
function handleResult(resultData) {

    console.log("handle results. populating movie from result data");
    // console.log(resultData);

    // var obj = JSON.parse(resultData);
    var raw_obj = resultData;
    var obj = raw_obj[0];
    sesInfo = raw_obj[1];
    console.log("Session info", sesInfo);
    let movieInfoElement = jQuery('#movie_info');
    movieTitle = obj.title;

    movieInfoElement.append("<p>Title: <strong>" + obj.title + "</strong></p>"
        + "<p>Year: <b>" + obj.year + "</b></p>" +
        "<p>Director: <b>" + obj.director + "</b></p>" +
        "<p>Rating: <b>" + obj.rating + "</b></p>");

    let movieTableBodyElement = jQuery('#movie_table_body');
    let rowHTML = "<p> Genres: ";

    for (let i = 0; i < obj.genres.length; i++){
        rowHTML += '<a href="aglr-movie-list.html?browse_by_genre=True&genre=' + obj.genres[i] + '">'
            + obj.genres[i] + '</a>' + "&emsp;";
    }
    rowHTML += "</p>";
    movieTableBodyElement.append(rowHTML);

    let rowsHTML = "<p>Stars: ";
    for (let i = 0; i < obj.stars.length; i++){
        rowsHTML += '<a href="single-star.html?id=' + obj.stars[i].id + '">'
            + obj.stars[i].name + '</a>' + "&emsp;";
    }
    rowsHTML += "</p>";
    movieTableBodyElement.append(rowsHTML);
}

let movieId = getParameterByName('id');

document.getElementById("backBtn").onclick = function(){
    let newUrl = "aglr-movie-list.html?";
    if( sesInfo.browseOpt === "genre"){
        newUrl += "browse_by_genre=True";
        newUrl += "&genre="+sesInfo.genre+"&page="+sesInfo.page+
            "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
            "&limit="+sesInfo.limit;
        console.log(newUrl);
    }
    else if( sesInfo.browseOpt === "search"){
        newUrl += "search_by_filter=True";
        newUrl += "&title="+sesInfo.title+"&year="+sesInfo.year+"&director="+sesInfo.director+"&starName="+sesInfo.starName+
            "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
            "&page="+sesInfo.page+"&limit="+sesInfo.limit;
    }
    else if(sesInfo.browseOpt == "full"){
        newUrl += "fullSearch=True";
        newUrl += "&q="+sesInfo.full_query + sesInfo.starName+
            "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
            "&page="+sesInfo.page+"&limit="+sesInfo.limit;
    }
    location.href = newUrl;
};

document.getElementById("smAddCartBtn").onclick = function(){
    $.ajax({
        type: "POST",
        url: "shoppingCart",
        data: {title:movieTitle, id:movieId, opt:"add"},
        success: alert("Successfully Add to Cart!")
    });
};

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});