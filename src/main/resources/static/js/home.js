document.addEventListener('DOMContentLoaded', (event) => {
    const productList = document.getElementById("product-list");

    // URL에서 카테고리 또는 검색어를 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = urlParams.get('category');
    const keyword = urlParams.get('keyword');

    if (categoryId) {
        loadProductsByCategory(categoryId);
    } else if (keyword) {
        loadProductsByKeyword(keyword);
    } else {
        loadProducts(); // 전체 상품 로드
    }


    function loadProducts() {
        fetch('/api/product')
            .then(response => response.json())
            .then(data => {
                displayProducts(data);
            })
            .catch(error => {
                console.error('Error:', error);
                productList.innerHTML = "<p>상품을 불러오는 중 오류가 발생했습니다.</p>";
            });
    }

    function loadProductsByCategory(categoryId) {
        fetch(`/api/product/category/${categoryId}`)
            .then(response => response.json())
            .then(data => {
                displayProducts(data);
            })
            .catch(error => {
                console.error('Error:', error);
                productList.innerHTML = "<p>카테고리 상품을 불러오는 중 오류가 발생했습니다.</p>";
            });
    }

    function loadProductsByKeyword(keyword) {
        fetch(`/api/product/search?keyword=${encodeURIComponent(keyword)}`)
            .then(response => response.json())
            .then(data => {
                displayProducts(data);
            })
            .catch(error => {
                console.error('Error:', error);
                productList.innerHTML = "<p>검색 중 오류가 발생했습니다.</p>";
            });
    }

    function displayProducts(products) {
        productList.innerHTML = ""; // 이전 결과 지우기
        if (products.length > 0) {
            products.forEach(product => {
                const productDiv = document.createElement("div");
                productDiv.className = "product-item";
                const imageUrl = Array.isArray(product.imageUrl) && product.imageUrl.length > 0 ? product.imageUrl[0] : 'default_image.png';

                productDiv.innerHTML = `
                    <img src="${imageUrl}" alt="${product.name}">
                    <h3>${product.name}</h3>
                    <p>${product.price} 원</p>
                `;
                productDiv.addEventListener('click', () => {
                    window.location.href = `/productdetail/${product.productId}`;
                });
                productList.appendChild(productDiv);
            });
        } else {
            productList.innerHTML = "<p>상품이 없습니다.</p>";
        }
    }

    function displayNoResults() {
        productList.innerHTML = '<div class="no-results">검색 결과가 없습니다.</div>';
    }
});