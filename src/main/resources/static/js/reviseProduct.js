document.getElementById('productForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const formData = new FormData();

    // Helper function to append form data
    function appendFormData(id, field) {
        formData.append(field, document.getElementById(id).value);
    }

    // Append text and number fields
    const fields = [
        { id: 'productName', field: 'productName' },
        { id: 'category', field: 'category' },
        { id: 'description', field: 'description' },
        { id: 'price', field: 'price' },
        { id: 'details', field: 'details' }
    ];

    fields.forEach(item => appendFormData(item.id, item.field));

    // Append stock fields
    const stockFields = [
        { name: 'stockS', field: 'stockS' },
        { name: 'stockM', field: 'stockM' },
        { name: 'stockL', field: 'stockL' },
        { name: 'stockXL', field: 'stockXL' }
    ];

    stockFields.forEach(item => {
        formData.append(item.field, document.querySelector(`input[name="${item.name}"]`).value);
    });

    // Append image files
    const imageFiles = document.getElementById('productImage').files;
    for (let i = 0; i < imageFiles.length; i++) {
        formData.append('productImages', imageFiles[i]);
    }

    // Send form data using fetch
    fetch('/api/products', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.ok) {
            alert('Product updated successfully!');
        } else {
            alert('Failed to update product.');
        }
    }).catch(error => {
        console.error('Error:', error);
        alert('Failed to update product.');
    });
});
