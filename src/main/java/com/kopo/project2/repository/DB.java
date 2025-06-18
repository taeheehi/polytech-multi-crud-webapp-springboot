package com.kopo.project2.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kopo.project2.model.User;

import org.sqlite.SQLiteConfig;

public class DB {
    private Connection connection; // 실제로 DB에 연결할 객체 선언

    // 프로그램 실행 시 가장 먼저 실행되는 static 블록
    static {
        try {
            // SQLite 드라이버 로딩 (JDBC를 사용하기 위해 꼭 필요함)
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            // 드라이버 로딩 실패 시 오류 메시지 출력
            e.printStackTrace();
        }
    }

    // DB 연결을 여는 메서드 (DB 파일에 접속)
    private void open() {
        try {
            // SQLite 데이터베이스 파일의 경로 지정
            String dbFileName = "c:/kopo/project1.sqlite";
            // SQLite 연결 설정 객체 생성
            SQLiteConfig config = new SQLiteConfig();
            // 위 경로로 데이터베이스 연결 열기
            this.connection = DriverManager.getConnection("jdbc:sqlite:/" + dbFileName, config.toProperties());
        } catch (SQLException e) {
            // 연결 실패 시 오류 메시지 출력
            e.printStackTrace();
        }
    }

    // DB 연결을 닫는 메서드 (사용이 끝난 뒤 꼭 닫아야 함)
    private void close() {
        try {
            this.connection.close(); // 연결 종료
        } catch (SQLException e) {
            e.printStackTrace(); // 오류 발생 시 메시지 출력
        }
    }

    // 🔧 connect(), disconnect() 메서드 추가 (호환용)
    private void connect() {
        this.open();
    }

    private void disconnect() {
        this.close();
    }

    public void createTable() { // 학생 정보를 저장할 테이블을 생성하는 함수
        this.open(); // DB 연결 열기

        // 테이블 생성 쿼리문: 학생 이름, 중간, 기말 점수를 저장
        String query = "CREATE TABLE user (idx INTEGER PRIMARY KEY AUTOINCREMENT, user_type TEXT, id TEXT, pwd TEXT, name TEXT, phone TEXT, address TEXT, created TEXT, last_updated TEXT);";
        try {
            Statement statement = this.connection.createStatement(); // Statement 객체 생성
            statement.executeQuery(query); // 쿼리 실행 (테이블 생성)
            statement.close(); // 리소스 반환
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 메시지 출력
        }
        this.close(); // DB 연결 닫기
    }

    // SHA-512 해시값을 생성하는 메서드 // 검색어 # java sha512생성하는 코드
    public static String hashSHA512(String input) {
        try {
            // MessageDigest 인스턴스를 SHA-512 알고리즘으로 초기화
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // 입력 문자열을 바이트 배열로 변환하고 해시 처리
            byte[] hashedBytes = md.digest(input.getBytes());
            // 해시된 바이트 배열을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                // 각 바이트를 2자리 16진수 문자열로 포맷하여 이어 붙임
                sb.append(String.format("%02x", b));
            }
            // 최종적으로 완성된 해시 문자열 반환
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-512 알고리즘이 지원되지 않을 경우 예외 발생
            throw new RuntimeException("SHA-512 알고리즘이 존재하지 않습니다.", e);
        }
    }

    public void insertData(User user) { // Student 객체를 받아 DB에 추가하는 함수
        this.open(); // DB 연결 열기

        // 값 바인딩 방식의 삽입 쿼리문 (보안에 안전)
        String query = "INSERT INTO user (user_type, id, pwd, name, phone, address, created, last_updated) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query); // 쿼리 준비
            statement.setString(1, "guest");
            statement.setString(2, user.id);
            user.pwd = hashSHA512(user.pwd); // PWD 해시로 암호화
            statement.setString(3, user.pwd);
            statement.setString(4, user.name);
            statement.setString(5, user.phone);
            statement.setString(6, user.address);
            String now = (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new java.util.Date());
            statement.setString(7, now);
            statement.setString(8, now);
            statement.execute(); // 쿼리 실행 (INSERT)
            statement.close(); // 리소스 정리
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 메시지 출력
        }
        this.close(); // DB 연결 닫기
    }

    public ArrayList<User> selectAll() {
        this.open(); // DB 연결 열기
        ArrayList<User> data = new ArrayList<>(); // 결과를 저장할 리스트

        try {
            String query = "SELECT idx, user_type, id, name, phone, address, created, last_updated FROM user";
            PreparedStatement statement = this.connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                // 각 컬럼값 읽기
                int idx = result.getInt("idx");
                String userType = result.getString("user_type");
                String id = result.getString("id");
                String name = result.getString("name");
                String phone = result.getString("phone");
                String address = result.getString("address");
                String created = result.getString("created");
                String lastUpdated = result.getString("last_updated");

                // ✅ User 객체를 만들고 setter로 값 대입
                User user = new User(); // 기본 생성자 사용
                user.setIdx(idx);
                user.setUserType(userType);
                user.setId(id);
                // ❌ user.setPwd(...) 안 함 (조회 시 비밀번호 제외!)
                user.setName(name);
                user.setPhone(phone);
                user.setAddress(address);
                user.setCreated(created);
                user.setLastUpdated(lastUpdated);

                // 리스트에 추가
                data.add(user);
            }

            statement.close(); // 쿼리 종료
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 출력
        }

        this.close(); // DB 연결 닫기
        return data; // 결과 리스트 반환
    }

    public User login(User user) { // 모든 학생 정보를 가져오는 함수
        this.open(); // DB 연결 열기
        User returnData = new User();
        try {
            String query = "SELECT * FROM user WHERE id=? AND pwd=?"; // 모든 학생 정보 가져오는 SQL
            PreparedStatement statement = this.connection.prepareStatement(query); // 쿼리 준비
            statement.setString(1, user.id);
            user.pwd = hashSHA512(user.pwd);
            statement.setString(2, user.pwd);
            ResultSet result = statement.executeQuery(); // 쿼리 실행 및 결과 받기
            // 결과를 한 줄씩 읽어서 Student 객체로 변환
            while (result.next()) {
                int idx = result.getInt("idx");
                String userType = result.getString("user_type");
                String id = result.getString("id");
                String pwd = result.getString("pwd");
                String name = result.getString("name"); // 이름
                String phone = result.getString("phone");
                String address = result.getString("address");
                String created = result.getString("created");
                String lastUpdated = result.getString("last_updated");

                // 실제 로그인 유저 정보 반환 : 읽은 데이터를 Student 객체로 만들어 리스트에 저장
                // returnData = new User(idx, userType, id, pwd, name, phone, address, created,
                // lastUpdated);

                // 수정된 코드 (setter 방식으로 정확히 대입)
                returnData = new User();
                returnData.idx = idx;
                returnData.userType = userType;
                returnData.id = id;
                returnData.pwd = pwd;
                returnData.name = name;
                returnData.phone = phone;
                returnData.address = address;
                returnData.created = created;
                returnData.lastUpdated = lastUpdated;
            }
            statement.close(); // 쿼리 종료
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 출력
        }
        this.close(); // DB 연결 닫기
        return returnData; // 결과 리스트 반환
    }

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

    // public void updateUser(User user) {
    // try {
    // connect();
    // String query = "UPDATE user SET name=?, phone=?, address=? WHERE id=?";
    // PreparedStatement ps = connection.prepareStatement(query);
    // ps.setString(1, user.name);
    // ps.setString(2, user.phone);
    // ps.setString(3, user.address);
    // ps.setString(4, user.id);
    // ps.executeUpdate();
    // disconnect();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    public void updateUser(User user) {
        PreparedStatement ps = null; // 🔧 예외 발생 시에도 close할 수 있도록 바깥에 선언
        try {
            connect();
            String query = "UPDATE user SET name=?, phone=?, address=? WHERE id=?";
            ps = connection.prepareStatement(query);
            ps.setString(1, user.name);
            ps.setString(2, user.phone);
            ps.setString(3, user.address);
            ps.setString(4, user.id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close(); // 🔧 리소스 누수 방지
            } catch (SQLException e) {
                e.printStackTrace();
            }
            disconnect(); // 🔧 finally 블록에서 항상 연결 종료
        }
    }

} // ← 🔧 여기가 진짜 DB 클래스 닫는 위치
