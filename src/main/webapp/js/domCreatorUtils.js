


/**
 * Utils for creating new row from product
 * -------------
 *
 *
 */
function createProductsRow(supplierProduct, shoppingCart, searchResultNum, resultsNum) {

    let prodTmp = {
        "productId": supplierProduct.productId,
        "productImagePath": supplierProduct.productImagePath,
        "productName": supplierProduct.productName,
        "productDescription": supplierProduct.productDescription,
        "productCategory": supplierProduct.productCategory,
        "supplierProductCost": supplierProduct.supplierProductCost,
        "supplierId": supplierProduct.supplier.supplierId,
        "supplierName": supplierProduct.supplier.supplierName,
        "supplier": supplierProduct.supplier
    }

    let row = document.createElement("tr");

    //td immagine prodotto
    let td = document.createElement("td");
    td.className = "table-product-img";
    let img = document.createElement("img");
    img.src = getProductsImageFolderURL().concat(supplierProduct.productImagePath); //set img
    td.appendChild(img);
    row.appendChild(td);

    //td nome prodotto
    td = document.createElement("td");
    prod_name = document.createElement("a");
    prod_name.innerHTML = supplierProduct.productName;
    prod_name.className = "clickable-link";
    td.appendChild(prod_name);
    row.appendChild(td);

    //mostra altri venditori
    //chiamata alla servlet che rende il prodotto visualizzato
    prod_name.addEventListener("click", (e) => {
        e.preventDefault();

        const params = {
            productId: supplierProduct.productId
        }
        const endpoint = createUrlWithParams("SetProductDisplayed", params);

        makeCall("POST", endpoint, null, (req) =>{

            switch(req.status){
                case 200:
                    //deve far comparire la tabella con le info sugli altri venditori
                    // e chiudere tutti gli altri prima
                    for (let i = 1; i <= resultsNum ; i++) {
                        //document.getElementById("tr-search-result-num-" + i).removeAttribute("style");
                        document.getElementById("tr-search-result-num-" + i).style.display = 'none';
                    }

                    document.getElementById("tr-search-result-num-" + searchResultNum).removeAttribute("style");
                    break;
                case 400: // bad request
                case 401: // unauthorized
                case 500: // server error
                    //TODO
                    //mostra l'errore
                    //shoppingCart.showFailure(req.responseText);
                    break;
                default: //Error
                    //mostra l'errore
                    //shoppingCart.showFailure("Request reported status " + req.status);
            }
        });

    });



    //td desc prodotto
    td = document.createElement("td");
    td.innerHTML = supplierProduct.productDescription;
    row.appendChild(td);

    //td costo prodotto
    td = document.createElement("td");
    td.innerHTML = Number.parseFloat(supplierProduct.supplierProductCost).toFixed(2);
    row.appendChild(td);

    //td immagine venditore
    td = document.createElement("td");
    td.className = "table-supplier-img";

    img = document.createElement("img");
    img.src = getSuppliersImageFolderURL().concat(supplierProduct.supplier.imagePath); //set img
    td.appendChild(img);
    row.appendChild(td);

    //td buy button
    //possiamo rimuovere tutto e lasciare solo il bottone
    td = document.createElement("td");
    let form = document.createElement("form");
    form.className = "buy-btn";
    form.action = "AddToCart";
    form.method = "POST";
    let form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.productId;
    form_input.name = "productId";
    form.appendChild(form_input);
    form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.supplierId;
    form_input.name = "supplierId";
    form.appendChild(form_input);
    form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.supplierProductCost;
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
        shoppingCart.addToCart(prodTmp, 1);
    }, false);

    return row;
}


//crea una table con intestazione e relativa classe di stile css
function createSearchProductsTable() {

    let table = document.createElement("table");
    table.className = "styled-table";

    let thead = document.createElement("thead");
    let tr = document.createElement("tr");

    let th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Supplier";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Cost";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Rating";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Free shipping from";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Range";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "items";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "AddToCart";
    tr.appendChild(th);

    thead.appendChild(tr);
    table.appendChild(thead);

    return table;

}

function createSupplierProductsRow(supplierProduct, shoppingCart) {

    let prodTmp = {
        "productId": supplierProduct.productId,
        "productImagePath": supplierProduct.productImagePath,
        "productName": supplierProduct.productName,
        "productDescription": supplierProduct.productDescription,
        "productCategory": supplierProduct.productCategory,
        "supplierProductCost": supplierProduct.supplierProductCost,
        "supplierId": supplierProduct.supplier.supplierId,
        "supplierName": supplierProduct.supplier.supplierName,
        "supplier": supplierProduct.supplier
    }

    let row = document.createElement("tr");

    //td immagine supplier
    let td = document.createElement("td");
    td.className = "table-supplier-img";
    let img = document.createElement("img");
    img.src = getSuppliersImageFolderURL().concat(supplierProduct.supplier.imagePath); //set img
    td.appendChild(img);
    row.appendChild(td);

    //td costo prodotto
    td = document.createElement("td");
    td.innerHTML = Number.parseFloat(supplierProduct.supplierProductCost).toFixed(2);
    row.appendChild(td);

    //td rating
    td = document.createElement("td");
    td.innerHTML = Number.parseFloat(supplierProduct.supplier.supplierRating).toFixed(1);
    row.appendChild(td);

    //td costo minimo spedizione gratuita
    td = document.createElement("td");
    if (supplierProduct.supplier.hasFreeShipping)
        td.innerHTML = Number.parseFloat(supplierProduct.supplier.freeShippingMinAmount).toFixed(2);
    else
        td.innerHTML = "No";

    row.appendChild(td);

    //range di prezzo
    td = document.createElement("td");
    td.appendChild(createSupplierRangesTable(supplierProduct.supplier.supplierShippingPolicy));
    row.appendChild(td);

    //items already in cart
    const tot = shoppingCart.totalItemsBySupplier.get(supplierProduct.supplier.supplierId);
    td = document.createElement("td");
    if (tot !== undefined) {
        td.innerHTML = tot;
    } else {
        td.innerHTML = "0";
    }
    row.appendChild(td);

    td.onmouseover = function () {
        shoppingCart.updateAndShowModal(supplierProduct.supplier.supplierId);
    }

    //td buy button
    td = document.createElement("td");
    let form = document.createElement("form");
    form.className = "styled-form";
    form.action = "AddToCart";
    form.method = "POST";

    let label = document.createElement("label");
    label.setAttribute("for", "howMany");
    label.innerHTML = "How many:";
    form.appendChild(label);

    let select = document.createElement("select");
    select.setAttribute("name", "howMany");

    //l'id da settare sarà id=supX-prodY-label
    const id = "sup-" + prodTmp.supplierId + "-prod" + prodTmp.productId + "-select";
    select.setAttribute("id", id);

    for (let i = 1; i <= 9; i++){
        select.options[select.options.length] = new Option(i, i);
    }

    form.appendChild(select);



    let form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.productId;
    form_input.name = "productId";
    form.appendChild(form_input);
    form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.supplierId;
    form_input.name = "supplierId";
    form.appendChild(form_input);
    form_input = document.createElement("input");
    form_input.type = "hidden";
    form_input.value = supplierProduct.supplierProductCost;
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

        const howMany = parseInt(document.getElementById(id).value);

        shoppingCart.addToCart(prodTmp, howMany);
    }, false);

    return row;
}

function createSupplierRangesTable(supplierShippingPolicy) {

    let table = document.createElement("table");
    table.className = "styled-table";

    let thead = document.createElement("thead");
    let tr = document.createElement("tr");
    let th = document.createElement("th");
    let td = document.createElement("td");

    th.setAttribute("scope", "col");
    th.innerHTML = "Range";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "minItem";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "maxItem";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Cost";
    tr.appendChild(th);

    thead.appendChild(tr);
    table.appendChild(thead);

    let tbody = document.createElement("tbody");

    for (let i = 0; i < supplierShippingPolicy.ranges.length; i++) {
        tr = document.createElement("tr");

        //indice del range
        let td = document.createElement("td");
        td.innerHTML = i+1;
        tr.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = supplierShippingPolicy.ranges[i].minAmount;
        tr.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = supplierShippingPolicy.ranges[i].maxAmount;
        tr.appendChild(td);

        td = document.createElement("td");
        td.innerHTML = supplierShippingPolicy.ranges[i].cost;
        tr.appendChild(td);

        tbody.appendChild(tr);
    }

    table.appendChild(tbody);
    return table;

}

//crea una table con intestazione e crelativa classe di stile css
function createOrderTable() {

    let table = document.createElement("table");
    table.className = "styled-table";

    let thead = document.createElement("thead");
    let tr = document.createElement("tr");

    let th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Order id";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Order placement date";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Supplier";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Order amount";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Shipping fees";
    tr.appendChild(th);


    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Delivery date";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Address";
    tr.appendChild(th);

    thead.appendChild(tr);
    table.appendChild(thead);

    return table;
}

function createOrderRow(order) {

    let trow1 = document.createElement("tr"), td1;

    //td id ordine
    td1 = document.createElement("td");
    td1.innerHTML = order.orderId;
    trow1.appendChild(td1);

    //td data ordine
    td1 = document.createElement("td");
    let date = order.orderPlacementDate;
    td1.innerHTML = date.date.day + " / " + date.date.month + " / " + date.date.year;
    trow1.appendChild(td1);

    //td immagine supplier
    td1 = document.createElement("td");
    td1.className = "table-supplier-img";
    let img = document.createElement("img");
    img.src = getSuppliersImageFolderURL().concat(order.orderSupplier.imagePath); //set img
    td1.appendChild(img);
    trow1.appendChild(td1);

    //td orderAmount
    td1 = document.createElement("td");
    td1.innerHTML = "€ " + order.orderAmount;
    trow1.appendChild(td1);

    //td orderShippingFees
    td1 = document.createElement("td");
    td1.innerHTML = "€ " + order.orderShippingFees;
    trow1.appendChild(td1);

    //td delivery
    td1 = document.createElement("td");
    td1.innerHTML = order.deliveryDate.day + " / " + order.deliveryDate.month + " / " + order.deliveryDate.year;
    trow1.appendChild(td1);

    //td shipping address
    td1 = document.createElement("td");
    td1.innerHTML = order.shippingAddress.recipient + " " + order.shippingAddress.address + " " + order.shippingAddress.city + " " + order.shippingAddress.state;
    trow1.appendChild(td1);


    return trow1;

}

function createProductsTableInOrder(prods) {

    let prodsTable = document.createElement("table");
    prodsTable.className = "styled-product-in-order-table";
    let tbody = document.createElement("tbody");

    prods.forEach( (function(prod) {

        let trow2 = document.createElement("tr"), td2;

        //td immagine prod
        td2 = document.createElement("td");
        td2.className = "table-product-img";
        let img = document.createElement("img");
        img.src = getProductsImageFolderURL().concat(prod.productImagePath); //set img
        td2.appendChild(img);
        trow2.appendChild(td2);

        //td nome prodotto
        td2 = document.createElement("td");
        td2.innerHTML = prod.productName;
        trow2.appendChild(td2);

        //td descrizione prodotto
        td2 = document.createElement("td");
        td2.innerHTML = prod.productDescription;
        trow2.appendChild(td2);

        //td categoria prodotto
        td2 = document.createElement("td");
        td2.innerHTML = prod.productCategory;
        trow2.appendChild(td2);

        //td numero di prodotti
        td2 = document.createElement("td");
        td2.innerHTML = prod.howMany;
        trow2.appendChild(td2);

        //td costo cad.
        td2 = document.createElement("td");
        td2.innerHTML = "€ " + prod.supplierProductCost;
        trow2.appendChild(td2);

        tbody.appendChild(trow2);

    }));

    prodsTable.appendChild(tbody);

    return prodsTable;
}

function createCartTable(shoppingCart, userInfo, modal, modalSupplierId) {

    let table = document.createElement("table");
    table.classList.add("styled-table");
    table.classList.add("cart-table");

    //titolo tabella
    let caption = document.createElement("caption");
    caption.style.lightingColor = "#04aa6d";
    caption.innerHTML = "CART";
    table.appendChild(caption);

    let thead = document.createElement("thead");
    let tr = document.createElement("tr");


    let th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.setAttribute("colspan", "2");
    th.innerHTML = "Product name";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Product Description";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Category";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "Cost";
    tr.appendChild(th);

    th = document.createElement("th");
    th.setAttribute("scope", "col");
    th.innerHTML = "How many";
    tr.appendChild(th);

    thead.appendChild(tr);
    table.appendChild(thead);


    //se modal è asserito allora basta crearla per un solo venditore
    if(modal) {

        let products = shoppingCart.prods.get(modalSupplierId);

        let supplier = products[0].supplier;
        let totalAmount = shoppingCart.totalAmountBySupplier.get(modalSupplierId);
        let tbody = document.createElement("tbody");
        tbody.setAttribute("id", "sup" + supplier.supplierId + "-cart-body");

        //qua si cicla per prodotti di un singolo venditore
        products.forEach( (function (prod) {
            tr = document.createElement("tr");

            //immagine prodotto
            let td = document.createElement("td");
            td.className = "table-product-img";
            let img = document.createElement("img");
            img.src = getProductsImageFolderURL().concat(prod.productImagePath);
            img.alt = "Product image";
            td.appendChild(img);
            tr.appendChild(td);

            //nome prodotto
            td = document.createElement("td");
            td.innerHTML = prod.productName;
            tr.appendChild(td);

            //desc prodotto
            td = document.createElement("td");
            td.innerHTML = prod.productDescription;
            tr.appendChild(td);

            //cat prodotto
            td = document.createElement("td");
            td.innerHTML = prod.productCategory;
            tr.appendChild(td);

            //cat prodotto
            td = document.createElement("td");
            td.innerHTML = "€" + prod.supplierProductCost;
            tr.appendChild(td);

            //howMany
            td = document.createElement("td");
            td.innerHTML = prod.howMany;
            tr.appendChild(td);

            tbody.appendChild(tr);
        }));

        //riga con info su costo di spedizione e scelta indirizzo;
        tr = document.createElement("tr");
        tr.classList.add("active-row");

        let td = document.createElement("td");
        td.innerHTML = "Total amount on " + supplier.supplierName + " € " + Number.parseFloat(shoppingCart.totalAmountBySupplier.get(supplier.supplierId)).toFixed(2);
        tr.appendChild(td);

        //check if free shipping
        if(!supplier.hasFreeShipping) {
            td = document.createElement("td");
            td.innerHTML = supplier.supplierName + " doesn't offer free shipping";
            tr.appendChild(td);
        } else {
            if (totalAmount < supplier.freeShippingMinAmount) {
                td = document.createElement("td");
                td.innerHTML = "Free shipping on " + supplier.supplierName + " € " + Number.parseFloat(supplier.freeShippingMinAmount).toFixed(2);
                tr.appendChild(td);
            } else if (totalAmount >= supplier.freeShippingMinAmount) {
                td = document.createElement("td");
                td.innerHTML = "You're getting free shipping";
                tr.appendChild(td);
            }
        }

        //bottone per completare l'ordine

        td = document.createElement("td");
        td.setAttribute("colspan", "4");
        let form = document.createElement("form");
        let label = document.createElement("label");
        label.innerHTML = "Select an address";

        let select = document.createElement("select");
        select.setAttribute("name", "userShippingAddressId");

        //l'id da settare sarà id=supX-prodY-label
        const addressId = "shipping-address-id-select";
        select.setAttribute("id", addressId);
        let option;

        let firstIter = true;
        userInfo.shippingAddresses.forEach( (function (address) {
            option = document.createElement("option");
            option.setAttribute("value", address.shippingAddressId);
            if (firstIter) {
                option.selected = true;
                firstIter = false;
            }
            option.innerHTML = address.recipient + " " + address.address + " " + address.city + " " + address.state;
            select.appendChild(option);
        }));

        label.appendChild(select);
        form.appendChild(label);

        let input = document.createElement("input");
        input.setAttribute("type", "hidden");
        input.setAttribute("name", "supplierId");
        input.setAttribute("value", supplier.supplierId);

        form.appendChild(input);

        let form_input = document.createElement("input");
        form_input.classList.add("clickable-link","clickable-link-large");
        form_input.setAttribute("type", "submit");
        form_input.setAttribute("value", "Place order");

        form.appendChild(form_input);

        //chiamata alla servlet place order
        //Attach to register button
        form_input.addEventListener("click", (e) => {
            e.preventDefault();

            let form = e.target.closest("form");
            const supplierId = parseInt(form.querySelector("input[name='supplierId']").value);
            const shippingAddressId = parseInt(document.getElementById(addressId).value);

            //prodotti in json
            let prodJson = JSON.stringify(shoppingCart.prods.get(supplierId));

            shoppingCart.shopping_cart_div_message.style.display = 'none';

            let formData = new FormData(form);
            formData.append("prodsJson",prodJson);



            if (form.checkValidity()) { //Do form check
                makeCall("POST", 'PlaceOrder', formData, function(req){
                    switch(req.status){ //Get status code
                        case 200: //order filled
                            shoppingCart.emptyShoppingCart(supplierId);
                            shoppingCart.shopping_cart_div_message.innerHTML = "Order filled successfully";
                            shoppingCart.shopping_cart_div_message.style.display = 'block';
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
            } else {
                form.reportValidity();
            } //If not valid, notify

        });



        td.appendChild(form);
        tr.appendChild(td);
        tbody.appendChild(tr);
        table.appendChild(tbody);


    } else { //altrimenti si crea tutt
        shoppingCart.prods.forEach( (function(products) {
            let supplier = products[0].supplier;
            let totalAmount = shoppingCart.totalAmountBySupplier.get(supplier.supplierId);
            let tbody = document.createElement("tbody");
            tbody.setAttribute("id", "sup" + supplier.supplierId + "-cart-body");

            //qua si cicla per prodotti di un singolo venditore
            products.forEach( (function (prod) {
                tr = document.createElement("tr");

                //immagine prodotto
                let td = document.createElement("td");
                td.className = "table-product-img";
                let img = document.createElement("img");
                img.src = getProductsImageFolderURL().concat(prod.productImagePath);
                img.alt = "Product image";
                td.appendChild(img);
                tr.appendChild(td);

                //nome prodotto
                td = document.createElement("td");
                td.innerHTML = prod.productName;
                tr.appendChild(td);

                //desc prodotto
                td = document.createElement("td");
                td.innerHTML = prod.productDescription;
                tr.appendChild(td);

                //cat prodotto
                td = document.createElement("td");
                td.innerHTML = prod.productCategory;
                tr.appendChild(td);

                //cat prodotto
                td = document.createElement("td");
                td.innerHTML = "€" + prod.supplierProductCost;
                tr.appendChild(td);

                //howMany
                td = document.createElement("td");
                td.innerHTML = prod.howMany;
                tr.appendChild(td);

                tbody.appendChild(tr);
            }));

            //riga con info su costo di spedizione e scelta indirizzo;
            tr = document.createElement("tr");
            tr.classList.add("active-row");

            let td = document.createElement("td");
            td.innerHTML = "Total amount on " + supplier.supplierName + "€ " + shoppingCart.totalAmountBySupplier.get(supplier.supplierId);
            tr.appendChild(td);

            //check if free shipping
            if(!supplier.hasFreeShipping) {
                td = document.createElement("td");
                td.innerHTML = supplier.supplierName + " doesn't offer free shipping";
                tr.appendChild(td);
            } else {
                if (totalAmount < supplier.freeShippingMinAmount) {
                    td = document.createElement("td");
                    td.innerHTML = "Free shipping on " + supplier.supplierName + ": € " + supplier.freeShippingMinAmount;
                    tr.appendChild(td);
                } else if (totalAmount >= supplier.freeShippingMinAmount) {
                    td = document.createElement("td");
                    td.innerHTML = "You're getting free shipping";
                    tr.appendChild(td);
                }
            }

            //bottone per completare l'ordine

            td = document.createElement("td");
            td.setAttribute("colspan", "4");
            let form = document.createElement("form");
            let label = document.createElement("label");
            label.innerHTML = "Select an address";

            let select = document.createElement("select");
            select.setAttribute("name", "userShippingAddressId");

            //l'id da settare sarà id=supX-prodY-label
            const addressId = "shipping-address-id-select";
            select.setAttribute("id", addressId);
            let option;

            let firstIter = true;
            userInfo.shippingAddresses.forEach( (function (address) {
                option = document.createElement("option");
                option.setAttribute("value", address.shippingAddressId);
                if (firstIter) {
                    option.selected = true;
                    firstIter = false;
                }
                option.innerHTML = address.recipient + " " + address.address + " " + address.city + " " + address.state;
                select.appendChild(option);
            }));

            label.appendChild(select);
            form.appendChild(label);

            let input = document.createElement("input");
            input.setAttribute("type", "hidden");
            input.setAttribute("name", "supplierId");
            input.setAttribute("value", supplier.supplierId);

            form.appendChild(input);

            let form_input = document.createElement("input");
            form_input.classList.add("clickable-link","clickable-link-large");
            form_input.setAttribute("type", "submit");
            form_input.setAttribute("value", "Place order");

            form.appendChild(form_input);

            //chiamata alla servlet place order
            //Attach to register button
            form_input.addEventListener("click", (e) => {
                e.preventDefault();

                let form = e.target.closest("form");
                const supplierId = parseInt(form.querySelector("input[name='supplierId']").value);
                const shippingAddressId = parseInt(document.getElementById(addressId).value);

                //prodotti in json
                let prodJson = JSON.stringify(shoppingCart.prods.get(supplierId));

                shoppingCart.shopping_cart_div_message.style.display = 'none';

                let formData = new FormData(form);
                formData.append("prodsJson",prodJson);



                if (form.checkValidity()) { //Do form check
                    makeCall("POST", 'PlaceOrder', formData, function(req){
                        switch(req.status){ //Get status code
                            case 200: //order filled
                                shoppingCart.emptyShoppingCart(supplierId);
                                shoppingCart.shopping_cart_div_message.innerHTML = "Order filled successfully";
                                shoppingCart.shopping_cart_div_message.style.display = 'block';
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
                } else {
                    form.reportValidity();
                } //If not valid, notify

            });



            td.appendChild(form);
            tr.appendChild(td);
            tbody.appendChild(tr);
            table.appendChild(tbody);
        }));
    }

    return table;

}