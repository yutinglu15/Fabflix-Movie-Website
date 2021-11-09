let addStars = $("#insertStars");
let addMovies = $("#insertMovies");
let showTables = $("#showTables");

function  handleDisplayResult(resultDataString) {
    alert(resultDataString);
}


function insertStarForm(formSubmitEvent){
    console.log("submit add form");
    // console.log(JSON.stringify(payment_form));
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "addStar", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addStars.serialize(),
            // success: resultData => handlePrintAdd(resultData)
            success: handleDisplayResult
        }
    );
}

function insertMovieForm(formSubmitEvent){
    console.log("submit add form");
    // console.log(JSON.stringify(payment_form));
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "addMovie", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: addMovies.serialize(),
            // success: resultData => handlePrintAdd(resultData)
            success: handleDisplayResult
        }
    );
}

function sentTableName(formSubmitEvent){
    console.log("submit add form");
    // console.log(JSON.stringify(payment_form));
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "showTables", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: showTables.serialize(),
            success: resultData => handleTable(resultData)
            // success: alert("added")
        }
    );
}

function handleTable(resultData) {
    let resultDataJson = JSON.parse(resultData);


    // If login succeeds, it will redirect the user to index.html
    // if (resultDataJson["status"] === "success") {
    //     window.location.replace("dashboard.html");
    // } else {
    //     // If login fails, the web page will display
    //     // error messages on <div> with id "login_error_message"
    //     console.log("show error message");
    //     console.log(resultDataJson["message"]);
    // document.getElementById("metadata").innerText = resultData;
    // var tempstring = resultDataJson["Field"].replace(/(^\s*)|(\s*$)/g, "");
    var tempstring2 = resultDataJson["Type"].replace(/(^\s*)|(\s*$)/g, "");
    var string = resultDataJson["Field"].replace(/(^\s*)|(\s*$)/g, "").replace(/\s/g,'\n');
    var string2 = tempstring2.replace(/\s/g,'\n');
    document.getElementById("metadatafield").innerText = string;
    document.getElementById("metadatatype").innerText = string2;
    // $("#metadatafield").text(resultDataJson["Field"]);
    // $("#metadatatype").text(resultDataJson["Type"]);
}



addStars.submit(insertStarForm);
addMovies.submit(insertMovieForm);
showTables.submit(sentTableName);