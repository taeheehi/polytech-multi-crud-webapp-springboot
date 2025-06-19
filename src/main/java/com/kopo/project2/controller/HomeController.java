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

    // 🏠 홈페이지 첫 화면 (home.html)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        return "home";
    }

    // 📝 회원가입 페이지 이동 (join.html)
    @RequestMapping(value = "/join", method = RequestMethod.GET)
    public String join(Locale locale, Model model) {
        return "join";
    }

    // ✅ 회원가입 처리 (DB insert)
    @RequestMapping(value = "/join_action", method = RequestMethod.POST)
    public String joinAction(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
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

        // ✅ 중복 체크
        if (db.isIdDuplicated(id)) {
            try {
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('이미 사용 중인 아이디입니다.'); history.back();</script>");
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null; // 더 이상 진행 안 함
        }

        // ✅ 중복이 아니라면 등록 진행
        db.insertData(new User(id, pwd, name, phone, address));
        return "redirect:/";
    }

    // 🔐 로그인 페이지 이동 (login.html)
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Locale locale, Model model) {
        return "login";
    }

    // ✅ 로그인 처리: 세션 저장 및 리디렉션
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

        if (loggedUser != null && loggedUser.getUserType() != null && !loggedUser.getUserType().isEmpty()) {
            session.setAttribute("is_login", true);
            session.setAttribute("user_type", loggedUser.getUserType());
            session.setAttribute("login_id", loggedUser.getId());
        } else {
            session.setAttribute("is_login", false);
            session.setAttribute("user_type", "");
            session.setAttribute("login_id", "");
        }

        // 디버깅 로그 출력
        System.out.println("로그인 결과 테스트(로그인 사용자 정보):");
        System.out.println("→ is_login: " + session.getAttribute("is_login"));
        System.out.println("→ ID: " + loggedUser.getId());
        System.out.println("→ user_type: " + session.getAttribute("user_type"));
        return "redirect:/";
    }

    // 👥 회원 목록 페이지 (관리자 전용)
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
            return "user_list"; // templates/user_list.html
        }

        return "redirect:/";
    }

    // 📌 게시판 뷰 이동 (board.html)
    @RequestMapping("/board")
    public String board() {
        return "board";
    }

    // 🛒 쇼핑몰 페이지 이동 (shop.html)
    @RequestMapping("/shop")
    public String shop() {
        return "shop";
    }

    // 🎮 게임 페이지 이동 (game.html)
    @RequestMapping("/game")
    public String game() {
        return "game";
    }

    // 🔓 로그아웃 처리 (세션 초기화)
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 🙋‍♂️ 마이페이지 진입 (mypage.html)
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

    // ✏️ 마이페이지 수정 화면 (mypage_edit.html)
    @RequestMapping("/mypage_edit")
    public String mypageEdit(HttpSession session, Model model) {
        String loginId = (String) session.getAttribute("login_id");
        DB db = new DB();
        User user = db.selectOne(loginId);
        model.addAttribute("user", user);
        return "mypage_edit";
    }

    // 💾 마이페이지 수정 처리 (DB update)
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

    // ✅ 현재 로그인 상태 및 사용자 유형(admin/guest) 확인용 API
    // - JS에서 fetch()로 호출해 로그인 여부와 user_type 전달
    // - user_type: admin일 경우 관리자 메뉴 노출
    @RequestMapping("/sessionCheck")
    public void sessionCheck(HttpSession session, HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        try {
            boolean isLogin = Boolean.TRUE.equals(session.getAttribute("is_login"));
            String loginId = (String) session.getAttribute("login_id");
            String userType = (String) session.getAttribute("user_type"); // 추가!

            String json = String.format(
                    "{\"is_login\": %b, \"login_id\": \"%s\", \"user_type\": \"%s\"}",
                    isLogin, loginId, userType);

            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
