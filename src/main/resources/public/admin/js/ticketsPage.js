/**
 * 
 */
// Used to know which url when paginating
var reportTypes = {
	all: "See all",
	login : "login/logout process",
	user : "user",
	post : "post",
	request : "request",
	other : "other"
};
var filterUrl = GET_ALL_TICKETS_ADMIN;
$(function() {
	makeRequest(GET_ALL_TICKETS_ADMIN, GET, "", "", populateTable, null);
	populateStateList();
	fillListType();
	$("#btnSaveItem").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		saveItem();
	})
	$("#lstTypes").change(function() {
		var type = $("#lstTypes").val();
		var url = GET_ALL_TICKETS_ADMIN;
		if (type !== "See all" ) {
			url = GET_TICKET_BY_TYPE_ADMIN + "?type=" + type;
		}
		filterUrl = url;
		makeRequest(url, GET, "", "", populateTable, null);

	});
	
	$("#lstStatus").change(function() {
		$("#txtStatus").val($("#lstStatus").val());
		$("#txtStatusComment").val("");
		$("#commentModal").modal();

		$("#btnUpdateTicket").click(function(e){
			e.preventDefault();
			e.stopPropagation();
			var id = $("#txtId").val();
			var status = $("#txtStatus").val();
			var message = $("#txtStatusComment").val();
			var message = {
					message : message	
				
			};
			var url = UPDATE_STATUS_TICKET + "?id="+id+"&status="+status;

			makeRequest(url, POST, JSON.stringify(message), APPLICATION_JSON, function(){
				$("#commentModal").modal("hide");
				makeRequest(GET_ALL_TICKETS_ADMIN, GET, "", "", populateTable, null);

			}, null);
		});
	});
	
	
	$("#pageList").change(function() {
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
	
	$("#btnNewComment").click(function(e){
		e.preventDefault();
		e.stopPropagation();
		var newComment = $("#txtNewComment").val();
		var id = $("#txtId").val();
		var message = {
			message : newComment
		}
		var url = SAVE_TICKET_MESSAGE + "?id=" + id;

		makeRequest(url, POST, JSON.stringify(message), APPLICATION_JSON, function(){
			var url = GET_TICKET_BY_ID + "?id=" + id;
			makeRequest(url, GET, "", "", fillModal, null);
		}, null);
		
	});

});



function fillListType() {
	var options = "";
	for ( var key in reportTypes) {
		options += "<option value='" + reportTypes[key] + "'>"
				+ reportTypes[key] + "</option>"
	}
	$("#lstTypes").html(options);

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

function saveItem() {
	var service = {
		id : $("#txtId").val(),
		serviceName : $("#serviceName").val(),
		status : $("#lstStatus").val()
	};

	makeRequest(SAVE_SERVICE_ADMIN, POST, JSON.stringify(service),
			APPLICATION_JSON, updateScreen, null);
}

function updateScreen() {
	$("#editModal").modal("hide");
	makeRequest(GET_ALL_TICKETS_ADMIN, GET, "", "", populateTable, null);

}

function populateTable(data) {
	var dir = "";
	var list = data.list;
	var pageNo = data.currentPage;
	var numberOfPages = data.numberOfPages;
	for (var i = 0; i < list.length; i++) {
		var item = list[i];
		var type = item.type;
		var owner = item.userID
		var createDate = new Date(item.creationDate).toLocaleString();
		var status = item.status;

		dir += "<tr>";
		dir += "<th>" + type + "</th>";
		dir += "<th>" + owner + "</th>";
		dir += "<th>" + createDate + "</th>";
		dir += "<th>" + status + "</th>";
		dir += "<th><a href='#' class='glyphicon glyphicon-cog btnSetting' id='"
				+ item.id + "'>View</a></th>";
		dir += "</tr>"
	}
	$("#usersContent").html(dir);
	$(".btnSetting").click(function(e) {
		e.preventDefault();
		e.stopPropagation();
		var url = GET_TICKET_BY_ID + "?id=" + this.id;
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
	$("#txtNewComment").val("");
	$("#txtId").val(data.ticket.id)
	$("#txtType").val(data.ticket.type)
	$("#lstStatus").val(fixString(data.ticket.status));
	var comment = data.ticket.comment.replace("<MESSAGE>", "\n")
	$("#txtComment").val(comment);
	var messages = data.ticket.messages;
	var messagesDiv = "";
	for (var i = 0; i < messages.length; i++){
		messagesDiv += "<div class='panel panel-info'>";
		messagesDiv += "<div class='panel-heading'>"+messages[i].username+" - " + new Date(messages[i].date).toLocaleString() +"</div>"
		messagesDiv += "<div class='panel-body'>"+messages[i].message+ "</div>"
		messagesDiv += "</div>"

	}
	$("#commentsDiv").html(messagesDiv);
	if(!($("#editModal").data('bs.modal') || {}).isShown){
		$("#editModal").modal();
	}
	
}