/**
 * Javascript meant for accountDetails
 */
//variable that tells if is saving user account
var isSavingUser = false;
var userSaved = false;
$(function () {

    loadInfo(refreshForm)

    $("#btnUpdatePassword").click(function (e) {
        e.preventDefault();
        e.stopPropagation();

        $("#updatePasswordModal").modal("show");
    });

    $("#btnEdit").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($("#btnEdit").html() === "Edit") {
            isSavingUser = !isSavingUser;
            $("#userForm .userInput").attr("disabled", !isSavingUser);
        }
        if ($("#btnEdit").html() === "Save" && isSavingUser) {
            saveUser();
        }
        if (isSavingUser) {
            $("#btnEdit").html("Save");
            $("#primaryButtons #btnCancel").removeClass("hide");
            $("#btnUpdatePassword").removeClass("hide");
        }
    });

    $("#primaryButtons #btnCancel").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        //when user clicks cancel, save changes to edit
        isSavingUser = false;
        $("#userForm .userInput").attr("disabled", true);

        //hiding button
        $("#primaryButtons #btnCancel").addClass("hide");
        $("#btnUpdatePassword").addClass("hide");

        //change name
        $("#btnEdit").html("Edit");

        $("#errorDiv").remove();
        loadInfo(refreshForm)
    });

    $("#updatePasswordModal #btnSave").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        var password = $("#updatePasswordModal #password1").val();
        var rePassword = $("#updatePasswordModal #password2").val();
        var message = "";
        var isValid = false;

        if (password === rePassword) {
            if (password.length >= MINIMUM_PASSWORD_LENGTH && password.length <= MAXIMUM_PASSWORD_LENGTH) {
                isValid = true;
            } else {
                message += PASSWORD_ERROR + "</br>";
            }
        } else {
            message += PASSWORD_UNMATCHED + "</br>";
        }

        if (isValid) {
            var url = UPDATE_PASSWORD;
            url += "?password1=" + password + "&password2=" + rePassword;
            makeRequest(url, GET, "", "", passwordSaveComplete, null);
        } else {
            displayError(message);
        }
        $("#updatePasswordModal").modal("hide");
    });

    $("#updatePasswordModal #btnCancel").click(function (e) {
        e.preventDefault();
        e.stopPropagation();
        $("#updatePasswordModal #password1").val("");
        $("#updatePasswordModal #password2").val("");
        $("#updatePasswordModal").modal("hide");

    });

    populateProvinces();
});

function errorUpdatingUser(error) {
    userSaved = false;

    var errorMsg = error.responseJSON.message.replace(";", "</br>");
    if (errorMsg.indexOf("brij_exception") !== -1) {
        errorMsg = errorMsg.replace("brij_exception", "");
        displayError(errorMsg);
    }
}

function passwordSaveComplete() {}

function populateProvinces() {
    $("#lstProvinces").html("");
    for (var key in PROVINCES) {
        if (PROVINCES.hasOwnProperty(key)) {
            $("#lstProvinces").append("<option value='" + key + "'>" + PROVINCES[key] + "</option>")
        }
    }
}



function validUserDetails() {
    var isValid = true;
    var message = "";
    var firstname = $("#userForm #firstName").val();
    var lastname = $("#userForm #lastName").val();
    var phonenumber = $("#userForm #phoneNumber").val();
    var address = $("#userForm #address").val();
    var city = $("#userForm #city").val();
    var province = $("#userForm #lstProvinces").val();

    phonenumber = phonenumber.replace(/-/g, "");
    $("#userForm #phoneNumber").val(phonenumber);
    if (firstname.length < MINIMUM_FIRSTNAME_LENGTH || firstname.length > MAXIMUM_FIRSTNAME_LENGTH) {
        isValid = false;
        message = FIRSTNAME_ERROR + "</br>";
    }

    if (lastname.length < MINIMUM_LASTNAME_LENGTH || lastname.length > MAXIMUM_LASTNAME_LENGTH) {
        isValid = false;
        message += LASTNAME_ERROR + "</br>";
    }

    if (phonenumber.length !== PHONE_NUMBER_LENGTH) {
        isValid = false;
        message += PHONENUMBER_ERROR + "</br>";
    }

    if (!address.length > 0) {
        isValid = false;
        message += ADDRESS_ERROR + "</br>";
    }

    if (city.length < MINIMUM_CITY_LENGTH || city.length > MAXIMUM_CITY_LENGTH) {
        isValid = false;
        message += CITY_ERROR + "</br>";
    }

    if (province.length > 2) {
        isValid = false;
        message += PROVINCE_ERROR + "</br>";
    }

    if (email.length < MINIMUM_EMAIL_LENGTH || email.length > MAXIMUM_EMAIL_LENGTH) {
        isValid = false;
        message += EMAIL_ERROR + "</br>";
    }

    if (!isValid) {
        displayError(message);
    }
    return isValid;
}

function saveUser() {

    if (validUserDetails()) {
        $("#errorDiv").remove();
        var updateUser = {
            firstName: $("#userForm #firstName").val(),
            lastName: $("#userForm #lastName").val(),
            phoneNumber: $("#userForm #phoneNumber").val(),
            address: $("#userForm #address").val(),
            city: $("#userForm #city").val(),
            province: $("#userForm #lstProvinces").val(),
            email: $("#userForm #email").val()
        };

        makeRequest(UPDATE_USER, POST, JSON.stringify(updateUser), APPLICATION_JSON, userSavedSuccessful, errorUpdatingUser);
    }

}

function userSavedSuccessful() {
    userSaved = true;
    userSavedButtons();
    toast.show("Your account has been saved!")
}

function userSavedButtons() {
    if (userSaved) {
        $("#btnEdit").html("Edit");
        $("#primaryButtons #btnCancel").addClass("hide");
        $("#btnUpdatePassword").addClass("hide");
        isSavingUser = !isSavingUser;
        $("#userForm .userInput").attr("disabled", !isSavingUser);
    }
}

function displayError(message) {
    $("#errorDiv").remove("");
    $("#userForm").prepend("<div id='errorDiv' class='alert alert-danger'>" + message + "</div>");
}

function eraseForm() {
    $("#username").html("");
    $("#userForm #firstName").val("");
    $("#userForm #lastName").val("");
    $("#userForm #phoneNumber").val("");
    $("#userForm #address").val("");
    $("#userForm #city").val("");
    $("#userForm #province").val("");
    $("#userForm #email").val("");
}

function recoverStateForm() {
    $("#userForm #firstName").val(user.firstName);
    $("#userForm #lastName").val(user.lastName);
    $("#userForm #phoneNumber").val(user.phoneNumber);
    $("#userForm #address").val(user.address);
    $("#userForm #city").val(user.city);
    $("#userForm #province").val(user.province);
    $("#userForm #email").val(user.email);
}

function refreshForm(data) {
    var user = data.user;
    var province = user.province;
    if (!province) {
        province = "ON";
    }
    fillRating(data.noOfRatingsByPoster,data.avgRateByPoster, "#posterRatingDiv");
    fillRating(user.ratings.length, data.avgRateByUser, "#userRatingDiv");
    $("#username").html(user.username);
    $("#userForm #firstName").val(user.firstName);
    $("#userForm #lastName").val(user.lastName);
    $("#userForm #phoneNumber").val(user.phoneNumber);
    $("#userForm #address").val(user.address);
    $("#userForm #city").val(user.city);
    $("#userForm #lstProvinces").val(province);
    $("#userForm #email").val(user.email);
}

function loadInfo(callback) {
    makeRequest(GET_CURRENT_USER, GET, "", APPLICATION_JSON, callback, null);
}