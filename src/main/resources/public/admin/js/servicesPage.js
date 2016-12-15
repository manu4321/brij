/**
 * 
 */
//Used to know which url when paginating
var filterUrl = GET_ALL_SERVICES_ADMIN;
$(function() {
	makeRequest(GET_ALL_SERVICES_ADMIN, GET, "", "", populateTable, null);
	populateStateList();
	$("#btnSaveItem").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		saveItem();
	})

	$("#pageList").change(
			function() {
				var url = "";
				if(filterUrl.indexOf("?") === -1){
					url = filterUrl + "?pageNo="
					+ ($("#pageList").val() - 1);
				}else{
					url = filterUrl + "&pageNo="
					+ ($("#pageList").val() - 1)	
				}
				 
				makeRequest(url, GET, "", "", populateTable, null);

			});

	$("#btnSearch").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var search = $("#txtSearch").val();
		if (search === "") {
			var url = GET_ALL_SERVICES_ADMIN;
			filterUrl = url;
			makeRequest(url, GET, "", "", populateTable, null);
		} else {
			var url = GET_SERVICE_LIKE_TITLE_ADMIN + "?serviceName=" + search;
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

function saveItem() {
	var service = {
		id: $("#txtId").val(),
		serviceName : $("#serviceName").val(),
		status: $("#lstStatus").val()
	};

	makeRequest(SAVE_SERVICE_ADMIN, POST, JSON.stringify(service),
			APPLICATION_JSON, updateScreen, null);
}

function updateScreen() {
	$("#editModal").modal("hide");
	makeRequest(GET_ALL_SERVICES_ADMIN, GET, "", "", populateTable, null);

}

function populateTable(data) {
	var dir = "";
	var list = data.list;
	var pageNo = data.currentPage;
	var numberOfPages = data.numberOfPages;
	for (var i = 0; i < list.length; i++) {
		var item = list[i];
		var serviceName = item.serviceName;
		var status = item.status;

		dir += "<tr>";
		dir += "<th>" + serviceName + "</th>";
		dir += "<th>" + status + "</th>";
		dir += "<th><a href='#' class='glyphicon glyphicon-cog btnSetting' id='"
				+ item.id + "'>Edit</a></th>";
		dir += "</tr>"
	}
	$("#usersContent").html(dir);
	$(".btnSetting").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var url = GET_SERVICE_BY_ID_ADMIN + "?id=" + this.id;
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
	$("#txtId").val(data.id)
	$("#serviceName").val(data.serviceName)
	$("#lstStatus").val(fixString(data.status));

	$("#editModal").modal();
}