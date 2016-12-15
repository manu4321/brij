$(function () {
    $("#btnSave").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        savePost();
    });

    $("#btnCancel").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        window.location.href = "postings.html";
    });

    $("#btnSaveService").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        saveService();
    });

    $("#serviceDropdown").change(function () {
        if ($("#serviceDropdown").val() === "create") {
            createService();
        }
    });

    makeRequest(GET_ALL_SERVICES, GET, "", "", populateServices, null);
});

function savePost() {
    var newPost = {
        title: $("#postForm #title").val(),
        servID: $("#postForm #serviceDropdown").val(),
        details: $("#postForm #description").val(),
        isPost: $("input:radio[name='rdIsPost']:checked").val()

    };

    makeRequest(SAVE_POST, POST, JSON.stringify(newPost), APPLICATION_JSON, savePostingComplete, savePostErrorHandler);
}
function savePostErrorHandler(error){
    var errorMsg = error.responseJSON.message.replace(";", "</br>");
    if(errorMsg.indexOf("brij_exception") !== -1){

        errorMsg = errorMsg.replace("brij_exception", "");
        displayError($("#postForm"), errorMsg);
    }

}
function savePostingComplete() {
    window.location.href = "postings.html";
}

function populateServices(data) {

    var options;

    for (var i = 0; i < data.length; i++) {
        options += "<option value='" + data[i].id + "'>" + data[i].serviceName + "</option>"
    }

    $("#serviceDropdown").html(options);
    options = "<option value='create'>Create New Service</option>"
    $("#serviceDropdown").prepend(options);
}

function createService() {
    $("#myModal").modal('show');
    //window.location.href = "createService.html";
}

function saveService() {
    var newService = {
        serviceName: $("#serviceName").val()
    };

    makeRequest(CREATE_SERVICE, POST, JSON.stringify(newService), APPLICATION_JSON, saveServiceComplete, null);
}

function saveServiceComplete(data) {
    //add successful check
    $("#myModal").modal('hide');

    $("#serviceDropdown").html("");
    
    makeRequest(GET_ALL_SERVICES, GET, "", "", populateServices, null);
}