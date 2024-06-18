document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/products')//수정
        .then(response => response.json())
        .then(products => {
            const productList = document.getElementById('product-list');
            products.forEach(product => {
                const productItem = document.createElement('div'); // 새로운 div 요소를 생성하여 상품 정보를 담음
                productItem.className = 'product-item'; // 클래스 이름을 'product-item'으로 설정

                const productImage = document.createElement('img'); // 상품 이미지를 표시할 img 요소를 생성
                productImage.src = product.imageUrl; // img 요소의 src 속성을 상품 이미지 URL로 설정
                productImage.alt = '상품 이미지'; // img 요소의 alt 속성을 '상품 이미지'로 설정

                const productDetails = document.createElement('div'); // 상품 세부 정보를 담을 div 요소를 생성
                productDetails.innerHTML = `
                    <p>상품이름: ${product.name}</p>
                    <p>가격: ${product.price}</p>
                    <p>상품설명: ${product.description}</p>
                `; // 상품 이름, 가격, 설명을 포함하는 HTML을 설정

                const editButton = document.createElement('button'); // 수정 버튼을 생성
                editButton.textContent = '수정'; // 버튼 텍스트를 '수정'으로 설정
                editButton.onclick = () => editProduct(product.id); // 클릭 시 editProduct 함수를 호출

                const deleteButton = document.createElement('button'); // 삭제 버튼을 생성
                deleteButton.textContent = '삭제'; // 버튼 텍스트를 '삭제'로 설정
                deleteButton.onclick = () => deleteProduct(product.image_id); // 클릭 시 deleteProduct 함수를 호출

                productItem.appendChild(productImage); // productItem에 이미지 요소 추가
                productItem.appendChild(productDetails); // productItem에 상품 세부 정보 요소 추가
                productItem.appendChild(editButton); // productItem에 수정 버튼 추가
                productItem.appendChild(deleteButton); // productItem에 삭제 버튼 추가

                productList.appendChild(productItem); // 최종적으로 productList에 productItem 추가
            });
        });
});

function editProduct(id) {
    location.href = '/product/edit/' + id; // 수정 페이지로 이동
}

function deleteProduct(image_id) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        fetch(`/api/products/${image_id}/delete`, { // Spring Boot 컨트롤러의 경로와 일치하는 URL로 DELETE 요청
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert("삭제되었습니다."); // 삭제 성공 시 알림 표시
                    location.reload(); // 페이지 새로고침
                } else {
                    alert("삭제에 실패했습니다."); // 삭제 실패 시 알림 표시
                }
            });
    }
}
