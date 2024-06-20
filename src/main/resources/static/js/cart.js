document.addEventListener('DOMContentLoaded', function () {
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                // 로그인 상태이면 서버와 동기화
                uploadLocalCartToServer(); // 로컬 스토리지 데이터를 서버로 업로드
            } else {
                // 비로그인 상태이면 로컬 스토리지에서 데이터 로드
                loadCartItemsFromLocalStorage();
                loadTotalPriceFromLocalStorage();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            loadCartItemsFromLocalStorage();
            loadTotalPriceFromLocalStorage();
        });

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
    const totalPrice = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
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
            <img src="${item.imageUrl}" alt="${item.name}">
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
    const totalPrice = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    document.getElementById('total-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
    document.getElementById('final-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
}

function updateQuantity(optionId, quantity) {
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                // 로그인 상태에서 서버로 업데이트 요청
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
                // 비로그인 상태에서 로컬 스토리지 업데이트
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
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                // 로그인 상태에서 서버로 삭제 요청
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
                // 비로그인 상태에서 로컬 스토리지에서 삭제
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
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                // 로그인 상태에서 서버로 모든 항목 삭제 요청
                fetch('/cart/all', {
                    method: 'DELETE'
                })
                    .then(() => {
                        localStorage.setItem('cart', JSON.stringify([]));
                        loadCartItemsFromLocalStorage();
                        loadTotalPriceFromLocalStorage();
                    });
            } else {
                // 비로그인 상태에서 로컬 스토리지에서 모든 항목 삭제
                localStorage.setItem('cart', JSON.stringify([]));
                loadCartItemsFromLocalStorage();
                loadTotalPriceFromLocalStorage();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
        });
}

async function handleOrder() {
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

function uploadLocalCartToServer() {
    const localCartItems = JSON.parse(localStorage.getItem('cart')) || [];
    if (localCartItems.length === 0) {
        // 로컬 스토리지에 항목이 없으면 동기화할 필요가 없음
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