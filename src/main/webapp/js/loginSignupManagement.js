/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

    var login_div = document.getElementById("login-div");
    var login_button = document.getElementById("login-button");
    var signup_div = document.getElementById("signup-div");
    var signup_button = document.getElementById("signup-button");

    var login_error_message_div = document.getElementById("loginErrorMessage-div");
    var login_error_message = document.getElementById("loginErrorMessage");

    var signup_error_message_div = document.getElementById("signupErrorMessage-div");
    var signup_error_message = document.getElementById("signupErrorMessage");


    setHeaderHrefs();

    //Attach to login button
    login_button.addEventListener("click", (e) => {
        e.preventDefault();
        var form = e.target.closest("form");

        login_error_message_div.style.display = 'none';

        if (form.checkValidity()) { //Do form check
            sendToServer(form, login_error_message_div, login_error_message, 'login');
        } else
            form.reportValidity(); //If not valid, notify
    });

    //Attach to register button
    signup_button.addEventListener("click", (e) => {
        e.preventDefault();

        var form = e.target.closest("form");

        signup_error_message_div.style.display = 'none';

        if (form.checkValidity()) { //Do form check
            sendToServer(form, signup_error_message_div, signup_error_message, 'signup');
        } else {
            form.reportValidity();
        } //If not valid, notify

    });



    function sendToServer(form, error_div, error_div_text, request_url){
        makeCall("POST", request_url, form, function(req){
            switch(req.status){ //Get status code
                case 200: //Okay
                    let data = JSON.parse(req.responseText);
                    sessionStorage.setItem('userdata', JSON.stringify(data));
                    window.location.href = "home.html";
                    break;
                case 302:
                    window.location.href = "login.html"
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    error_div.innerHTML = req.responseText;
                    error_div.style.display = 'block';
                    break;
                default: //Error
                    error_div.innerHTML = "Request reported status " + req.status;
                    error_div.style.display = 'block';
            }
      });
    }



    function setHeaderHrefs(){
        var homepage_link_logo = document.getElementById("homepage-link-logo");
        homepage_link_logo.setAttribute("href", "home.html");
    }

})();