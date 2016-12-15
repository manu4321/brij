/**
 * 
 */
var filterUrl = GET_ALL_USERS;
$(function() {
	makeRequest(GET_ALL_USERS, GET, "", "", populateTable, null);
	populateProvinceList();
	populateStateList();
	$("#btnSaveUser").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		saveUser();
	})

	$("#pageList").change(function() {
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
		var url = "";
		var search = $("#txtSearch").val();
		if (search === "") {
			url = GET_ALL_USERS;
			filterUrl = url;
		} else {
			url = GET_LIKE_USER + "?username=" + search;
			filterUrl = url;
			console.log(url);

		}
		makeRequest(url, GET, "", "", populateTable, null);

	});
});

function populateProvinceList() {
	$("#lstProvinces").html("");
	for ( var key in PROVINCES) {
		if (PROVINCES.hasOwnProperty(key)) {
			$("#lstProvinces").append(
					"<option value='" + key + "'>" + PROVINCES[key]
							+ "</option>")
		}
	}
}

function saveUser() {
	var updateUser = {
		firstName : $("#txtFirstName").val(),
		lastName : $("#txtLastName").val(),
		phoneNumber : $("#txtPhoneNo").val(),
		address : $("#txtStreet").val(),
		city : $("#txtCity").val(),
		province : $("#lstProvinces").val(),
		email : $("#txtEmail").val(),
		status : $("#lstStatus").val()
	};
	var url = UPDATE_USER_BY_NAME + "?username=" + $("#txtUsername").val();
	makeRequest(url, POST, JSON.stringify(updateUser), APPLICATION_JSON,
			updateScreen, null);
}

function updateScreen() {
	$("#editModal").modal("hide");
	makeRequest(GET_ALL_USERS, GET, "", "", populateTable, null);

}

function populateTable(data) {
	var dir = "";
	var users = data.users;
	var pageNo = data.currentPage;
	var numberOfPages = data.numberOfPages;
	console.log(data);
	for (var i = 0; i < users.length; i++) {
		var user = users[i];
		var address = fixString(user.address + ", " + user.city + ", "
				+ user.province);
		var username = fixString(user.username);
		var firstName = fixString(user.firstName);
		var lastName = fixString(user.lastName);
		var email = fixString(user.email);
		var status = fixString(user.status);

		dir += "<tr>";
		dir += "<th>" + username + "</th>";
		dir += "<th>" + firstName + "</th>";
		dir += "<th>" + lastName + "</th>";
		dir += "<th>" + email + "</th>";
		dir += "<th>" + address + "</th>";
		dir += "<th>" + status + "</th>";
		dir += "<th><a href='#' class='glyphicon glyphicon-cog btnSetting' id='"
				+ username + "'>Edit</a></th>";
		dir += "</tr>"
	}
	$("#usersContent").html(dir);
	$(".btnSetting").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var url = GET_USER + "?username=" + this.id;
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
	$("#txtUsername").val(data.username);
	$("#txtFirstName").val(fixString(data.firstName));
	$("#txtLastName").val(fixString(data.lastName));
	$("#txtEmail").val(fixString(data.email));
	$("#txtPhoneNo").val(fixString(data.phoneNumber));
	$("#txtStreet").val(fixString(data.address));
	$("#txtCity").val(fixString(data.city));
	$("#lstProvinces").val(fixString(data.province));
	$("#lstStatus").val(fixString(data.status));

	$("#editModal").modal();
}