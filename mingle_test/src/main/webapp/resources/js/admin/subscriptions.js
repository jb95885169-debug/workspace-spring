document.addEventListener('DOMContentLoaded', loadProducts);

function loadProducts() {
    fetch(contextPath + '/api/admin/subscriptions/products')
        .then(readResponse)
        .then(function (products) {
            var list = document.getElementById('productList');
            list.textContent = '';
            products.forEach(function (product) {
                list.appendChild(createProductCard(product));
            });
        })
        .catch(showError);
}

function createProductCard(product) {
    var card = document.createElement('article');
    card.className = 'product-card';
    card.innerHTML = '<div class="product-top"><h2></h2><span class="tier"></span></div>'
        + '<div class="fields">'
        + '<label>가격(원)<input class="price" type="number" min="0"></label>'
        + '<label>기간(일)<input class="duration" type="number" min="1"></label>'
        + '<label>설명<textarea class="description"></textarea></label>'
        + '<label class="switch">판매<input class="active" type="checkbox"></label>'
        + '</div><div class="actions"><button type="button">저장</button></div><p class="status" hidden></p>';

    card.querySelector('h2').textContent = product.name;
    card.querySelector('.tier').textContent = product.tier;
    card.querySelector('.price').value = product.price;
    card.querySelector('.duration').value = product.durationDays;
    card.querySelector('.description').value = product.description || '';
    card.querySelector('.active').checked = product.active;
    card.querySelector('button').addEventListener('click', function () {
        saveProduct(product.id, card);
    });
    return card;
}

function saveProduct(productId, card) {
    var button = card.querySelector('button');
    var status = card.querySelector('.status');
    button.disabled = true;
    status.hidden = true;
    fetch(contextPath + '/api/admin/subscriptions/products/' + productId, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            price: Number(card.querySelector('.price').value),
            durationDays: Number(card.querySelector('.duration').value),
            description: card.querySelector('.description').value,
            active: card.querySelector('.active').checked
        })
    }).then(readResponse).then(function () {
        status.textContent = '저장되었습니다.';
        status.className = 'status';
        status.hidden = false;
    }).catch(function (error) {
        status.textContent = error.message;
        status.className = 'status error';
        status.hidden = false;
    }).finally(function () {
        button.disabled = false;
    });
}

function readResponse(response) {
    return response.json().catch(function () { return {}; }).then(function (data) {
        if (!response.ok) {
            throw new Error(data.message || '관리자 요청에 실패했습니다.');
        }
        return data;
    });
}

function showError(error) {
    var list = document.getElementById('productList');
    list.innerHTML = '<p class="error"></p>';
    list.querySelector('.error').textContent = error.message;
}
