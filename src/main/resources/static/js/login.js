function handleSubmit(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const data = {
        email: email,
        password: password
    };

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                // 로그인 성공 후 메인 페이지로 리다이렉션
                window.location.href = '/';
            } else {
                // 로그인 실패 처리
                alert('로그인에 실패하였습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('로그인 중 오류가 발생하였습니다.');
        });
}