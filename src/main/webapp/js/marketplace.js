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
                JSON.parse(sessionStorage.getItem('userdata')),
                [document.getElementById("title-username")]
            );

            searchForm = new SearchForm(
                document.getElementById("search-form-div"),
                document.getElementById("search-form-div-button"),
                document.getElementById("search-form-error"),
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

            //nascondi tutto il resto
            shoppingCart.hide();
            orders.hide();

            userInfo.show();
            productsCatalogue.showHomeProducts();
        };


        //deve nascondermi tutti i div eccetto quelo del carrello
        this.showShoppingCart = function () {
            updateHeaderCss('CART');

            productsCatalogue.hide();
            orders.hide();

            //mostra carrello
            shoppingCart.show();

        }

        //deve nascondermi tutti i div eccetto quelo degli ordini
        this.showOrders = function () {
            updateHeaderCss('ORDERS');

            //nascondi tutto il resto
            productsCatalogue.hide();
            shoppingCart.hide();


            //mostra carrello
            orders.show();

        }

        this.showSearchPage = function () {
            updateHeaderCss('HOME');

            //nascondi tutto il resto
            shoppingCart.hide();
            orders.hide();


            //mostra ricerca
            productsCatalogue.showCatalogue();

        }

        this.updatePage = function () {

            if(searchForm.lastKeywordSearch == null || searchForm.lastKeywordSearch === "") {
                pageOrchestrator.showHomePage();
                return;
            }

            //fake a click on the button
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
        _search_form_error) {

        this.search_form_div = _search_form_div;
        this.search_form_button = _search_form_button;
        this.search_form_error = _search_form_error;
        this.lastKeywordSearch = null;

        var self = this; //Necessary only for in-function helpers (makeCall)

        //search button
        this.search_form_button.addEventListener('click', (e) =>{
            e.preventDefault()

            self.search_form_error.style.display = 'none';

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
                        productsCatalogue.updateSearchProducts(products);
                        productsCatalogue.showCatalogue();
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
            self.search_form_div.style.display = 'block';
        };

        this.update = function() {
            self.search_form_div.style.display = 'none';
        };
    }


    function ShoppingCartNew(
        _shopping_cart_div,
        _shopping_cart_div_message
    ) {

        /*

        This line get all the elements with class ".buy-btn" in the document
        const buyBtns = document.querySelectorAll(".buy-btn");

         */
        this.shopping_cart_div = _shopping_cart_div;
        this.shopping_cart_div_message = _shopping_cart_div_message;

        this.KEY = 'asc';
        this.contents = [];

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
            pageOrchestrator.updatePage();

        };

        this.emptyShoppingCart = function (supplierId) {
            //update variables
            this.prods.delete(supplierId);
            this.totalAmountBySupplier.delete(supplierId);
            this.totalItemsBySupplier.delete(supplierId);

            //update table
            document.getElementById("sup" + supplierId + "-cart-body").remove();
        }


        this.sync = async function  (){
            let _cart = JSON.stringify(self.contents);
            await localStorage.setItem(self.KEY, _cart);
        };

        this.find = function (id){
            //find an item in the cart by it's id
            let match = self.contents.filter(item=>{
                if(item.id == id)
                    return true;
            });
            if(match && match[0])
                return match[0];
        };


        this.increase = function (id, qty=1){
            //increase the quantity of an item in the cart
            self.contents = self.contents.map(item=>{
                if(item.id === id)
                    item.qty = item.qty + qty;
                return item;
            });
            //update localStorage
            self.sync()
        };

        this.reduce = function (id, qty=1){
            //reduce the quantity of an item in the cart
            self.contents = self.contents.map(item=>{
                if(item.id === id)
                    item.qty = item.qty - qty;
                return item;
            });
            self.contents.forEach(async item=>{
                if(item.id === id && item.qty === 0)
                    await self.remove(id);
            });
            //update localStorage
            self.sync()
        };


        this.empty = function (){
            //empty whole cart
            self.contents = [];
            //update localStorage
            self.sync()
        };

        this.sort = function (field='title'){
            //sort by field - title, price
            //return a sorted shallow copy of the CART.contents array
            let sorted = self.contents.sort( (a, b)=>{
                if(a[field] > b[field]){
                    return 1;
                }else if(a[field] < a[field]){
                    return -1;
                }else{
                    return 0;
                }
            });
            return sorted;

        };

        this.logContents = function (prefix){
            console.log(prefix, self.contents)
        }

        this.show = function() {

            let cartTables = self.shopping_cart_div.getElementsByClassName("cart-table");

            for (const table of cartTables) {
                self.shopping_cart_div.removeChild(table);
            }

            //show empty cart
            if (self.prods.size === 0) {
                self.shopping_cart_div_message.innerHTML = "Your cart is empty";
                self.shopping_cart_div_message.style.display = "block";
            } else { //show cart
                self.shopping_cart_div_message.innerHTML = "";
                self.shopping_cart_div_message.style.display = "none";
                self.shopping_cart_div.appendChild(createCartTable(self, userInfo))
            }

            self.shopping_cart_div.style.display = 'block';
        }

        this.hide = function () {
            self.shopping_cart_div.style.display = 'none';

        }

        this.showFailure = function (_message) {

            this.shopping_cart_div.style.display = 'block';
            this.shopping_cart_div_message.innerHTML = _message;
            this.shopping_cart_div_message.style.display = 'block';

        }

        this.updateAndShowModal = function (supplierId) {
            //creo la table con i prodotti presi dal carrello per il supplId e la passo al catalogo che la metterà nel div con id modal-window-items

            productsCatalogue.updateAndShowModalCatalogue(createCartTable(self, userInfo, true, supplierId));
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

            var self = this;
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

        //hide modal windows
        this.modal_window_content_div.onmouseleave = function () {

            //remove all tables tags in modal-window-content-div
            while (this.childElementCount > 0) {
                this.removeChild(this.children[0]);
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