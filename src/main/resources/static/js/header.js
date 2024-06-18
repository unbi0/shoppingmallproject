document.addEventListener('DOMContentLoaded', (event) => {
    const modal = document.getElementById("search-modal");
    const searchLink = document.getElementById("search-link");
    const homeLink = document.getElementById("home-link");
    const searchInput = document.getElementById("search-input");
    const shopLink = document.getElementById("shop-link");
    const shopDropdown = document.getElementById("shop-dropdown");
    const loginLink = document.getElementById("loginLink"); // 수정된 부분: loginLink의 id가 "loginLink"임을 확인
    const logNav = document.querySelector(".Lognav");

    modal.style.display = "none"; // 페이지 로드 시 모달을 숨김

    searchLink.onclick = function() {
        modal.style.display = "flex";  // 모달을 중앙 정렬하여 보여줌
        searchInput.value = ""; // 검색창 초기화
    }

    modal.addEventListener('click', (e) => {
        if (e.target === modal || e.target.className === 'close') {
            modal.style.display = "none";
        }
    });

    searchInput.addEventListener("keypress", function(event) {
        if (event.key === "Enter") {
            event.preventDefault();
            performSearch();
        }
    });

    function performSearch() {
        const keyword = searchInput.value;
        location.href = `/?keyword=${encodeURIComponent(keyword)}`;
    }

    shopLink.onmouseover = function() {
        if (shopDropdown.childElementCount === 0) {
            shopDropdown.innerHTML = "<li>로딩 중...</li>";
            fetch('/category')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Categories Data:', data);
                    shopDropdown.innerHTML = "";
                    data.sort((a, b) => b.categoryId - a.categoryId);
                    data.forEach(category => {
                        const categoryItem = document.createElement("li");
                        categoryItem.innerHTML = `<a href="#" data-id="${category.categoryId}">${category.name}</a>`;
                        shopDropdown.appendChild(categoryItem);
                    });
                    const categoryLinks = shopDropdown.querySelectorAll('a');
                    categoryLinks.forEach(link => {
                        link.addEventListener('click', function(event) {
                            event.preventDefault();
                            const categoryId = this.getAttribute('data-id');
                            location.href = `/?category=${categoryId}`;
                        });
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    shopDropdown.innerHTML = "<li>카테고리를 불러오는 중 오류가 발생했습니다.</li>";
                });
        }
    }

    homeLink.onclick = function(event) {
        event.preventDefault();
        location.href = "/";
    }

    // 로그인 상태 확인
    fetch('/loginCheck')
        .then(response => {
            if (response.status === 204) {
                updateLoginLinkToLogout();
                checkUserRole();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
        });

    function updateLoginLinkToLogout() {
        loginLink.textContent = 'LOGOUT';
        loginLink.removeEventListener('click', redirectToLogin);
        loginLink.addEventListener('click', handleLogout);
    }

    function handleLogout(event) {
        event.preventDefault();
        fetch('/logout', { method: 'POST' })
            .then(() => {
                loginLink.textContent = 'LOGIN';
                loginLink.removeEventListener('click', handleLogout);
                loginLink.addEventListener('click', redirectToLogin);
                const adminPageLink = document.getElementById('admin-page-link');
                if (adminPageLink) {
                    adminPageLink.remove();
                }
            })
            .catch(error => {
                console.error('Error during logout:', error);
            });
    }

    function redirectToLogin(event) {
        event.preventDefault();
        location.href = '/loginForm';
    }

    function checkUserRole() {
        fetch('/my')
            .then(response => response.json())
            .then(data => {
                if (data.role === 'ADMIN') {
                    const adminPageLink = document.createElement('li');
                    adminPageLink.id = 'admin-page-link';
                    adminPageLink.innerHTML = '<a href="/admin">ADMIN PAGE</a>';
                    logNav.appendChild(adminPageLink);
                }
            })
            .catch(error => {
                console.error('Error fetching user role:', error);
            });
    }

    loginLink.addEventListener('click', redirectToLogin);
});