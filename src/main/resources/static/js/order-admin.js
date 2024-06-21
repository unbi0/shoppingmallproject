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
const size = 10;
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

  fetch(`/admin/orders?${params.toString()}`)
    .then((response) => response.json())
    .then((data) => {
      if (reset) {
        document.getElementById("orderList").innerHTML = "";
      }

      if (data.content.length === 0) {
        allLoaded = true;
        if (page === 0) {
          document.getElementById("noOrdersMessage").style.display = "block";
        }
      } else {
        document.getElementById("noOrdersMessage").style.display = "none";
        console.log(data);
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
      <p style="width: 100px;">${formattedDate}</p>
      <div style="display: flex; justify-content: center; align-items: center; width: 200px;">
        <p style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 100px; font-weight: bold;">${getUniqueProductNames(
          order.productName
        ).join(", ")}</p>
        <chip class="chip gray-chip" style="margin-left: 5px;">
          <a href="/order/${order.orderId}">주문상세</a>
        </chip>
      </div>
      <div style="width: 100px; display: flex; justify-content: center;">
        <chip class="chip ${getChipColor(
          order.orderStatus
        )}">${koreanStatus}</chip>
        <select style="height: 20px; width: 20px;" onchange="changeOrderStatus(event, '${
          order.orderId
        }')">
          <option value="PLACED" ${
            order.orderStatus === "PLACED" ? "selected" : ""
          }>주문완료</option>
          <option value="SHIPPED" ${
            order.orderStatus === "SHIPPED" ? "selected" : ""
          }>배송중</option>
          <option value="DELIVERED" ${
            order.orderStatus === "DELIVERED" ? "selected" : ""
          }>배송완료</option>
        </select>
      </div>
      <p style="font-weight: bold; width: 80px;">${order.recipientName}</p>
      <p style="font-weight: bold; width: 120px;">${order.recipientTel}</p>
      <p style="font-weight: bold; width: 60px;">${order.totalCount}</p>
      <p style="font-weight: bold; width: 140px;">KRW ${(
        order.totalPrice + order.deliveryFee
      ).toLocaleString()}</p>
    <div>
    `;

    const orderElementDiv = document.createElement("div");
    orderElementDiv.classList.add("order-item");
    orderElementDiv.innerHTML = orderElement;
    orderListContainer.appendChild(orderElementDiv);
  });
}

// 주문 상태 변경 함수
function changeOrderStatus(event, orderId) {
  const newStatus = event.target.value;
  const koreanStatus = statusMapping[newStatus];

  const confirmed = confirm(
    `주문 상태를 "${koreanStatus}"로 변경하시겠습니까?`
  );
  if (!confirmed) {
    // Revert to the previous status if the user cancels
    event.target.value = statusMapping[newStatus];
    return;
  }

  // API 요청 보내기
  fetch(`/orders/status/${orderId}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ orderStatus: newStatus }),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to update order status");
      }
      location.reload();
      console.log(`Order ${orderId} status updated to ${newStatus}`);
    })
    .catch((error) => {
      console.error("Error updating order status:", error);
      alert("주문 상태를 변경하는 중 오류가 발생했습니다.");
    });
}

// 주문 통계 업데이트
function updateOrderSummary(data) {
  const orderCountElement = document.getElementById("listLength");
  const completedCountElement = document.getElementById("completedCount");
  const inProgressCountElement = document.getElementById("inProgressCount");
  const deliveredCountElement = document.getElementById("deliveredCount");
  const startDateValue = document.getElementById("startDate").value;
  const endDateValue = document.getElementById("endDate").value;

  const startDate = startDateValue ? new Date(startDateValue) : null;
  const endDate = endDateValue ? new Date(endDateValue) : null;

  const orderCount = data.totalElements || 0;
  const completedCount = (
    data.content.filter((order) => order.orderStatus === "PLACED") || []
  ).length;
  const inProgressCount = (
    data.content.filter((order) => order.orderStatus === "SHIPPED") || []
  ).length;
  const deliveredCount = (
    data.content.filter((order) => order.orderStatus === "DELIVERED") || []
  ).length;

  orderCountElement.textContent = `${orderCount} 건`;
  completedCountElement.textContent = completedCount || 0;
  inProgressCountElement.textContent = inProgressCount || 0;
  deliveredCountElement.textContent = deliveredCount || 0;

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
  const date = new Date(dateString);
  const isoDateTime = date.toISOString();
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
