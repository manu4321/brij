var postID;
var requestID;
var isSavingRequest = false;
var conversationTimer;
var openConvo = false;
var openRate = false;
var status = "";
var ratingValue = -1;
var otherUser = "";
$(function () {
    $("#btnBack").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        window.location.href = "postings.html";

    });

    $("#btnEdit").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        isSavingRequest = !isSavingRequest;

        $("#requestForm textarea").attr("disabled", !isSavingRequest);

        if (isSavingRequest) {
            $("#btnEdit").html("Save");
            $("#btnCancel").removeClass("hide");
        } else {
            $("#btnEdit").html("Edit");
            $("#btnCancel").addClass("hide");
            updateRequest();
        }
    });

    $("#btnOpenConvo").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        requestConversation();
        conversationTimer = setInterval(requestConversation, 50000);
    });
    
    $("#btnSaveMessage").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        var url = SAVE_MESSAGE;
        url = url.replace(":id", requestID);
        var message = {
            "message": $("#txtaComment").val()
        }
        makeRequest(url, POST, JSON.stringify(message), APPLICATION_JSON, function(data){
            $("#txtaComment").val("");
            requestConversation();
        }, null);
    })
    $("#btnAccept").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        questionModal("Do you wish to change the status to 'in progress'?",function(){
            changeStatus("in_progress"); 
        });
    });
    
    $("#btnDeny").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        questionModal("Do you wish to deny this request?", function(){
            changeStatus("denied"); 
        });
    });
    
    $("#btnComplete").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        questionModal("Do you wish to complete this request?", function(){
            changeStatus("complete");
            setupRating("Rate " + otherUser, function(){
                ratingValue = ratingValue > 5? 5 : ratingValue < 1? 0: ratingValue;
                var rating = {
                    value: ratingValue
                }
                if(ratingValue > 0){
                    makeRequest(RATE_USER + "?username="+otherUser, POST, JSON.stringify(rating), APPLICATION_JSON, function(e){
                        $("#formModal").modal("hide");
                    }, null);  
                }else{
                   displayError("#formModal .modal-body", "You need to select a rating")
                }
            });
        });
    });
    $("#btnClose").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        questionModal("Do you wish to Cancel this request?",function(){
            changeStatus("cancelled"); 
        });
    });
    
    $( window ).resize(function() {
        $("#chatRoomModal #modalBody").css({"height": $(window).height() - 200});
    });
    getRequest($.urlParam("id"));
    openConvo = $.urlParam("openConvo");
    openRate = $.urlParam("openRate");
    openRate = openRate === "true";
    $(document).on('onScrollRefresh', function(e, opts) {
        openConvo = false;
        getRequest(requestID);
  });
});

function questionModal(message, positiveCallback){
    $("#statusChangeModal #modalBody").html(message);
    $('#btnChangeStatus').unbind('click');
    $("#btnChangeStatus").click(function(e){
            e.preventDefault();
            e.stopPropagation();
            positiveCallback();
            $("#statusChangeModal").modal("hide");
        });


    $("#statusChangeModal").modal();
}

function changeStatus(newStatus){
    var url = CHANGE_REQUEST_STATUS + "?id= " + requestID + "&status=" + newStatus;
    makeRequest(url, POST,"", APPLICATION_JSON, function(data){
        var url = GET_REQUEST_BY_ID;
        url = url.replace(":id", requestID);
        makeRequest(url, GET, "", "", populateRequest, null);
    }, null);
}

function requestConversation(){
    var url = GET_CONVERSATION_BY_REQUEST;
    url = url.replace(":id", requestID);
    makeRequest(url, GET, "", "", openConversation, null);
}
function openConversation(data){
    
    $('#chatRoomModal').on('shown.bs.modal', function() {
        $('#chatRoomModal #modalBody').scrollTop($('#chatRoomModal #modalBody')[0].scrollHeight);
    })
    var conversation = data.conversation;
    var currentUser = data.currentUser;
    
    
    var body = createMessagesDiv(conversation.messages, currentUser);
    $("#chatRoomModal #modalTitle").html(conversation.title);
    $("#chatRoomModal #modalBody").css({"height": $(window).height() - 200});
    $("#chatRoomModal #modalBody").html(body);
    $("#chatRoomModal").modal();

    $('#chatRoomModal').on('hidden.bs.modal', function () {
        clearInterval(conversationTimer);    
    })
    

}



function createMessagesDiv(messages, currentUser){
    var div = "<div id='messageDiv' class='clearfix'>";
    for(var i = 0; i < messages.length; i++){
        var username = messages[i].username;
        var message = messages[i].message;
        var date = messages[i].date;
        var correspondingClass = "";
        var addressUser = "";
        if(username === currentUser){
            correspondingClass = "bg-success pull-right text-right"
            addressUser = "You:";
        }else{
            correspondingClass = "pull-sm-left"
            addressUser = username + ":";
        }
        
        div += "<div class='individualMessage "+ correspondingClass+"'> <span class='chatUser'>"+addressUser+"</span> <p class='message'>"+message+"<br><span class='date'>"+new Date(date).toLocaleString()+"</span></p> </div>";
        //add a clearfix div to put an space between both conversations
        div += "<div class='clearfix'></div>"
    }
    
    div += "</div>";
    return div;
    
}

function updateRequest() {
    var updateRequest = {
        notes: $("#requestForm #notes").val(),
        postID: postID
    };
    makeRequest(CREATE_REQUEST, POST, JSON.stringify(updateRequest), APPLICATION_JSON, null, null);
};

function requestService() {
    window.location.href = "createRequest.html?id=" + id;
}

function getRequest(id) {
    //Need to get based on id
    var url = GET_REQUEST_BY_ID;
    url = url.replace(":id", id);
    makeRequest(url, GET, "", "", populateRequest, null);
}
function populateUserInfo(data, noOfRatingsByPoster,ratingForPoster, ratingForUser) {
    $("#username").html(data.username);
    fillRating(noOfRatingsByPoster,ratingForPoster, "#posterRatingDiv");
    fillRating(data.ratings.length,ratingForUser, "#userRatingDiv");
    $("#userForm #firstName").val(data.firstName);
    $("#userForm #lastName").val(data.lastName);
    $("#userForm #phoneNumber").val(data.phoneNumber);
    $("#userForm #address").val(data.address);
    $("#userForm #city").val(data.city);
    $("#userForm #province").val(data.province);
    $("#userForm #email").val(data.email);
    $("#userForm #firstName").html();
}


function changeStatusInPage(status){
    var message = status.replace("_", " ");
    var classToUse = "label pull-right "; 
    switch(status){
        case "pending":
          classToUse += "label-warning";
          break;
        case "in_progress":
            classToUse += "label-info";
            break;
        case "completed":
            classToUse += "label-success";
      default:
            classToUse += "label-default";
            break;
    }
    $("#status").removeClass();
    $("#status").html(message);
    $("#status").addClass(classToUse);
}

function populateRequest(data) {
    postID = data.posting.id;
    requestID = data.request.requestID;
    var aboutUser = "About Requester";
    var requestType = "You have requested this service";
    status = data.request.status;
    changeStatusInPage(status);
    if(data.isOwner){
        aboutUser = "About Poster";
        otherUser = data.posting.user.username;
        populateUserInfo(data.posting.user,data.noOfRatingsByPoster,data.avgRateByPoster, data.avgRateByUser);
        $(".showOwner").removeClass("hide");
        if(status === "complete" || status === "cancelled" || status === "denied"){
            $(".showOwner").addClass("hide");
        }
    }else{
        aboutUser = "About Requester"
        otherUser = data.requester.username;
        if(status === "in_progress"){
            $(".inProgress").removeClass("hide");
            $(".inPending").addClass("hide");
        }else if(status === "denied"){
            $(".inPending").addClass("hide");
            $(".inProgress").addClass("hide");
        }else if(status === "pending"){
            $(".inPending").removeClass("hide");
            $(".inProgress").addClass("hide");
        }else if(status === "complete"){
            $(".inProgress").addClass("hide");
        } 
            
        
        populateUserInfo(data.requester ,data.noOfRatingsByPoster,data.avgRateByPoster, data.avgRateByUser);
        if (data.posting.isPost) {
            requestType = data.requester.username + " has requested this service";
        } else {
            requestType = data.requester.username + " has requested to perform this service";
        }
    }

    $("#btnSeeUser").html(aboutUser);
    $("#requestType").html(requestType);
    $("#postName").html(data.posting.title);

    $("#service").val(data.serviceName);
    $("#description").val(data.posting.details);
    $("#notes").val(data.request.notes);
    if(openConvo){
        openConvo = false;
        requestConversation();
        conversationTimer = setInterval(requestConversation, 50000);
    }
    if(openRate){

        setupRating("Rate post",function(){
            ratingValue = ratingValue > 5? 5 : ratingValue < 1? 0: ratingValue;
            var rating = {
                value: ratingValue
            }
            if(ratingValue > 0){
                makeRequest(RATE_POST + "?id="+postID, POST, JSON.stringify(rating), APPLICATION_JSON, function(e){
                    $("#formModal").modal("hide");
                }, null);  
            }else{
               displayError("#formModal .modal-body", "You need to select a rating")
            }
        });

    }

}

function setupRating(title, clickCallback){
        openRate = false;
        $("#formModal #title").html(title);
        $("#btnSaveForm").html("Rate");
        $("#btnSaveForm").click(function(e){
            e.preventDefault();
            e.stopPropagation();
            clickCallback();
        });
        
        setupStar();
        $("#formModal").modal();
}

function setupStar(){
        var starFormBody = getStarForm();
        $("#formModal .modal-body").html(starFormBody);
        
        $(".star").click(function(e){
            e.preventDefault();
            e.stopPropagation();
            var number = this.id.split("_")[1];
            number = number > 5? 5: number;
            $(".star").removeClass("glyphicon-star");
            for(var i =1; i <= 5; i++){
                if(number >= i){
                    $("#star_" + i).removeClass("glyphicon-star-empty");
                    $("#star_" + i).addClass("glyphicon-star"); 
                }else{
                    $("#star_" + i).addClass("glyphicon-star-empty");
                }

            }
            ratingValue = number;
        });
        $(".star").hover(function(e){
            e.preventDefault();
            e.stopPropagation();
                var number = this.id.split("_")[1];
                number = number > 5? 5: number;
                $(".star").removeClass("glyphicon-star");
                for(var i =1; i <= 5; i++){
                    if(number >= i){
                        $("#star_" + i).removeClass("glyphicon-star-empty");
                        $("#star_" + i).addClass("glyphicon-star"); 
                    }else{
                        $("#star_" + i).addClass("glyphicon-star-empty");
                    }
                }
            

        }, function(){
            if(ratingValue === -1){
                $(".star").removeClass("glyphicon-star");
                $(".star").addClass("glyphicon-star-empty"); 
                
            }else{
                for(var i =1; i <= 5; i++){
                    if(ratingValue >= i){
                        $("#star_" + i).removeClass("glyphicon-star-empty");
                        $("#star_" + i).addClass("glyphicon-star"); 
                    }else{
                        $("#star_" + i).addClass("glyphicon-star-empty");
                    }
                }
            }
        })
}


function getStarForm(){
    var div = "<div class='text-center starDiv'>";
    for(var i =1 ; i <= 5; i++){
        div += "<span class='glyphicon glyphicon-star-empty star' id='star_"+i+"'></span>";
    }
    div += "</div>"
    return div;
}