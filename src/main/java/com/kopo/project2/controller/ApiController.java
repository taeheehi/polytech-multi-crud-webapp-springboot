package com.kopo.project2.controller;

import com.kopo.project2.repository.DB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class ApiController {

    @GetMapping("/create")
    public HashMap<String, String> create() {
        HashMap<String, String> data = new HashMap<>();
        DB db = new DB();
        db.createTable();
        data.put("message", "테이블이 생성되었습니다.");
        return data;
    }
}
