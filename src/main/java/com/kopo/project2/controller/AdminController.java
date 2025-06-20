package com.kopo.project2.controller;

import com.kopo.project2.model.User;
import com.kopo.project2.repository.DB;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    // 관리자용 회원 정보 수정 폼 이동
    @GetMapping("/admin/user/edit")
    public String adminUserEdit(@RequestParam("id") String id, Model model) {
        DB db = new DB();
        User user = db.selectOne(id);
        model.addAttribute("user", user);
        return "edit_user_admin";
    }

    // 수정 반영 처리
    @PostMapping("/admin/user/update")
    public String adminUserUpdate(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        DB db = new DB();
        User original = db.selectOne(id); // 📌 기존 정보 조회

        // ✅ 변경된 정보가 있는 경우에만 update
        if (!name.equals(original.getName()) ||
                !phone.equals(original.getPhone()) ||
                !address.equals(original.getAddress())) {

            db.updateUser(new User(id, "", name, phone, address));
            return "redirect:/user_list?message=updated";
        }

        // ✅ 변경 사항이 없으면 메시지 없이 목록으로만 이동
        return "redirect:/user_list";
    }
}
