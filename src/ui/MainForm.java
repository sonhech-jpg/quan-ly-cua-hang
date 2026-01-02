package ui;

import javax.swing.*;
import util.Session;

public class MainForm extends JFrame {

    public MainForm() {

        if (Session.currentEmployee == null) {
            JOptionPane.showMessageDialog(this, "Chưa đăng nhập!");
            System.exit(0);
        }

        setTitle("Quản lý cửa hàng - Xin chào " 
                + Session.currentEmployee.getFullname());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar mb = new JMenuBar();
        JMenu mQL = new JMenu("Quản lý");

        JMenuItem mProduct = new JMenuItem("Sản phẩm");
        JMenuItem mCustomer = new JMenuItem("Khách hàng");
        JMenuItem mEmployee = new JMenuItem("Nhân viên");
        JMenuItem mInvoice = new JMenuItem("Tạo hóa đơn");

        // ===== ACTION =====
        mProduct.addActionListener(e -> new ProductForm().setVisible(true));
        mCustomer.addActionListener(e -> new CustomerForm().setVisible(true));
        mEmployee.addActionListener(e -> new EmployeeForm().setVisible(true));
        mInvoice.addActionListener(e ->
                new InvoiceForm(Session.currentEmployee.getId()).setVisible(true)
        );

        // ===== ADD MENU =====
        mQL.add(mProduct);
        mQL.add(mCustomer);
        mQL.add(mEmployee);
        mQL.add(mInvoice);

        mb.add(mQL);
        setJMenuBar(mb);
    }
}
