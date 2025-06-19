package com.kopo.project2.controller;

import com.kopo.project2.model.User;
import com.kopo.project2.repository.DB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession; // 상단 import 추가 필요

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class ApiController {

    // 🧱 테이블 생성용 API (초기 1회용)
    @GetMapping("/create")
    public HashMap<String, String> create() {
        HashMap<String, String> data = new HashMap<>();
        DB db = new DB();
        db.createTable();
        data.put("message", "테이블이 생성되었습니다.");
        return data;
    }

    // 📋 전체 회원 목록 반환 (JSON)
    @GetMapping("/getUsersJson")
    public ArrayList<User> getUsersJson() {
        DB db = new DB();
        return db.selectAll(); // User 리스트(JSON 배열로 직렬화됨)
    }

    @GetMapping("/getMyInfoJson")
    public User getMyInfoJson(HttpSession session) {
        String loginId = (String) session.getAttribute("login_id");

        if (loginId == null || loginId.isEmpty()) {
            return new User(); // 빈 객체 반환 (undefined 방지용)
        }

        DB db = new DB();
        return db.selectOne(loginId);
    }

}
