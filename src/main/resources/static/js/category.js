document.addEventListener('DOMContentLoaded', (event) => {
    const productList = document.getElementById("product-list");
    const addCategoryButton = document.getElementById("add-category");
    const newCategoryInput = document.getElementById("new-category");
    const categoryList = document.getElementById("category-list");
// 카테고리 추가 기능
    addCategoryButton.addEventListener('click', () => {
        const categoryName = newCategoryInput.value.trim();
        if (categoryName) {
            fetch('/admin/category', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name: categoryName })
            })
                .then(response => response.json())
                .then(data => {
                    newCategoryInput.value = ''; // 입력 필드 초기화
                    loadCategories(); // 카테고리 목록 새로고침
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        } else {
            alert('카테고리 이름을 입력하세요.');
        }
    });

// 카테고리 목록 로드
    loadCategories();
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
        fetch(`/admin/category/${categoryId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: categoryName })
        })
            .then(response => {
                if (response.ok) {
                    loadCategories(); // 카테고리 목록 새로고침
                } else {
                    console.error('카테고리 수정 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error updating category:', error);
            });
    }

    function deleteCategory(categoryId) {
        fetch(`/admin/category/${categoryId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    loadCategories(); // 카테고리 목록 새로고침
                } else {
                    console.error('카테고리 삭제 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                console.error('Error deleting category:', error);
            });
    }
});