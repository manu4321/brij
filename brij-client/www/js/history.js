/**
 * Javascript meant for history
 */
var filterBy = "posts";
var currentPage = 1;
var noOfPages = 1;
var filterUrl = GET_POST_HISTORY;
var loadNew = false;
var currentPostId = -1;
var requestNoPages = 1;
var requestCurrentPage = 1;

$(function () {
    loadInfo(refreshForm)
    getAllPosts();

    $('#historyList').scroll(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight && loadNew) {
            var url = filterUrl;
            if (url.indexOf("?") === -1) {
                url += "?";
            } else {
                url += "&";
            }
            url += "pageNo=" + currentPage;
            if (currentPage !== noOfPages) {
                if (filterBy === "posts") {
                    makeRequest(url, GET, "", "", function (data) {
                        createHistoryList(data, true);
                    }, null);
                } else {
                    makeRequest(url, GET, "", "", function (data) {
                        createMyRequestList(data, true);
                    }, null);
                }

            }
        }
        loadNew = true;

    })

    $('#requestList').scroll(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight && loadNew) {
            var url = GET_REQUESTS_BY_POST_ID + "?pageNo=" + requestCurrentPage + "&postID=" + currentPostId;
            if (requestNoPages !== requestCurrentPage) {
                makeRequest(url, GET, "", "", function (data) {
                    createRequestList(data, true);
                }, null);

            }
        }
        loadNew = true;

    })

    $(".btnMyPosts").addClass("active");

    $("#btnMyPosts").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(".btnPosts").removeClass("active");
        $("#btnMyPosts").addClass("active");
        getAllPosts();
    });

    $("#btnMyReplies").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(".btnPosts").removeClass("active");
        $("#btnMyReplies").addClass("active");
        getAllRequests();
    });
    
    $(document).on('onScrollRefresh', function(e, opts) {
        if (filterBy === "posts") {
            getAllPosts();
        }else{
            getAllRequests();

        }
  });

});

function refreshForm(data) {
    $("#username").html(data.username);
}

function loadInfo(callback) {
    makeRequest(GET_CURRENT_USER, GET, "", APPLICATION_JSON, callback, null);
}

function getAllPosts() {
    filterBy = "posts";
    filterUrl = GET_POST_HISTORY;
    makeRequest(GET_POST_HISTORY, GET, "", "", function (data) {
        createHistoryList(data, false);
    }, null);
}

function getAllRequests() {
    filterBy = "requests";
    filterUrl = GET_MY_REQUESTS;
    makeRequest(GET_MY_REQUESTS, GET, "", "", function (data) {
        createMyRequestList(data, false);
    }, null);
}

function createHistoryList(data, append) {
    var listItems = "";
    var array = data.list;
    noOfPages = data.numberOfPages;
    currentPage = data.currentPage;
    for (var i = 0; i < array.length; i++) {
        var badge1 = "Number of Replies: " + data.countMap[array[i].id];
        var badge2 = "Date: " + new Date(array[i].creationDate).toLocaleString();
        var service = data.titleMap[array[i].id];

        listItems += listItemGenerator("", array[i].id, "historyListItem", "Title: " + array[i].title, "Service: " + service, badge1, badge2);

    }

    loadNew = false;
    if (append) {
        if (noOfPages === currentPage) {
            listItems = "<a class='list-group-item'><h3 class='list-group-item historyListItem'>No more posts to show</h3></a>"
        }
        $("#historyList").append(listItems);
    } else {
        $("#historyList").html(listItems);
    }

    $(".historyListItem").click(function (e) {
        $("#postInfoModal").modal('show');
        var url = GET_REQUESTS_BY_POST_ID;
        url += "?postID=" + this.id;
        currentPostId = this.id;
        $("#btnViewMyPost").attr("href", "post.html?id=" + currentPostId)
        makeRequest(url, GET, "", "", function (data) {
            createRequestList(data, false);
        }, null);
    });
}

function createRequestList(data, append) {
    var listItems = "";
    var array = data.list;
    requestNoPages = data.numberOfPages;
    requestCurrentPage = data.currentPage;
    for (var i = 0; i < array.length; i++) {
        var date = new Date(array[i].creationDate).toLocaleDateString();
        listItems += listItemGenerator("request.html?id=" + array[i].requestID, "", "historyListItem", "Reply From: " + array[i].userID, "", "Date: " + date, "");

    }
    loadNew = false;
    if (append) {
        if (requestNoPages === requestCurrentPage) {
            listItems = "<a class='list-group-item'><h3 class='list-group-item historyListItem'>No more requests to show</h3></a>"
        }
        $("#requestList").append(listItems);
    } else {
        $("#requestList").html(listItems);
    }
}

function createMyRequestList(data, append) {
    var listItems = "";
    var array = data.list;
    noOfPages = data.numberOfPages;
    currentPage = data.currentPage;
    var titles = data.postTitles;
    for (var i = 0; i < array.length; i++) {
        var requestId = array[i].requestID;
        var date = new Date(array[i].creationDate).toLocaleDateString();
        listItems += listItemGenerator("request.html?id=" + requestId, requestId, "requestListItem", "Post Title: " + titles[requestId], "Service: " + data.serviceTitles[requestId], array[i].status.replace("_", " "), "Date: " + date);

    }

    loadNew = false;
    if (append) {
        if (noOfPages === currentPage) {
            listItems = "<h3 class='list-group-item'>No more requests to show</h3>"
        }
        $("#historyList").append(listItems);
    } else {
        $("#historyList").html(listItems);
    }
}