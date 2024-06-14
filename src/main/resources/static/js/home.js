document.addEventListener('DOMContentLoaded', (event) => {
    const modal = document.getElementById("search-modal");
    const searchLink = document.getElementById("search-link");
    const homeLink = document.getElementById("home-link"); // HOME 링크
    const span = document.getElementsByClassName("close")[0];
    const searchInput = document.getElementById("search-input");
    const searchResults = document.getElementById("search-results");
    const shopLink = document.getElementById("shop-link");
    const shopDropdown = document.getElementById("shop-dropdown");
    const productList = document.getElementById("product-list");

    modal.style.display = "none"; // 페이지 로드 시 모달을 숨김

    searchLink.onclick = function() {
        modal.style.display = "flex";  // 모달을 중앙 정렬하여 보여줌
        searchInput.value = ""; // 검색창 초기화
    }

    span.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }

    searchInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            performSearch();
        }
    });

    function performSearch() {
        const keyword = searchInput.value;
        // 검색 시 /home으로 이동하면서 검색어 전달
        location.href = `/home?keyword=${encodeURIComponent(keyword)}`;
    }

    // SHOP 링크에 마우스를 올릴 때 카테고리를 로드하고 표시
    shopLink.onmouseover = function() {
        if (shopDropdown.childElementCount === 0) { // 이미 로드된 경우 다시 로드하지 않음
            fetch('/category')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Categories Data:', data);
                    shopDropdown.innerHTML = ""; // 이전 내용 지우기
                    // 카테고리 데이터를 categoryId 기준으로 내림차순 정렬
                    data.sort((a, b) => b.categoryId - a.categoryId);
                    data.forEach(category => {
                        const categoryItem = document.createElement("li");
                        categoryItem.innerHTML = `<a href="#" data-id="${category.categoryId}">${category.name}</a>`;
                        shopDropdown.appendChild(categoryItem);
                    });
                    // 카테고리 클릭 이벤트 리스너 추가
                    const categoryLinks = shopDropdown.querySelectorAll('a');
                    categoryLinks.forEach(link => {
                        link.addEventListener('click', function(event) {
                            event.preventDefault();
                            const categoryId = this.getAttribute('data-id');
                            // 카테고리 클릭 시 /home으로 이동하면서 카테고리 ID 전달
                            location.href = `/home?category=${categoryId}`;
                        });
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    shopDropdown.innerHTML = "<li>카테고리를 불러오는 중 오류가 발생했습니다.</li>";
                });
        }
    }

    // HOME 링크 클릭 시 홈 페이지로 이동
    homeLink.onclick = function(event) {
        event.preventDefault();
        location.href = "/home"; // 홈 페이지로 이동
    }

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
                productDiv.innerHTML = `
                    <img src="${product.imageUrl ? product.imageUrl : 'default_image.png'}" alt="${product.name}">
                    <h3>${product.name}</h3>
                    <p>${product.price} 원</p>
                `;
                productDiv.addEventListener('click', () => {
                    window.location.href = `/product/${product.productId}`;
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