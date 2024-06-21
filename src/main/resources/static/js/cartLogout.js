document.addEventListener('DOMContentLoaded', function () {
    const clearCartButton = document.querySelector('.checkout.clear');
    const orderButton = document.querySelector('.checkout.order');

    const cartItems = JSON.parse(localStorage.getItem('productList')) || [];
    renderCartItems(cartItems, false);
    updateTotalPrice(cartItems);

    window.updateQuantity = function(cartId, quantity) {
        if (quantity < 1) {
            alert('수량은 1 이상이어야 합니다.');
            return;
        }

        let cart = JSON.parse(localStorage.getItem('productList')) || [];
        cart = cart.map(item => {
            if (item.optionId === cartId) {
                item.quantity = quantity;
            }
            return item;
        });
        localStorage.setItem('productList', JSON.stringify(cart));
        renderCartItems(cart, false);
        updateTotalPrice(cart);
    };

    window.removeCartItem = function(cartId) {
        let cart = JSON.parse(localStorage.getItem('productList')) || [];
        cart = cart.filter(item => item.optionId !== cartId);
        localStorage.setItem('productList', JSON.stringify(cart));
        renderCartItems(cart, false);
        updateTotalPrice(cart);
    };

    clearCartButton.addEventListener('click', function() {
        localStorage.removeItem('productList');
        renderCartItems([], false);
        updateTotalPrice([]);
    });

    orderButton.addEventListener('click', function() {
        const cartItems = JSON.parse(localStorage.getItem('productList')) || [];
        if (cartItems.length === 0) {
            alert('장바구니에 상품이 없습니다.');
            return;
        }
        alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
        window.location.href = '/loginForm';
    });
});