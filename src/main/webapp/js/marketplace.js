/**
 * Marketplace
 */


(function(){

    //Vars
    var searchForm, productsCatalogue, userInfo, shoppingCart, orders;
    
    var pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        pageOrchestrator.start(); // initialize the components
        pageOrchestrator.showHomePage(); // display initial content
        setHeaderHrefs();
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
                document.getElementById("shopping-cart-div"),
                document.getElementById("shopping-cart-div-message")
            )

            productsCatalogue = new ProductsCatalogue(
                document.getElementById("products-table-div"),
                document.getElementById("products-table")
            )

            orders = new OrdersList(
                document.getElementById("orders-div"),
                document.getElementById("orders-table")
            )
        };

        this.showHomePage = function(){
            userInfo.show();
            productsCatalogue.showHomeProducts();
        };


        //deve nascondermi tutti i div eccetto quelo del carrello
        this.showShoppingCart = function () {

            //nascondi tutto il resto
            productsCatalogue.hide();

            //mostra carrello
            shoppingCart.show();

        }

        //deve nascondermi tutti i div eccetto quelo degli ordini
        this.showOrders = function () {

            //nascondi tutto il resto
            productsCatalogue.hide();

            //mostra carrello
            orders.show();

        }
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
            e.preventDefault()

            self.search_form_error.style.display = 'none';

            const paramOneValue = e.currentTarget.closest('form')['keyword'].value;
            const params = {
                keyword: paramOneValue
            }
            const endpoint = createUrlWithParams("GetSearchProducts", params);

            makeCall("GET", endpoint, null, (req) =>{
                switch(req.status){
                    case 200:
                        let products = JSON.parse(req.responseText);
                        productsCatalogue.updateSearchProducts(products);
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

        });

        this.show = function() {
            //TODO se servirà a qualcosa
        };

        this.update = function() {
            //TODO se servirà a qualcosa
        };
    }


    function ShoppingCart(
        _shopping_cart_div,
        _shopping_cart_div_message) {

        this.shopping_cart_div = _shopping_cart_div;
        this.shopping_cart_div_message = _shopping_cart_div_message;

        var self = this; //Necessary only for in-function helpers (makeCall)

        this.show = function() {
            self.shopping_cart_div = sessionStorage.getItem(shoppingCart);

            self.shopping_cart_div.style.display = 'block';
        }

        this.hide = function () {
            //TODO da fare

            self.shopping_cart_div.style.display = 'block';
        }


        this.update = function () {

            //aggiungo il prodotto alla sessione

            pageOrchestrator.showShoppingCart();
        }

        //TODO da completare per farlo scomparire
        this.showFailure = function (_message) {

            self.shopping_cart_div.style.display = 'block';
            self.shopping_cart_div_message.innerHTML = _message;
            self.shopping_cart_div_message.style.display = 'block';

        }
    }


    // il products catalogue riceve come primo argomento il catalogo di prodotti da mostrare nella home
    // come secondo argomento invece sono i risultati ottenuti dalla ricerca
    function ProductsCatalogue(
        products_table_div,
        products_table) {


        this.products_table_div = products_table_div;
        this.products_table = products_table;

        var self = this;

        this.showHomeProducts = function(){
            //Request and update with the results
            makeCall("GET", 'GetHomeProducts', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        let products = JSON.parse(req.responseText);
                        self.updateHomeProducts(products);
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.updateHomeProducts(null, req.responseText);
                        break;
                    default: //Error
                        self.updateHomeProducts(null, "Request reported status " + req.status);
                        break;
                }
            });
        };

        this.updateHomeProducts = function(products, _error) {

            //remove all tbody tags in products_table
            while (this.products_table.tBodies.length > 0) {
                this.products_table.removeChild(this.products_table.tBodies[0]);
            }



            //se errore allora mostra l'errore a schermo
            if (products == null) {
                this.products_table_div.innerHTML = _error;
                return;
            }

            let prodsMap = new Map();
            let table_body = document.createElement("tbody");

            for (const prodId in products.supplierProductMap) {
                prodsMap.set(prodId, products.supplierProductMap[prodId]);
            }


            var self = this;
            let searchResultNum = 1;
            let resultsNum = prodsMap.size;

            prodsMap.forEach( (function(prods) { // self visible here, not this
                let currentProd = prods[0];

                table_body.appendChild(createProductsRow(currentProd, shoppingCart, searchResultNum, resultsNum));
                searchResultNum++;
            }));

            self.products_table.appendChild(table_body);
            self.products_table_div.style.display = 'block';
        };


        this.updateSearchProducts = function(products) {

            //remove all tbody tags in products_table
            while (this.products_table.tBodies.length > 0) {
                this.products_table.removeChild(this.products_table.tBodies[0]);
            }

            let prodsMap = new Map();
            let table_body;

            for (const prodId in products.supplierProductMap) {
                prodsMap.set(prodId, products.supplierProductMap[prodId]);
            }

            var self = this;
            let firstIteration = true;
            let searchResultNum = 1;
            let resultsNum = prodsMap.size;

            // self visible here, not this
            //iterazione per ogni prodId
            prodsMap.forEach( (function(prods) {

                let tr, td, table;
                //creo un nuovo body per ogni proId
                table_body = document.createElement("tbody");
                //creo un nuovo body per la tabella interna con gli altri venditori
                let table_body_per_id = document.createElement("tbody");

                for (const supplierProduct of prods) {

                    if (firstIteration) {

                        //inserisco la riga col prodotto al prezzo più basso
                        table_body.appendChild(createProductsRow(supplierProduct, shoppingCart, searchResultNum, resultsNum));

                        //e "preparo" la tabella più interna con gli altri venditori
                        tr = document.createElement("tr");

                        td = document.createElement("td");
                        td.setAttribute("colspan", "7")

                        table = createSearchProductsTable();

                        table_body_per_id.appendChild(createSupplierProductsRow(supplierProduct, shoppingCart));

                        firstIteration = false;

                    } else {

                        table_body_per_id.appendChild(createSupplierProductsRow(supplierProduct, shoppingCart));



                        //prendere la riga precedente a questa,
                        //prendere il td con il nome,
                        //prendere <a> e metterci dentro l'event listener
                        //che deve andare a mostrare la tabella sotto con gli altri venditori
                    }

                }

                //assegno id ad ogni risultato della ricerca
                let tbID = 'tb-search-result-num-' + searchResultNum;
                table_body.setAttribute("id", tbID);

                //assegno id alle tr interne
                let trID = 'tr-search-result-num-' + searchResultNum;
                tr.setAttribute("id", trID);
                tr.style.display = 'none';

                table.appendChild(table_body_per_id);
                td.appendChild(table);
                tr.appendChild(td);
                table_body.appendChild(tr);
                self.products_table.appendChild(table_body);
                firstIteration = true;
                searchResultNum++;

            }));


            self.products_table_div.style.display = 'block';

        };

        this.hide = function () {
            //FIXME
            this.products_table_div.style.display = 'none';
        };

    }

    function OrdersList(
        _orders_div,
        _orders_table) {

        this.orders_div = _orders_div;
        this.orders_table = _orders_table;

        var self = this;

        this.show = function(){
            //Request and update with the results
            makeCall("GET", 'GetOrders', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        let ordersFromReq = JSON.parse(req.responseText);
                        self.updateOrdersList(ordersFromReq);
                        self.orders_div.display.style = "block";
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.updateOrdersList(null, req.responseText);
                        break;
                    default: //Error
                        self.updateOrdersList(null, "Request reported status " + req.status);
                        break;
                }
            });
        };

        this.updateOrdersList = function (orders) {

            /*
            //TODO prima va popolato, poi in seguito vedremo come svuotarlo
            //remove all tables in orders-div
            while (this.products_table.tBodies.length > 0) {
                this.products_table.removeChild(this.products_table.tBodies[0]);
            }

             */

            let ordersMap = new Map();
            let table_body;

            for (const prodId in products.supplierProductMap) {
                ordersMap.set(prodId, products.supplierProductMap[prodId]);
            }

            var self = this;
            let firstIteration = true;
            let searchResultNum = 1;
            let resultsNum = ordersMap.size;

            // self visible here, not this
            //iterazione per ogni prodId
            ordersMap.forEach( (function(prods) {

                //riempi tabella ordini
            }));

        }
    }





    function setHeaderHrefs(){
        let homepage_link_logo = document.getElementById("homepage-link-logo");
        homepage_link_logo.setAttribute("href", "GoToHome");

        let home_link = document.getElementById("home-link");
        home_link.setAttribute("href", "home.html");

        let shopping_cart_link = document.getElementById("shopping-cart-link");
        shopping_cart_link.onclick = pageOrchestrator.showShoppingCart;
        //shopping_cart_link.setAttribute("href", "GoToShoppingCart")

        let orders_link = document.getElementById("orders-link");
        orders_link.onclick = pageOrchestrator.showOrders;
        //orders_link.setAttribute("href", "GoToOrders");

        let logout_link = document.getElementById("logout-link");
        logout_link.setAttribute("href", "logout")

        logout_link.addEventListener("click", e => {
            sessionStorage.clear();
        });


    }

})();