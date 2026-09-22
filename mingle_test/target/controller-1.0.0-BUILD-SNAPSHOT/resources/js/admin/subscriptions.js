document.addEventListener('DOMContentLoaded', loadAdminOverview);

function loadAdminOverview() {
    fetch(contextPath + '/api/admin/management')
        .then(readResponse)
        .then(function (data) {
            renderProducts(data.products);
            renderUsers(data.users);
            renderTickets(data.tickets);
            renderReports(data.reports);
        })
        .catch(showError);
}

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

function renderProducts(products) {
    var list = document.getElementById('productList');
    list.textContent = '';
    products.forEach(function (product) { list.appendChild(createProductCard(product)); });
}

function renderUsers(users) {
    var list = document.getElementById('userList');
    list.textContent = '';
    users.forEach(function (user) {
        var row = document.createElement('div');
        row.className = 'admin-row';
        row.innerHTML = '<strong></strong><small></small><select><option>ACTIVE</option><option>BANNED</option><option>WITHDRAWN</option></select><button>변경</button>';
        row.querySelector('strong').textContent = user.id;
        row.querySelector('small').textContent = user.email;
        row.querySelector('select').value = user.status;
        row.querySelector('button').onclick = function () {
            adminPatch('/users/' + user.id + '/status', { status: row.querySelector('select').value }, row.querySelector('button'));
        };
        list.appendChild(row);
    });
    if (!users.length) list.innerHTML = '<p class="empty">회원이 없습니다.</p>';
}

function renderTickets(tickets) {
    var list = document.getElementById('ticketList');
    list.textContent = '';
    tickets.forEach(function (ticket) {
        var row = document.createElement('div');
        row.className = 'admin-row';
        row.innerHTML = '<strong></strong><div><b></b><p></p><textarea placeholder="답변 입력"></textarea></div><small></small><button>답변</button>';
        row.querySelector('strong').textContent = '#' + ticket.id;
        row.querySelector('b').textContent = ticket.title || ticket.category;
        row.querySelector('p').textContent = ticket.content;
        row.querySelector('small').textContent = ticket.status;
        row.querySelector('button').onclick = function () {
            adminPatch('/tickets/' + ticket.id, { answer: row.querySelector('textarea').value }, row.querySelector('button'));
        };
        list.appendChild(row);
    });
    if (!tickets.length) list.innerHTML = '<p class="empty">문의가 없습니다.</p>';
}

function renderReports(reports) {
    var list = document.getElementById('reportList');
    list.textContent = '';
    reports.forEach(function (report) {
        var row = document.createElement('div');
        row.className = 'admin-row';
        row.innerHTML = '<strong></strong><div class="report-detail"><b></b><p class="report-users"></p><p class="report-date"></p><p class="report-reason"></p><p class="report-content"></p></div><div class="report-actions"><label>정지 기간(일)<input class="suspension-days" type="number" min="1" max="3650" placeholder="일수"></label><button class="resolve-report">처리</button><button class="reject-report" type="button">조치없음</button></div>';
        row.querySelector('strong').textContent = '#' + report.id;
        row.querySelector('b').textContent = report.title || report.reason || '신고 내용';
        row.querySelector('.report-users').textContent = '신고자: ' + (report.reporterNickname || '-') + ' (#' + report.reporterId + ')  |  피신고자: ' + (report.targetNickname || '-') + ' (#' + report.targetUserId + ')';
        row.querySelector('.report-date').textContent = '신고일시: ' + formatReportDate(report.createdAt);
        row.querySelector('.report-reason').textContent = '신고 사유: ' + reportReasonLabel(report.reason);
        row.querySelector('.report-content').textContent = '신고 내용: ' + (report.content || '-');

        var daysInput = row.querySelector('.suspension-days');
        var resolveButton = row.querySelector('.resolve-report');
        var rejectButton = row.querySelector('.reject-report');
        resolveButton.onclick = function () {
            adminPatch('/reports/' + report.id, {
                status: 'RESOLVED',
                suspensionDays: Number(daysInput.value)
            }, resolveButton);
        };
        rejectButton.onclick = function () {
            adminPatch('/reports/' + report.id, {
                status: 'REJECTED'
            }, rejectButton);
        };

        if (report.status !== 'PENDING') {
            daysInput.disabled = true;
            resolveButton.disabled = true;
            rejectButton.disabled = true;
            row.classList.add('report-processed');
        }

        list.appendChild(row);
    });
    if (!reports.length) list.innerHTML = '<p class="empty">신고가 없습니다.</p>';
}

function reportReasonLabel(reason) {
    var labels = {
        SPAM: '스팸 및 홍보',
        ABUSE: '욕설 및 비방',
        INAPPROPRIATE: '부적절한 콘텐츠',
        FAKE_PROFILE: '사칭 및 도용',
        ETC: '기타'
    };
    return labels[reason] || reason || '-';
}

function formatReportDate(value) {
    if (!value) return '-';
    var date = new Date(value);
    return isNaN(date.getTime()) ? value : date.toLocaleString('ko-KR');
}

function adminPatch(path, body, button) {
    button.disabled = true;
    fetch(contextPath + '/api/admin/management' + path, {
        method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body)
    }).then(function (response) {
        if (!response.ok) {
            return response.json().catch(function () { return {}; }).then(function (data) {
                throw new Error(data.message || '관리자 처리에 실패했습니다.');
            });
        }
        return response;
    }).then(loadAdminOverview).catch(function (error) { alert(error.message); button.disabled = false; });
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
