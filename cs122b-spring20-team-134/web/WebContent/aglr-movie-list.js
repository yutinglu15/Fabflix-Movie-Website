// import{ActivatedRoute} from '@angular/router';
//
// constructor(
//     var route: ActivatedRoute
// ){}
//
// ngOnInit(){
//     this.browse_by_genre= this.route.snapshot.paramMap.get('browse-by-genre');
//     this.genre = this.route.snapshot.paramMap.get('genre');
// }
console.log('???');
// import {ChangeDetectorRef} from '@angular/core';
//
// constructor(
//     private changeDetectorRef:ChangeDetectorRef
// ){}

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

let browse_by_genre = getParameterByName('browse_by_genre');
let genre = getParameterByName('genre');

// handle full text search
let full_search = getParameterByName('fullSearch');
let full_search_query = getParameterByName("q");

let sortoption1= null;
let sortoption2 = null;
let pageNum = 0;
let pageLim = 10;
let pageNumGet=getParameterByName('page');
let pageLimGet=getParameterByName('limit');
let browse_by_title = getParameterByName('browse_by_title');
let ch = getParameterByName('ch');
let query_url = "aglr-movie-list?";
let browseOpt = "";
let sortOpt = "";
let pageOpt = "";

//
let search_by_filter = getParameterByName("search_by_filter");
let title = getParameterByName("title");
let year = getParameterByName("year");
let director = getParameterByName("director");
let starName = getParameterByName("starName");
let sortoption1_get = getParameterByName("sortoption1");
let sortoption2_get = getParameterByName("sortoption2");
//

var app=angular.module('myApp',[]);
app.controller('myCtrl', function($rootScope, $scope, $http, $location){
    console.log('???');
    if(full_search === 'True'){
        browseOpt = 'fullSearch='+full_search+'&q='+full_search_query;
    }
   if(browse_by_genre==='True'){
        browseOpt='browse_by_genre='+browse_by_genre+"&genre="+genre;
    }
    else{
        if(browse_by_title==='True'){
            browseOpt='browse_by_title='+browse_by_title+"&ch="+ch;
        }
       if(search_by_filter==='True'){
           browseOpt='search_by_filter=True&title='+title+"&year="+year+"&director="+director+"&starName="+starName;
       }
    }
    if(pageNumGet != null && pageLimGet != null){
        pageOpt += '&limit='+pageLimGet+'&page='+pageNumGet;
        pageNum = pageNumGet;
        pageLim = pageLimGet;
        // console.log(query_url);
    }
    if(sortoption1_get != null){
        sortOpt += '&sortoption1='+sortoption1_get;
        sortoption1 = sortoption1_get;
        // console.log(query_url);
    }

    if(sortoption2_get != null){
        sortOpt += '&sortoption2='+sortoption2_get;
        sortoption2 = sortoption2_get;
    }

    $http({
        method : "GET",
        url: query_url + browseOpt + sortOpt + pageOpt
        // url: "aglr-movie-list?"+query_url
        // url: "aglr-movie-list?browse_by_genre="+ browse_by_genre + "&genre="+ genre
    }).then(function mySuccess(response) {
            console.log(response);
            $rootScope.data = response.data;
            // console.log($rootScope.data)
        },

        function myError(response){
        $rootScope.data = response.statusText;
        })

});
app.controller('pageCtrl', function($rootScope, $scope, $http){
    $scope.prev = function(){
        pageNum--;
        // console.log(pageNum);
        if(pageNum < 1){
            $scope.disable1=true;
        }
        pageOpt = "&page="+pageNum + "&limit=" + pageLim;

        $http({
            method : "GET",
            url: query_url + browseOpt + sortOpt + pageOpt

            // url: "aglr-movie-list?"+query_url+"&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            // + "&page="+pageNum + "&limit=" + pageLim
        }).then(function mySuccess(response) {
                // console.log(response);
                $rootScope.data = response.data;},

            function myError(response){
                $rootScope.data = response.statusText;
            });

    };

    $scope.next = function(){
        pageNum++;
        if(pageNum > 0){
            $scope.disable1=false;
        }
        pageOpt = "&page="+pageNum + "&limit=" + pageLim;

        $http({
            method : "GET",
            url: query_url + browseOpt + sortOpt + pageOpt
            // url: "aglr-movie-list?"+query_url+"&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            //     + "&page="+pageNum + "&limit=" + pageLim
        }).then(function mySuccess(response) {
                // console.log(response);
                $rootScope.data = response.data;},

            function myError(response){
                $rootScope.data = response.statusText;
            });

    };

    $scope.turnPageLim = function(val){
        pageLim = val;
        // console.log(pageLim);
        pageOpt = "&page="+pageNum + "&limit=" + pageLim;
        $http({
            method : "GET",
            url: query_url + browseOpt + sortOpt + pageOpt
            // url: "aglr-movie-list?browse_by_genre="+ browse_by_genre + "&genre="+ genre + "&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            //     + "&page="+pageNum + "&limit=" + pageLim
            // url: "aglr-movie-list?"+query_url+"&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            //     + "&page="+pageNum + "&limit=" + pageLim
        }).then(function mySuccess(response) {
                // console.log(response);
                $rootScope.data = response.data;},

            function myError(response){
                $rootScope.data = response.statusText;
            });
    };
});

app.controller('sortCtrl', function($rootScope, $scope, $http, $location){
    $scope.select1 = function(val){
        sortoption1 = val;
        // console.log("my text", sortoption1);
        sortOpt = "&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2;
        $http({
            method : "GET",
            url: query_url + browseOpt + sortOpt + pageOpt
            // url: "aglr-movie-list?"+query_url+"&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            //     + "&page="+pageNum + "&limit=" + pageLim
            // url: "aglr-movie-list?browse_by_genre="+ browse_by_genre + "&genre="+ genre + "&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
        }).then(function mySuccess(response) {
                // console.log(response);
                $rootScope.data = response.data;},

            function myError(response){
                $rootScope.data = response.statusText;
            });

    };

    $scope.select2 = function(val){
        console.log("calling select 2");
        sortoption2 = val;
        // console.log("my text", sortoption2);
        sortOpt = "&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2;
        $http({
            method : "GET",
            url: query_url + browseOpt + sortOpt + pageOpt
            // url: "aglr-movie-list?"+query_url+"&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
            //     + "&page="+pageNum + "&limit=" + pageLim
            // url: "aglr-movie-list?browse_by_genre="+ browse_by_genre + "&genre="+ genre + "&sortoption1=" + sortoption1 + "&sortoption2=" + sortoption2
        }).then(function mySuccess(response) {
                // console.log(response);
                $rootScope.data = response.data;},

            function myError(response){
                $rootScope.data = response.statusText;
            });

    };
});

app.controller('addCartCtrl', function($rootScope, $scope, $http, $location){
    console.log('addCartCtrl');
    $scope.addToCart = function(title, id){
        // console.log(title);
        // console.log(id);
        alert("Successfully add to cart!");
        let item = {title:title, id:id, opt:"add"};

        console.log(item);
        var addCartUrl = 'shoppingCart';
        $http({
            method : "POST",
            url: addCartUrl,
            params: item
       }).then(function mySuccess(response) {
           console.log("respongding");
           // console.log(response);
           },

            function myError(response){
                $rootScope.data = response.statusText;
            });

    };


});