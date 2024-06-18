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
    let quantity = 1; // 기본 수량을 1로 설정

    // Get productId from data attribute
    const productId = document.querySelector('.product-detail-page').getAttribute('data-product-id');
    console.log('Product ID:', productId); // Product ID 확인

    fetch(`/product/${productId}`)
        .then(response => response.json())
        .then(product => {
            console.log('Product data:', product); // 데이터 확인을 위해 콘솔에 출력
            if (!product) {
                console.error('No product found');
                return;
            }
            document.getElementById('product-name').textContent = product.name;
            document.getElementById('product-image-1').src = product.imageUrl;
            document.getElementById('product-price').textContent = product.price + '원';
            document.querySelector('.product-details').textContent = product.details;
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

        localStorage.setItem("productData", JSON.stringify(productDetails));
        window.location.href = `/order`;
    });

    addToCartButton.addEventListener('click', function() {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        //localstore
        const productDetails = {
            id: productId,
            size: selectedSize,
            quantity: quantity,
            price: document.getElementById('product-price').textContent,
            imageUrl: document.getElementById('product-image-1').src
        };

        localStorage.setItem("productData", JSON.stringify(productDetails));
        window.location.href = `/cart/view`;
    });
});
