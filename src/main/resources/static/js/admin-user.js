document.addEventListener('DOMContentLoaded', function() {
    let currentPage = 0; // 현재 페이지 번호
    const pageSize = 20; // 한 페이지에 표시할 항목 수
    let totalPages = 1; // 총 페이지 수 (초기값 1로 설정)
    let loading = false; // 데이터 로딩 중 여부

    // 사용자 데이터 가져오기
    function fetchUsers(page, reset = false) {
        if (loading) return; // 이미 로딩 중이면 새로운 요청 무시
        loading = true;

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
                    loading = false;
                    return;
                }

                if (reset) {
                    document.getElementById('user-table-body').innerHTML = ""; // 기존 데이터 초기화
                }

                if (page === 0) {
                    document.getElementById('total-members').textContent = data.totalUserCount;
                    document.getElementById('admin-count').textContent = data.adminCount;
                }

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

                totalPages = data.totalPages; // 총 페이지 수 업데이트
                loading = false; // 로딩 완료

                // 마지막 항목을 옵저버로 감시하도록 설정
                const lastRow = tbody.querySelector('tr:last-child');
                if (lastRow) observer.observe(lastRow);

                // 모든 페이지를 로드했으면 옵저버 해제
                if (currentPage >= totalPages - 1) {
                    observer.disconnect();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                loading = false; // 로딩 실패
            });
    }

    // Intersection Observer 콜백 함수
    const observerCallback = (entries, observer) => {
        currentPage++;
        fetchUsers(currentPage);


    };

    const observer = new IntersectionObserver(observerCallback, {

        root: null,
        rootMargin: '0px',
        threshold: 1.0
    });
    console.log(observer);

    const target = document.querySelector("#observer");
    observer.observe(target);
    // 초기 데이터 로드
    fetchUsers(currentPage);
});
