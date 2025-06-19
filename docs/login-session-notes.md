# ✅ 세션 관리 + 클라이언트 분기 처리 요약

## 1. 세션 관리

- 로그인 성공 시, 서버에서는 `HttpSession` 객체에 다음 정보 저장:
  - `is_login`: true
  - `login_id`: 현재 사용자 ID
  - `user_type`: guest / admin

## 2. API 응답

- `/sessionCheck` 요청에 대해 아래와 같이 JSON 응답

```json
{
  "is_login": true,
  "login_id": "moa"
}
```
