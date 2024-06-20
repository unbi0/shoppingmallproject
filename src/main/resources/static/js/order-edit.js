// 페이지가 로드 되기 전에 실행
document.addEventListener("DOMContentLoaded", (event) => {
  fetchOrder();
});

// 우편번호 찾기 기능
function execDaumPostcode() {
  daum.postcode.load(function () {
    new daum.Postcode({
      oncomplete: function (data) {
        var addr = "";

        if (data.userSelectedType === "R") {
          addr = data.roadAddress;
        } else {
          addr = data.jibunAddress;
        }

        document.getElementById("postCode").value = data.zonecode;
        document.getElementById("deliveryAddress").value = addr;
        document.getElementById("deliveryDetailAddress").focus();
      },
    }).open();
  });
}

// 주문 정보 가져오기
function fetchOrder() {
  // 경로에 들어있는 orderId로 변경해야 함
  // 현재 URL 가져오기
  const currentUrl = window.location.href;

  // URL에서 pathname(경로) 부분 가져오기
  const url = new URL(currentUrl);
  const pathname = url.pathname;

  // 경로에서 orderId 추출
  // 예시: /order/30/edit 의 경우
  const parts = pathname.split('/');
  const orderId = parts[2];

  fetch(`/orders/${orderId}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      updateView(data);
    })
    .catch((error) =>{
    console.error("Error fetching orders:", error)
    alert('주문을 조회할 수 없습니다. 다시 시도해주세요.');
              // 에러 페이지로 리디렉션
     window.history.back(); // 예시: 에러 페이지로 리디렉션
    });
}

function updateView(data) {
  // 주문 정보를 각 입력 필드에 설정
  document.getElementById("recipientName").value = data.recipientName;
  document.getElementById("postCode").value = data.postCode;
  document.getElementById("deliveryAddress").value = data.deliveryAddress;
  document.getElementById("deliveryDetailAddress").value =
    data.deliveryDetailAddress;
  document.getElementById("deliveryRequest").value = data.deliveryRequest;

  // 전화번호 설정
  const telParts = data.recipientTel.split("-");
  document.getElementById("tel1").value = telParts[1];
  document.getElementById("tel2").value = telParts[2];

  // 상품 금액, 배송비, 결제 금액 설정
  document.getElementById(
    "totalPrice"
  ).textContent = `KRW ${data.totalPrice.toLocaleString()}`;
  document.getElementById(
    "deliveryFee"
  ).textContent = `+KRW ${data.deliveryFee.toLocaleString()}`;
  const totalAmount = data.totalPrice + data.deliveryFee;
  document.getElementById(
    "totalAmount"
  ).textContent = `KRW ${totalAmount.toLocaleString()}`;

  // 화면에 그리기
  const productContainer = document.getElementById("product-list-order");
  productContainer.innerHTML = "";

  // 여러 개의 상품 정보 반복으로 출력
  data.orderDetailDtoList.forEach((product) => {
    const productItem = document.createElement("div");
    productItem.classList.add("product-list-wrap");

    productItem.innerHTML = `
      <img class="thumb" src="${product.imageUrl}" alt="">
        <div class="product-info-order">
          <span style="font-weight: bold;">${product.productName}</span>
          <span style="color: gray;">KRW ${product.price.toLocaleString()}</span>
          <span>[옵션 : ${product.optionSize}]</span>
          <span>수량 : ${product.count}</span>
        </div>
      `;
    productContainer.appendChild(productItem);
  });
}

// 전화번호 검증 코드
function validatePhoneNumber() {
  const tel1 = document.getElementById("tel1").value;
  const tel2 = document.getElementById("tel2").value;
  const isValid = tel1.length === 4 && tel2.length === 4;
  document.getElementById("phoneNumberError").style.display = isValid
    ? "none"
    : "block";
  return isValid;
}

// 수정 폼 검증
function validateForm() {
  const recipientName = document.getElementById("recipientName").value;
  const postCode = document.getElementById("postCode").value;
  const deliveryAddress = document.getElementById("deliveryAddress").value;
  const deliveryDetailAddress = document.getElementById(
    "deliveryDetailAddress"
  ).value;
  const tel1 = document.getElementById("tel1").value;
  const tel2 = document.getElementById("tel2").value;
  const deliveryRequest = document.getElementById("deliveryRequest").value;

  const isFormValid =
    recipientName &&
    postCode &&
    deliveryAddress &&
    deliveryDetailAddress &&
    tel1 &&
    tel2 &&
    deliveryRequest;
  const isPhoneValid = validatePhoneNumber();

  if (!isFormValid) {
    document.getElementById("formError").style.display = "block";
  } else {
    document.getElementById("formError").style.display = "none";
  }

  return isFormValid && isPhoneValid;
}

// 수정 폼 제출

function handleOrderUpdate() {
  // 경로에 들어있는 orderId로 변경해야 함
  // 현재 URL 가져오기
  const currentUrl = window.location.href;

  // URL에서 pathname(경로) 부분 가져오기
  const url = new URL(currentUrl);
  const pathname = url.pathname;

  // 경로에서 orderId 추출
  // 예시: /order/30/edit 의 경우
  const parts = pathname.split('/');
  const orderId = parts[2];

  // 수정할 데이터 폼 세팅
  const updatedOrder = {
    deliveryRequest: document.getElementById("deliveryRequest").value,
    recipientName: document.getElementById("recipientName").value,
    recipientTel: `${document.getElementById("telPrefix").value}-${document.getElementById("tel1").value}-${document.getElementById("tel2").value}`,
    postCode: document.getElementById("postCode").value,
    deliveryAddress: document.getElementById("deliveryAddress").value,
    deliveryDetailAddress: document.getElementById("deliveryDetailAddress").value,
  };

  // 검증이 되면 수정 요청
  if (!validateForm()) {
    alert("모든 필드를 올바르게 채워주세요.");
    return false; // 이벤트 전파 중단
  }

  // 사용자에게 수정 여부를 확인
  if (confirm("주문 정보를 수정하시겠습니까?")) {
      return fetch(`/orders/${orderId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedOrder),
      })
        .then((response) => {
          if (response.ok) {
            alert("주문이 수정되었습니다.");
            // 주문 목록 페이지로 이동하거나 페이지를 새로고침
            window.location.href = `/order/${orderId}`;
          } else {
            alert("주문 수정에 실패했습니다.");
          }
        })
        .catch((error) => {
          console.error("Error updating order:", error.message);
          alert("주문 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
        });
    } else {
      console.log("주문 수정이 취소되었습니다.");
      return false;
    }
}

function goBack() {
            window.history.back();
        }