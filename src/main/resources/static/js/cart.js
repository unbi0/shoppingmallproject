document.addEventListener('DOMContentLoaded', function () {
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                synchronizeCartWithServer();
            } else {
                loadCartItemsFromLocalStorage();
                loadTotalPriceFromLocalStorage();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            loadCartItemsFromLocalStorage();
            loadTotalPriceFromLocalStorage();
        });

    // "주문하기" 버튼 클릭 이벤트 처리
    const orderButton = document.getElementById('order-button');
    orderButton.addEventListener('click', handleOrder);
});

function loadCartItemsFromLocalStorage() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    renderCartItems(cartItems);
    loadTotalPriceFromLocalStorage();
}

function loadTotalPriceFromLocalStorage() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const totalPrice = cartItems.reduce((total, item) => total + (item.productPrice * item.quantity), 0);
    document.getElementById('total-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
    document.getElementById('final-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
}

function synchronizeCartWithServer() {
    fetch('/cart')
        .then(response => response.json())
        .then(serverCartItems => {
            const localCartItems = JSON.parse(localStorage.getItem('cart')) || [];
            const mergedCartItems = mergeCarts(localCartItems, serverCartItems);
            localStorage.setItem('cart', JSON.stringify(mergedCartItems));
            renderCartItems(mergedCartItems);
            loadTotalPriceFromLocalStorage();
        })
        .catch(error => console.error('Error synchronizing cart with server:', error));
}

function mergeCarts(localCartItems, serverCartItems) {
    const mergedCart = [...serverCartItems];
    localCartItems.forEach(localItem => {
        const existingItem = mergedCart.find(item => item.optionId === localItem.optionId);
        if (existingItem) {
            existingItem.quantity = Math.max(existingItem.quantity, localItem.quantity);
        } else {
            mergedCart.push(localItem);
        }
    });
    return mergedCart;
}

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
            <img src="${item.imageUrl}" alt="${item.productName}">
            <div class="item-details">
                <h2>${item.productName}</h2>
                <p class="price">Unit Price: KRW ${item.productPrice.toFixed(2)}</p>
                <p class="price">Total: KRW ${(item.productPrice * item.quantity).toFixed(2)}</p>
                <p class="size">Size: ${item.productSize}</p>
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

function updateTotalPrice(cartItems) {
    const totalPrice = cartItems.reduce((total, item) => total + (item.productPrice * item.quantity), 0);
    document.getElementById('total-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
    document.getElementById('final-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
}

function updateQuantity(cartId, quantity) {
    console.log(`Updating quantity for cartId: ${cartId}, quantity: ${quantity}`);
    if (quantity < 1) {
        return;
    }

    fetch(`/cart/${cartId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ quantity: quantity })
    })
        .then(response => {
            console.log('Server response:', response);
            if (!response.ok) {
                return response.json().then(errorData => {
                    console.error('Error details:', errorData);
                    throw new Error('Error updating quantity');
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Updated cart item:', data);
            const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
            const updatedCartItems = cartItems.map(item => item.cartId === cartId ? { ...item, quantity: data.quantity } : item);
            localStorage.setItem('cart', JSON.stringify(updatedCartItems));
            renderCartItems(updatedCartItems);
            updateTotalPrice(updatedCartItems);
        })
        .catch(error => {
            console.error('Error updating quantity:', error);
        });
}

function removeCartItem(cartId) {
    console.log(`Removing cart item with cartId: ${cartId}`);
    fetch(`/cart/${cartId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    console.error('Error details:', errorData);
                    throw new Error('Error removing cart item');
                });
            }
            console.log('Cart item removed');
            const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
            const updatedCartItems = cartItems.filter(item => item.cartId !== cartId);
            localStorage.setItem('cart', JSON.stringify(updatedCartItems));
            renderCartItems(updatedCartItems);
            updateTotalPrice(updatedCartItems);
        })
        .catch(error => {
            console.error('Error removing cart item:', error);
        });
}

function clearCart() {
    fetch('/cart/all', {
        method: 'DELETE'
    })
        .then(() => {
            localStorage.setItem('cart', JSON.stringify([]));
            loadCartItemsFromLocalStorage();
            loadTotalPriceFromLocalStorage();
        });
}

// 주문하기 버튼 클릭 이벤트 핸들러
function handleOrder() {
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const orderData = cartItems.map(item => ({
        productId: item.optionId, // Assuming optionId is the productId
        productName: item.productName,
        price: item.productPrice,
        optionSize: item.productSize,
        count: item.quantity,
        imageUrl: item.imageUrl
    }));

    // orderData를 서버로 전송
    fetch('/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(orderData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    console.error('Error details:', errorData);
                    throw new Error('Error submitting order');
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Order submitted successfully:', data);
            location.href = '/order/order.html'; // 주문서 작성 페이지로 이동
        })
        .catch(error => {
            console.error('Error submitting order:', error);
        });
}