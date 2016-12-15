var postID;
var isDisabled = true;
var post;
var clicked = false;
var requestCurrentPage = 1;
var requestTotalPage = 1;
var filterUrl = GET_REQUESTS_BY_POST_ID;
var loadNew = false;

$(function () {

    $('#requestList').scroll(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight && loadNew) {
            var url = filterUrl;
            if (url.indexOf("?") === -1) {
                url += "?";
            } else {
                url += "&";
            }
            url += "pageNo=" + requestCurrentPage;
            if (requestCurrentPage !== requestTotalPage) {
                makeRequest(url, GET, "", "", function (data) {
                    createRequestList(data, true);
                }, null);
            }
        }
        loadNew = true;

    })

    $("#btnBack").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        window.location.href = "postings.html";

    });

    $("#btnRequest").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        requestService();
    });

    $("#btnEdit").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        isDisabled = !isDisabled;
        $("#postForm .postinput").attr("disabled", isDisabled);
        if (isDisabled) {
            savePost();
            $("#btnEdit").html("Edit");
            $("#btnCancel").addClass("hide");
            $("#btnDelete").addClass("hide");
        } else {
            $("#btnEdit").html("Save");
            $("#btnCancel").removeClass("hide");
            $("#btnDelete").removeClass("hide");
        }
    });

    $("#btnCancel").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        isDisabled = !isDisabled;
        $("#postForm .postinput").attr("disabled", isDisabled);

        if (isDisabled) {
            $("#btnEdit").html("Edit");
            $("#btnCancel").addClass("hide");
            $("#btnDelete").addClass("hide");
        } else {
            $("#btnEdit").html("Save");
            $("#btnCancel").removeClass("hide");
            $("#btnDelete").removeClass("hide");
        }
    });

    $("#btnDelete").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#confirmDelete").modal("show");

    });

    $("#btnConfirmDelete").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        deletePost();
    });

    $("#reportBtn").click(function (e) {
        $("#txaMessage").val("");
        $("#lstType").val("post");
        $("#lstType").attr("disabled", true);
        additionalTicketComment += "Post: " + postID;
        $("#reportModal").modal();
    });

    $("#btnShowRequest").click(function (e) {
        var url = GET_REQUESTS_BY_POST_ID;
        url += "?postID=" + postID;
        filterUrl = url;
        makeRequest(url, GET, "", "", function (data) {
            createRequestList(data, false)
        }, null);
    });
    getPosts($.urlParam("id"));

});

function deletePost() {
    var url = DELETE_POST;
    url += "?id=" + postID;
    makeRequest(url, GET, "", "", savePostSuccessful, errorDeletingPost);
}

function savePostSuccessful() {
    window.location = "postings.html";
}

function errorDeletingPost(error) {
    var errorMsg = error.responseJSON.message.replace(";", "</br>");
    if (errorMsg.indexOf("brij_exception") !== -1) {
        errorMsg = errorMsg.replace("brij_exception", "");
        displayErrorInModal(errorMsg);
    }
}


function createRequestList(data, append) {
    var listItems = "";
    var array = data.list;
    requestCurrentPage = data.currentPage;
    requestTotalPage = data.numberOfPages;

    for (var i = 0; i < array.length; i++) {
        var status = array[i].status.replace("_", " ");
        if (i % 2 === 0) {
            listItems += "<a href='request.html?id=" + array[i].requestID + "' class='list-group-item historyListItem' id='" + array[i].requestID + "'>" + array[i].userID + "<span class='badge'>" + status + "</span></a>";
        } else {
            listItems += "<a href='request.html?id=" + array[i].requestID + "' class='list-group-item list-group-item-info historyListItem' id='" + array[i].requestID + "'>" + array[i].userID + "<span class='badge'>" + status + "</span></a>";
        }
    }
    if (array.length === 0) {
        listItems = "<h3 class='list-group-item'>No request to show</h3>"
    }
    loadNew = false;
    if (append) {
        if (requestCurrentPage === requestTotalPage) {
            listItems = "<h3 class='list-group-item'>No more requests to show</h3>"
        }

        $("#requestList").append(listItems);
    } else {
        $("#requestList").html(listItems);
    }
}

function savePost() {
    var updatePost = {
        id: postID,
        title: $("#postForm #title").val(),
        details: $("#postForm #description").val()
    };


    makeRequest(SAVE_POST, POST, JSON.stringify(updatePost), APPLICATION_JSON, function(){
         toast.show("Your post has been successfully updated!")
    }, null);
}


function requestService() {
    if (post.isPost) {
        $("#formModal #title").html("You are requesting this service");
    } else {
        $("#formModal #title").html("You are offering to fulfill this request");
    }
    $("#btnSaveForm").html("Send");
    $("#btnSaveForm").click(function (e) {
        if (clicked) return;
        clicked = true;
        e.preventDefault();
        e.stopPropagation();
        var newRequest = {
            notes: $("#txtRequestNotes").val(),
            postID: postID
        };

        makeRequest(CREATE_REQUEST, POST, JSON.stringify(newRequest), APPLICATION_JSON, function (data) {
            clicked = false;
            $("#formModal").modal("hide");
            getPosts(postID);
            toast.show("You successfully requested this post!")

        }, savePostErrorHandler);
    });
    var requestForm = "<form>" +
        "<label for='txtRequestNotes'>Notes:</label>" +
        "<textarea id='txtRequestNotes' class='form-control'></textarea>" +
        "</form>"
    $("#formModal .modal-body").html(requestForm);
    $("txtRequestNotes").html("");
    $("#formModal").modal();
}

function savePostErrorHandler(error) {
    $("#formModal").modal("hide");
    var errorMsg = error.responseJSON.message.replace(";", "</br>");
    if (errorMsg.indexOf("brij_exception") !== -1) {

        errorMsg = errorMsg.replace("brij_exception", "");
        displayError($("#postForm"), errorMsg);
    }

}


function getPosts(id) {
    //Need to get based on id
    postID = id;
    var url = GET_POST_BY_ID;
    url = url.replace(":id", id);
    makeRequest(url, GET, "", "", populatePost, null);
}

function populateUserInfo(data, noOfRatingsByPoster,ratingForPoster, ratingForUser) {
    $("#username").html(data.username);
    fillRating(noOfRatingsByPoster,ratingForPoster, "#posterRatingDiv");
    fillRating(data.ratings.length,ratingForUser, "#userRatingDiv")
    $("#userForm #firstName").val(data.firstName);
    $("#userForm #lastName").val(data.lastName);
    $("#userForm #phoneNumber").val(data.phoneNumber);
    $("#userForm #address").val(data.address);
    $("#userForm #city").val(data.city);
    $("#userForm #province").val(data.province);
    $("#userForm #email").val(data.email);

}

function populatePost(data) {
    post = data.posting;
    id = data.posting.id;
    populateUserInfo(data.posting.user, data.noOfRatingsByPoster,data.avgRateByPoster, data.avgRateByUser);
    $("#postName").html(data.posting.title);
    $("#postForm #title").val(data.posting.title);
    $("#postForm #serviceName").val(data.serviceName);
    $("#postForm #description").val(data.posting.details);
    fillRating(data.posting.ratings.length, data.avgRate, "#ratingDiv");
    var messageInfo = "";
    var hasRequest = data.hasRequested;
    if (data.posting.isPost) {
        messageInfo = IS_POSTING_MESSAGE_FOR_OTHERS;
        $("#btnRequest").html("Request Service");
    } else {
        messageInfo = IS_REQUEST_MESSAGE_FOR_OTHERS;
        $("#btnRequest").html("Fulfill Request");

    }

    if (hasRequest) {
        $("#btnRequest").html("View your request");
    }
    $("#btnRequest").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if (hasRequest) {
            window.location = "request.html?id=" + data.requestID;
        } else {
            requestService();
        }
    });


    $("#isPostDiv").html(messageInfo);

    var isOwner = (data.isOwner);
    if (isOwner) {
        $(".showOwner").removeClass("hide");
    } else {
        $(".showUser").removeClass("hide");
    }


}