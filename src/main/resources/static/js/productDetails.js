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
    const productImagesContainer = document.getElementById('product-images');

    let selectedSize = null;
    let productPrice = 0;
    let totalPrice = 0;
    let optionId = null;
    let quantity = 1; // 초기 수량 설정
    const sizeQuantities = {}; // 각 사이즈별 수량을 저장하는 객체
    let isCart = false;
    let productList = [];

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
            document.getElementById('product-price').textContent = product.price + '원';
            document.querySelector('.product-details').textContent = product.details;
            productPrice = product.price;

            // 여러 이미지 추가
            product.imageUrl.forEach((url, index) => {
                const img = document.createElement('img');
                img.src = url;
                img.alt = `상품 이미지 ${index + 1}`;
                img.className = 'product-image';
                productImagesContainer.appendChild(img);
            });

            // 사이즈 버튼에 optionId를 설정
            sizeButtons.forEach((button, index) => {
                button.setAttribute('data-option-id', product.optionId[index]);
            });
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
                // 이전 사이즈의 수량 저장
                if (selectedSize) {
                    sizeQuantities[selectedSize] = quantity;
                }

                sizeButtons.forEach(btn => btn.classList.remove('selected'));
                button.classList.add('selected');
                selectedSize = button.getAttribute('data-size');
                optionId = button.getAttribute('data-option-id'); // 선택한 옵션 ID
                selectedSizeButton.textContent = selectedSize;
                selectedSizeQuantity.style.display = 'flex';

                // 선택한 사이즈의 수량 설정
                quantity = sizeQuantities[selectedSize] || 1;
                quantityElement.textContent = quantity;
                updatePrice();
            }
        });
    });

    function updatePrice() {
        if (quantity < 1) {
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
        fetch('/loginCheck')
            .then(response => {
                if (response.status === 204) { // HTTP 204 No Content
                    const productDetails = {
                        id: optionId,
                        name: document.getElementById('product-name').textContent,
                        size: selectedSize,
                        quantity: quantity,
                        price: productPrice,
                        imageUrl: document.querySelector('.product-image').src,
                    };

                    productList.push(productDetails);
                    window.localStorage.setItem('productList', JSON.stringify(productList));
                    window.localStorage.setItem('isCart', JSON.stringify(isCart));
                    window.location.href = `/order`;

                } else if (response.status === 401) { // HTTP 401 Unauthorized
                    alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                    window.location.href = `/loginForm`; // 로그인 페이지로 리다이렉트
                } else {
                    alert('로그인 상태를 확인하는 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error during login check:', error);
                alert('로그인 상태를 확인하는 중 오류가 발생했습니다.');
            });
    });

    addToCartButton.addEventListener('click', function() {
        if (!selectedSize) {
            alert('사이즈를 선택해주세요.');
            return;
        }
        fetch('/loginCheck')
            .then(response => {
                if (response.status === 204) {
                    const cartCreateDTO = {
                        optionId: optionId,
                        quantity: quantity
                    };

                    fetch('/cart', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(cartCreateDTO)
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log('Success:', data);
                            alert('장바구니에 상품이 추가되었습니다.');
                            window.location.href = `/cart/view`;
                        })
                        .catch((error) => {
                            console.error('Error:', error);
                            alert('장바구니에 상품을 추가하는 중 오류가 발생했습니다.');
                        });

                } else if (response.status === 401) {
                    const productDetails = {
                        id: productId,
                        size: selectedSize,
                        quantity: quantity,
                        price: document.getElementById('product-price').textContent,
                        imageUrl: document.querySelector('.product-image').src,
                        optionId: optionId
                    };
                    window.localStorage.setItem('productList', JSON.stringify(productDetails));
                    console.log('Test productDetails:', productDetails);
                    console.log('Test optionId:', optionId);

                    window.location.href = `/cart/view`;
                } else {
                    alert('로그인 상태를 확인하는 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error during login check:', error);
                alert('로그인 상태를 확인하는 중 오류가 발생했습니다.');
            });
    });
});
