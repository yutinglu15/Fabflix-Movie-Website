
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");
    console.log("sending AJAX request to backend Java Servlet");

    // check past query
    if (sessionStorage.getItem(query) !== null){
        console.log("Query Status: find cache query");
        console.log("query");
        var data = sessionStorage.getItem(query);
        handleLookupAjaxSuccess(data, query, doneCallback);
    }
    else{
        console.log("Query Status: retrieve from backend");
    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "suggestion?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error");
                console.log(errorData)
            }
        })
    }
}


function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful");

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData);

    // cache result
    sessionStorage.setItem(query, data);

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // jump
    window.location = "single-movie.html?id="+suggestion["data"]["movieId"];
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieId"])
}


$('#autocomplete').autocomplete({
    minChars: 3,
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300
    // min char

    // there are some other parameters that you might want to use to satisfy all the requirements

});



$(function () {
    $('#myList a:last').tab('show')
});


$('#myTab a').click(function (e) {
    e.preventDefault();
    $(this).tab('show');
});


function handleInitialResult(resultData){
    let alpha = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R'
        ,'S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9','*'];
    console.log("getting initial data");
    console.log(resultData);

    let genreElement = jQuery("#genre");
    jQuery.each(resultData, function (id, g_name) {
            genreElement.append('<a href="aglr-movie-list.html?browse_by_genre=True&genre='+g_name.name+'">'+g_name.name+' | </a>');
        });

    let alphaElement = jQuery("#char");
    jQuery.each(alpha, function () {
        alphaElement.append('<a href="aglr-movie-list.html?browse_by_title=True&ch='+this+'">'+this+' | </a>');
    });

    // let numElement = jQuery("#num");
    // jQuery.each(nums, function () {
    //     alphaElement.append('<a href="aglr-movie-list.html?browse_by_title=True&ch='+this+'">'+this+' | </a>');
    // });



}


jQuery.ajax({
    dataType:"json",
    mehtod:"GET",
    url:"main",
    success: resultData => handleInitialResult(resultData)
});