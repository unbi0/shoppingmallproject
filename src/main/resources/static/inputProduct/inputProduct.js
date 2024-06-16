document.getElementById('productForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the form from submitting the default way

    const form = document.getElementById('productForm');
    const formData = new FormData(form);

    fetch('/upload/images', {
        method: 'POST',
        body: formData
    })
        //컨트롤에서 리턴 값으로 string으로 줄꺼면 .test()로
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);
            alert('File uploaded and product saved successfully.'); // Display success message
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.'); // Display error message
        });
});