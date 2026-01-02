package ui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerForm extends JFrame {

    JTextField txtId, txtName, txtPhone;
    JTable table;
    DefaultTableModel model;
    CustomerDAO dao = new CustomerDAO();

    public CustomerForm() {
        setTitle("Quản lý khách hàng");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ===== FORM =====
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        txtId = new JTextField();
        txtId.setEnabled(false);
        txtName = new JTextField();
        txtPhone = new JTextField();

        form.add(new JLabel("ID"));
        form.add(txtId);
        form.add(new JLabel("Tên"));
        form.add(txtName);
        form.add(new JLabel("SĐT"));
        form.add(txtPhone);

        // ===== BUTTON =====
        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Mới");

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"ID", "Tên", "SĐT"}, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        add(form, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // ===== EVENTS =====
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadData() {
        model.setRowCount(0);
        List<Customer> list = dao.getAll();
        for (Customer c : list) {
            model.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone()
            });
        }
    }

    private void addCustomer() {
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được trống");
            return;
        }
        Customer c = new Customer();
        c.setName(txtName.getText());
        c.setPhone(txtPhone.getText());
        dao.insert(c);
        loadData();
        clearForm();
    }

    private void updateCustomer() {
        if (txtId.getText().isEmpty()) return;
        Customer c = new Customer();
        c.setId(Integer.parseInt(txtId.getText()));
        c.setName(txtName.getText());
        c.setPhone(txtPhone.getText());
        dao.update(c);
        loadData();
    }

    private void deleteCustomer() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        if (JOptionPane.showConfirmDialog(this, "Xóa khách hàng?") == 0) {
            dao.delete(id);
            loadData();
            clearForm();
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(model.getValueAt(row, 0).toString());
            txtName.setText(model.getValueAt(row, 1).toString());
            txtPhone.setText(model.getValueAt(row, 2).toString());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }
}