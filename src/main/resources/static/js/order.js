document.addEventListener("DOMContentLoaded", (event) => {
    fetchMy();
    viewProductInfo();
  });
  function execDaumPostcode() {
    daum.postcode.load(function () {
      new daum.Postcode({
        oncomplete: function (data) {

          var addr = '';

          if (data.userSelectedType === 'R') {
            addr = data.roadAddress;
          } else {
            addr = data.jibunAddress;
          }

          document.getElementById('info-postCode').value = data.zonecode;
          document.getElementById('info-address').value = addr;
          document.getElementById("info-detailAddress").focus();
        }
      }).open();
    })
  }

  // 우편번호 찾기 기능
  function execDaumPostcode2() {
    daum.postcode.load(function () {
      new daum.Postcode({
        oncomplete: function (data) {

          var addr = '';

          if (data.userSelectedType === 'R') {
            addr = data.roadAddress;
          } else {
            addr = data.jibunAddress;
          }

          document.getElementById('form-postCode').value = data.zonecode;
          document.getElementById('form-address').value = addr;
          document.getElementById("form-detailAddress").focus();
        }
      }).open();
    })
  }

  function fetchMy(){
fetch(`/my`, {
      method: "GET"
    }
  )
    .then((response) => response.json())
    .then((data) => {
      console.log(data)

      // 주소가 없는 경우 빈 객체로 대체
    const address = data.address || {};

    document.getElementById('info-name').value = data.username || "";
    document.getElementById('info-postCode').value = address.postcode || "";
    document.getElementById('info-address').value = address.address || "";
    document.getElementById("info-detailAddress").value = address.detailAddress || "";

    populateEmailForm(data.email || "");
    })
    .catch((error) => console.error("Error fetching orders:", error))
}

 function populateEmailForm(email) {
    const emailParts = email.split('@');
    if (emailParts.length === 2) {
      document.getElementById('emailPart1').value = emailParts[0];
      document.getElementById('emailPart2').value = emailParts[1];
    } else {
      console.error('Invalid email format');
    }
  }

// 상품 정보 리스트 세팅
function viewProductInfo() {
  const productsData = JSON.parse(localStorage.getItem("productList"));

  let totalPrice = 0;

  if(Array.isArray(productsData)){
    productsData.forEach((product) => {

      const productInfoList = document.getElementById("product-list-order");

      const productInfoElement = `
        <div class="product-list-wrap">
          <img class="thumb"
               src="${product.imageUrl}"
               alt="">
          <div class="product-info-order">
            <span style="font-weight: bold;">${product.name}</span>
            <span style="color: gray;">KRW ${product.price.toLocaleString()}</span>
            <span></span>
            <span>[옵션 : ${product.size}]</span>
            <span>수량 : ${product.quantity}</span>
          </div>
        </div>
      `;

      const productElementDiv = document.createElement("div");
      productElementDiv.classList.add("product-list-wrap");
      productElementDiv.innerHTML = productInfoElement;
      productInfoList.appendChild(productElementDiv);

      totalPrice += product.price * product.quantity;
    });
  }else{
    console.error("상품정보가 없습니다.");
  }
  document.getElementById("totalPrice").textContent = `KRW ${totalPrice.toLocaleString()}`;
  document.getElementById("totalAmount").textContent = `KRW ${(totalPrice+2000).toLocaleString()}`;

}

function createOrder(){

  let orderDetailList = [];

  const productsData = JSON.parse(localStorage.getItem("productList"));

  if(Array.isArray(productsData)){
    productsData.forEach((product) => {

      const orderDetailInit = {
        "productOptionId": product.id,
        "count": product.quantity
      }

      orderDetailList.push(orderDetailInit);
    })
  }else{
    console.error("상품정보가 없습니다.");
    return false;
  }

  const part1 = document.getElementById("tel1").value;
  const part2 = document.getElementById("tel2").value;
  const part3 = document.getElementById("tel3").value;
  const recipientTel = `${part1}-${part2}-${part3}`;

  const createOrderData = {
    deliveryRequest : document.getElementById("form-deliveryRequest").value,
    recipientName : document.getElementById("form-recipientName").value,
    recipientTel : recipientTel,
    postCode : document.getElementById("form-postCode").value,
    deliveryAddress : document.getElementById("form-address").value,
    deliveryDetailAddress : document.getElementById("form-detailAddress").value,
    orderDetailRequestDtoList : orderDetailList
  }

  // 검증이 되면 생성 요청
  if (!validateForm()) {
    alert("모든 필드를 올바르게 채워주세요.");
    return false; // 이벤트 전파 중단
  }

  if (confirm("주문을 생성하시겠습니까?")){
    fetch(`/orders`, {
      method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(createOrderData),
    })
    .then(response => {
      if (!response.ok) {
        // 서버 응답이 2xx 범위가 아니면 에러를 던짐
        throw new Error('재고가 부족하여 주문이 불가한 상품입니다.');
      }
      return response.json();
    })
    .then((data) => {
      console.log(data);

      const isCart = JSON.parse(localStorage.getItem("isCart"));

      // 장바구니에서 온 주문이면 localStorage 비우고 장바구니 전체삭제
      // 상품상세에서 온 주문이면 localStorage 비움
      if(isCart){
        localStorage.removeItem("productList");
        localStorage.removeItem("isCart");

        fetch(`/cart/all`, {
                method: "DELETE"
              })
              .then((cartData) => {
                console.log(cartData);
                alert("주문이 완료되었습니다.");
                window.location.href = "/user/order";
              })
              .catch((error) => {
                console.error("Error deleting cart:", error);
                alert("장바구니 삭제 중 오류가 발생했습니다. 다시 시도해주세요.");
              });

      }else{
        localStorage.removeItem("productList");
        localStorage.removeItem("isCart");
        alert("주문이 완료되었습니다.");
        window.location.href = "/user/order";
      }
    })
    .catch((error) => {
      console.error("Error fetching orders:", error);
      alert("재고가 부족하여 주문이 불가한 상품입니다.");
      window.history.back();
    })
  }else{
    return false;
  }
}

window.addEventListener('beforeunload', function(event) {
  // 사용자에게 경고 메시지 표시
  const confirmationMessage = '페이지를 떠나시겠습니까?';
  event.preventDefault();
  event.returnValue = confirmationMessage;

  return confirmationMessage;
});

window.addEventListener('unload', function() {
  // 페이지가 실제로 언로드될 때 로컬 스토리지 비우기
  localStorage.removeItem("productList");
  localStorage.removeItem("isCart");
});

// 전화번호 검증 코드
function validatePhoneNumber() {
  const tel2 = document.getElementById("tel2").value;
  const tel3 = document.getElementById("tel3").value;
  const isValid = tel2.length === 4 && tel3.length === 4;
  document.getElementById("phoneNumberError").style.display = isValid
    ? "none"
    : "block";
  return isValid;
}

// 주문 폼 검증
function validateForm() {
  const deliveryRequest = document.getElementById("form-deliveryRequest").value;
  const recipientName = document.getElementById("form-recipientName").value;
  const tel2 = document.getElementById("tel2").value;
  const tel3 = document.getElementById("tel3").value;
  const postCode = document.getElementById("form-postCode").value;
  const deliveryAddress = document.getElementById("form-address").value;
  const deliveryDetailAddress = document.getElementById("form-detailAddress").value;

  const isFormValid = deliveryRequest && recipientName && tel2 && tel3 && postCode && deliveryAddress && deliveryDetailAddress;
  const isPhoneValid = validatePhoneNumber();

  if (!isFormValid) {
    document.getElementById("formError").style.display = "block";
  } else {
    document.getElementById("formError").style.display = "none";
  }

  return isFormValid && isPhoneValid;
}

 function populateEmailForm(email) {
    const emailParts = email.split('@');
    if (emailParts.length === 2) {
      document.getElementById('emailPart1').value = emailParts[0];
      document.getElementById('emailPart2').value = emailParts[1];
    } else {
      console.error('Invalid email format');
    }
  }

function copyOrderInfo() {
    document.getElementById('form-recipientName').value = document.getElementById('info-name').value;
    document.getElementById('form-postCode').value = document.getElementById('info-postCode').value;
    document.getElementById('form-address').value = document.getElementById('info-address').value;
    document.getElementById('form-detailAddress').value = document.getElementById('info-detailAddress').value;
  }

  function clearDeliveryInfo() {
    document.getElementById('form-recipientName').value = '';
    document.getElementById('form-postCode').value = '';
    document.getElementById('form-address').value = '';
    document.getElementById('form-detailAddress').value = '';
  }

  function goBack() {
              window.history.back();
          }