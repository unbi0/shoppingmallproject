document.addEventListener('DOMContentLoaded', function () {
    const clearCartButton = document.querySelector('.checkout.clear');
    const orderButton = document.querySelector('.checkout.order');

    uploadLocalCartToServer();

    window.updateQuantity = function(cartId, quantity) {
        if (quantity < 1) {
            alert('수량은 1 이상이어야 합니다.');
            return;
        }

        fetch(`/cart/${cartId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ quantity })
        })
            .then(response => response.json())
            .then(cartItems => {
                fetchCartFromServer();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('장바구니 수량을 업데이트하는 중 오류가 발생했습니다.');
            });
    };

    window.removeCartItem = function(cartId) {
        fetch(`/cart/${cartId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                fetchCartFromServer();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('장바구니 아이템을 삭제하는 중 오류가 발생했습니다.');
            });
    };

    clearCartButton.addEventListener('click', function() {
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
    });

    orderButton.addEventListener('click', function() {
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
                window.localStorage.setItem('isCart', JSON.stringify(true));
                window.location.href = '/order';
            })
            .catch(error => {
                console.error('Error fetching cart for order:', error);
                alert('주문 데이터를 가져오는 중 오류가 발생했습니다.');
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
    }
});