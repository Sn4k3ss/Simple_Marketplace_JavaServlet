


/**
 * Utils for creating new row from product
 * -------------
 *
 *
 */
function createProductsRow(supplierProduct, shoppingCart, searchResultNum, resultsNum) {


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
    td.innerHTML = supplierProduct.supplierProductCost;
    row.appendChild(td);

    //td immagine venditore
    td = document.createElement("td");
    td.className = "table-supplier-img";

    img = document.createElement("img");
    img.src = getSuppliersImageFolderURL().concat(supplierProduct.supplier.imagePath); //set img
    td.appendChild(img);
    row.appendChild(td);

    //td buy button
    td = document.createElement("td");
    let form = document.createElement("form");
    form.className = "styled-form";
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
    //TODO l'aggiungi al carrello non fa nessuna chiamata al server
    form_input.addEventListener("click", (e) => {
        e.preventDefault();
        let addToCartForm = e.currentTarget.closest("form");
        let productRow = e.currentTarget.closest("tr");

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

    return row;
}


//crea una table con intestazione e crelativa classe di stile css
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
    th.innerHTML = "AddToCart";
    tr.appendChild(th);

    thead.appendChild(tr);
    table.appendChild(thead);

    let tbody = document.createElement("tbody");
    table.appendChild(tbody);

    return table;

}

function createSupplierProductsRow(supplierProduct, shoppingCart) {

    let row = document.createElement("tr");

    //td immagine supplier
    let td = document.createElement("td");
    td.className = "table-supplier-img";
    let img = document.createElement("img");
    img.src = getProductsImageFolderURL().concat(supplierProduct.supplier.imagePath); //set img
    td.appendChild(img);
    row.appendChild(td);

    //td costo prodotto
    td = document.createElement("td");
    td.innerHTML = supplierProduct.supplierProductCost;
    row.appendChild(td);

    //td rating
    td = document.createElement("td");
    td.innerHTML = supplierProduct.supplier.supplierRating;
    row.appendChild(td);

    //td costo minimo spedizione gratuita
    td = document.createElement("td");
    if (supplierProduct.supplier.hasFreeShipping)
        td.innerHTML = supplierProduct.supplier.freeShippingMinAmount;
    else
        td.innerHTML = "No";

    row.appendChild(td);

    //range di prezzo
    td = document.createElement("td");
    td.appendChild(createSupplierRangesTable(supplierProduct.supplier.supplierShippingPolicy));
    row.appendChild(td);


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
    select.setAttribute("id", "howMany");

    for (let i = 0; i < 9; i++){
        select.options[select.options.length] = new Option(i+1, i);
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
        let addToCartForm = e.currentTarget.closest("form");
        let productRow = e.currentTarget.closest("tr");

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