$(function () {
    $("#error").hide();
    var resetID = $.urlParam("resetid");
    console.log(resetID);
    
    $("#btnSubmit").click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        var password1 = $("#password1").val();
        var password2 = $("#password2").val();

        if (password1 === password2) {
            var url = UPDATE_FORGOT_EMAIL + "?password1=" + password1 + "&password2=" + password2 + "&resetid=" + resetID;
            makeRequest(url, GET, "", "", updatePasswordComplete, null);
        } else {
            $("#error").show();
        }
    });

});

function updatePasswordComplete(data) {
    window.location = "";
}