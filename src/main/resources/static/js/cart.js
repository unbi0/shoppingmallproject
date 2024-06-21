document.addEventListener('DOMContentLoaded', function () {
    const cartContainer = document.getElementById('cart-items');
    const itemCount = document.getElementById('item-count');
    const totalPriceElement = document.getElementById('total-price');
    const finalPriceElement = document.getElementById('final-price');
    const clearCartButton = document.querySelector('.checkout.clear');
    const orderButton = document.querySelector('.checkout.order');
    let isCart = true;
    let isLoggedIn = false;

    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                isLoggedIn = true;
                uploadLocalCartToServer();
            } else {
                const cartItems = JSON.parse(localStorage.getItem('productList')) || [];
                renderCartItems(cartItems, false);
                updateTotalPrice(cartItems);
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            const cartItems = JSON.parse(localStorage.getItem('productList')) || [];
            renderCartItems(cartItems, false);
            updateTotalPrice(cartItems);
        });

    function renderCartItems(cartItems, fromServer) {
        cartContainer.innerHTML = '';
        itemCount.textContent = cartItems.length;

        if (!Array.isArray(cartItems)) {
            cartItems = [];
        }

        cartItems.forEach(item => {
            const productName = fromServer ? item.productName : item.name;
            const productPrice = fromServer ? item.productPrice : parseFloat(item.price);
            const productSize = fromServer ? item.productSize : item.size;
            const cartId = fromServer ? item.cartId : item.optionId; // 서버 데이터에서는 cartId를 사용
            const optionId = fromServer ? item.optionId : item.optionId;

            const cartItem = document.createElement('div');
            cartItem.classList.add('cart-item');
            cartItem.innerHTML = `
                <img src="${item.imageUrl}" alt="${productName}">
                <div class="item-details">
                    <h2>${productName}</h2>
                    <p class="price">Unit Price: KRW ${productPrice.toLocaleString()}</p>
                    <p class="price">Total: KRW ${(productPrice * item.quantity).toLocaleString()}</p>
                    <p class="size">Size: ${productSize}</p>
                    <div class="quantity">
                        <button onclick="updateQuantity('${cartId}', '${optionId}', ${item.quantity - 1}, ${fromServer})">-</button>
                        <span>${item.quantity}</span>
                        <button onclick="updateQuantity('${cartId}', '${optionId}', ${item.quantity + 1}, ${fromServer})">+</button>
                    </div>
                </div>
                <button class="remove" onclick="removeCartItem('${cartId}', '${optionId}', ${fromServer})">삭제</button>
            `;
            cartContainer.appendChild(cartItem);
        });
    }

    function updateTotalPrice(cartItems) {
        if (!Array.isArray(cartItems)) {
            cartItems = [];
        }

        const totalPrice = cartItems.reduce((total, item) => {
            const price = item.productPrice || parseFloat(item.price);
            return total + (price * item.quantity);
        }, 0);
        totalPriceElement.textContent = `KRW ${totalPrice.toLocaleString()}`;
        finalPriceElement.textContent = `KRW ${totalPrice.toLocaleString()}`;
    }

    window.updateQuantity = function(cartId, optionId, quantity, fromServer) {
        if (quantity < 1) {
            alert('수량은 1 이상이어야 합니다.');
            return;
        }

        if (fromServer) {
            fetch('/loginCheck')
                .then(response => {
                    if (response.status === 204) {
                        return fetch(`/cart/${cartId}`, {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ quantity })
                        })
                            .then(response => response.json());
                    } else {
                        throw new Error('Not logged in');
                    }
                })
                .then(cartItems => {
                    if (cartItems) {
                        fetchCartFromServer();
                    }
                })
                .catch(error => {
                    console.log('Not logged in or error occurred:', error);
                    let cart = JSON.parse(localStorage.getItem('productList')) || [];
                    cart = cart.map(item => {
                        if (item.optionId === optionId) {
                            item.quantity = quantity;
                        }
                        return item;
                    });
                    localStorage.setItem('productList', JSON.stringify(cart));
                    renderCartItems(cart, false);
                    updateTotalPrice(cart);
                });
        } else {
            let cart = JSON.parse(localStorage.getItem('productList')) || [];
            cart = cart.map(item => {
                if (item.optionId === optionId) {
                    item.quantity = quantity;
                }
                return item;
            });
            localStorage.setItem('productList', JSON.stringify(cart));
            renderCartItems(cart, false);
            updateTotalPrice(cart);
        }
    };

    window.removeCartItem = function(cartId, optionId, fromServer) {
        if (fromServer) {
            fetch('/loginCheck')
                .then(response => {
                    if (response.status === 204) {
                        return fetch(`/cart/${cartId}`, {
                            method: 'DELETE',
                            headers: {
                                'Content-Type': 'application/json'
                            }})
                            .then(response => {
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                fetchCartFromServer();
                            })
                    } else {
                            throw new Error('Not logged in');
                        }
                    })
                .catch(error => {
                        console.log('Not logged in or error occurred:', error);
                        let cart = JSON.parse(localStorage.getItem('productList')) || [];
                        cart = cart.filter(item => item.optionId !== optionId);
                        localStorage.setItem('productList', JSON.stringify(cart));
                        renderCartItems(cart, false);
                        updateTotalPrice(cart);
                    });
                } else {
                let cart = JSON.parse(localStorage.getItem('productList')) || [];
                cart = cart.filter(item => item.optionId !== optionId);
                localStorage.setItem('productList', JSON.stringify(cart));
                renderCartItems(cart, false);
                updateTotalPrice(cart);
            }
        };

        clearCartButton.addEventListener('click', function() {
            fetch('/loginCheck')
                .then(response => {
                    if (response.status === 204) {
                        fetch('/cart/all', {
                            method: 'DELETE'
                        })
                            .then(() => {
                                fetchCartFromServer();
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                alert('장바구니를 비우는 중 오류가 발생했습니다.');
                            });
                    } else {
                        localStorage.removeItem('productList');
                        renderCartItems([], false);
                        updateTotalPrice([]);
                    }
                })
                .catch(error => {
                    console.error('Error checking login status:', error);
                });
        });

        orderButton.addEventListener('click', function() {
            fetch('/loginCheck')
                .then(response => {
                    if (response.status === 204) {
                        fetch('/cart')
                            .then(response => response.json())
                            .then(cartItems => {
                                if (cartItems.length === 0) {
                                    alert('장바구니에 상품이 없습니다.');
                                    return;
                                }
                                const formattedCartItems = cartItems.map(item => ({
                                    id: item.optionId,
                                    imageUrl: item.imageUrl,
                                    name: item.productName,
                                    optionId: item.optionId,
                                    price: item.productPrice,
                                    quantity: item.quantity,
                                    size: item.productSize
                                }));
                                localStorage.setItem('productList', JSON.stringify(formattedCartItems));
                                window.localStorage.setItem('isCart', JSON.stringify(isCart));
                                window.location.href = '/order';
                            })
                            .catch(error => {
                                console.error('Error fetching cart for order:', error);
                                alert('주문 데이터를 가져오는 중 오류가 발생했습니다.');
                            });
                    } else {
                        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                        window.location.href = '/loginForm';
                    }
                })
                .catch(error => {
                    console.error('Error checking login status:', error);
                });
        });

        function uploadLocalCartToServer() {
            const localCartItems = JSON.parse(localStorage.getItem('productList')) || [];
            if (localCartItems.length === 0) {
                fetchCartFromServer();
                return;
            }

            const uploadPromises = localCartItems.map(item => {
                const cartCreateDTO = {
                    optionId: item.optionId,
                    quantity: item.quantity
                };

                return fetch('/cart', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(cartCreateDTO)
                });
            });

            Promise.all(uploadPromises)
                .then(() => {
                    localStorage.removeItem('productList');
                    fetchCartFromServer();
                })
                .catch(error => {
                    console.error('Error uploading local cart to server:', error);
                    alert('로컬 장바구니 데이터를 서버로 업로드하는 중 오류가 발생했습니다.');
                });
        }


        function fetchCartFromServer() {
            fetch('/cart')
                .then(response => response.json())
                .then(cartItems => {
                    renderCartItems(cartItems, true);
                    updateTotalPrice(cartItems);
                })
                .catch(error => {
                    console.error('Error fetching cart from server:', error);
                    alert('장바구니 데이터를 가져오는 중 오류가 발생했습니다.');
                });
        }});
