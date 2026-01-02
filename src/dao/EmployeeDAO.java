package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Employee;
import util.HashUtil;

public class EmployeeDAO {

    // ===== THÊM NHÂN VIÊN =====
    public boolean insert(Employee e) {
        String sql = "INSERT INTO employee(username,password,fullname) VALUES(?,?,?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getUsername());
            ps.setString(2, HashUtil.sha256(e.getPassword()));
            ps.setString(3, e.getFullname());
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ===== LẤY DANH SÁCH NHÂN VIÊN =====
    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("fullname")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== XÓA NHÂN VIÊN =====
    public boolean delete(int id) {
        String sql = "DELETE FROM employee WHERE id=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== ĐĂNG NHẬP =====
    public Employee authenticate(String username, String password) {
    String sql = "SELECT * FROM employee WHERE username=? AND password=?";
    try (Connection conn = DBConnect.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, password); // 🔥 HASH Ở ĐÂY

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Employee e = new Employee();
            e.setId(rs.getInt("id"));
            e.setUsername(rs.getString("username"));
            e.setFullname(rs.getString("fullname"));
            return e;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}
