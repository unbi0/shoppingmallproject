// 페이지가 로드 되기 전에 실행
document.addEventListener("DOMContentLoaded", (event) => {
  fetchOrder();
});

const statusMapping = {
  PLACED: "주문완료",
  SHIPPED: "배송중",
  DELIVERED: "배송완료",
};

// 주문 정보 가져오기
function fetchOrder() {
  // 경로에 들어있는 orderId로 변경해야 함
  // 현재 URL 가져오기
  const currentUrl = window.location.href;

  // URL에서 pathname(경로) 부분 가져오기
  const pathname = new URL(currentUrl).pathname;

  // 경로에서 orderId 추출
  // 예시: /order/31 의 경우
  const parts = pathname.split('/');
  const orderId = parts[parts.length - 1];

  fetch(`/orders/${orderId}`)
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      console.log(data.orderDetailDtoList);
      updateView(data);
    })
    .catch((error) => {
          console.error('주문 조회 오류:', error.message);
          // 오류 메시지 표시
          alert('주문을 조회할 수 없습니다. 다시 시도해주세요.');
          // 에러 페이지로 리디렉션
          window.history.back(); // 예시: 에러 페이지로 리디렉션
        });
}

function updateView(data) {

const path = window.location.pathname;

  // 경로에서 orderId 값을 추출
  // 현재 URL 가져오기
  const currentUrl = window.location.href;

  // URL에서 pathname(경로) 부분 가져오기
  const pathname = new URL(currentUrl).pathname;

  // 경로에서 orderId 추출
  // 예시: /order/31 의 경우
  const parts = pathname.split('/');
  const orderId = parts[parts.length - 1];

  // 버튼의 href 속성에 orderId 값 추가
  const editLink = document.getElementById('editLink');
  editLink.href = `./${orderId}/edit`;

  const formattedDate = formatDate(data.createAt);
  const koreanStatus = statusMapping[data.orderStatus] || data.orderStatus;

  // 주문 정보를 각 입력 필드에 설정
  document.getElementById("orderStatus").textContent = koreanStatus;
  document.getElementById("recipientName").textContent = data.recipientName;
  document.getElementById(
    "createAt"
  ).textContent = `주문일시 : ${formattedDate}`;
  document.getElementById("orderId").textContent = `주문번호 : ${data.orderId}`;
  document.getElementById("postCode").textContent = data.postCode;
  document.getElementById("deliveryAddress").textContent = data.deliveryAddress;
  document.getElementById("deliveryDetailAddress").textContent =
    data.deliveryDetailAddress;
  document.getElementById("deliveryRequest").textContent = data.deliveryRequest;

  // 전화번호 설정
  document.getElementById("recipientTel").textContent = data.recipientTel;

  // 상품 금액, 배송비, 결제 금액 설정
  document.getElementById(
    "totalPrice"
  ).textContent = `KRW ${data.totalPrice.toLocaleString()}`;
  document.getElementById(
    "deliveryFee"
  ).textContent = `+KRW ${data.deliveryFee.toLocaleString()}`;
  document.getElementById(
    "totalAmount"
  ).textContent = `KRW ${data.totalAmount.toLocaleString()}`;

  // 주문 상태가 PLACED일 때만 버튼 보이기
  if (data.orderStatus !== "PLACED") {
    document.getElementById("orderButtons").style.display = "none";
  }

  const orderListContainer = document.getElementById("productList");
  orderListContainer.innerHTML = ""; // 기존 목록 초기화

  data.orderDetailDtoList.forEach((orderDetails) => {
    const orderElement = `
    <div class="product-list-order" style="border: 1px solid #C0C0C0; display: flex;">
      <img class="thumb2" src="${orderDetails.imageUrl}"
        alt="">
      <div class="product-info-order" style="margin-left: 5px;">
        <span style="font-weight: bold;">${orderDetails.productName}</span>
        <span style="color: gray;">KRW ${orderDetails.price.toLocaleString()}</span>
        <span></span>
        <span>[옵션 : ${orderDetails.optionSize}]</span>
        <span>수량 : ${orderDetails.count}</span>
      </div>
    </div>
    `;

    const orderElementDiv = document.createElement("div");
    orderElementDiv.classList.add("product-item-order");
    orderElementDiv.innerHTML = orderElement;
    orderListContainer.appendChild(orderElementDiv);
  });
}

function formatDate(datetimeString) {
  const date = new Date(datetimeString);
  const year = date.getFullYear();
  const month = padZero(date.getMonth() + 1); // Months are zero based
  const day = padZero(date.getDate());
  const dayOfWeek = getDayOfWeek(date.getDay());
  const hours = padZero(date.getHours());
  const minutes = padZero(date.getMinutes());

  const formattedDate = `${year}년 ${month}월 ${day}일 (${dayOfWeek}) ${hours}:${minutes}`;
  return formattedDate;
}

function padZero(num) {
  return num < 10 ? `0${num}` : num;
}

function getDayOfWeek(dayOfWeek) {
  const daysOfWeek = ["일", "월", "화", "수", "목", "금", "토"];
  return daysOfWeek[dayOfWeek];
}

function confirmDeleteOrder() {
  if (confirm("주문을 취소하시겠습니까?")) {
    deleteOrder();
  }
}

function deleteOrder() {
  // 현재 URL 가져오기
    const currentUrl = window.location.href;

    // URL에서 pathname(경로) 부분 가져오기
    const pathname = new URL(currentUrl).pathname;

    // 경로에서 orderId 추출
    // 예시: /order/31 의 경우
    const parts = pathname.split('/');
    const orderId = parts[parts.length - 1];

  fetch(`/orders/${orderId}`, {
    method: "DELETE",
  })
    .then((response) => {
      if (response.ok) {
        alert("주문이 취소되었습니다.");
        // 주문 목록 페이지로 이동하거나 페이지를 새로고침
        window.location.href = "/user/order";
      } else {
        alert("주문 취소에 실패했습니다.");
      }
    })
    .catch((error) => {
      console.error("Error deleting order:", error);
      alert("주문 취소에 실패했습니다.");
    });
}

function goBack() {
            window.history.back();
        }