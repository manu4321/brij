//hide this information
var SERVER_URL = "";
var LOGIN = "/login?username=:username&password=:password";
var LOGOUT = "/logout";
var HEARTBEAT = "/heartbeat";
var GET_CURRENT_USER = "/user/current";
var UPDATE_USER = "/user/save";
var SAVE_POST = "/posting/save";
var GET_POSTS = "/posting/findAll";
var GET_POST_BY_ID = "/posting/findById?id=:id";
var GET_MY_POSTS = "/posting/findByUser";
var GET_ALL_SERVICES = "/service/findAll";
var UPDATE_NOTIFICATION = "/notification/edit";
var GET_USER_NOTIFICATION = "/notification/findByUser";
var CREATE_REQUEST = "/request/save";
var GET_REQUEST_BY_ID = "/request/findById?id=:id";
var CREATE_SERVICE = "/service/save";
var GET_MY_REQUESTS = "/request/findByRequester";
var REGISTER_USER = "/user/register";
var GET_CONVERSATION_BY_REQUEST = "/conversation/getByRequest?id=:id";
var SAVE_MESSAGE = "/conversation/saveMessage?id=:id"
var GET_TICKET_BY_ID = "/ticket/findById";
var GET_TICKET_BY_TYPE = "/ticket/byType";
var GET_TICKET_USER = "/ticket/findByUser";
var UPDATE_STATUS_TICKET = "/ticket/updateStatus";
var SAVE_TICKET_MESSAGE = "/ticket/saveMessage";
var SAVE_TICKET = "/ticket/save";
var UPDATE_FORGOT_EMAIL = "/user/updateForgotPassword";
var UPDATE_PASSWORD = "/user/updatepassword";

//ADMIN
var GET_ALL_USERS = "/admin/user/findAll"
var GET_USER = "/admin/user/current";
var GET_LIKE_USER = "/admin/user/like";
var UPDATE_USER_BY_NAME = "/admin/user/save";
var GET_ALL_POSTS = "/admin/posting/findAll";
var SAVE_POST_ADMIN = "/admin/posting/save";
var GET_POST_BY_ID_ADMIN = "/admin/posting/findById";
var GET_LIKE_POSTS = "/admin/posting/like";
var GET_ALL_SERVICES_ADMIN = "/admin/service/findAll";
var GET_SERVICE_BY_ID_ADMIN = "/admin/service/findById";
var GET_SERVICE_LIKE_TITLE_ADMIN = "/admin/service/findLikeTitle";
var SAVE_SERVICE_ADMIN = "/admin/service/save";
var GET_ALL_TICKETS_ADMIN = "/admin/ticket/findAll";
var GET_TICKETS_BY_TYPE_ADMIN = "/admin/ticket/byType";
var GET_ALL_TICKET_ADMIN = "/admin/ticket/findAll";
var GET_TICKET_BY_TYPE_ADMIN = "/admin/ticket/byType";
var GET_TICKET_USER_ADMIN = "/admin/ticket/findByUser";
var SAVE_TICKET_ADMIN = "/admin/ticket/save";


//PORTAL
var HOME = SERVER_URL + "/homePage";
var ADMIN_USER_PAGE = SERVER_URL + "/admin/userPage";
var ADMIN_POST_PAGE = SERVER_URL + "/admin/postPage";
var ADMIN_SERVICES_PAGE = SERVER_URL + "/admin/servicePage";
var ADMIN_TICKET_PAGE = SERVER_URL + "/admin/ticketPage";
var ADMIN_REPORT_PAGE = SERVER_URL + "/admin/reportPage";
var WEB_PORTAL_PAGE = SERVER_URL + "/";
var ACCOUNT_DETAILS_PAGE = SERVER_URL + "/userPortal/accountDetail";
var USER_PORTAL_PAGE = SERVER_URL + "/userPortal/home";
var USER_TICKET_PAGE = SERVER_URL + "/userPortal/ticketPage";
$(function(){
	 SERVER_URL =  "http://" + $(location).attr('host');
	 HOME = SERVER_URL + "/homePage";
	 ADMIN_USER_PAGE = SERVER_URL + "/admin/userPage";
	 ADMIN_POST_PAGE = SERVER_URL + "/admin/postPage";
	 ADMIN_SERVICES_PAGE = SERVER_URL + "/admin/servicePage";
	 ADMIN_TICKET_PAGE = SERVER_URL + "/admin/ticketPage";
	 ADMIN_REPORT_PAGE = SERVER_URL + "/admin/reportPage";

})
//Request Types
var GET = "GET";
var POST = "POST";
var PUT = "PUT";
var DELETE = "DELETE";



function get_hostname(url) {
    var m = url.match(/^http:\/\/[^/]+/);
    return m ? m[0] : null;
}


var APPLICATION_JSON = "application/json; charset=utf-8";

function logout(callback, errorCallBack){
	makeRequest(LOGOUT, POST, "", "", callback, errorCallBack);
}
function initializeUser() {
    var user = {};
    user.firstName = "";
    user.lastName = "";
    user.phoneNumber = "";
    user.address = "";
    user.city = "";
    user.province = "";
    user.email = "";
    return user;
}

/*
 *	Request functions
 */
function makeRequest(url, type, data, dataType, successCallBack, errorCallBack) {
    url = SERVER_URL + url;
    console.log(successCallBack);
    $.ajaxSetup({
       beforeSend: function(){
           loading.show("loading");
       },
        complete: function(){
            loading.end();
        }
    });
    $.ajax({
        type: type,
        contentType: dataType,
        headers: {
            "X-Requested-With": "XMLHttpRequest"
        },
        url: url,
        data: data,
        timeout: 600000,
        success: successCallBack,
        error: errorCallBack
    });
}

function defaultError(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) {
    case 401:
        if (window.location.href.indexOf("index.html") == -1) {
            window.location = "/index.html";
        }
    default:
        console.log(errorThrown);
    }
}

function checkIfOnline(ifOnline, useDefaultError) {
    var errorCallBack = null;
    if (useDefaultError) {
        errorCallBack = defaultError;
    }
    makeRequest(HEARTBEAT, GET, "", "", ifOnline, errorCallBack);
}
/*
 *	Request functions ENDS
 */