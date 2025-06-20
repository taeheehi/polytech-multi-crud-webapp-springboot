package com.kopo.project2.repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kopo.project2.model.User;

public class DB {
    private Connection connection;

    // ✅ MySQL 드라이버 로딩
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL용 드라이버
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ MySQL 연결 (localhost:3307/kopoSpringDB)
    private void open() {
        try {
            String url = "jdbc:mysql://localhost:3307/kopoSpringDB?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "RootPass%1234"; // ← 실제 비밀번호로 수정
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        this.open();
    }

    private void disconnect() {
        this.close();
    }

    // 📌 테이블 생성 (MySQL용 쿼리)
    public void createTable() {
        this.open();
        String query = """
                    CREATE TABLE IF NOT EXISTS user (
                        idx INT AUTO_INCREMENT PRIMARY KEY,
                        user_type VARCHAR(20),
                        id VARCHAR(100),
                        pwd VARCHAR(255),
                        name VARCHAR(50),
                        phone VARCHAR(20),
                        address TEXT,
                        created DATETIME,
                        last_updated DATETIME
                    );
                """;
        try {
            Statement statement = this.connection.createStatement();
            statement.execute(query); // SQLite에서는 executeQuery, MySQL에서는 execute
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
    }

    // 🧾 회원 추가
    public void insertData(User user) {
        this.open();
        String query = "INSERT INTO user (user_type, id, pwd, name, phone, address, created, last_updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, user.getUserType()); // 관리자 회원가입으로 변경
            statement.setString(2, user.getId());
            statement.setString(3, hashSHA512(user.getPwd()));
            statement.setString(4, user.getName());
            statement.setString(5, user.getPhone());
            statement.setString(6, user.getAddress());
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            statement.setString(7, now);
            statement.setString(8, now);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
    }

    // 📋 전체 회원 조회
    public ArrayList<User> selectAll() {
        this.open();
        ArrayList<User> data = new ArrayList<>();
        try {
            String query = "SELECT idx, user_type, id, name, phone, address, created, last_updated FROM user";
            PreparedStatement statement = this.connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                User user = new User();
                user.setIdx(result.getInt("idx"));
                user.setUserType(result.getString("user_type"));
                user.setId(result.getString("id"));
                user.setName(result.getString("name"));
                user.setPhone(result.getString("phone"));
                user.setAddress(result.getString("address"));
                user.setCreated(result.getString("created"));
                user.setLastUpdated(result.getString("last_updated"));
                data.add(user);
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return data;
    }

    // 🔐 로그인 (ID + PWD 확인)
    public User login(User user) {
        this.open();
        User returnData = new User();
        try {
            String query = "SELECT * FROM user WHERE id=? AND pwd=?";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, user.getId());
            statement.setString(2, hashSHA512(user.getPwd()));
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                returnData.setIdx(result.getInt("idx"));
                returnData.setUserType(result.getString("user_type"));
                returnData.setId(result.getString("id"));
                returnData.setPwd(result.getString("pwd"));
                returnData.setName(result.getString("name"));
                returnData.setPhone(result.getString("phone"));
                returnData.setAddress(result.getString("address"));
                returnData.setCreated(result.getString("created"));
                returnData.setLastUpdated(result.getString("last_updated"));
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return returnData;
    }

    // 👤 아이디 중복 체크 추가 (id 기준)
    public boolean isIdDuplicated(String id) {
        boolean isDuplicate = false;
        this.open();
        try {
            String query = "SELECT COUNT(*) FROM user WHERE id = ?";
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                isDuplicate = result.getInt(1) > 0;
            }
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.close();
        return isDuplicate;
    }

    // 🔐 비밀번호 암호화 (SHA-512)
    public static String hashSHA512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 알고리즘 오류", e);
        }
    }

    // 👤 특정 유저 조회 (id 기준)
    public User selectOne(String id) {
        User user = null;
        try {
            connect();
            String query = "SELECT * FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getString("id"),
                        rs.getString("pwd"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"));
            }
            disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // ✏️ 회원 수정 (name, phone, address)
    public boolean updateUser(User user) {
        boolean result = false;
        try {
            connect();
            String query = "UPDATE user SET name=?, phone=?, address=?, last_updated=NOW() WHERE id=?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setString(4, user.getId());

            int rows = ps.executeUpdate();
            result = rows > 0;
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return result;
    }

}
