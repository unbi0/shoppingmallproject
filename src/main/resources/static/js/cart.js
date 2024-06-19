// DOMContentLoaded 이벤트가 발생하면 장바구니 항목과 총 가격을 불러옵니다.
document.addEventListener('DOMContentLoaded', function () {
    loadCartItems();  // 장바구니 항목 불러오기
    loadTotalPrice(); // 총 가격 불러오기
});

// 장바구니 항목을 서버에서 불러와서 화면에 렌더링하는 함수
function loadCartItems() {
    fetch('/cart')
        .then(response => response.json())
        .then(data => {
            const cartContainer = document.getElementById('cart-items');  // 장바구니 항목을 담을 컨테이너
            const itemCount = document.getElementById('item-count');  // 장바구니 항목 수를 표시할 요소
            cartContainer.innerHTML = '';  // 기존 장바구니 항목 초기화
            let totalItems = 0;  // 총 항목 수 초기화

            // 서버에서 받은 데이터를 순회하면서 장바구니 항목을 생성
            data.forEach(item => {
                totalItems += item.quantity;  // 총 항목 수 계산
                const cartItem = document.createElement('div');  // 새로운 장바구니 항목 요소 생성
                cartItem.classList.add('cart-item');  // 장바구니 항목 클래스 추가
                cartItem.innerHTML = `
                    <img src="${item.productImageUrl}" alt="${item.productName}">
                    <div class="item-details">
                        <h2>${item.productName}</h2>
                        <p class="price">KRW ${item.productPrice * item.quantity}</p>
                        <p>[옵션: ${item.productSize}] <a href="#">옵션변경</a></p>
                        <div class="quantity">
                            <button onclick="updateQuantity(${item.cartId}, ${item.quantity - 1})">-</button>
                            <span>${item.quantity}</span>
                            <button onclick="updateQuantity(${item.cartId}, ${item.quantity + 1})">+</button>
                        </div>
                    </div>
                    <button class="remove" onclick="removeCartItem(${item.cartId})">삭제</button>
                `;
                cartContainer.appendChild(cartItem);  // 생성된 장바구니 항목을 컨테이너에 추가
            });

            itemCount.textContent = totalItems;  // 총 항목 수를 화면에 표시
        });
}

// 총 가격을 서버에서 불러와서 화면에 렌더링하는 함수
function loadTotalPrice() {
    fetch('/cart/total')
        .then(response => response.json())
        .then(data => {
            const totalPriceContainer = document.getElementById('total-price');  // 총 가격을 표시할 요소
            const finalPriceContainer = document.getElementById('final-price');  // 최종 가격을 표시할 요소
            const discountPriceContainer = document.getElementById('discount-price');  // 할인 금액을 표시할 요소
            const discount = 103200; // 예시 할인 금액
            totalPriceContainer.textContent = `KRW ${data.toFixed(2)}`;  // 총 가격을 표시
            discountPriceContainer.textContent = `-KRW ${discount.toFixed(2)}`;  // 할인 금액을 표시
            finalPriceContainer.textContent = `KRW ${(data - discount).toFixed(2)}`;  // 최종 가격을 표시
        });
}

// 장바구니 항목의 수량을 업데이트하는 함수
function updateQuantity(cartId, quantity) {
    if (quantity < 1) return;  // 수량이 1 미만이면 리턴
    fetch(`/cart/${cartId}`, {
        method: 'PUT',  // PUT 메서드 사용
        headers: {
            'Content-Type': 'application/json'  // 요청 헤더 설정
        },
        body: JSON.stringify({ quantity: quantity })  // 요청 본문에 수량을 JSON 형식으로 포함
    })
        .then(response => response.json())
        .then(() => {
            loadCartItems();  // 수량 업데이트 후 장바구니 항목 다시 불러오기
            loadTotalPrice();  // 수량 업데이트 후 총 가격 다시 불러오기
        });
}

// 장바구니 항목을 삭제하는 함수
function removeCartItem(cartId) {
    fetch(`/cart/${cartId}`, {
        method: 'DELETE'  // DELETE 메서드 사용
    })
        .then(() => {
            loadCartItems();  // 항목 삭제 후 장바구니 항목 다시 불러오기
            loadTotalPrice();  // 항목 삭제 후 총 가격 다시 불러오기
        });
}

// 장바구니를 비우는 함수
function clearCart() {
    fetch('/cart/all', {
        method: 'DELETE'  // DELETE 메서드 사용
    })
        .then(() => {
            loadCartItems();  // 장바구니 비운 후 장바구니 항목 다시 불러오기
            loadTotalPrice();  // 장바구니 비운 후 총 가격 다시 불러오기
        });
}