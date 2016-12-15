/**
	Global Variables
*/
/**
    Constants
*/
var IS_REQUEST_MESSAGE_FOR_OTHERS = "This post is requesting for help";
var IS_POSTING_MESSAGE_FOR_OTHERS = "This post is offering a service";
var USERNAME_ERROR = "Your username must be between 5 and 20 characters long";
var PASSWORD_ERROR = "Your password must be between 5 and 15 characters long";
var PASSWORD_UNMATCHED = "Your password does not match the password entered above";
var EMAIL_ERROR = "Your email does not match the required length";
var FIRSTNAME_ERROR = "Your first name must be between 2 and 30 character";
var LASTNAME_ERROR = "Your last name must be between 2 and 30 character";
var PHONENUMBER_ERROR = "Your phone number must be 10 digits long";
var ADDRESS_ERROR = "You must enter an address";
var CITY_ERROR = "You must enter a city";
var PROVINCE_ERROR = "You must enter a province";
var MINIMUM_USERNAME_LENGTH = 5;
var MAXIMUM_USERNAME_LENGTH = 20;
var MINIMUM_PASSWORD_LENGTH = 5;
var MAXIMUM_PASSWORD_LENGTH = 15;
var MINIMUM_EMAIL_LENGTH = 6;
var MAXIMUM_EMAIL_LENGTH = 30;
var MINIMUM_FIRSTNAME_LENGTH = 2;
var MAXIMUM_FIRSTNAME_LENGTH = 30;
var MINIMUM_LASTNAME_LENGTH = 2;
var MAXIMUM_LASTNAME_LENGTH = 30;
var PHONE_NUMBER_LENGTH = 10;
//var MAXIMUM_PHONE_LENGTH = 12;
var ADDRESS = true;
var MINIMUM_CITY_LENGTH = 3;
var MAXIMUM_CITY_LENGTH = 25;
var PROVINCE_LENGTH = 2;
var global_notifications;
var NOTIFICATION_LIMIT = 10;
//settings values
var search_km = 25;

/**
 type of reports
**/
var additionalTicketComment = "";

var REPORT_APP = "app";
var REPORT_POST = "post";
var notification_timer;
var PROVINCES = {
    ON: "Ontario",
    QC: "Quebec",
    NS: "Nova Scotia",
    NB: "New Brunswick",
    MB: "Manitoba",
    BC: "British Columbia",
    PE: "Prince Edward Island",
    SK: "Saskatchewan",
    AB: "Alberta",
    NL: "Newfoundland and Labrador"

}

var reportTypes = {
    login: "login/logout process",
    user: "user",
    post: "post",
    service: "service",
    request: "request",
    other: "other"
};

document.addEventListener("deviceready", onDeviceReady, false);

function onDeviceReady() {
    AndroidFullScreen.immersiveMode()
}

var app = {
    // Application Constructor
    initialize: function () {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function () {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function () {
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function (id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

    }
};

$(function () {
    if (window.location.href.indexOf("index.html") == -1) {
        checkIfOnline(null, true);
    }

    initializeMainMenu();

    //set up a timer to look out for unread notifications
    notificationRequest();
    notification_timer = setInterval(notificationRequest, 50000);

    setupStorage();
    setupSettingModal();
    setupScrollable();
    setupReportModal();
    createTwoButtonForm();

    $(".navbar #createPost").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        window.location.href = "createPost.html";
    });

    $(document).click(function (event) {
        var clickover = $(event.target);
        var _opened = $("#myNavbar").hasClass("collapse in");

        if (_opened === true && !clickover.hasClass("navbar-toggle")) {
            $("#btnOpenMenu").click();
        }
    });

});



function createTwoButtonForm() {
    var modal = "<div class='container' ><div id='formModal' class='modal fade' role='dialog'>" +
        "<div class='modal-dialog'>" +
        "<div class='modal-content'> <div class='modal-header'>" +
        "<h2 id='title'></h2> </div>" +
        "<div  class='modal-body'> " +
        "</div><div class='modal-footer'>" +
        "<button type='button' class='btn btn-info' id='btnSaveForm' > Send </button>" +
        "<button type='button' class='btn btn-default' data-dismiss='modal'>close</button>" + "</div> </div> </div>" +
        "</div> </div>";
    $("body").append(modal);
}

function setupScrollable() {
    var window_height = $(window).height(),
        content_height = window_height - 200;
    $('.scrollableArea').height(content_height);
}
$(window).resize(function () {
    var window_height = $(window).height(),
        content_height = window_height - 200;
    $('.scrollableArea').height(content_height);
    setupScrollForRefresh();
});

function fillListType() {
    var options = "";
    for (var key in reportTypes) {
        options += "<option value='" + reportTypes[key] + "'>" + reportTypes[key] + "</option>"
    }
    $("#lstType").html(options);

}

function setupReportModal() {
    var modal = "<div class='container' ><div id='reportModal' class='modal fade' role='dialog'>" +
        "<div class='modal-dialog'>" +
        "<div class='modal-content'> <div class='modal-header'>" +
        "<h2 id='typeOfReport'>Report</h2> </div>" +
        "<div class='modal-body'> " +
        reportBody() +
        "</div><div class='modal-footer'>" +
        "<button type='button' class='btn btn-info' id='btnSaveReport' > Send </button>" +
        "<button type='button' class='btn btn-default' data-dismiss='modal'>close</button>" + "</div> </div> </div>" +
        "</div> </div>";
    $("body").append(modal);

    fillListType();

    $("#btnSaveReport").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        console.log("as")
        var type = $("#lstType").val();
        var comment = $("#txaMessage").val() + "<MESSAGE>" + additionalTicketComment;
        var report = {
            type: type,
            comment: comment
        }

        additionalTicketComment = "";
        makeRequest(SAVE_TICKET, POST, JSON.stringify(report), APPLICATION_JSON, ticketSaved, null);



    })
}

function ticketSaved(data) {
    if (data !== "") {
        $("#reportModal").modal("hide");
        toast.show("You have successfully sent the ticket!")

    }
}



function reportBody() {
    var modalBody = "<div>" + "<form novalidate onSubmit='return false'>" +
        "<div class='form-group'><label>Title</label> <select class='form-control' id='lstType' ></select> </div> " +
        "<label>Comment:</label> <textarea id='txaMessage' class='form-control'></textarea> </div> " +
        "</form> "
    return modalBody;
}


function setupSettingModal() {
//    var modal = "<div class='container' ><div id='settingModal' class='modal fade' role='dialog'>" +
//        "<div class='modal-dialog'>" +
//        "<div class='modal-content'> <div class='modal-header'>" +
//        "<h4>Setting</h4> </div>" +
//        "<div class='modal-body'> " +
//        settingBody() +
//        "</div><div class='modal-footer'>" +
//        "<button type='button' class='btn btn-info' id='btnSaveSetting' > Save </button>" +
//        "<button type='button' class='btn btn-default' data-dismiss='modal'>close</button>" + "</div> </div> </div>" +
//        "</div> </div>";
//    $("body").append(modal);


    $("#btnSaveSetting").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        //get the distance for the km.
        var distanceKm = $("#txtKm").val();
        if (window.localStorage) {
            window.localStorage.setItem("searchKm", distanceKm);
            search_km = distanceKm;
            $("#txtKm").val(distanceKm);
            $("#settingModal").modal("hide");

            $(document).trigger('onSettingChange', {

            });
        }
        window.location.href = "postings.html";
    })
}

function settingBody() {
    var modalBody = "<div>" + "<form novalidate onSubmit='return false'>" +
        "<div class='form-group'><label>Distance to filter (km)</label> <input class='form-control' type='number' id='txtKm' value='" + search_km + "'/> </div> " +
        "</form> </div>"
    return modalBody;
}

function setupStorage() {
    if (window.localStorage) {
        var seachKmSetting = localStorage.getItem("searchKm");
        if (seachKmSetting !== null) {
            search_km = seachKmSetting;
        } else {
            window.localStorage.setItem("searchKm", 25);
        }

    }
}

function initializeMainMenu() {
    $("nav.mainMenu .navbar-header").append(
        "<div id='notificationDropDown'> <button id='notificationBtn' class='navbar-toggle dropdown-toggle glyphicon glyphicon-bell' data-toggle='dropdown'> </button> </div>"
    );
    $("#notificationDropDown").append(
        "<ul class='dropdown-menu dropdown-menu-right' id='notificationNavBar'> </ul>"
    );
    var navbar = "" +
        "<li class='menuLinks'><a href='postings.html'>View Posts</a></li>" +
        "<li class='menuLinks'><a href='accountDetails.html'>Account Details</a></li>" +
        "<li class='menuLinks'><a href='history.html'>Account History</a></li>" +
        "<li class='menuLinks'><a href='#' id='btnReport' >Create Ticket</a></li>" +
//        "<li class='menuLinks'><a href='#' id='btnSetting' >Distance Settings </a></li>" +
        "<li class='menuLinks'><a href='#' id='btnDonate' >Donate</a></li>" +
        "<li class='menuLinks'><a id='logoutMenuItem'>Logout</a></li>";
    $("#navbar").html(navbar);


    $("#notificationBtn").click(function () {
        var _opened = $("#myNavbar").hasClass("collapse in");

        if (_opened === true) {
            $("#btnOpenMenu").click();
        }
    })
    $("#logoutMenuItem").click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        makeRequest(LOGOUT, POST, "", "", function (data) {
            window.location = "index.html"
        }, null);
    });

    $("#brijHome").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        window.location.href = "postings.html";
    });

    $("#btnReport").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#txaMessage").val("");
        $("#lstType").attr("disabled", false);
        additionalTicketComment = "";
        $("#reportModal").modal();
    })

    $("#btnSetting").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#txtKm").val(search_km);
        $("#settingModal").modal();
        $("#navbar").removeClass("in");
    });

    $("#btnDonate").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        donateModal();
        $("#donateModal").modal("show");
        $("#navbar").removeClass("in");
    });

    //set up refresh by pulling in mobile
    setupScrollForRefresh();
}

function isMobile() {
    return document.URL.indexOf('http://') === -1 && document.URL.indexOf('https://') === -1;
}

function setupScrollForRefresh() {


    if (isMobile()) {
        $("nav.mainMenu").css("padding-top", "50px");
        var window_height = $(window).height(),
            content_height = window_height + 50;
        $('body').height(content_height);
        window.scrollBy(0, 50);

        window.document.addEventListener("scroll", function () {
            if (window.pageYOffset == 0) {
                makeRequest(GET_USER_NOTIFICATION, GET, "", "", fillNotifications, null);
                $(document).trigger('onScrollRefresh');
                window.scrollBy({
                    behavior: 'smooth'
                }, 50);

            }
        }, false);
    } else {
        // Web page
    }
}

function notificationRequest() {
    showLoading = false;
    makeRequest(GET_USER_NOTIFICATION, GET, "", "", fillNotifications, null);
}



function fillNotifications(data) {
    global_notifications = data;
    var noOfNotification = data.noOfUnRead;
    if (noOfNotification !== undefined && noOfNotification !== 0) {
        $("#notificationBtn").html("<span class='badge notification-badge'>" + data.noOfUnRead + "</span>")
    }
    var notifications = data.notifications;
    var navbar = "<li class='dropdown-header' ><h6 class=dropdown-header'>Notifications:</h6> </li>";
    if (notifications !== undefined) {
        for (var i = 0; i < notifications.length; i++) {
            var href = "";
            var classCss = "";
            var openRate = false;
            if (!notifications[i].readFlag) {
                classCss = "bg-danger readFlag";
                openRate = true;
            }
            var notificationId = notifications[i].id;
            if (notifications[i].type === "request") {
                href = "request.html?id=" + notifications[i].targetID;
            } else if (notifications[i].type === "requestComplete") {
                href = "request.html?id=" + notifications[i].targetID + "&openRate="+openRate;
            } else if (notifications[i].type === "conversation") {
                href = "request.html?id=" + notifications[i].targetID + "&openConvo=true";
            }
            navbar += "<li class='dropdown-item " + classCss + "'><a id='notification_" + notificationId + "' onclick='return notificationOnClick(this)' href='" + href + "' class='" + classCss + "'> " + notifications[i].description + "</a></li>";

        }
    }

    navbar += "<li class='dropdown-item'><div class='dropdown-divider'></div></li> " +
        "<li class='dropdown-item' > <a href='notifications.html' >View all notifications</a> </li>";

    $("#notificationNavBar").html(navbar);
}

function notificationOnClick(anchor) {
    var nId = $(anchor).attr("id").split("_")[1];
    var hasClass = $(anchor).hasClass("readFlag");
    if (hasClass) {
        var notification = {
            id: nId,
            readFlag: true
        };
        makeRequest(UPDATE_NOTIFICATION, POST, JSON.stringify(notification), APPLICATION_JSON, function () {
            window.location = $(anchor).attr("href");

        }, null);
        return false;
    }
    return true;

}

$.urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    } else {
        return results[1] || 0;
    }
}

function paginationDiv(id, item) {
    var pagination = "<nav aria-label='Page navigation' id='" + id + "'><ul class='pagination'>" +
        "<li class='page-item'> <a href='#' class='page-link backbtn' aria-label='Previous'>" +
        "<span aria-hidden='true'>&laquo;</span>" +
        "<span class='sr-only'>Previous</span></a> </li>" +
        item +
        "<li class='page-item'> <a class='page-link nextBtn' aria-label='Next'>" +
        "<span aria-hidden='true'>&raquo;</span>" +
        "<span class='sr-only'>Next</span></a> </li>" +
        "</lu></nav>";
    return pagination;
}
/**
 *   SETUP settings
 */

var loading = {
    show: function () {
        if (!$("#loadingDiv").length) {
            $("<div id='loadingDiv'><h3>Loading<br><span class='glyphicon glyphicon-refresh gly-ani'></span></h3> </div>")
                .css({
                    display: "block",
                    opacity: 0.90,
                    position: "fixed",
                    padding: "7px",
                    "text-align": "center",
                    background: "black",
                    color: "white",
                    width: "270px",
                    "z-index": 99999999,
                    left: ($(window).width() - 284) / 2,
                    top: $(window).height() / 2
                })
                .appendTo($("body"));
        }

    },
    end: function () {
        $("#loadingDiv").fadeOut("slow", function () {
            this.remove();
        })
    }

};

var errorAlert = {
    show: function (msg) {
        $("<div id='errorDiv'><h3>" + msg + "</h3></div>")
            .css({
                display: "block",
                opacity: 0.90,
                position: "fixed",
                padding: "7px",
                "text-align": "center",
                background: "black",
                color: "white",
                width: "270px",
                left: ($(window).width() - 284) / 2,
                top: 0
            })
            .appendTo($("body")).delay(1500);
    },
    end: function () {
        $("#toastID").hide();
    }

};

var toast = {
    show: function (msg) {
        $("<div id='toastID'><h3>" + msg + "</h3></div>")
            .css({
                display: "block",
                opacity: 0.90,
                position: "fixed",
                padding: "7px",
                "text-align": "center",
                background: "black",
                color: "white",
                width: "270px",
                left: ($(window).width() - 284) / 2,
                top: $(window).height() / 2
            })
            .appendTo($("body")).delay(1500)
            .fadeOut(400, function () {
                $(this).remove();
            });
    },
    end: function () {
        $("#toastID").hide();
    }


};

function donateModal() {
    var modal = "<center><div class='container' ><div id='donateModal' class='modal fade' role='dialog'>" +
        "<div class='modal-dialog'>" +
        "<div class='modal-content'>" +
        '<div class="modal-header">' +
        '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
        '<h4 class="modal-title">Donations</h4>' +
        '</div>' +
        "<div class='modal-body'> Every little bit helps! :)<br /><br />" +
        '<form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">' +
        '<input type="hidden" name="cmd" value="_s-xclick">' +
        '<input type="hidden" name="hosted_button_id" value="3FBBXRFYBYQSL">' +
        '<input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit"' + 'alt="PayPal - The safer, easier way to pay online!">' +
        '<img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">' +
        '</form>' +
        "</div></div> </div>" +
        "</div> </div></center>";

    $("body").append(modal);
}

function listItemGenerator(href, id, customClass, heading, body, badge1, body2) {
    var li = "";
    if (href === "") {
        href = "#";
    }
    li += '<a href="' + href + '" id="' + id + '" class="list-group-item list-group-item-action ' + customClass + '"><span class="badge">' + badge1 + '</span>' +
        '<h4 class="list-group-item-heading">' + heading + '</h4>' +
        '<p class="list-group-item-text">' + body + '</p>' + '<p class="list-group-item-text">' + body2 + '</p>'
    '</a>';
    return li;
}

function displayError(div, message) {
    $(div + " #errorDiv").remove("");
    $(div).prepend("<div id='errorDiv' class='alert alert-danger'>" + message + "</div>");
}

function fillRating(ratingsNo, avgRate, div) {
    var noOfRatings = ratingsNo;
    var starDiv = "";
    for (var i = 0; i < 5; i++) {
        var classToUse = "glyphicon ";
        if (i < avgRate) {
            classToUse += "glyphicon-star";
        } else {
            classToUse += "glyphicon-star-empty";
        }
        starDiv += "<span class='" + classToUse + "'> </span>"
    }
    $(div + " .starDiv").html(starDiv);
    var wordUser = "users";
    if (noOfRatings === 1) {
        wordUser = "user";
    }
    avgRate = avgRate.toFixed(0);
    $(div + " .ratingInfo").html(avgRate + " out of 5 - " + noOfRatings + " " + wordUser);
}
