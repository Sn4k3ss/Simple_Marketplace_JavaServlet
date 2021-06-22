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
                document.getElementById("shopping-cart-div"),
                document.getElementById("shopping-cart-div-message")
            )

            productsCatalogue = new ProductsCatalogue(
                document.getElementById("products-table-div"),
                document.getElementById("products-table-body")
            )

            orders = new OrdersList(
                document.getElementById("orders-div")
            )
        };


        this.refresh = function(){
            //Refresh view
            userInfo.show();
            productsCatalogue.products_table_div.show();
        };


        //deve nascondermi tutti i div eccetto quelo del carrello
        this.showShoppingCart = function () {


        }

        //deve nascondermi tutti i div eccetto quelo degli ordini
        this.showOrders = function () {


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
                    case 200: //ok
                        // var click = new Event("click");
                        // self.create_account_button.dispatchEvent(click);
                        // self.show();

                        var products = JSON.parse(req.responseText);
                        productsCatalogue.update(products, null, true); //aggiorno la tabella dei prodotti
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

        //da invocare in seguito a risposta positiva alla servlet 'AddToCart'
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
        products_table_body) {


        this.products_table_div = products_table_div;
        this.products_table_body = products_table_body;

        var self = this;

        this.products_table_div.show = function(){
            //Request and update with the results
            makeCall("GET", 'GetHomeProducts', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        var products = JSON.parse(req.responseText);
                        self.update(products);
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

            //se errore allora mostra l'errore a schermo
            if (products == null) {
                this.products_table_div.innerHTML = _error;
                this.products_table_body.innerHTML = ""; // empty the table body
                return;
            }

            this.products_table_body.innerHTML = ""; // empty the table body

            var table_body, row, td, img, prod_name, form, form_input;

            var prods = new Map();

            for (const prodId in products.supplierProductMap) {
                prods.set(prodId, products.supplierProductMap[prodId]);
            }

            var self = this;

            //prods = Map<integer, Array<SupplierProduct> >
            //quindi applicando il forEach mi ritorna l'array di prodotti con venditori diversi


            //bisogna differenziare per forza perchè altrimenti in ricerca non creo una nuova table body per ogni prodotto

            prods.forEach( (function(product) { // self visible here, not this

                //per ogni prodId si crea una nuova table body

                table_body = document.createElement("tbody");


                var currentProd = product[0]; //questo è per prendere un solo prodotto venduto da un solo venditore

                row = document.createElement("tr");

                //td immagine prodotto
                td = document.createElement("td");
                td.className = "table-product-img";
                img = document.createElement("img");
                img.src = getProductsImageFolderURL().concat(currentProd.productImagePath); //set img
                td.appendChild(img);
                row.appendChild(td);

                //td nome prodotto
                td = document.createElement("td");
                prod_name = document.createElement("a");
                prod_name.innerHTML = currentProd.productName;
                prod_name.className = "clickable-link";
                td.appendChild(prod_name);
                row.appendChild(td);

                //
                prod_name.addEventListener("click", (e) => {
                    e.preventDefault();
                    //deve mettere in tabella le info degli altri venditori dei prodotti


                }, false);

                //td desc prodotto
                td = document.createElement("td");
                td.innerHTML = currentProd.productDescription;
                row.appendChild(td);

                //td costo prodotto
                td = document.createElement("td");
                td.innerHTML = currentProd.supplierProductCost;
                row.appendChild(td);

                //td immagine prodotto
                td = document.createElement("td");
                td.className = "table-supplier-img";

                img = document.createElement("img");
                img.src = getSuppliersImageFolderURL().concat(currentProd.supplier.imagePath); //set img
                td.appendChild(img);
                row.appendChild(td);

                //td buy button
                td = document.createElement("td");
                form = document.createElement("form");
                form.className = "styled-form";
                form.action = "AddToCart";
                form.method = "POST";
                form_input = document.createElement("input");
                form_input.type = "hidden";
                form_input.value = currentProd.productId;
                form_input.name = "productId";
                form.appendChild(form_input);
                form_input = document.createElement("input");
                form_input.type = "hidden";
                form_input.value = currentProd.supplierId;
                form_input.name = "supplierId";
                form.appendChild(form_input);
                form_input = document.createElement("input");
                form_input.type = "hidden";
                form_input.value = currentProd.supplierProductCost;
                form_input.name = "supplierProductCost";
                form.appendChild(form_input);
                form_input = document.createElement("input");
                form_input.className = "clickable-link clickable-link-medium";
                form_input.type = "submit";
                form_input.value = "Add to cart";
                form.appendChild(form_input);

                td.appendChild(form);
                row.appendChild(td);


                //AGGIUNGI AL CARRELLO
                form_input.addEventListener("click", (e) => {
                    e.preventDefault();
                    var addToCartForm = e.currentTarget.closest("form");
                    var productRow = e.currentTarget.closest("tr");

                    if (form.checkValidity()) {
                        //chiamata a servlet
                        makeCall("POST", 'products/AddToCart', addToCartForm, (req) =>{

                            //nascondo la tabella
                            self.products_table_div.style.display = 'block';

                            switch(req.status){
                                case 200: //ok

                                    shoppingCart.update();
                                    break;
                                case 400: // bad request
                                case 401: // unauthorized
                                case 500: // server error

                                    shoppingCart.showFailure(req.responseText);
                                    break;
                                default: //Error
                                    shoppingCart.showFailure("Request reported status " + req.status);
                            }
                        });
                    } else
                        form.reportValidity(); //If not valid, notify
                }, false);



                self.products_table_body.appendChild(row);

                self.products_table_div.style.display = 'block';

            }));

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
        shopping_cart_link.setAttribute("href", "GoToShoppingCart")

        let orders_link = document.getElementById("orders-link");
        orders_link.setAttribute("href", "GoToOrders");

        let logout_link = document.getElementById("logout-link");
        logout_link.setAttribute("href", "logout")

        logout_link.addEventListener("click", e => {
            sessionStorage.clear();
        });


    }

})();