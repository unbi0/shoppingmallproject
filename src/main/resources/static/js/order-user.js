// 페이지가 로드 되기 전에 실행
document.addEventListener("DOMContentLoaded", (event) => {
  document.getElementById("searchButton").addEventListener("click", () => {
    page = 0;
    allLoaded = false;
    fetchOrder(true);
  });
  fetchOrder(true);
  window.addEventListener("scroll", handleScroll);
});

const statusMapping = {
  PLACED: "주문완료",
  SHIPPED: "배송중",
  DELIVERED: "배송완료",
};

let page = 0;
const size = 15;
let isLoading = false;
let allLoaded = false;

function handleScroll() {
  const scrollTop = window.scrollY;
  const scrollHeight = document.documentElement.scrollHeight;
  const clientHeight = document.documentElement.clientHeight;

  if (scrollTop + clientHeight >= scrollHeight - 10) {
    fetchOrder();
  }
}

// 주문 정보 가져오기
function fetchOrder(reset = false) {
  if (isLoading || allLoaded) return;
  isLoading = true;

  const orderId = document.getElementById("orderId").value;
  const startDate = convertToISOFormat(
    document.getElementById("startDate").value
  );
  const endDate = convertToISOFormat(document.getElementById("endDate").value);

  const params = new URLSearchParams({
    orderId: orderId,
    startDate: startDate,
    endDate: endDate,
    page: page,
    size: size,
  });

  fetch(`/orders?${params.toString()}`, {
      method: "GET"
    }
  )
    .then((response) => response.json())
    .then((data) => {
      if (reset) {
        document.getElementById("orderList").innerHTML = ""; // Clear existing orders
      }

      if (data.content.length === 0) {
        allLoaded = true;
        if (page === 0) {
          document.getElementById("noOrdersMessage").style.display = "block";
        }
      } else {
        document.getElementById("noOrdersMessage").style.display = "none";
        console.log(data.content);
        updateOrderList(data.content);
        updateOrderSummary(data);
        page++;
      }
    })
    .catch((error) => console.error("Error fetching orders:", error))
    .finally(() => {
      isLoading = false;
    });
}

// 주문 리스트 업데이트
function updateOrderList(data) {
  const orderListContainer = document.getElementById("orderList");

  if (data.length === 0) {
    noOrdersMessage.style.display = "block";
  } else {
    noOrdersMessage.style.display = "none";
  }

  data.forEach((order) => {
    const formattedDate = formatDate(order.createAt);
    const koreanStatus = statusMapping[order.orderStatus] || order.orderStatus;

    // orderDetailList가 정의되지 않았거나 빈 배열인 경우를 처리
    const orderProducts = order.orderDetailList
      ? order.orderDetailList
          .map((detail) => detail.productOption.product.name)
          .join(", ")
      : "";

    const orderElement = `
      <div class="orders-list">
      <p style="font-weight: bold; width: 100px;">${order.orderId}</p>
      <div style="display: flex; justify-content: center; align-items: center; width: 250px;">
        <p style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 150px; font-weight: bold;">${getUniqueProductNames(
          order.productName
        ).join(", ")}</p>
        <chip id="orderDetailBtn" class="chip gray-chip" style="margin-left: 5px;">
          <a href="/order/${order.orderId}">주문상세</a>
        </chip>
      </div>
      <p style="width: 120px;">${formatDate(order.createAt)}</p>
      <div style="width: 115px; display: flex; justify-content: center;">
        <chip class="chip ${getChipColor(
          order.orderStatus
        )}">${koreanStatus}</chip>
      </div>
      <p style="font-weight: bold; width: 115px;">${order.totalCount}</p>
      <p style="font-weight: bold; width: 140px;">KRW ${(
        order.totalPrice + order.deliveryFee
      ).toLocaleString()}</p>
    </div>
    `;

    const orderElementDiv = document.createElement("div");
    orderElementDiv.classList.add("order-item");
    orderElementDiv.innerHTML = orderElement;
    orderListContainer.appendChild(orderElementDiv);
  });
}

// 주문 통계 업데이트
function updateOrderSummary(data) {
  const totalAmount = document.getElementById("totalAmount");
  const listLength = document.getElementById("listLength");
  const dateLength = document.getElementById("dateLength");
  const startDateValue = document.getElementById("startDate").value;
  const endDateValue = document.getElementById("endDate").value;

  const startDate = startDateValue ? new Date(startDateValue) : null;
  const endDate = endDateValue ? new Date(endDateValue) : null;

  listLength.textContent = `${data.totalElements} 건`;
  const totalSum = data.content.reduce(
    (accumulator, order) => accumulator + order.deliveryFee + order.totalPrice,
    0
  );
  totalAmount.textContent = `${totalSum.toLocaleString()} 원`;

  const startDateString = startDate ? formatDate(startDate) : "";
  const endDateString = endDate ? formatDate(endDate) : "";

  dateLength.textContent = `${startDateString} ~ ${endDateString}`;
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function convertToISOFormat(dateString) {
  if (!dateString) {
    return "";
  }
  const date = new Date(dateString); // 입력된 날짜 문자열을 Date 객체로 변환
  const isoDateTime = date.toISOString(); // Date 객체를 ISO 8601 형식의 문자열로 변환
  return isoDateTime.slice(0, 19);
}

// 주문 상태에 따른 색상 반환
function getChipColor(status) {
  const statusColors = {
    PLACED: "blue-chip",
    SHIPPED: "red-chip",
    DELIVERED: "green-chip",
  };
  return statusColors[status] || "gray-chip";
}

function getUniqueProductNames(productNames) {
  const uniqueNames = [];
  productNames.forEach((name) => {
    if (!uniqueNames.includes(name)) {
      uniqueNames.push(name);
    }
  });
  return uniqueNames;
}

window.onscroll = function () {
  scrollFunction();
};

function scrollFunction() {
  const scrollToTopBtn = document.getElementById("scrollToTopBtn");
  if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
    scrollToTopBtn.style.display = "block";
  } else {
    scrollToTopBtn.style.display = "none";
  }
}

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

document.addEventListener('DOMContentLoaded', function () {
        const startDateInput = document.getElementById('startDate');
        const endDateInput = document.getElementById('endDate');

        startDateInput.addEventListener('input', function () {
            // endDate의 최소값을 startDate의 값으로 설정
            endDateInput.min = startDateInput.value;
        });

        endDateInput.addEventListener('input', function () {
            // startDate의 최대값을 endDate의 값으로 설정
            startDateInput.max = endDateInput.value;
        });
    });