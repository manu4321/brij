var notifPage = 0;
$(function () {
    $("#notificationDiv").html("");
    waitForGlobalNotification();
});

function waitForGlobalNotification(){
    if(typeof global_notifications !== "undefined"){
        var notifications = global_notifications;
        fillList(notifications);
    }else{
        setTimeout(waitForGlobalNotification, 250);
    }
}

function fillList(data){
    var notifications = data.notifications;
    var noOfPages = data.numberOfPages;
    notifPage = data.currentPage;
    var div = "";
    if (notifications !== undefined) {
        var addButton = notifications.length === NOTIFICATION_LIMIT && notifPage < noOfPages;
        for (var i = 0; i < notifications.length; i++) {
            var href = "";
            var classCss = "";
            if (!notifications[i].readFlag) {
                classCss = "list-group-item-danger readFlag";
            }
            var notificationId = notifications[i].id;
            if (notifications[i].type === "request") {
                href = "request.html?id=" + notifications[i].targetID;
            } else if (notifications[i].type === "conversation") {
                href = "request.html?id=" + notifications[i].targetID + "&openConvo=true";
            }
            div += "<a href='"+href+"' class='list-group-item list-group-item-action "+ classCss + "' id='notification_" + notificationId + "'  onclick='return notificationOnClick(this)'> "+ notifications[i].description +"</a>";

        }
        if(addButton){
            div += "<a href='#' class='list-group-item list-group-item-info' id='getMoreNotifications'>See more</a>";
        }
    }
    
    $("#notificationDiv").append(div);
    $("#getMoreNotifications").click(function(e){
        e.preventDefault();
        e.stopPropagation();
        var url = GET_USER_NOTIFICATION;
        url += "?pageNo=" + notifPage;
        makeRequest(url, GET, "", "", populateNotificationPage, null);
                
    })
}
function populateNotificationPage(data){
    $("#getMoreNotifications").hide();
    fillList(data);
}
