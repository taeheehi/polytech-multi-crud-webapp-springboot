package com.kopo.project2.controller;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kopo.project2.repository.DB;
import com.kopo.project2.model.User;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        return "home";
    }

    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public String join(Locale locale, Model model) {
        return "join";
    }

    // 스프링POST방식으로 한글 깨짐 해결 방법
    @RequestMapping(value = "/join_action", method = RequestMethod.POST)
    public String joinAction(Locale locale, Model model, HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        DB db = new DB();
        db.insertData(new User(id, pwd, name, phone, address));
        return "redirect:/";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Locale locale, Model model) {
        return "login";
    }

    @RequestMapping(value = "/login_action", method = RequestMethod.POST)
    public String loginAction(Locale locale, Model model, HttpServletRequest request, HttpSession session) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String id = request.getParameter("id");
        String pwd = request.getParameter("pwd");

        DB db = new DB();
        User loggedUser = db.login(new User(id, pwd));

        if (loggedUser != null && loggedUser.userType != null && !loggedUser.userType.isEmpty()) {
            session.setAttribute("is_login", true);
            session.setAttribute("user_type", loggedUser.userType);
            session.setAttribute("login_id", loggedUser.id);
        } else {
            session.setAttribute("is_login", false);
            session.setAttribute("user_type", "");
            session.setAttribute("login_id", "");
        }

        // 디버깅 로그 출력
        System.out.println("로그인 결과 테스트:");
        System.out.println("→ is_login: " + session.getAttribute("is_login"));
        System.out.println("→ user_type: " + session.getAttribute("user_type"));
        return "redirect:/";
    }

    // ✅ user_list.jsp → HTML 마이그레이션 대응
    @RequestMapping(value = "/user_list", method = RequestMethod.GET)
    public String userList(Locale locale, Model model, HttpSession session) {
        boolean isLogin = false;
        String userType = "";

        try {
            isLogin = (Boolean) session.getAttribute("is_login");
            userType = (String) session.getAttribute("user_type");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isLogin && "admin".equals(userType)) {
            DB db = new DB();
            ArrayList<User> users = db.selectAll();
            model.addAttribute("users", users);
            return "user_list"; // templates/user_list.html → JSTL 제거 예정
        }

        return "redirect:/";
    }

    @RequestMapping("/board")
    public String board() {
        return "board";
    }

    @RequestMapping("/shop")
    public String shop() {
        return "shop";
    }

    @RequestMapping("/game")
    public String game() {
        return "game";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(value = "/mypage", method = RequestMethod.GET)
    public String mypage(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("login_id");

        if (loginId == null || loginId.isEmpty()) {
            return "redirect:/login";
        }

        DB db = new DB();
        User user = db.selectOne(loginId);

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "mypage";
    }

    @RequestMapping("/mypage_edit")
    public String mypageEdit(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("login_id");
        DB db = new DB();
        User user = db.selectOne(loginId);
        model.addAttribute("user", user);
        return "mypage_edit";
    }

    @RequestMapping(value = "/mypage_edit_action", method = RequestMethod.POST)
    public String mypageEditAction(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        System.out.println("📅 수정 요청 들어옴:");
        System.out.println("id = " + id);
        System.out.println("name = " + name);
        System.out.println("phone = " + phone);
        System.out.println("address = " + address);

        DB db = new DB();
        db.updateUser(new User(id, "", name, phone, address));

        return "redirect:/mypage";
    }
}
