// ✅ 로그인 상태 기반 상단 메뉴 렌더링 함수
function renderNavMenu() {
  fetch("/sessionCheck")
    .then(res => res.json())
    .then(data => {
      const nav = document.getElementById("nav-right");
      let html = "";

      if (data.is_login) {
        // 관리자 또는 일반 회원에 따라 다르게 표현
        const roleLabel = data.user_type === "admin" ? "관리자" : "일반회원";
        html += `<span>${roleLabel} ${data.login_id}님</span>`;

        if (data.user_type === "admin") {
          html += `<a href="/user_list">회원관리</a>`;
        }

        html += `<a href="/mypage">마이페이지</a>`;
        html += `<a href="/logout">로그아웃</a>`;
      } else {
        html += `<a href="/login">로그인</a>`;
        html += `<a href="/join">회원가입</a>`;
      }

      if (nav) nav.innerHTML = html;
    });
}

// ✅ 공통 - 수정 메시지 알림 함수
function showMessageIfExists() {
  const urlParams = new URLSearchParams(window.location.search);
  const msg = urlParams.get("message");
  const alertBox = document.getElementById("customAlert");

  if (msg && alertBox) {
    const messages = {
      updated: "회원 정보가 수정되었습니다.",
      deleted: "회원이 삭제되었습니다.",
    };

    alertBox.innerHTML = `
      ${messages[msg] || "처리 결과가 없습니다."}
      <button onclick="this.parentElement.style.display='none'">✖</button>
    `;
    alertBox.style.display = "block";

    setTimeout(() => {
      alertBox.style.display = "none";
    }, 3000);

    history.replaceState({}, "", location.pathname);
  }
}











