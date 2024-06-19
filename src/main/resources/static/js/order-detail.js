document.addEventListener('DOMContentLoaded', function() {
  let currentPage = 0; // 페이지 번호 초기화
  const pageSize = 10; // 한 페이지에 표시할 항목 수

  function fetchUsers(page) {
    // 페이지 번호가 유효한지 확인
    if (isNaN(page) || page < 0) {
      console.error('Invalid page number:', page);
      return;
    }

    fetch(`/admin/users?page=${page}&size=${pageSize}`)
        .then(response => {
          if (!response.ok) {
            throw new Error('Network response was not ok');
          }
          return response.json();
        })
        .then(data => {
          if (!data || !data.users) {
            console.error('Invalid data format:', data);
            return;
          }

          document.getElementById('total-members').textContent = data.totalUserCount;
          document.getElementById('admin-count').textContent = data.adminCount;

          const tbody = document.getElementById('user-table-body');
          tbody.innerHTML = '';

          data.users.forEach(user => {
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

            tbody.appendChild(tr);
          });

          document.getElementById('page-info').textContent = `${data.currentPage + 1} / ${data.totalPages}`;
          currentPage = data.currentPage;
          checkPageButtons(data.totalPages);
        })
        .catch(error => {
          console.error('Error:', error);
        });
  }

  function checkPageButtons(totalPages) {
    document.getElementById('prev-page').disabled = currentPage <= 0;
    document.getElementById('next-page').disabled = currentPage >= totalPages - 1;
  }

  document.getElementById('prev-page').addEventListener('click', () => {
    if (currentPage > 0) {
      fetchUsers(currentPage - 1);
    }
  });

  document.getElementById('next-page').addEventListener('click', () => {
    fetchUsers(currentPage + 1);
  });

  fetchUsers(currentPage);
});
