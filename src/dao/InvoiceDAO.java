package dao;

import java.sql.*;
import java.util.*;
import model.Invoice;
import model.InvoiceDetail;

public class InvoiceDAO {
   public int createInvoice(Invoice inv, Connection conn) throws Exception {
    String sql = "INSERT INTO invoice(employee_id, customer_id, date, total) VALUES(?,?,?,?)";
    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    ps.setInt(1, inv.getEmployeeId());
    ps.setInt(2, inv.getCustomerId());
    ps.setTimestamp(3, inv.getDate());
    ps.setDouble(4, inv.getTotal());
    ps.executeUpdate();

    ResultSet rs = ps.getGeneratedKeys();
    if (rs.next()) return rs.getInt(1);
    return -1;
}
}


