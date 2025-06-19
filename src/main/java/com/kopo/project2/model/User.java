package com.kopo.project2.model;

// 📦 사용자 정보를 담는 VO 객체 (회원가입, 로그인, 조회 등에서 사용)
public class User {

    // 🆔 기본 필드들
    private int idx; // 고유 번호 (DB PK)
    private String id; // 아이디
    private String pwd; // 비밀번호
    private String userType; // 사용자 유형 (admin, user 등)
    private String name; // 이름
    private String phone; // 전화번호
    private String address; // 주소
    private String created; // 생성일자
    private String lastUpdated; // 최종 수정일

    // 🧱 기본 생성자 (필수)
    public User() {
    }

    // 👤 로그인용 생성자
    public User(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    // 📝 회원가입용 생성자
    public User(String id, String pwd, String name, String phone, String address) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // 🧾 전체 필드 초기화용 생성자
    public User(int idx, String userType, String id, String pwd, String name,
            String phone, String address, String created, String lastUpdated) {
        this.idx = idx;
        this.userType = userType;
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    // 📤 Getter & Setter

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
