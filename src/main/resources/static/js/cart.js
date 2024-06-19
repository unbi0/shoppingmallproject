// 장바구니 관련 로직을 처리하는 파일입니다.

// 장바구니 데이터를 로컬스토리지에서 가져오는 함수
function getLocalCart() {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : [];
}

// 장바구니 데이터를 로컬스토리지에 저장하는 함수
function setLocalCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
}

// 로그인 여부를 확인하는 함수
function isUserLoggedIn() {
    // 실제 구현에 따라 로그인 여부를 확인하는 로직을 추가하세요.
    // 예를 들어, 쿠키나 로컬스토리지에서 토큰을 확인할 수 있습니다.
    return !!localStorage.getItem('authToken');
}

// 로그인 상태에 따라 장바구니 데이터를 가져오는 함수
function loadCartData() {
    if (isUserLoggedIn()) {
        // 로그인 상태라면 서버에서 장바구니 데이터를 가져옵니다.
        fetch('/api/cart')
            .then(response => response.json())
            .then(cartItems => {
                renderCartItems(cartItems);
                calculateAndDisplayTotalPrice(cartItems);
            });
    } else {
        // 비로그인 상태라면 로컬스토리지에서 장바구니 데이터를 가져옵니다.
        const cartItems = getLocalCart();
        renderCartItems(cartItems);
        calculateAndDisplayTotalPrice(cartItems);
    }
}

// 장바구니 아이템을 화면에 렌더링하는 함수
function renderCartItems(cartItems) {
    const cartContainer = document.getElementById('cart-container');
    cartContainer.innerHTML = '';

    cartItems.forEach(item => {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td><img src="${item.productImageUrl}" alt="Product Image" width="50" height="50"></td>
            <td>${item.productName}</td>
            <td>${item.productSize}</td>
            <td>${item.productPrice}</td>
            <td>${item.quantity}</td>
            <td>
                <input type="number" value="${item.quantity}" min="1" onchange="updateCartItem(${item.cartId}, this.value)">
                <button onclick="deleteCartItem(${item.cartId})">Delete</button>
            </td>
        `;

        cartContainer.appendChild(row);
    });
}

// 총 가격을 계산하고 HTML에 표시하는 함수
function calculateAndDisplayTotalPrice(cartItems) {
    const totalPrice = cartItems.reduce((total, item) => {
        return total + (item.productPrice * item.quantity);
    }, 0);

    const totalPriceContainer = document.getElementById('total-price-container');
    totalPriceContainer.textContent = 'Total Price: ' + totalPrice + ' 원';
}

// 장바구니 아이템을 업데이트하는 함수
function updateCartItem(cartId, quantity) {
    if (isUserLoggedIn()) {
        // 로그인 상태라면 서버에 업데이트 요청을 보냅니다.
        fetch(`/api/cart/${cartId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ quantity }),
        }).then(loadCartData);
    } else {
        // 비로그인 상태라면 로컬스토리지에서 업데이트합니다.
        const cart = getLocalCart();
        const itemIndex = cart.findIndex(item => item.cartId === cartId);
        if (itemIndex !== -1) {
            cart[itemIndex].quantity = quantity;
            setLocalCart(cart);
            loadCartData();
        }
    }
}

// 장바구니 아이템을 삭제하는 함수
function deleteCartItem(cartId) {
    if (isUserLoggedIn()) {
        // 로그인 상태라면 서버에 삭제 요청을 보냅니다.
        fetch(`/api/cart/${cartId}`, {
            method: 'DELETE',
        }).then(loadCartData);
    } else {
        // 비로그인 상태라면 로컬스토리지에서 삭제합니다.
        const cart = getLocalCart();
        const updatedCart = cart.filter(item => item.cartId !== cartId);
        setLocalCart(updatedCart);
        loadCartData();
    }
}

// 전체 장바구니를 비우는 함수
function clearCart() {
    if (isUserLoggedIn()) {
        fetch('/api/cart/all', {
            method: 'DELETE',
        }).then(loadCartData);
    } else {
        localStorage.removeItem('cart');
        loadCartData();
    }
}

// 장바구니 초기화
document.addEventListener('DOMContentLoaded', loadCartData);

// 'Add to Cart' 버튼 클릭 시 처리하는 함수 (예제)
document.getElementById('addToCartButton').addEventListener('click', () => {
    const productId = document.getElementById('productId').value;
    const quantity = document.getElementById('quantity').value;

    if (isUserLoggedIn()) {
        fetch('/api/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ productId, quantity }),
        }).then(loadCartData);
    } else {
        const cart = getLocalCart();
        const itemIndex = cart.findIndex(item => item.productId === productId);

        if (itemIndex !== -1) {
            cart[itemIndex].quantity += parseInt(quantity);
        } else {
            cart.push({ cartId: Date.now(), productId, quantity: parseInt(quantity), productImageUrl: 'example.jpg', productName: 'Example', productSize: 'M', productPrice: '100' });
        }

        setLocalCart(cart);
        loadCartData();
    }
});
