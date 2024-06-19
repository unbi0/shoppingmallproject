document.addEventListener('DOMContentLoaded', function () {
    // 서버로부터 로그인 상태 확인
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                // 로그인된 상태
                synchronizeCartWithServer();
            } else {
                // 로그인되지 않은 상태
                loadCartItemsFromLocalStorage();
                loadTotalPriceFromLocalStorage();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            loadCartItemsFromLocalStorage();
            loadTotalPriceFromLocalStorage();
        });
});

// 로컬 스토리지에서 장바구니 항목을 불러와서 화면에 렌더링하는 함수
function loadCartItemsFromLocalStorage() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const cartContainer = document.getElementById('cart-items');
    const itemCount = document.getElementById('item-count');
    cartContainer.innerHTML = '';
    let totalItems = 0;

    cartItems.forEach(item => {
        totalItems += item.quantity;
        const cartItem = document.createElement('div');
        cartItem.classList.add('cart-item');
        cartItem.innerHTML = `
            <img src="${item.productImageUrl}" alt="${item.productName}">
            <div class="item-details">
                <h2>${item.productName}</h2>
                <p class="price">KRW ${item.productPrice * item.quantity}</p>
                <div class="quantity">
                    <button onclick="updateQuantityInLocalStorage(${item.cartId}, ${item.quantity - 1})">-</button>
                    <span>${item.quantity}</span>
                    <button onclick="updateQuantityInLocalStorage(${item.cartId}, ${item.quantity + 1})">+</button>
                </div>
            </div>
            <button class="remove" onclick="removeCartItemFromLocalStorage(${item.cartId})">삭제</button>
        `;
        cartContainer.appendChild(cartItem);
    });

    itemCount.textContent = totalItems;
}

// 로컬 스토리지에서 총 가격을 불러와서 화면에 렌더링하는 함수
function loadTotalPriceFromLocalStorage() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const totalPriceContainer = document.getElementById('total-price');
    const finalPriceContainer = document.getElementById('final-price');
    const totalPrice = cartItems.reduce((total, item) => total + (item.productPrice * item.quantity), 0);

    totalPriceContainer.textContent = `KRW ${totalPrice.toFixed(2)}`;
    finalPriceContainer.textContent = `KRW ${(totalPrice - discount).toFixed(2)}`;
}

// 서버로부터 장바구니 항목을 불러와서 로컬 스토리지와 동기화하는 함수
function synchronizeCartWithServer() {
    fetch('/cart')
        .then(response => response.json())
        .then(serverCartItems => {
            const localCartItems = JSON.parse(localStorage.getItem('cart')) || [];
            const mergedCartItems = mergeCarts(localCartItems, serverCartItems);

            localStorage.setItem('cart', JSON.stringify(mergedCartItems));
            renderCartItems(mergedCartItems);
            loadTotalPrice();
        })
        .catch(error => console.error('Error synchronizing cart with server:', error));
}

// 로컬 스토리지와 서버의 장바구니를 병합하는 함수
function mergeCarts(localCartItems, serverCartItems) {
    const mergedCart = [...serverCartItems];

    localCartItems.forEach(localItem => {
        const existingItem = mergedCart.find(item => item.optionId === localItem.optionId);
        if (existingItem) {
            existingItem.quantity += localItem.quantity;
        } else {
            mergedCart.push(localItem);
        }
    });

    return mergedCart;
}

// 서버에서 장바구니 항목을 불러와서 화면에 렌더링하는 함수
function renderCartItems(cartItems) {
    const cartContainer = document.getElementById('cart-items');
    const itemCount = document.getElementById('item-count');
    cartContainer.innerHTML = '';
    let totalItems = 0;

    cartItems.forEach(item => {
        totalItems += item.quantity;
        const cartItem = document.createElement('div');
        cartItem.classList.add('cart-item');
        cartItem.innerHTML = `
            <img src="${item.productImageUrl}" alt="${item.productName}">
            <div class="item-details">
                <h2>${item.productName}</h2>
                <p class="price">KRW ${item.productPrice * item.quantity}</p>
                <div class="quantity">
                    <button onclick="updateQuantity(${item.cartId}, ${item.quantity - 1})">-</button>
                    <span>${item.quantity}</span>
                    <button onclick="updateQuantity(${item.cartId}, ${item.quantity + 1})">+</button>
                </div>
            </div>
            <button class="remove" onclick="removeCartItem(${item.cartId})">삭제</button>
        `;
        cartContainer.appendChild(cartItem);
    });

    itemCount.textContent = totalItems;
}

// 서버에서 총 가격을 불러와서 화면에 렌더링하는 함수
function loadTotalPrice() {
    fetch('/cart/total')
        .then(response => response.json())
        .then(data => {
            const totalPriceContainer = document.getElementById('total-price');
            const finalPriceContainer = document.getElementById('final-price');
            const discountPriceContainer = document.getElementById('discount-price');
            const discount = 103200; // 예시 할인 금액
            totalPriceContainer.textContent = `KRW ${data.toFixed(2)}`;
            discountPriceContainer.textContent = `-KRW ${discount.toFixed(2)}`;
            finalPriceContainer.textContent = `KRW ${(data - discount).toFixed(2)}`;
        });
}

// 로컬 스토리지의 장바구니 항목의 수량을 업데이트하는 함수
function updateQuantityInLocalStorage(cartId, quantity) {
    if (quantity < 1) return;
    let cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const itemIndex = cartItems.findIndex(item => item.cartId === cartId);
    if (itemIndex !== -1) {
        cartItems[itemIndex].quantity = quantity;
        localStorage.setItem('cart', JSON.stringify(cartItems));
        loadCartItemsFromLocalStorage();
        loadTotalPriceFromLocalStorage();
    }
}

// 로컬 스토리지의 장바구니 항목을 삭제하는 함수
function removeCartItemFromLocalStorage(cartId) {
    let cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    cartItems = cartItems.filter(item => item.cartId !== cartId);
    localStorage.setItem('cart', JSON.stringify(cartItems));
    loadCartItemsFromLocalStorage();
    loadTotalPriceFromLocalStorage();
}

// 서버에서 장바구니 항목의 수량을 업데이트하는 함수
function updateQuantity(cartId, quantity) {
    if (quantity < 1) return;
    fetch(`/cart/${cartId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ quantity: quantity })
    })
        .then(response => response.json())
        .then(() => {
            synchronizeCartWithServer();
        });
}

// 서버에서 장바구니 항목을 삭제하는 함수
function removeCartItem(cartId) {
    fetch(`/cart/${cartId}`, {
        method: 'DELETE'
    })
        .then(() => {
            synchronizeCartWithServer();
        });
}

// 장바구니를 비우는 함수
function clearCart() {
    fetch('/cart/all', {
        method: 'DELETE'
    })
        .then(() => {
            localStorage.setItem('cart', JSON.stringify([])); // 로컬 스토리지 초기화
            loadCartItemsFromLocalStorage();
            loadTotalPriceFromLocalStorage();
        });
}
