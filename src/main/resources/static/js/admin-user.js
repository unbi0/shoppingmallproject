document.addEventListener('DOMContentLoaded', function() {
    fetch('/admin/users')
        .then(response => response.json())
        .then(data => {
            if (!data || !data.users) {
                console.error('Invalid data format:', data);
                return;
            }

            document.getElementById('total-members').textContent = data.totalUserCount;
            document.getElementById('admin-count').textContent = data.adminCount;

            const tbody = document.getElementById('user-table-body');
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
        })
        .catch(error => console.error('Error:', error));
});
