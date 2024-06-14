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

    const password = document.getElementById('cipherPassword').value;
    const confirmPassword = document.getElementById('confirm').value;

    if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }

    const address = {
        postcode: document.getElementById('postcode').value,
        address: document.getElementById('address').value,
        detailAddress: document.getElementById('detailAddress').value
    };

    const data = {
        email: document.getElementById('email').value,
        password: password,
        username: document.getElementById('username').value,
        address: address
    };

    fetch('/sign-up', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert('회원가입이 완료되었습니다.');
                window.location.href = '/';
            } else {
                alert('회원가입에 실패하였습니다.');
            }
        });
}
