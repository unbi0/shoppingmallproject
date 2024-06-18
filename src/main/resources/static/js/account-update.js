document.addEventListener('DOMContentLoaded', function() {
    fetch('/my')
        .then(response => response.json())
        .then(data => {
            document.getElementById('email').value = data.email;
            document.getElementById('username').value = data.username;
            document.getElementById('postcode').value = data.address.postcode;
            document.getElementById('address').value = data.address.address;
            document.getElementById('detailAddress').value = data.address.detailAddress;
        })
        .catch(error => console.error('Error:', error));
});

function daumPost() {
    new daum.Postcode({
        oncomplete: function(data) {
            let addr = ''; // 주소 변수
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('address').value = addr;
            document.getElementById('detailAddress').focus();
        }
    }).open();
}

function handleSubmit(event) {
    event.preventDefault();

    const address = {
        postcode: document.getElementById('postcode').value,
        address: document.getElementById('address').value,
        detailAddress: document.getElementById('detailAddress').value
    };

    const data = {
        username: document.getElementById('username').value,
        address: address
    };

    fetch('/user', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert('회원정보가 수정되었습니다.');
                location.href = '/'; // 수정 후 메인 페이지로 리다이렉션
            } else {
                alert('회원정보 수정에 실패하였습니다.');
            }
        });
}

function handleDelete(event) {
    event.preventDefault();

    fetch('/user', {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                alert('회원 탈퇴가 완료되었습니다.');
                location.href = '/'; // 탈퇴 후 메인 페이지로 리다이렉션
            } else {
                alert('회원 탈퇴에 실패하였습니다.');
            }
        });
}
