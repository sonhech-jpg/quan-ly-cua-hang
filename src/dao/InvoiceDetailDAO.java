package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import model.InvoiceDetail;

public class InvoiceDetailDAO {

    // ❌ KHÔNG DÙNG KHI TRANSACTION
    public boolean addDetail(InvoiceDetail d) {
        String sql = "INSERT INTO invoice_detail(invoice_id, product_id, quantity, price)VALUES(?,?,?,?)";
        

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, d.getInvoiceId());
            ps.setInt(2, d.getProductId());
            ps.setInt(3, d.getQuantity());
            ps.setDouble(4, d.getPrice());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ METHOD ĐƯỢC GỌI TRONG THANHTOAN()
    public void addDetail(InvoiceDetail d, Connection conn) throws Exception {

        String sql = "INSERT INTO invoice_detail(invoice_id, product_id, quantity, price)VALUES(?,?,?,?)";
     

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, d.getInvoiceId());
            ps.setInt(2, d.getProductId());
            ps.setInt(3, d.getQuantity());
            ps.setDouble(4, d.getPrice());

            ps.executeUpdate();
        }
    }
}