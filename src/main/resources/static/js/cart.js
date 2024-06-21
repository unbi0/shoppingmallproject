document.addEventListener('DOMContentLoaded', function () {
    console.log("DOMContentLoaded event fired in cart.js");
    fetch('/loginCheck')
        .then(response => {
            console.log(`loginCheck response: ${response.status}`);
            if (response.status === 204) {
                console.log("User is logged in");
                loadCartScript('cartLogin.js', () => {
                    initializeOrderButton();
                });
            } else {
                console.log("User is not logged in");
                loadCartScript('cartLogout.js', () => {
                    initializeOrderButton();
                });
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            loadCartScript('cartLogout.js', () => {
                initializeOrderButton();
            });
        });
});

function loadCartScript(scriptName, callback) {
    const script = document.createElement('script');
    script.src = `/js/${scriptName}`;
    script.onload = () => {
        console.log(`${scriptName} loaded successfully`);
        callback();
    };
    script.onerror = () => console.error(`Failed to load ${scriptName}`);
    document.head.appendChild(script);
}

function initializeOrderButton() {
    const orderButton = document.getElementById('order-button');
    if (orderButton) {
        orderButton.addEventListener('click', handleOrder);
    }
}