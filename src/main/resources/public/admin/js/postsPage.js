/**
 * 
 */
var filterUrl = GET_ALL_POSTS;

$(function() {
	makeRequest(GET_ALL_POSTS, GET, "", "", populateTable, null);
	populateStateList();
	$("#btnSavePost").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		savePost();
	})

	$("#pageList").change(function() {
		var url = "";
		if (filterUrl.indexOf("?") === -1) {
			url = filterUrl + "?pageNo=" + ($("#pageList").val() - 1);
		} else {
			url = filterUrl + "&pageNo=" + ($("#pageList").val() - 1)
		}
		makeRequest(url, GET, "", "", populateTable, null);

	});

	$("#btnSearch").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var search = $("#txtSearch").val();
		if (search === "") {
			var url = GET_ALL_POSTS;
			filterUrl = url;
			makeRequest(url, GET, "", "", populateTable, null);
		} else {

			var url = GET_LIKE_POSTS + "?title=" + search;
			filterUrl = url;
			makeRequest(url, GET, "", "", populateTable, null);
		}

	});
});

function populateStateList() {
	$("#lstStatus").html("");
	for ( var key in STATUS) {
		if (STATUS.hasOwnProperty(key)) {
			$("#lstStatus").append(
					"<option value='" + key + "'>" + STATUS[key] + "</option>")
		}
	}
}

function savePost() {
	var updatePost = {
		id : $("#txtId").val(),
		status : $("#lstStatus").val()
	};
	var url = SAVE_POST_ADMIN;
	makeRequest(url, POST, JSON.stringify(updatePost), APPLICATION_JSON,
			updateScreen, null);
}

function updateScreen() {
	$("#editModal").modal("hide");
	makeRequest(GET_ALL_POSTS, GET, "", "", populateTable, null);

}

function populateTable(data) {
	var dir = "";
	var posts = data.list;
	var pageNo = data.currentPage;
	var numberOfPages = data.numberOfPages;
	for (var i = 0; i < posts.length; i++) {
		var post = posts[i];
		var title = post.title;
		var username = post.user.username;
		var type = "request"
		if (post.isPost) {
			type = "post";
		}
		var creationDate = new Date(post.creationDate).toLocaleString();
		var noOfRequests = data["replies_" + post.id];
		var service = data["service_" + post.id];
		var status = fixString(post.status);
		console.log(data);
		dir += "<tr>";
		dir += "<th>" + title + "</th>";
		dir += "<th>" + username + "</th>";
		dir += "<th>" + service + "</th>";
		dir += "<th>" + type + "</th>";
		dir += "<th>" + creationDate + "</th>";
		dir += "<th>" + noOfRequests + "</th>";
		dir += "<th>" + status + "</th>";
		dir += "<th><a href='#' class='glyphicon glyphicon-cog btnSetting' id='"
				+ post.id + "'>Edit</a></th>";
		dir += "</tr>"
	}
	$("#usersContent").html(dir);
	$(".btnSetting").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var url = GET_POST_BY_ID_ADMIN + "?id=" + this.id;
		makeRequest(url, GET, "", "", fillModal, null);
	});

	makePagination(pageNo, numberOfPages);
}
function makePagination(pageNo, numberOfPages) {
	var optionPage = "";
	for (var i = 1; i <= numberOfPages; i++) {
		var selected = "";
		if (i === pageNo) {
			selected = "selected = 'selected'"
		}
		optionPage += "<option " + pageList + " " + selected + " value='" + i
				+ "'>" + i + " </option>"
	}
	$("#pageList").html(optionPage);
	$("#noOfPage").html(numberOfPages);
}
function fillModal(data) {
	var post = data.posting;
	var serviceName = data.serviceName;

	$("#txtId").val(post.id)
	$("#txtTitle").val(post.title);
	$("#lstStatus").val(fixString(post.status));
	$("#editModal").modal();
}