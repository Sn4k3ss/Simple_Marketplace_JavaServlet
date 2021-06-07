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

    //this is the syntax needed to take the value from every <input> tag in the 'form' element with "name=password"
    //var password_input = signup_button.closest("form").querySelector('input[name="password"]');

    //Attach to login button
    login_button.addEventListener("click", (e) => {
      var form = e.target.closest("form");

      login_error_message_div.style.display = 'none';
      if (form.checkValidity()) { //Do form check
        sendToServer(form, login_error_message_div, login_error_message, 'login');
      }else
        form.reportValidity(); //If not valid, notify
    });

    //Attach to register button
    signup_button.addEventListener("click", (e) => {
      var form = e.target.closest("form");
      signup_error_message_div.style.display = 'none';
      if (form.checkValidity()) { //Do form check
        sendToServer(form, signup_error_message_div, signup_error_message, 'signup');
      }else
        form.reportValidity(); //If not valid, notify
    });



    function sendToServer(form, error_div, error_div_text, request_url){
        makeCall("POST", request_url, form, function(req){
            switch(req.status){ //Get status code
                case 200: //Okay
                    var data = JSON.parse(req.responseText);
                    sessionStorage.setItem('id', data.id);
                    sessionStorage.setItem('name', data.name);
                    window.location.href = "home.html";
                    break;
                case 302:
                    window.location.href = "login.html"
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    error_div.textContent = req.responseText;
                    error_div.style.display = 'block';
                    break;
                default: //Error
                    error_div.textContent = "Request reported status " + req.status;
                    error_div.style.display = 'block';
            }
      });
    }



    function setHeaderHrefs(){
        var homepage_link_logo = document.getElementById("homepage-link-logo");
        homepage_link_logo.setAttribute("href", "GoToHome");
    }

})();