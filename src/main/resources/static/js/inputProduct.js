document.getElementById('productForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 폼의 기본 제출 동작을 막음

    const form = document.getElementById('productForm');
    const formData = new FormData(form);

    // JSON 객체를 만들어 formData에 추가
    const productJson = {
        categoryId: form.elements['categoryId'].value,
        name: form.elements['productName'].value,
        description: form.elements['description'].value,
        price: parseInt(form.elements['price'].value),
        details: form.elements['details'].value,
        options: [
            { optionSize: 'S', stock: parseInt(form.elements['stockS'].value) },
            { optionSize: 'M', stock: parseInt(form.elements['stockM'].value) },
            { optionSize: 'L', stock: parseInt(form.elements['stockL'].value) },
            { optionSize: 'XL', stock: parseInt(form.elements['stockXL'].value) }
        ]
    };

    formData.append('productJson', JSON.stringify(productJson));

    // 파일 추가
    const fileInput = document.getElementById('productImage');
    if (fileInput.files.length > 0) {
        for (let i = 0; i < fileInput.files.length; i++) {
            formData.append('files', fileInput.files[i]);
        }
    }

    fetch('/admin/api/product', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if(response.ok) {
                alert("서버 등록 완료.");
                window.location.href = "/";
            }else{
                alert("상품 등록 실패");
            }
        })
});