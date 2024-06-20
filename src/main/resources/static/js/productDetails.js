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
    let quantity = 1;
    let productPrice = 0;
    let totalPrice = 0;

    const pathArray = window.location.pathname.split('/');
    const productId = pathArray[pathArray.length - 1];
    console.log('Product ID:', productId);

    const productDetailPage = document.querySelector('.product-detail-page');
    productDetailPage.setAttribute('data-product-id', productId);

    fetch(`/api/product/${productId}`)
        .then(response => response.json())
        .then(product => {
            console.log('Product data:', product);
            if (!product) {
                console.error('No product found');
                return;
            }
            document.getElementById('product-name').textContent = product.name;
            document.getElementById('product-image-1').src = product.imageUrl;
            document.getElementById('product-price').textContent = product.price + '원';
            document.querySelector('.product-details').textContent = product.details;
            productPrice = product.price;
        })
        .catch(error => {
            console.error('Error fetching product:', error);
        });

    sizeButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (button.classList.contains('selected')) {
                button.classList.remove('selected');
                selectedSize = null;
                selectedSizeQuantity.style.display = 'none';
            } else {
                sizeButtons.forEach(btn => btn.classList.remove('selected'));
                button.classList.add('selected');
                selectedSize = button.getAttribute('data-size');
                selectedSizeButton.textContent = selectedSize;
                selectedSizeQuantity.style.display = 'flex';
            }
        });
    });

    function updatePrice() {
        if(quantity < 1) {
            totalPrice = 0;
        }
        totalPrice = productPrice * quantity;
        document.getElementById('product-price').textContent = totalPrice + '원';
    }

    decreaseQuantityButton.addEventListener('click', function() {
        if (quantity > 1) {
            quantity--;
            quantityElement.textContent = quantity;
            updatePrice();
        }
    });

    increaseQuantityButton.addEventListener('click', function() {
        quantity++;
        quantityElement.textContent = quantity;
        updatePrice();
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

        window.location.href = `/order`;
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

        window.location.href = `/cart/view`;
    });
});
