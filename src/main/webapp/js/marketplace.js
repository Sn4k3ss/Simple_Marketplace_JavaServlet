/**
 * Marketplace
 */

(function(){

    //Vars
    var searchForm, productsCatalogue, userInfo, shoppingCart, orders;
    
    var pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        pageOrchestrator.start();
        pageOrchestrator.showHomePage();
        setHeaderHrefs();
    });

    function PageOrchestrator(){
        this.start = function() {

            userInfo = new UserInfo(
                JSON.parse(sessionStorage.getItem('userdata')),
                [document.getElementById("title-username")]
            );

            searchForm = new SearchForm(
                document.getElementById("search-form-div"),
                document.getElementById("search-form-div-button"),
                document.getElementById("search-form-error"),
                document.getElementById("search-form-info")
            )

            shoppingCart = new ShoppingCartNew(
                document.getElementById("shopping-cart-div"),
                document.getElementById("shopping-cart-div-message")
            )

            productsCatalogue = new ProductsCatalogue(
                document.getElementById("products-table-div"),
                document.getElementById("products-table"),
                document.getElementById("modal-window-div"),
                document.getElementById("modal-window-content-div"),
                document.getElementById("products-table-error-div")
            )

            orders = new OrdersList(
                document.getElementById("orders-div"),
                document.getElementById("orders-error-div")
            )
        };

        this.showHomePage = function(){
            updateHeaderCss('HOME');

            searchForm.clearError();
            searchForm.clearSearchBox();
            shoppingCart.hide();
            orders.hide();

            userInfo.show();
            productsCatalogue.showHomeProducts();
        };

        this.showShoppingCart = function () {
            updateHeaderCss('CART');

            searchForm.clearError();
            searchForm.clearMessageInfo();
            searchForm.clearSearchBox();
            productsCatalogue.hide();
            orders.hide();

            shoppingCart.show();
        }

        this.showOrders = function () {
            updateHeaderCss('ORDERS');

            searchForm.clearError();
            searchForm.clearMessageInfo();
            searchForm.clearSearchBox();
            productsCatalogue.hide();
            shoppingCart.hide();

            orders.show();
        }

        this.showSearchPage = function () {
            updateHeaderCss('HOME');

            searchForm.clearError();
            shoppingCart.hide();
            orders.hide();

            productsCatalogue.showCatalogue();
        }

        this.updatePage = function () {

            if(searchForm.lastKeywordSearch == null || searchForm.lastKeywordSearch === "") {
                pageOrchestrator.showHomePage();
                return;
            }

            searchForm.search_form_button.click();
        }
    }

    function UserInfo(
        _userdata,
        nameElements){

        this.name = _userdata.userName;
        this.id = _userdata.userId;
        this.shippingAddresses = _userdata.shippingAddresses;




        this.show = function(){
            nameElements.forEach(element => {
                element.textContent = this.name;
            });
        }
    }

    function SearchForm(
        _search_form_div,
        _search_form_button,
        _search_form_error,
        _search_form_info) {

        this.search_form_div = _search_form_div;
        this.search_form_button = _search_form_button;
        this.search_form_error = _search_form_error;
        this.search_form_info = _search_form_info;
        this.lastKeywordSearch = null;

        var self = this; //Necessary only for in-function helpers (makeCall)

        //search button
        this.search_form_button.addEventListener('click', (e) =>{
            e.preventDefault()

            pageOrchestrator.showSearchPage();

            const paramOneValue = e.currentTarget.closest('form')['keyword'].value;

            self.lastKeywordSearch = paramOneValue;
            const params = {
                keyword: paramOneValue
            }
            const endpoint = createUrlWithParams("GetSearchProducts", params);

            makeCall("GET", endpoint, null, (req) =>{
                switch(req.status){
                    case 200:
                        let products = JSON.parse(req.responseText);

                        if (self.noProdsRetrieved(products)){
                            self.showError("No products have been found, please try something else");
                        } else {
                            productsCatalogue.updateSearchProducts(products);
                            productsCatalogue.showCatalogue();
                        }
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.showError(req.responseText);
                        break;
                    default: //Error
                        self.showError("Request reported status " + req.status);
                }
            });

        });

        this.noProdsRetrieved = function (productsRetrieved) {
            let numOfProds = 0;
            for (const prodId in productsRetrieved.supplierProductMap) {
                numOfProds++;
            }

            if (numOfProds === 0)
                return true;
        }

        this.show = function() {
            this.search_form_div.style.display = 'block';
        };

        this.showError = function (message) {
            this.search_form_error.textContent = message;
            this.search_form_error.style.display = 'block';
        }

        this.clearError = function () {
            this.search_form_error.textContent = "";
            this.search_form_error.style.display = 'none';
        }

        this.clearSearchBox = function () {
            this.search_form_button.closest('form')['keyword'].value = "";
        }

        this.showMessageInfo = function (message) {
            this.search_form_info.textContent = message;
            this.search_form_info.style.display = 'block';

            //da implementare che il
            setTimeout(this.clearMessageInfo, 5000);

        }

        this.clearMessageInfo = function () {
            self.search_form_info.textContent = "";
            self.search_form_info.style.display = 'none';
        }

    }


    function ShoppingCartNew(
        _shopping_cart_div,
        _shopping_cart_div_message)
    {
        this.shopping_cart_div = _shopping_cart_div;
        this.shopping_cart_div_message = _shopping_cart_div_message;

        this.prods = new Map();

        this.totalAmountBySupplier = new Map();
        this.totalItemsBySupplier = new Map();


        let self = this; //Needed for in-function helpers only


        this.addToCart = function (product, howMany){

            let alreadyInShoppingCart = false;

            if (self.prods.get(product.supplierId) !== undefined) {

                self.prods.get(product.supplierId).forEach(prod => {
                    if (prod.productId === product.productId) {
                        alreadyInShoppingCart = true;
                        let tmp = prod.howMany;
                        tmp += howMany;
                        prod.howMany = tmp;
                        prod.totalAmount = tmp * prod.supplierProductCost;
                    }
                });
            }



            if (!alreadyInShoppingCart) {
                let list = self.prods.get(product.supplierId);
                //settiamo le proprietà che vanno settate una volta sola
                product.howMany = howMany;
                product.totalAmount = howMany * product.supplierProductCost;

                // if list does not exist create it
                if(list === undefined) {
                    list = [];
                    list.push(product);
                    self.prods.set(product.supplierId, list);
                } else {
                    // add if item is not already in list
                    let itemInList = false;

                    list.forEach(item => {
                        if (item.productId === product.productId)
                            itemInList = true;
                    });

                    if(!itemInList)
                        list.push(product);
                }
            }


            //update the total of the current supplier
            let totalAmount = 0;
            let totalItems = 0;

            this.prods.get(product.supplierId).forEach(item => {
                totalAmount += item.totalAmount;
                totalItems += item.howMany;
            });

            this.totalAmountBySupplier.set(product.supplierId, totalAmount);
            this.totalItemsBySupplier.set(product.supplierId, totalItems);

            //bisogna riaggiornare la pagina
            searchForm.showMessageInfo("Product added to cart!");
            pageOrchestrator.updatePage();

        };

        this.emptyShoppingCart = function (supplierId) {
            //update variables
            self.prods.delete(supplierId);
            self.totalAmountBySupplier.delete(supplierId);
            self.totalItemsBySupplier.delete(supplierId);

            //update table
            document.getElementById("sup" + supplierId + "-cart-body").remove();

            //ricarico il carrello
            pageOrchestrator.showShoppingCart();
        }


        this.show = function() {

            let cartTables = self.shopping_cart_div.getElementsByClassName("cart-table");

            for (const table of cartTables) {
                self.shopping_cart_div.removeChild(table);
            }


            if (self.prods.size === 0) {
                self.showMessage("Your cart is empty");
            } else {
                self.clearMessage();
                self.shopping_cart_div.appendChild(createCartTable(self, userInfo))
            }

            self.shopping_cart_div.style.display = 'block';
        }

        this.hide = function () {
            self.shopping_cart_div.style.display = 'none';

        }

        this.showMessage = function (_message) {
            self.shopping_cart_div_message.innerHTML = _message;
            self.shopping_cart_div_message.style.display = 'block';

            setTimeout(self.clearMessage, 3000);
        }

        this.clearMessage = function () {
            self.shopping_cart_div_message.innerHTML = "";
            self.shopping_cart_div_message.style.display = "none";
        }

        this.showFailure = function (_message) {

            self.shopping_cart_div.style.display = 'block';
            self.shopping_cart_div_message.innerHTML = _message;
            self.shopping_cart_div_message.style.display = 'block';

        }

        this.updateAndShowModal = function (supplierId) {
            productsCatalogue.updateAndShowModalCatalogue(createCartTable(self, userInfo, true, supplierId));
        }

        this.hideModal = function () {
            productsCatalogue.hideModalCatalogue();
        }
    }

    // il products catalogue riceve come primo argomento il catalogo di prodotti da mostrare nella home
    // come secondo argomento invece sono i risultati ottenuti dalla ricerca
    function ProductsCatalogue(
        products_table_div,
        products_table,
        modal_window_div,
        modal_window_content_div,
        products_table_error_div) {


        this.products_table_div = products_table_div;
        this.products_table = products_table;
        this.products_table_error_div = products_table_error_div;
        this.modal_window_div = modal_window_div;
        this.modal_window_content_div = modal_window_content_div;

        var self = this;

        this.showHomeProducts = function(){
            //Request and update with the results
            makeCall("GET", 'GetHomeProducts', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        let products = JSON.parse(req.responseText);
                        self.updateHomeProducts(products);
                        self.showCatalogue();
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
                this.products_table_error_div.innerHTML = _error;
                this.products_table_error_div.style.display = "block";
                this.products_table_div.style.display = 'block';
                return;
            }

            let prodsMap = new Map();
            let table_body = document.createElement("tbody");

            for (const prodId in products.supplierProductMap) {
                prodsMap.set(prodId, products.supplierProductMap[prodId]);
            }

            //asc sort
            let sortedProds = sortAscCost(prodsMap);


            let searchResultNum = 1;
            let resultsNum = sortedProds.size;

            sortedProds.forEach( (function(prods) { // self visible here, not this
                let currentProd = prods[0];

                table_body.appendChild(createProductsRow(currentProd, shoppingCart, searchResultNum, resultsNum));
                searchResultNum++;
            }));

            this.products_table.appendChild(table_body);
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

            //asc sort
            let sortedProds = sortAscCost(prodsMap);

            let firstIteration = true;
            let searchResultNum = 1;
            let resultsNum = sortedProds.size;

            // self visible here, not this
            //iterazione per ogni prodId
            sortedProds.forEach( (function(prods) {

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

        };

        this.hide = function () {
            this.products_table_div.style.display = 'none';
        };

        this.showCatalogue = function () {
            this.products_table_div.style.display = 'block';
        }

        this.updateAndShowModalCatalogue = function (table) {
            this.modal_window_content_div.appendChild(table);
            this.modal_window_div.style.display = 'block';

        }

        //hide modal windows on mouse leave
        this.modal_window_content_div.onmouseleave = function () {

            //remove all tables tags in modal-window-content-div
            while (this.childElementCount > 0) {
                this.removeChild(this.children[0]);
            }

            self.modal_window_div.style.display = 'none';
        }

        //hide modal windows explicitly
        this.hideModalCatalogue = function () {
            //remove all tables tags in modal-window-content-div
            while (self.modal_window_div.childElementCount > 0) {
                self.modal_window_div.removeChild(self.modal_window_div.children[0]);
            }

            self.modal_window_div.style.display = 'none';
        }
    }

    function OrdersList(
        _orders_div,
        _orders_error_div) {

        this.orders_div = _orders_div;
        this.orders_error_div = _orders_error_div;

        var self = this;

        this.show = function(){
            //Request and update with the results
            makeCall("GET", 'GetOrders', null, (req) =>{
                switch(req.status){
                    case 200: //ok
                        let ordersFromReq = JSON.parse(req.responseText);
                        self.updateOrdersList(ordersFromReq);

                        self.orders_div.style.display = 'block';
                        break;
                    case 400: // bad request
                    case 401: // unauthorized
                    case 500: // server error
                        self.showFailure(req.responseText);
                        break;
                    default: //Error
                        self.showFailure("Request reported status " + req.status);
                        break;
                }
            });
        };

        this.updateOrdersList = function (orders) {

            //remove all tables in orders-div
            while (this.orders_div.getElementsByTagName("table").length > 0) {
                this.orders_div.removeChild(_orders_div.getElementsByTagName("table")[0]);
            }

            //iterazione per ogni ordine
            orders.forEach( (function(order) {
                let orderTable, tbody1;

                //tabella con le info sull'ordine
                orderTable = createOrderTable();
                tbody1 = document.createElement("tbody");
                tbody1.appendChild(createOrderRow(order));
                orderTable.appendChild(tbody1);
                self.orders_div.appendChild(orderTable);

                //tabella con i prodotti dell'ordine
                let prodsTable = createProductsTableInOrder(order.orderProductsList);
                self.orders_div.appendChild(prodsTable);

            }));
        }


        this.showFailure = function (_message) {
            this.orders_error_div.innerHTML = _message;
            this.orders_error_div.style.display = 'block';
            this.orders_div.style.display = 'block';
        }

        this.hide = function () {
            this.orders_error_div.innerHTML = "";
            this.orders_error_div.style.display = 'none';
            this.orders_div.style.display = 'none';
        };
    }



    function setHeaderHrefs(){
        let homepage_link_logo = document.getElementById("homepage-link-logo");
        homepage_link_logo.onclick = pageOrchestrator.showHomePage;

        let home_link = document.getElementById("home-link");
        home_link.onclick = pageOrchestrator.showHomePage;

        let shopping_cart_link = document.getElementById("shopping-cart-link");
        shopping_cart_link.onclick = pageOrchestrator.showShoppingCart;

        let orders_link = document.getElementById("orders-link");
        orders_link.onclick = pageOrchestrator.showOrders;

        let logout_link = document.getElementById("logout-link");
        logout_link.setAttribute("href", "logout")

        logout_link.addEventListener("click", e => {
            sessionStorage.clear();
        });


    }

    function updateHeaderCss(a){

        document.getElementById("home-link").classList.remove('active');
        document.getElementById("shopping-cart-link").classList.remove('active');
        document.getElementById("orders-link").classList.remove('active');

        switch (a) {

            case 'HOME': document.getElementById("home-link").classList.add('active');
                    break;
            case 'CART': document.getElementById("shopping-cart-link").classList.add('active');
                    break;
            case 'ORDERS': document.getElementById("orders-link").classList.add('active');
                    break;
            default:
        }




    }

})();