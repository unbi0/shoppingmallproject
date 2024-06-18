document.addEventListener('DOMContentLoaded', () => {
    const sizeButtons = document.querySelectorAll('.size-btn');
    const quantityElement = document.getElementById('quantity');
    const decreaseQuantityButton = document.getElementById('decrease-quantity');
    const increaseQuantityButton = document.getElementById('increase-quantity');
    const buyNowButton = document.getElementById('buy-now');
    const addToCartButton = document.getElementById('add-to-cart');

    let selectedSize = 'S';
    let quantity = 1;

    // body의 data-product-id 속성에서 productId를 가져옴
    const productId = document.body.dataset.productId;

    fetch(`/images${productId}`)
        .then(response => response.json())
        .then(product => {
            if (!product) {
                console.error('No product found');
                return;
            }
            document.getElementById('product-name').textContent = product.name;
            document.getElementById('product-image-1').src = product.imageUrl;
            document.getElementById('product-image-2').src = product.imageUrl;
            document.getElementById('product-price').textContent = product.price + '원';
        })
        .catch(error => {
            console.error('Error fetching product:', error);
        });

    sizeButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 클릭된 버튼이 이미 선택된 상태라면 선택을 해제
            if (button.classList.contains('selected')) {
                button.classList.remove('selected');
                button.style.backgroundColor = ''; // 기본 배경색으로 되돌림
                selectedSize = null;
            } else {
                // 다른 버튼들의 선택을 해제하고 클릭된 버튼을 선택
                sizeButtons.forEach(btn => {
                    btn.classList.remove('selected');
                    btn.style.backgroundColor = ''; // 기본 배경색으로 되돌림
                });
                button.classList.add('selected');
                button.style.backgroundColor = 'yellow';
                selectedSize = button.dataset.size;
            }
        });
    });

    decreaseQuantityButton.addEventListener('click', () => {
        if (quantity > 1) {
            quantity--;
            quantityElement.textContent = quantity;
        }
    });

    increaseQuantityButton.addEventListener('click', () => {
        quantity++;
        quantityElement.textContent = quantity;
    });

    buyNowButton.addEventListener('click', () => {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        alert(`Proceeding to buy ${quantity} item(s) of size ${selectedSize}`);
        // Implement the redirection to purchase page
    });

    addToCartButton.addEventListener('click', () => {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        alert(`Added ${quantity} item(s) of size ${selectedSize} to cart`);
        // Implement adding to cart functionality
    });
});
