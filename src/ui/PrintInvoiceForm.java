package ui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.MessageFormat;

public class PrintInvoiceForm extends JFrame {

    private JTable table;
    private JLabel lblTongTien;
    private JButton btnThanhToan, btnTiepTuc, btnPrint;

    private boolean daThanhToan = false;

    private InvoiceForm parent;
    private int employeeId;
    private int customerId;

    public PrintInvoiceForm(
            InvoiceForm parent,
            DefaultTableModel mInvoice,
            double tongTien,
            int employeeId,
            int customerId
    ) {
        this.parent = parent;
        this.employeeId = employeeId;
        this.customerId = customerId;

        setTitle("HÓA ĐƠN THANH TOÁN");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== TABLE =====
        table = new JTable(copyModel(mInvoice));
        table.setEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel bottom = new JPanel(new BorderLayout());

        lblTongTien = new JLabel("Tổng tiền: " + tongTien + " VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        bottom.add(lblTongTien, BorderLayout.WEST);

        btnThanhToan = new JButton("Thanh toán");
        btnTiepTuc = new JButton("Tiếp tục");
        btnPrint = new JButton("In hóa đơn");

        btnTiepTuc.setEnabled(false);

        JPanel pBtn = new JPanel();
        pBtn.add(btnThanhToan);
        pBtn.add(btnTiepTuc);
        pBtn.add(btnPrint);

        bottom.add(pBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnThanhToan.addActionListener(e -> thanhToan());
        btnPrint.addActionListener(e -> inHoaDon());

        btnTiepTuc.addActionListener(e -> {
            if (!daThanhToan) {
                JOptionPane.showMessageDialog(this, "Chưa thanh toán!");
                return;
            }
            parent.clearInvoice();
            parent.setVisible(true);
            dispose();
        });
    }

    // ===== COPY MODEL (ĐÚNG CỘT) =====
    private DefaultTableModel copyModel(DefaultTableModel m) {
        DefaultTableModel copy = new DefaultTableModel(
                new String[]{"ID", "Tên", "SL", "Giá"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (int i = 0; i < m.getRowCount(); i++) {
            copy.addRow(new Object[]{
                    m.getValueAt(i, 0), // ID
                    m.getValueAt(i, 1), // Tên
                    m.getValueAt(i, 2), // SL
                    m.getValueAt(i, 3)  // Giá
            });
        }
        return copy;
    }

    // ===== THANH TOÁN =====
    private void thanhToan() {

        if (daThanhToan) return;

        DefaultTableModel m = (DefaultTableModel) table.getModel();
        if (m.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hóa đơn trống!");
            return;
        }

        InvoiceDAO invoiceDAO = new InvoiceDAO();
        InvoiceDetailDAO detailDAO = new InvoiceDetailDAO();
        ProductDAO productDAO = new ProductDAO();

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);

            // 1️⃣ Tạo hóa đơn
            Invoice inv = new Invoice();
            inv.setEmployeeId(employeeId);
            inv.setCustomerId(customerId);
            inv.setDate(new Timestamp(System.currentTimeMillis()));
            inv.setTotal(tinhTongTien());

            int invoiceId = invoiceDAO.createInvoice(inv, conn);
            if (invoiceId <= 0)
                throw new Exception("Không tạo được hóa đơn");

            // 2️⃣ Chi tiết + trừ kho
            for (int i = 0; i < m.getRowCount(); i++) {
                int pid = (int) m.getValueAt(i, 0);
                int sl = Integer.parseInt(m.getValueAt(i, 2).toString());
                double price = Double.parseDouble(m.getValueAt(i, 3).toString());

                if (!productDAO.truKho(conn, pid, sl))
                    throw new Exception("Không đủ hàng");

                InvoiceDetail d = new InvoiceDetail();
                d.setInvoiceId(invoiceId);
                d.setProductId(pid);
                d.setQuantity(sl);
                d.setPrice(price);

                detailDAO.addDetail(d, conn);
            }

            conn.commit();

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            daThanhToan = true;
            btnThanhToan.setEnabled(false);
            btnTiepTuc.setEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại!");
        }
    }

    private double tinhTongTien() {
        double tong = 0;
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            int sl = Integer.parseInt(m.getValueAt(i, 2).toString());
            double price = Double.parseDouble(m.getValueAt(i, 3).toString());
            tong += sl * price;
        }
        return tong;
    }

    // ===== IN =====
    private void inHoaDon() {
        try {
            table.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat("HÓA ĐƠN BÁN HÀNG"),
                    new MessageFormat(lblTongTien.getText())
            );
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn");
        }
    }
}