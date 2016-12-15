/**
 *  Javascript meant for login
 */

$(function () {
    $("#error").hide();
    checkIfOnline(checkIfOnlineCallback, false);
    $("#btnSignIn").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var username = $("#loginForm #username").val();
        var password = $("#loginForm #password").val();
        if (!(username === "" && password === "")) {
            var url = LOGIN;
            url = url.replace(":username", username).replace(":password", password);
            makeRequest(url, POST, "", "", function (data) {
                //if online go to the homePage as you have permission
                checkIfOnline(checkIfOnlineCallback, false);

            }, error);
        } else {
            error();
        }
    });

    $("#btnRegistration").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#registerModal").modal('show');

    });

    $("#btnForgotPassword").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#forgotPasswordModal").modal('show');

    });
    
    $("#btnNow").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        completeAccount("Now");
    });

    $("#btnLater").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        completeAccount("Later");
    });

    $("#btnSubmit").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var url = FORGOT_EMAIL + "?email=" + $("#forgotEmail").val();
        makeRequest(url, GET, "", "", forgotEmailSent, null);
    });

    $("#btnRegister").click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        if (validUser()) {
            var newUser = {
                username: $("#registerForm #username").val(),
                password: $("#registerForm #password").val(),
                email: $("#registerForm #email").val()
            };

            makeRequest(REGISTER_USER, POST, JSON.stringify(newUser), APPLICATION_JSON, saveUserComplete, errorSavingUser);
        }
    });

});

function checkIfOnlineCallback(data) {
    if (data) {
        goToHome(HOME);
    }
}

function validUser() {
    var isValid = true;
    var message = "";
    var username = $("#registerForm #username").val();
    var password = $("#registerForm #password").val();
    var rePassword = $("#registerForm #confirmPassword").val();
    var email = $("#registerForm #email").val();

    if (username.length < MINIMUM_USERNAME_LENGTH || username.length > MAXIMUM_USERNAME_LENGTH) {
        isValid = false;
        message += USERNAME_ERROR + "</br>";
    }

    if (password.length < MINIMUM_PASSWORD_LENGTH || password.length > MAXIMUM_PASSWORD_LENGTH) {
        isValid = false;
        message += PASSWORD_ERROR + "</br>";
    }

    if (password !== rePassword) {
        isValid = false;
        message += PASSWORD_UNMATCHED + "</br>";
    }

    if (email.length < MINIMUM_EMAIL_LENGTH || email.length > MAXIMUM_EMAIL_LENGTH) {
        isValid = false;
        message += EMAIL_ERROR + "</br>";
    }

    if (!isValid) {
        displayErrorInModal(message);
    }
    return isValid;
}

function goToHome(where) {
    window.location = where;
}

function completeAccountDetails(data) {
    if (data) {
        goToHome(ACCOUNT_DETAILS_PAGE);
    }
}

function saveUserComplete() {
    $("#registerModal").modal('hide');
    $("#completeAccountModal").modal('show');
}

function displayErrorInModal(message) {
    $("#errorDiv").remove("");
    $("#registerModal #registerForm").prepend("<div id='errorDiv' class='alert alert-danger'>" + message + "</div>");
}
function errorSavingUser(error) {
    var errorMsg = error.responseJSON.message.replace(";", "</br>");
    if(errorMsg.indexOf("brij_exception") !== -1){
        errorMsg = errorMsg.replace("brij_exception", "");
        displayErrorInModal(errorMsg);
    }
}

function completeAccount(when) {
    var username = $("#registerForm #username").val();
    var password = $("#registerForm #password").val();
    var callbackLocation = checkIfOnlineCallback;

    var url = LOGIN;
    url = url.replace(":username", username).replace(":password", password);

    if (when === "Now") {
        callbackLocation = completeAccountDetails;
    }

    makeRequest(url, POST, "", "", function (data) {
        //if online go to the homePage as you have permission
        checkIfOnline(callbackLocation, false);
    }, defaultError);
}

function error(e) {
    $("#error").show();
}

function forgotEmailSent() {
    $("#forgotPasswordModal").modal('hide');
}