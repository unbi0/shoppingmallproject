console.log("cartLogout.js loaded");

function loadCartItemsFromLocalStorage() {
    console.log("Loading cart items from local storage...");
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    cartItems.forEach(item => {
        if (typeof item.price === 'string') {
            item.price = parseInt(item.price.replace('원', '').trim(), 10);
        }
    });
    console.log("Cart items:", cartItems);
    renderCartItems(cartItems);
    loadTotalPriceFromLocalStorage();
}

function loadTotalPriceFromLocalStorage() {
    console.log("Loading total price from local storage...");
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const totalPrice = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);
    document.getElementById('total-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
    document.getElementById('final-price').textContent = `KRW ${totalPrice.toFixed(2)}`;
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
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const updatedCartItems = cartItems.map(item => item.optionId === optionId ? { ...item, quantity: quantity } : item);
    localStorage.setItem('cart', JSON.stringify(updatedCartItems));
    renderCartItems(updatedCartItems);
    updateTotalPrice(updatedCartItems);
}

function removeCartItem(optionId) {
    console.log(`Removing cart item with optionId: ${optionId}`);
    const cartItems = JSON.parse(localStorage.getItem('cart')) || [];
    const updatedCartItems = cartItems.filter(item => item.optionId !== optionId);
    localStorage.setItem('cart', JSON.stringify(updatedCartItems));
    renderCartItems(updatedCartItems);
    updateTotalPrice(updatedCartItems);
}

function clearCart() {
    console.log("Clearing cart...");
    localStorage.setItem('cart', JSON.stringify([]));
    loadCartItemsFromLocalStorage();
    loadTotalPriceFromLocalStorage();
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