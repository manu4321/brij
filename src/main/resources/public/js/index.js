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

var PROVINCES = {
	ON : "Ontario",
	QC : "Quebec",
	NS : "Nova Scotia",
	NB : "New Brunswick",
	MB : "Manitoba",
	BC : "British Columbia",
	PE : "Prince Edward Island",
	SK : "Saskatchewan",
	AB : "Alberta",
	NL : "Newfoundland and Labrador"

}
var STATUS = {
	active : "active",
	closed : "closed"
}

var loading = {
	show : function() {
		if (!$("#loadingDiv").length) {
			$(
					"<div id='loadingDiv'><h3>Loading<br><span class='glyphicon glyphicon-refresh gly-ani'></span></h3> </div>")
					.css({
						display : "block",
						opacity : 0.90,
						position : "fixed",
						padding : "7px",
						"text-align" : "center",
						background : "black",
						color : "white",
						width : "270px",
						left : ($(window).width() - 284) / 2,
						top : $(window).height() / 2
					}).appendTo($("body"));
		}

	},
	end : function() {
		$("#loadingDiv").fadeOut("slow", function() {
			this.remove();
		})
	}

};
$(function() {

	var menuDiv = makeMenu();
	$("#wrapper").prepend(menuDiv);

	$("#menu-toggle").click(function(e) {
		e.preventDefault();
		$("#wrapper").toggleClass("active");
	});
	$(".homeDiv").click(function(e) {
		window.location = HOME;
	});
	$(".usersDiv").click(function(e) {
		window.location = ACCOUNT_DETAILS_PAGE;
	});
	$(".webPortalDiv").click(function(e) {
		window.location = WEB_PORTAL_PAGE;
	});
	$(".ticketDiv").click(function(e) {
		window.location = USER_TICKET_PAGE;
	});
	$(".logoutDiv").click(function(e) {
		logout(function(e) {
			window.location = "/";
		}, null);
	});
	$(".modal").addClass("precedence");
});

function makeMenu() {
	var div = "<div id='sidebar-wrapper'>'";
	div += "<ul id='sidebar_menu' class='sidebar-nav'>"
			+ "<li class='sidebar-brand'><a id='menu-toggle' href='#'>Brij"
			+ "<span id='main_icon' class='glyphicon glyphicon-align-justify'></span></a></li>"
			+ "</ul>";

	div += "<ul class='sidebar-nav' id='sidebar'> "
			+ "<li><a href='#' class='menuItem homeDiv' id='menuBtnUser'> Home <span class='glyphicon glyphicon-home'></span></a></li>"
			+ "<li><a href='#' class='menuItem usersDiv' id='menuBtnUser'> Edit account <span class='glyphicon glyphicon-user'></span></a></li>"
			+ "<li><a href='#' class='menuItem webPortalDiv' id='menuBtnPortal'> Web Portal <span class='glyphicon glyphicon-th-large'></span></a></li>"
			+ "<li><a href='#' class='menuItem ticketDiv' id='menuBtnTicket'> Tickets <span class='glyphicon glyphicon-th-list'></span></a></li>"
			+ "<li><a href='#' class='menuItem logoutDiv' id='menuBtnLogout'> Log-out <span class='glyphicon glyphicon-log-out'></span></a></li>"
			+ "</ul> </div>";

	div += "<nav class='navbar navbar-inverse mainMenu'>"
			+ "<div class='container-fluid'>"
			+ "<div class='navbar-header'>"
			+ "<a href='#' class='glyphicon glyphicon-user navbar-toggle' id='btnUserAccount'> "
			+ "<span id='userName'>User</span> </a> "
			+ "</div> </div> </div> </nav>";
	return div;
}

function fixString(str) {
	if (typeof str === 'undefined' || str === null
			|| str.indexOf("null") !== -1) {
		str = "";
	}
	return str;
}

function paginationDiv(item) {
	var pagination = "<nav aria-label='Page navigation'><ul class='pagination'>"
			+ "<li class='page-item'> <a href='#' class='page-link backbtn' aria-label='Previous'>"
			+ "<span aria-hidden='true'>&laquo;</span>"
			+ "<span class='sr-only'>Previous</span></a> </li>"
			+ item
			+ "<li class='page-item'> <a class='page-link nextBtn' aria-label='Next'>"
			+ "<span aria-hidden='true'>&raquo;</span>"
			+ "<span class='sr-only'>Next</span></a> </li>" + "</lu></nav>";
	return pagination;
}

function populateStateList() {
	$("#lstStatus").html("");
	for ( var key in STATUS) {
		if (STATUS.hasOwnProperty(key)) {
			$("#lstStatus").append(
					"<option value='" + key + "'>" + STATUS[key] + "</option>")
		}
	}
}

// REMOVE AFTER
function makeNoOfNewUsers(start, length) {

	var end = start + length;
	for (var i = start; i < end; i++) {
		var newUser = {
			username : "user#" + i,
			password : "user#" + i,
			email : "user#" + i
		};

		makeRequest(REGISTER_USER, POST, JSON.stringify(newUser),
				APPLICATION_JSON, null, null);
	}
}

function makePostsAdmin(start, length) {

	var end = start + length;
	for (var i = start; i < end; i++) {
		var newPost = {
			title : "POST_" + i,
			servID : "0",
			details : "POST_" + i,
			isPost : i % 2 === 0

		};

		makeRequest(SAVE_POST, POST, JSON.stringify(newPost), APPLICATION_JSON,
				null, null);
	}
}

function makeServiceAdmin(start, length) {

	var end = start + length;
	for (var i = start; i < end; i++) {
		var service = {
			serviceName : "service_" + i,
		};

		makeRequest(CREATE_SERVICE, POST, JSON.stringify(service),
				APPLICATION_JSON, updateScreen, null);
	}
}

$.urlParam = function (name) {
    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
    if (results == null) {
        return null;
    } else {
        return results[1] || 0;
    }
}
