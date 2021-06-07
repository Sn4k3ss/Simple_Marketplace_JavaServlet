/**
 * Marketplace
 */
(function(){

    //Vars
    var searchForm, productsCatalogue, userInfo, shoppingCart, orders;
    
    var pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        setHeaderHrefs();
        pageOrchestrator.start(); // initialize the components
        pageOrchestrator.refresh(); // display initial content
    });

    /**
     * Notes:
     * - excludeContacts parameter in PageOrchestrator.refresh(..) is used to avoid requesting
     *   address book to server when it's not needed (i.e. always but at first loading).
     *   Others refreshes will be managed by AddressBook object itself.
     */
    function PageOrchestrator(){
        this.start = function() {
            //Init components
            userInfo = new UserInfo(
                sessionStorage.getItem('userName'),
                sessionStorage.getItem('userId'),
                [document.getElementById("title-username")]
            );

            searchForm = new SearchForm(
                document.getElementById("search-form-div"),
                document.getElementById("search-form-div-button"),
                document.getElementById("search-form-error"),
            )

            shoppingCart = new ShoppingCart(
                document.getElementById("shopping-cart-div")
            )

            productsCatalogue = new ProductsCatalogue(
                document.getElementById("home-products-table-div"),
                document.getElementById("search-products-table-div")
            )

            orders = new OrdersList(
                document.getElementById("orders-div")
            )
        };


        this.refresh = function(excludeContacts){
            //Refresh view
            userInfo.show();
            productsCatalogue.home_products_table_div.show();
        };
    }

    function UserInfo(
        _userName,
        _userId,
        nameElements){

        this.name = _userName;
        this.id = _userId;



        this.show = function(){
            nameElements.forEach(element => {
                element.textContent = this.name;
            });
        }
    }

    function SearchForm(
        _search_form_div,
        _search_form_button,
        _search_form_error) {

        this.search_form_div = _search_form_div;
        this.search_form_button = _search_form_button;
        this.search_form_error = _search_form_error;

        var self = this; //Necessary only for in-function helpers (makeCall)

        //search button
        this.search_form_button.addEventListener('click', (e) =>{

            self.search_form_error.style.display = 'none';

            var searchForm = e.target.closest("form");
            if(searchForm.checkValidity()){
                makeCall("POST", 'search/products', searchForm, (req) =>{
                    switch(req.status){
                        case 200: //ok
                            var click = new Event("click");
                            //self.create_account_button.dispatchEvent(click);
                            self.show();
                            break;
                        case 400: // bad request
                        case 401: // unauthorized
                        case 500: // server error
                            self.search_form_error.textContent = req.responseText;
                            self.search_form_error.style.display = 'block';
                            break;
                        default: //Error
                            self.search_form_error.textContent = "Request reported status " + req.status;
                            self.search_form_error.style.display = 'block';
                    }
                });
            }else{
                searchForm.reportValidity();
            }

        });

        this.show = function() {

            //To update products table
            self.update();

        };

        this.update = function() {

            //To update products table

        };
    }


    function ShoppingCart(
        _shopping_cart_div) {

        this.shopping_cart = _shopping_cart_div;

        var self = this; //Necessary only for in-function helpers (makeCall)

        this.show = function() {
            self.shopping_cart = sessionStorage.getItem(shoppingCart);

            shoppingCart.style.display = 'block';
        }

    }


    function ProductsCatalogue(
        _home_products_table_div,
        _search_products_table_div) {

        this.home_products_table_div = _home_products_table_div;
        this.search_products_table_div = _search_products_table_div;

        var self = this;

        this.home_products_table_div.show = function(){
            //Request and update with the results
            makeCall("GET", 'GetHomeProducts', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        var products = JSON.parse(req.responseText);
                        self.update(products);

                        /*

                        if(!isNaN(self.currentSelectedId)){
                            var open_account_button = document.querySelector("a[data_accountid='" + self.currentSelectedId + "']");
                            var click = new Event("click");
                            if(open_account_button)
                                open_account_button.dispatchEvent(click);
                        }

                        */
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.update(null, req.responseText);
                        break;
                    default: //Error
                        self.update(null, "Request reported status " + req.status);
                        break;
                }
            });
        };

        this.update = function(products, _error) {

            //To update products table

        };
    }

    function OrdersList(_orders_div) {

        this.orders_div = _orders_div;


    }





    function setHeaderHrefs(){
        let homepage_link_logo = document.getElementById("homepage-link-logo");
        homepage_link_logo.setAttribute("href", "GoToHome");

        let home_link = document.getElementById("home-link");
        home_link.setAttribute("href", "GoToHome");

        let shopping_cart_link = document.getElementById("shopping-cart-link");
        shopping_cart_link.setAttribute("hred", "GoToShoppingCart")

        let orders_link = document.getElementById("orders-link");
        orders_link.setAttribute("href", "GoToOrders");

        let logout_link = document.getElementById("logout-link");
        logout_link.setAttribute("href", "logout")

        logout_link.addEventListener("click", e => {
            sessionStorage.clear();
        });


    }

})();