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
                sessionStorage.getItem('userdata'),
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
                document.getElementById("products-table")
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
    }

    function UserInfo(
        _userdata,
        nameElements){

        this.name = _userdata.name;
        this.id = _userdata.id;



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
            self.search_form_div.style.display = 'block';
        };

        this.update = function() {
            self.search_form_div.style.display = 'none';
        };
    }


    function ShoppingCart(
        _shopping_cart_div,
        _shopping_cart_div_message) {

        this.shopping_cart_div = _shopping_cart_div;
        this.shopping_cart_div_message = _shopping_cart_div_message;

        var self = this; //Needed for in-function helpers only (makeCall)

        this.show = function() {
            self.shopping_cart_div = sessionStorage.getItem(shoppingCart);

            self.shopping_cart_div.style.display = 'block';
        }

        this.hide = function () {
            //TODO da fare

            self.shopping_cart_div.style.display = 'none';
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

    function ShoppingCartNew(
        _shopping_cart_div,
        _shopping_cart_div_message
    ) {

        this.shopping_cart_div = _shopping_cart_div;
        this.shopping_cart_div_message = _shopping_cart_div_message;

        self.KEY = 'bkasjbdfkjasdkfjhaksdfjskd';
        self.contents = [];
        self.prods = new Map();


        var self = this; //Needed for in-function helpers only (makeCall)

        this.init = function () {
            //check localStorage and initialize the contents of CART.contents
            let _contents = localStorage.getItem(self.KEY);
            if(_contents){
                self.contents = JSON.parse(_contents);
            }else{
                //dummy test data
                self.contents =
                    [
                        {   id:1,
                            title:'Apple',
                            qty:5,
                            itemPrice: 0.85
                        },
                        {   id:2,
                            title:'Banana',
                            qty:3,
                            itemPrice: 0.35
                        },
                        {   id:3,
                            title:'Cherry',
                            qty:8,
                            itemPrice: 0.05
                        }
                    ];

                self.contents =
                    [
                        {   id:1,
                            title:'Apple',
                            qty:5,
                            itemPrice: 0.85
                        },
                        {   id:2,
                            title:'Banana',
                            qty:3,
                            itemPrice: 0.35
                        },
                        {   id:3,
                            title:'Cherry',
                            qty:8,
                            itemPrice: 0.05
                        }
                    ];

                self.sync();
            }
        };

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

        this.add = function (id){
            //add a new item to the cart
            //check that it is not in the cart already
            if(self.find(id)){
                self.increase(id, 1);
            }else{
                let arr = PRODUCTS.filter(product=>{
                    if(product.id == id){
                        return true;
                    }
                });
                if(arr && arr[0]){
                    let obj = {
                        id: arr[0].id,
                        title: arr[0].title,
                        qty: 1,
                        itemPrice: arr[0].price
                    };
                    self.contents.push(obj);
                    //update localStorage
                    self.sync();
                }else{
                    //product id does not exist in products data
                    console.error('Invalid Product');
                }
            }
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

        this.remove = function (id){
            //remove an item entirely from CART.contents based on its id
            self.contents = self.contents.filter(item=>{
                if(item.id !== id)
                    return true;
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
            //NO impact on localStorage
        };

        this.logContents = function (prefix){
            console.log(prefix, self.contents)
        }

        this.show = function() {
            self.shopping_cart_div = sessionStorage.getItem(self.KEY);

            self.shopping_cart_div.style.display = 'block';
        }

        this.hide = function () {
            self.shopping_cart_div.style.display = 'none';
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

            // self visible here, not this
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

            self.orders_div.style.display = 'block';
            self.orders_error_div.innerHTML = _message;
            self.orders_error_div.style.display = 'block';

        }

        this.hide = function () {
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


    function cart() {


    }

})();