package com.kopo.project2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 🔧 getter/setter/toString 자동 생성
@NoArgsConstructor // 🔧 기본 생성자
@AllArgsConstructor // 🔧 전체 필드 생성자
public class User {
    int idx;
    String id;
    String pwd;
    String userType;
    String name;
    String phone;
    String address;
    String created;
    String lastUpdated;

    // @NoArgsConstructor 기능과 동일
    // User(){
    // }

    // @AllArgsConstructor 기능과 동일
    // User(int idx, String userType, String id, String pwd, String name, String
    // phone, String address, String created, String lastUpdated){
    // this.idx = idx;
    // this.userType = userType;
    // this.id = id;
    // this.pwd = pwd;
    // this.name = name;
    // this.phone = phone;
    // this.address = address;
    // this.created = created;
    // this.lastUpdated = lastUpdated;
    // }

    // 로그인용
    User(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    // 회원가입용
    User(String id, String pwd, String name, String phone, String address) {
        this.id = id;
        this.pwd = pwd;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    // getter (잭슨 라이브러리 사용하기 위해서 세터 게터가 필요하다)
    // public int getIdX() {
    // return this.idx;
    // }
    //
    // public String getId() {
    // return this.id;
    // }
    // public String getPwd() {
    // return this.pwd;
    // }
    // public String getUserType() {
    // return this.userType;
    // }
    // public String getName() {
    // return this.name;
    // }
    // public String getPhone() {
    // return this.phone;
    // }
    // public String getAddress() {
    // return this.address;
    // }
    // public String getCreated() {
    // return this.created;
    // }
    // public String getLastUpdated() {
    // return this.lastUpdated;
    // }
}
