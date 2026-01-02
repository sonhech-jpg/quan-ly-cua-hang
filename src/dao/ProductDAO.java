package dao;

import java.sql.*;
import java.util.*;
import model.Product;

public class ProductDAO {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try {
            Connection conn = DBConnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean insertOrUpdate(Product p) {

    String checkSql =
        "SELECT id, quantity FROM product WHERE name = ?";

    String updateSql =
        "UPDATE product SET quantity = quantity + ?, price = ? WHERE id = ?";

    String insertSql =
        "INSERT INTO product(name, price, quantity) VALUES (?, ?, ?)";

    try (Connection conn = DBConnect.getConnection();
         PreparedStatement psCheck = conn.prepareStatement(checkSql)) {

        psCheck.setString(1, p.getName());
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) {
            // 🔹 ĐÃ TỒN TẠI → CỘNG DỒN
            int id = rs.getInt("id");

            try (PreparedStatement psUpdate =
                     conn.prepareStatement(updateSql)) {

                psUpdate.setInt(1, p.getQuantity());
                psUpdate.setDouble(2, p.getPrice());
                psUpdate.setInt(3, id);
                psUpdate.executeUpdate();
            }
            return false; // update
        } else {
            // 🔹 CHƯA TỒN TẠI → INSERT
            try (PreparedStatement psInsert =
                     conn.prepareStatement(insertSql)) {

                psInsert.setString(1, p.getName());
                psInsert.setDouble(2, p.getPrice());
                psInsert.setInt(3, p.getQuantity());
                psInsert.executeUpdate();
            }
            return true; // insert
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

    public boolean insert(Product p) {
        String sql = "INSERT INTO product(name,price,quantity) VALUES(?,?,?)";
        try {
            Connection conn = DBConnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(Product p) {
        String sql = "UPDATE product SET name= ?, price =?, quantity=? WHERE id=?";
        try{
        Connection conn = DBConnect.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setInt(4, p.getId());
            return ps.executeUpdate() > 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean delete(Product p) {
        String sql = "DELETE FROM product WHERE id = ?";
        try {
            Connection conn = DBConnect.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
         return false;
    }
    public void updatesoluong(int id, int newqty) {
        String sql = "UPDATE product SET quantity = ? WHERE id= ?";
         try (Connection conn = DBConnect.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, newqty);
        ps.setInt(2, id);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    
}
    }
    public boolean truKho(Connection conn, int productId, int soLuong) throws Exception {

    String sql =
        "UPDATE product " +
        "SET quantity = quantity - ? " +
        "WHERE id = ? AND quantity >= ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, soLuong);
        ps.setInt(2, productId);
        ps.setInt(3, soLuong);

        return ps.executeUpdate() == 1;
    }
}
}
