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

// angular js mvc structure

// var app=angular.module('payApp',[]);
// app.controller('payCtrl', function($rootScope, $scope, $http, $location){
//     console.log('cart Controller working');
//     $http({
//         method : "GET",
//         url: "shoppingCart",
//     }).then(function mySuccess(response) {
//             console.log(response);
//             console.log(response.data);
//             var cart = response.data[0].cart;
//             total = 0;
//             Object.keys(cart).forEach(function(key){
//                 total += cart[key].price * cart[key].quantity;
//             });
//             console.log(total);
//             $rootScope.total = total;
//             // $rootScope.sesOpt = response.data[1];
//         },
//
//         function myError(response){
//             $rootScope.data = response.statusText;
//         });
//
// });

// app.controller('submitCtrl', function ($rootScope, $scope, $http) {
//     $scope.onSubmit = function(form){
//         console.log(JSON.stringify(form));
//
//         $http({
//             method : "POST",
//             url: "payment",
//             data: form
//         }).then(function mySuccess(response) {
//                 console.log(response);
//                 console.log(response.data);
//                 // $rootScope.sesOpt = response.data[1];
//             },
//
//             function myError(response){
//                 $rootScope.data = response.statusText;
//             });
//     }
// });


let payment_form = $("#payment_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    // console.log(resultDataString);
    // // let resultDataJson = JSON.parse(resultDataString);
    //
    // console.log("handle login response");
    // console.log(resultDataString["status"]);
    // $("#totalPrice").text(resultDataJson["price"]);
    if (resultDataString["status"] === "success") {
        let confirmEle = document.getElementById("confirm");
        confirmEle.innerText = "Your confirmation is " + resultDataString["confirmId"] + ". Your Item is ";
        let items = eval(resultDataString["confirmItem"]);
        let quants = eval(resultDataString["quantity"]);

        for (let i = 0; i < items.length; i++){
        confirmEle.innerText += quants[i] + " " + items[i] + ", ";
        }

        confirmEle.innerText +=  "Your total is " + resultDataString["total"] + " ";
        let checkBtn = document.getElementById("checkOutBtn");
        checkBtn.disabled= true;

    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        let confirmEle = document.getElementById("confirm");
        confirmEle.innerText = "No matching record. Please try again";
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    // console.log("submit login form");
    // console.log(JSON.stringify(payment_form));
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handleLoginResult
        }
    );
}

function handlePrintTotal(resultData){
    let totalEle = document.getElementById("totalNum");
    totalEle.innerText = "Your total is " + resultData;
}


// Bind the submit action of the form to a handler function
payment_form.submit(submitLoginForm);

$.ajax({
        method: "GET",
        dataType: "json",
        // Serialize the login form to the data sent by POST request
        url: "payment",
        success: resultData => handlePrintTotal(resultData)
    }
);
