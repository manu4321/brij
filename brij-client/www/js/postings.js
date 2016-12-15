var currentPage = 1;
var noOfPages = 1;
var loadNew = false;
var filterUrl = GET_POSTS;
$(function () {
    $('#postingDiv').scroll(function(e){
        e.preventDefault();
        e.stopPropagation();
        if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight && loadNew) {
            var url = filterUrl;
            if(url.indexOf("?") === -1){
                url += "?";
            }else{
                url += "&";
            }
            url += "pageNo="+ currentPage;
                if (search_km !== null) {
                    url += "&distance=" + search_km;
                }
                if(currentPage !== noOfPages){
                    makeRequest(url, GET, "", "", function(data){
                        createPostingList(data, true);
                    }, null);
                }
        }
        loadNew = true;
    })
    $("#txtKm").val(search_km);
    getAllPosts();

    $("#btnSearch").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($("#txtSearch").val().length > 0) {
            var url = GET_POSTINGS_LIKE;
            url += "?title=" + $("#txtSearch").val();
            if (search_km !== null) {
                url += "&distance=" + search_km;
            }
            filterUrl = url;
            makeRequest(url, GET, "", "", function(data){
                createPostingList(data, false);
             }, null);
        } else {
            getAllPosts();
        }
    });
    
    $(document).on('onSettingChange', function(e, opts) {
                getAllPosts();
  });
    $(document).on('onScrollRefresh', function(e, opts) {
                getAllPosts();
  });

    
});

function getAllPosts() {
    var url = GET_POSTS;
    if (search_km !== null) {
        url += "?distance=" + search_km;
    }
    filterUrl = url;
    makeRequest(url, GET, "", "", function(data){
        createPostingList(data, false);
    }, null);
}

function createPostingList(data, append) {
    var listItems = "";
    var array = data.list;
    noOfPages = data.numberOfPages;
    currentPage = data.currentPage;
    for (var i = 0; i < array.length; i++) {
        var badge = "WANTED"
        var additionalClass = "list-group-item";
        var ribbonColor ="";
        if (array[i].isPost) {
            badge = "OFFER";
        }else{
            ribbonColor = "blue"
        }
        if (i % 2 !== 0) {
            additionalClass += " list-group-item-info"
        }
        var ratings = array[i].ratings;
        var starDiv = fillRating(ratings, data["rate_" + array[i].id]);
        var userWord = "";
        if(ratings.length === 1){
            userWord = " - 1 vote";
        }else{
            userWord = " - " + ratings.length + " votes";
        }
        var creationDate = new Date(array[i].creationDate).toLocaleString();
        listItems += "<div ><a class='"+additionalClass+"' href='post.html?id=" + array[i].id + "'  id='posting#" + array[i].id + "'>" + "<div class='ribbon "+ribbonColor+"'><span>" + badge + "</span></div><h3>" + array[i].title + "<small> by " + array[i].user.username+ "</small></h3>"+
            starDiv +userWord+" <br> Posted: "+creationDate+"</a></div>";
    }
    loadNew = false;
    if(append){
        if(currentPage === noOfPages){
            listItems += "<a class='list-group-item'><h3>No more posts to show</h3></a>";
        }
        $("#postingList").append(listItems);
    }else{
        $("#postingList").html(listItems);
    }

}

function fillRating(ratings, avgRate){
    var noOfRatings = ratings.length;
    var starDiv = "";
    for(var i = 0; i < 5; i++){
        var classToUse = "glyphicon ";
        if(i < avgRate){
            classToUse += "glyphicon-star";
        }else{
            classToUse += "glyphicon-star-empty";
        }
        starDiv += "<span class='"+classToUse+"'> </span>"
    }
   return starDiv;
}

function checkNull() {
    if ($("#txtSearch").val() === "") {
        getAllPosts();
    }
}