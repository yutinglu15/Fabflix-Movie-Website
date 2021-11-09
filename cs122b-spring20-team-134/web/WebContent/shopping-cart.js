
// get url paramters
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

var app=angular.module('cartApp',[]);
app.controller('cartCtrl', function($rootScope, $scope, $http, $location){
    console.log('cart Controller working');
    $http({
        method : "GET",
        url: "shoppingCart"
}).then(function mySuccess(response) {
            // console.log(response);
            // console.log(response.data);
            $rootScope.cart = response.data[0].cart;
            $rootScope.sesOpt = response.data[1];
    },

        function myError(response){
            $rootScope.data = response.statusText;
        })

});

app.controller('quantityCtrl', function ($rootScope, $scope, $http){
    $scope.plusItem = function (itemId) {
        // console.log(itemId);
        let item = {id:itemId, opt:"plus"};

        var addCartUrl = 'shoppingCart';
        $http({
            method : "POST",
            url: addCartUrl,
            params: item
        }).then(function mySuccess(response) {
                console.log("responding");
                // console.log(response);
                },

            function myError(response){
                $rootScope.data = response.statusText;
            });

        $http({
            method : "GET",
            url: "shoppingCart"
        }).then(function mySuccess(response) {
                // console.log(response);
                // console.log(response.data);
                $rootScope.cart = response.data[0].cart;
                $rootScope.sesOpt = response.data[1];
            },

            function myError(response){
                $rootScope.data = response.statusText;
            })


        // $rootScope.cart[index].quantity += 1;

    };

    $scope.minusItem = function (itemId) {
        console.log(itemId);
        let item = {id:itemId, opt:"minus"};

        var addCartUrl = 'shoppingCart';
        $http({
            method : "POST",
            url: addCartUrl,
            params: item
        }).then(function mySuccess(response) {
                console.log("responding");
                console.log(response);},

            function myError(response){
                $rootScope.data = response.statusText;
            });

        $http({
            method : "GET",
            url: "shoppingCart"
        }).then(function mySuccess(response) {
                // console.log(response);
                // console.log(response.data);
                $rootScope.cart = response.data[0].cart;
                $rootScope.sesOpt = response.data[1];
            },

            function myError(response){
                $rootScope.data = response.statusText;
            })
    };

    $scope.delItem = function (itemId) {
        console.log("deleting item");
        let item = {id:itemId, opt:"del"};

        var addCartUrl = 'shoppingCart';
        $http({
            method : "POST",
            url: addCartUrl,
            params: item
        }).then(function mySuccess(response) {
                // console.log("responding");
                // console.log(response);
                },

            function myError(response){
                $rootScope.data = response.statusText;
            });

        $http({
            method : "GET",
            url: "shoppingCart"
        }).then(function mySuccess(response) {
                // console.log(response);
                // console.log(response.data);
                $rootScope.cart = response.data[0].cart;
                $rootScope.sesOpt = response.data[1];
            },

            function myError(response){
                $rootScope.data = response.statusText;
            })
    };

});

// document.getElementById("backBtn").onclick = function(){
//     let newUrl = "aglr-movie-list.html?";
//     if( sesInfo.browseOpt === "genre"){
//         newUrl += "browse_by_genre=True";
//         newUrl += "&genre="+sesInfo.genre+"&page="+sesInfo.page+
//             "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
//             "&limit="+sesInfo.limit;
//         console.log(newUrl);
//     }
//     else if( sesInfo.browseOpt === "search"){
//         newUrl += "search_by_filter=True";
//         newUrl += "&title="+sesInfo.title+"&year="+sesInfo.year+"&director="+sesInfo.director+"&starName="+sesInfo.starName+
//             "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
//             "&page="+sesInfo.page+"&limit="+sesInfo.limit;
//     }
//     else if(sesInfo.browseOpt == "full"){
//         newUrl += "fullSearch=True";
//         newUrl += "&q="+sesInfo.full_query + sesInfo.starName+
//             "&sortoption1=" + sesInfo.sortOption1 + "&sortoption2="+ sesInfo.sortOption2 +
//             "&page="+sesInfo.page+"&limit="+sesInfo.limit;
//     }
//     location.href = newUrl;
// };