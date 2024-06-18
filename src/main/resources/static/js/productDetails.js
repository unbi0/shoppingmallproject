document.addEventListener('DOMContentLoaded', function() {
    const sizeButtons = document.querySelectorAll('.size-btn');
    const selectedSizeQuantity = document.getElementById('selected-size-quantity');
    const selectedSizeButton = document.getElementById('selected-size');
    const quantityControls = document.getElementById('quantity-controls');
    const quantityElement = document.getElementById('quantity');
    const decreaseQuantityButton = document.getElementById('decrease-quantity');
    const increaseQuantityButton = document.getElementById('increase-quantity');
    const buyNowButton = document.getElementById('buy-now');
    const addToCartButton = document.getElementById('add-to-cart');

    let selectedSize = null;

    //수량은 DB에 있으니까 이 부분은 db에서 끌고온 수량 값을 대입하기.
    let quantity = 0;

    // body의 data-product-id 속성에서 productId를 가져옴
    const productId = document.body.getAttribute('data-product-id');

    fetch(`/api/product/${productId}`)
        .then(function(response) {
            return response.json();
        })
        .then(function(product) {
            if (!product) {
                console.error('No product found');
                return;
            }
            document.getElementById('product-name').textContent = product.name;
            document.getElementById('product-image-1').src = product.imageUrl;
            document.getElementById('product-image-2').src = product.imageUrl;
            document.getElementById('product-price').textContent = product.price + '원';
        })
        .catch(function(error) {
            console.error('Error fetching product:', error);
        });

    sizeButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            if (button.classList.contains('selected')) {
                button.classList.remove('selected');
                selectedSize = null;
                selectedSizeQuantity.style.display = 'none';
            } else {
                sizeButtons.forEach(function(btn) {
                    btn.classList.remove('selected');
                });
                button.classList.add('selected');
                selectedSize = button.getAttribute('data-size');
                selectedSizeButton.textContent = selectedSize;
                selectedSizeQuantity.style.display = 'flex';
            }
        });
    });

    decreaseQuantityButton.addEventListener('click', function() {
        if (quantity > 1) {
            quantity--;
            quantityElement.textContent = quantity;
        }
    });

    increaseQuantityButton.addEventListener('click', function() {
        quantity++;
        quantityElement.textContent = quantity;
    });

    buyNowButton.addEventListener('click', function() {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        const productDetails = {
            id: productId,
            size: selectedSize,
            quantity: quantity,
            price: document.getElementById('product-price').textContent,
            imageUrl: document.getElementById('product-image-1').src
        };
        window.location.href = `/order/order.html` + new URLSearchParams(productDetails).toString();
    });

    addToCartButton.addEventListener('click', function() {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        const productDetails = {
            id: productId,
            size: selectedSize,
            quantity: quantity,
            price: document.getElementById('product-price').textContent,
            imageUrl: document.getElementById('product-image-1').src
        };
        window.location.href = `/carts/cart.html` + new URLSearchParams(productDetails).toString();
    });
});
