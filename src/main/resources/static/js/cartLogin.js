console.log("cartLogin.js loaded");

function synchronizeCartWithServer() {
    console.log("Synchronizing cart with server...");
    fetch('/cart')
        .then(response => response.json())
        .then(serverCartItems => {
            console.log("Server cart items:", serverCartItems);
            renderCartItems(serverCartItems);
            updateTotalPrice(serverCartItems);
        })
        .catch(error => console.error('Error synchronizing cart with server:', error));
}

// 로컬 장바구니를 서버로 업로드하고 로컬 스토리지 비우기
function uploadLocalCartToServer() {
    console.log("Uploading local cart to server...");
    const localCartItems = JSON.parse(localStorage.getItem('cart')) || [];
    console.log("Local cart items:", localCartItems);
    if (localCartItems.length === 0) {
        synchronizeCartWithServer();
        return;
    }

    fetch('/cart/upload', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(localCartItems)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    console.error('Error details:', errorData);
                    throw new Error('Error uploading cart');
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Uploaded local cart to server:', data);
            localStorage.removeItem('cart'); // 로컬 스토리지 비우기
            synchronizeCartWithServer(); // 서버와 동기화
        })
        .catch(error => {
            console.error('Error uploading local cart to server:', error);
        });
}

function renderCartItems(cartItems) {
    console.log("Rendering cart items:", cartItems);
    const cartContainer = document.getElementById('cart-items');
    const itemCount = document.getElementById('item-count');
    cartContainer.innerHTML = '';
    let totalItems = 0;

    cartItems.forEach(item => {
        totalItems += item.quantity;
        const cartItem = document.createElement('div');
        cartItem.classList.add('cart-item');
        cartItem.innerHTML = `
            <img src="${item.imageUrl}" alt="${item.name}" onerror="this.onerror=null; this.src='/path/to/default/image.png';">
            <div class="item-details">
                <h2>${item.name}</h2>
                <p class="price">Unit Price: KRW ${item.price.toFixed(2)}</p>
                <p class="price">Total: KRW ${(item.price * item.quantity).toFixed(2)}</p>
                <p class="size">Size: ${item.size}</p>
                <div class="quantity">
                    <button onclick="updateQuantity(${item.optionId}, ${item.quantity - 1})">-</button>
                    <span>${item.quantity}</span>
                    <button onclick="updateQuantity(${item.optionId}, ${item.quantity + 1})">+</button>
                </div>
            </div>
            <button class="remove" onclick="removeCartItem(${item.optionId})">삭제</button>
        `;
        cartContainer.appendChild(cartItem);
    });

    itemCount.textContent = totalItems;
}

function updateTotalPrice(cartItems) {
    console.log("Updating total price...");
    const totalPrice = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    document.getElementById('total-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
    document.getElementById('final-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
}

function updateQuantity(optionId, quantity) {
    console.log(`Updating quantity for optionId: ${optionId}, quantity: ${quantity}`);
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                fetch(`/cart/update`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ optionId, quantity })
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(errorData => {
                                console.error('Error details:', errorData);
                                throw new Error('Error updating quantity');
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
                        const updatedCartItems = cartItems.map(item => item.optionId === optionId ? { ...item, quantity: data.quantity } : item);
                        localStorage.setItem('cart', JSON.stringify(updatedCartItems));
                        renderCartItems(updatedCartItems);
                        updateTotalPrice(updatedCartItems);
                    })
                    .catch(error => {
                        console.error('Error updating quantity:', error);
                    });
            } else {
                const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
                const updatedCartItems = cartItems.map(item => item.optionId === optionId ? { ...item, quantity: quantity } : item);
                localStorage.setItem('cart', JSON.stringify(updatedCartItems));
                renderCartItems(updatedCartItems);
                updateTotalPrice(updatedCartItems);
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
        });
}

function removeCartItem(optionId) {
    console.log(`Removing cart item with optionId: ${optionId}`);
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                fetch(`/cart/delete`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ optionId })
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(errorData => {
                                console.error('Error details:', errorData);
                                throw new Error('Error removing cart item');
                            });
                        }
                        const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
                        const updatedCartItems = cartItems.filter(item => item.optionId !== optionId);
                        localStorage.setItem('cart', JSON.stringify(updatedCartItems));
                        renderCartItems(updatedCartItems);
                        updateTotalPrice(updatedCartItems);
                    })
                    .catch(error => {
                        console.error('Error removing cart item:', error);
                    });
            } else {
                const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
                const updatedCartItems = cartItems.filter(item => item.optionId !== optionId);
                localStorage.setItem('cart', JSON.stringify(updatedCartItems));
                renderCartItems(updatedCartItems);
                updateTotalPrice(updatedCartItems);
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
        });
}

function clearCart() {
    console.log("Clearing cart...");
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                fetch('/cart/all', {
                    method: 'DELETE'
                })
                    .then(() => {
                        localStorage.setItem('cart', JSON.stringify([]));
                        loadCartItemsFromLocalStorage();
                        loadTotalPriceFromLocalStorage();
                    });
            } else {
                localStorage.setItem('cart', JSON.stringify([]));
                loadCartItemsFromLocalStorage();
                loadTotalPriceFromLocalStorage();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
        });
}

function handleOrder() {
    console.log("Handling order...");
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const orderData = cartItems.map((item) => {
        return {
            product_id: item.productID, // productID를 사용하도록 수정
            productName: item.name,
            price: item.price,
            optionSize: item.size,
            count: item.quantity,
            imageUrl: item.imageUrl
        };
    });

    localStorage.setItem('product', JSON.stringify(orderData));
    localStorage.setItem('iscart', true);

    location.href = '/order';
}