document.addEventListener('DOMContentLoaded', () => {
    let currentPage = 0;
    const pageSize = 20;
    let isLoading = false;
    let allLoaded = false;

    document.getElementById("scrollToTopBtn").style.display = "none";

    function handleScroll() {
        const scrollTop = window.scrollY;
        const scrollHeight = document.documentElement.scrollHeight;
        const clientHeight = document.documentElement.clientHeight;

        if (scrollTop + clientHeight >= scrollHeight - 10) {
            fetchUsers();
        }

        const scrollToTopBtn = document.getElementById("scrollToTopBtn");
        if (scrollTop > 20) {
            scrollToTopBtn.style.display = "block";
        } else {
            scrollToTopBtn.style.display = "none";
        }
    }

    function scrollToTop() {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    }

    window.onscroll = handleScroll;

    function fetchUsers(reset = false) {
        if (isLoading || allLoaded) return;
        isLoading = true;

        fetch(`/admin/users?page=${currentPage}&size=${pageSize}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (reset) {
                    document.getElementById('user-table-body').innerHTML = ""; // Clear existing users
                }

                if (data.users.length === 0) {
                    allLoaded = true;
                    if (currentPage === 0) {
                        document.getElementById("noUsersMessage").style.display = "block";
                    }
                } else {
                    document.getElementById("noUsersMessage").style.display = "none";
                    updateUserList(data.users);
                    currentPage++;
                }

                document.getElementById('total-members').textContent = data.totalUserCount;
                document.getElementById('admin-count').textContent = data.adminCount;
            })
            .catch(error => console.error('Error fetching users:', error))
            .finally(() => {
                isLoading = false;
            });
    }

    function updateUserList(users) {
        const userTableBody = document.getElementById("user-table-body");

        users.forEach(user => {
            const tr = document.createElement('tr');

            const tdDate = document.createElement('td');
            tdDate.textContent = new Date(user.createAt).toLocaleDateString();
            tr.appendChild(tdDate);

            const tdEmail = document.createElement('td');
            tdEmail.textContent = user.email;
            tr.appendChild(tdEmail);

            const tdName = document.createElement('td');
            tdName.textContent = user.username;
            tr.appendChild(tdName);

            const tdRole = document.createElement('td');
            tdRole.textContent = user.role;
            tr.appendChild(tdRole);

            userTableBody.appendChild(tr);
        });
    }

    fetchUsers(true); // Load initial users
});
