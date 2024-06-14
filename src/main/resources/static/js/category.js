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
    const addCategoryButton = document.getElementById("add-category");
    const newCategoryInput = document.getElementById("new-category");
    const categoryList = document.getElementById("category-list");

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
        productList.innerHTML = ""; // 새로운 검색을 시작할 때 이전 결과 지우기
        searchInput.value = ""; // 새로운 검색을 시작할 때 입력 필드를 비우기
        fetch(`/api/product/search?keyword=${encodeURIComponent(keyword)}`)
            .then(response => response.json())
            .then(data => {
                displayProducts(data);
                modal.style.display = "none"; // 검색 결과가 있든 없든 검색 모달을 숨김
                if (data.length === 0) {
                    displayNoResults();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                productList.innerHTML = "<p>검색 중 오류가 발생했습니다.</p>";
                modal.style.display = "none"; // 검색 중 오류가 발생해도 검색 모달을 숨김
            });
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
                            loadProductsByCategory(categoryId);
                        });
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    shopDropdown.innerHTML = "<li>카테고리를 불러오는 중 오류가 발생했습니다.</li>";
                });
        }
    }

    // HOME 링크 클릭 시 페이지 새로고침 효과
    homeLink.onclick = function(event) {
        event.preventDefault();
        location.reload(); // 페이지를 완전히 새로고침
    }

    // 페이지 로드 시 모든 상품을 로드
    /*loadProducts();  -----------------------------------------------------------------> 주석을 빼면 처음에 전체상품 표시 */

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

    function displayProducts(products) {
        productList.innerHTML = ""; // 이전 결과 지우기
        if (products.length > 0) {
            products.forEach(product => {
                const productDiv = document.createElement("div");
                productDiv.className = "product-item";
                productDiv.innerHTML = `
                    <img src="${product.imageUrl ? product.imageUrl : '/images/default_image.png'}" alt="${product.name}">
                    <h3>${product.name}</h3>
                    <p>${product.price} 원</p>
                `;
                productDiv.addEventListener('click', () => {
                    window.location.href = `/${product.productId}`;
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

    // 카테고리 추가 기능
    addCategoryButton.addEventListener('click', () => {
        const categoryName = newCategoryInput.value.trim();
        if (categoryName) {
            fetch('/category', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name: categoryName })
            })
                .then(response => response.json())
                .then(data => {
                    alert('카테고리가 추가되었습니다.');
                    newCategoryInput.value = ''; // 입력 필드 초기화
                    loadCategories(); // 카테고리 목록 새로고침
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('카테고리 추가 중 오류가 발생했습니다.');
                });
        } else {
            alert('카테고리 이름을 입력하세요.');
        }
    });

    // 카테고리 목록 로드
    function loadCategories() {
        fetch('/category')
            .then(response => response.json())
            .then(data => {
                displayCategories(data);
            })
            .catch(error => {
                console.error('Error fetching categories:', error);
                categoryList.innerHTML = "<p>카테고리를 불러오는 중 오류가 발생했습니다.</p>";
            });
    }

    function displayCategories(categories) {
        categoryList.innerHTML = ""; // 기존 카테고리 목록 초기화
        categories.forEach(category => {
            const categoryDiv = document.createElement("div");
            categoryDiv.className = "category-item";
            categoryDiv.innerHTML = `
                <h4>${category.name}</h4>
                <div>
                    <button class="edit-category" data-id="${category.categoryId}">수정</button>
                    <button class="delete-category" data-id="${category.categoryId}">삭제</button>
                </div>
            `;
            categoryList.appendChild(categoryDiv);
        });
        // 수정 및 삭제 버튼 이벤트 리스너 추가
        const editButtons = document.querySelectorAll('.edit-category');
        const deleteButtons = document.querySelectorAll('.delete-category');

        editButtons.forEach(button => {
            button.addEventListener('click', () => {
                const categoryId = button.getAttribute('data-id');
                const categoryName = prompt('새 카테고리 이름을 입력하세요:');
                if (categoryName) {
                    updateCategory(categoryId, categoryName);
                }
            });
        });

        deleteButtons.forEach(button => {
            button.addEventListener('click', () => {
                const categoryId = button.getAttribute('data-id');
                if (confirm('정말로 이 카테고리를 삭제하시겠습니까?')) {
                    deleteCategory(categoryId);
                }
            });
        });
    }

    function updateCategory(categoryId, categoryName) {
        fetch(`/category/${categoryId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: categoryName })
        })
            .then(response => {
                if (response.ok) {
                    alert('카테고리가 수정되었습니다.');
                    loadCategories(); // 카테고리 목록 새로고침
                } else {
                    alert('카테고리 수정 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error updating category:', error);
                alert('카테고리 수정 중 오류가 발생했습니다.');
            });
    }

    function deleteCategory(categoryId) {
        fetch(`/category/${categoryId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert('카테고리가 삭제되었습니다.');
                    loadCategories(); // 카테고리 목록 새로고침
                } else {
                    alert('카테고리 삭제 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error deleting category:', error);
                alert('카테고리 삭제 중 오류가 발생했습니다.');
            });
    }

    // 페이지 로드 시 카테고리 목록 로드
    loadCategories();
});