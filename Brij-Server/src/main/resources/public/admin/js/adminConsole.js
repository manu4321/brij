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
		window.location = ADMIN_USER_PAGE;
	});
	$(".postsDiv").click(function(e) {
		window.location = ADMIN_POST_PAGE;

	});
	$(".serviceDiv").click(function(e) {
		window.location = ADMIN_SERVICES_PAGE;
	});
	$(".webPortalDiv").click(function(e) {
		window.location = WEB_PORTAL_PAGE;
	});
	$(".ticketDiv").click(function(e) {
		window.location = ADMIN_TICKET_PAGE;
	});
	$(".reportDiv").click(function(e) {
		window.location = ADMIN_REPORT_PAGE;
	});
	$(".userPortalDiv").click(function(e) {
		window.location = USER_PORTAL_PAGE;
	});
	
	$(".logoutDiv").click(function(e) {
		logout(function(e) {
			window.location = "/";
		}, null);
	});
	$(".modal").addClass("precedence");

	setupScrollable();
});

function setupScrollable() {
	var window_height = $(window).height(), content_height = window_height - 200;
	$('.scrollableArea').height(content_height);
}
$(window)
		.resize(
				function() {
					var window_height = $(window).height(), content_height = window_height - 200;
					$('.scrollableArea').height(content_height);
				});

function makeMenu() {
	var div = "<div id='sidebar-wrapper'>'";
	div += "<ul id='sidebar_menu' class='sidebar-nav'>"
			+ "<li class='sidebar-brand'><a id='menu-toggle' href='#'>Brij"
			+ "<span id='main_icon' class='glyphicon glyphicon-align-justify'></span></a></li>"
			+ "</ul";

	div += "<ul class='sidebar-nav' id='sidebar'> "
			+ "<li><a href='#' class='menuItem homeDiv' id='menuBtnUser'> Home <span class='glyphicon glyphicon-home'></span></a></li>"
			+ "<li><a href='#' class='menuItem usersDiv' id='menuBtnUser'> User <span class='glyphicon glyphicon-user'></span></a></li>"
			+ "<li><a href='#' class='menuItem postsDiv' id='menuBtnPost'> Postings <span class='glyphicon glyphicon-book'></span></a></li>"
			+ "<li><a href='#' class='menuItem serviceDiv' id='menuBtnService'> Services <span class='glyphicon glyphicon-globe'></span></a></li>"
			+ "<li><a href='#' class='menuItem userPortalDiv' id='menuBtnPortal'> User Portal <span class='glyphicon glyphicon-th-large'></span></a></li>"
			+ "<li><a href='#' class='menuItem webPortalDiv' id='menuBtnPortal'> Web Portal <span class='glyphicon glyphicon-th-large'></span></a></li>"
			+ "<li><a href='#' class='menuItem ticketDiv' id='menuBtnTicket'> Tickets <span class='glyphicon glyphicon-th-list'></span></a></li>"
			+ "<li><a href='#' class='menuItem reportDiv' id='menuBtnReport'> Reports <span class='glyphicon glyphicon-signal'></span></a></li>"
			+ "<li><a href='#' class='menuItem logoutDiv' id='menuBtnLogout'> Log-out <span class='glyphicon glyphicon-log-out'></span></a></li>"
			+ "</ul> </div>";

	div += "<nav class='navbar navbar-inverse mainMenu'>"
			+ "<div class='container-fluid'>"
			+ "<div class='navbar-header'>"
			+ "<a href='#' class='glyphicon glyphicon-user navbar-toggle' id='btnUserAccount'> "
			+ "<span id='userName'>Admin</span> </a> "
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
			servID : "1",
			details : "POST_" + i,
			isPost : i % 2 === 0

		};

		makeRequest(SAVE_POST, POST, JSON.stringify(newPost), APPLICATION_JSON,
				null, null);
	}
}

function makeRequestAdmin(start, length, postID) {

	var end = start + length;
	for (var i = start; i < end; i++) {
		var newRequest = {
			notes : "request +" + i,
			postID : postID
		};

		makeRequest(CREATE_REQUEST, POST, JSON.stringify(newRequest),
				APPLICATION_JSON, null, null);
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

function makeTicketAdmin(start, length) {

	var end = start + length;
	for (var i = start; i < end; i++) {
		var type = "post";
		var comment = "Comment# " + i;
		var report = {
			type : type,
			comment : comment
		}

		makeRequest(SAVE_TICKET, POST, JSON.stringify(report),
				APPLICATION_JSON, null, null);
	}
}
