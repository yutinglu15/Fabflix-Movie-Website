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
function handleResult(resultData) {

    console.log("handle results. populating movie from result data");

    // var obj = JSON.parse(resultData);
    var raw_obj = resultData;
    var obj = raw_obj[0];
    sesInfo = raw_obj[1];

    console.log(obj.movies);
    console.log(sesInfo);
    let movieInfoElement = jQuery('#star_info');

    movieInfoElement.append("<p> Star name: <b>" + obj.name + "</b></p>"
        + "<p> Date of Birth: <b>" + obj.birthYear + "</b></p>");

    let starTableBodyElement = jQuery('#star_table_body');

    for (let i = 0; i < obj.movies.length; i++){
        let rowHTML = "";
        rowHTML += '<a href="single-movie.html?id=' + obj.movies[i].id + '">' +'(' + obj.movies[i].year + ')'
            + obj.movies[i].title + '</a>' + "&emsp;";
        starTableBodyElement.append(rowHTML);

    }
}

let starId = getParameterByName('id');

document.getElementById("backBtn").onclick = function(){
    let newUrl = "aglr-movie-list.html?";
    if( sesInfo.browseOpt === "genre"){
        newUrl += "browse_by_genre=True";
        newUrl += "&genre="+sesInfo.genre+"&page="+ sesInfo.page +
            "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
            "&limit="+sesInfo.limit;
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

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});