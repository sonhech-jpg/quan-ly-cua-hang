package ui;

import dao.EmployeeDAO;
import model.Employee;
import util.HashUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeForm extends JFrame {

    JTextField txtId, txtUser, txtName;
    JPasswordField txtPass;
    JTable table;
    DefaultTableModel model;
    EmployeeDAO dao = new EmployeeDAO();

    public EmployeeForm() {
        setTitle("Quản lý nhân viên");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        txtId = new JTextField();
        txtId.setEnabled(false);
        txtUser = new JTextField();
        txtName = new JTextField();
        txtPass = new JPasswordField();

        form.add(new JLabel("ID"));
        form.add(txtId);
        form.add(new JLabel("Username"));
        form.add(txtUser);
        form.add(new JLabel("Họ tên"));
        form.add(txtName);
        form.add(new JLabel("Mật khẩu"));
        form.add(txtPass);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Mới");

        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        model = new DefaultTableModel(new String[]{"ID", "Username", "Họ tên"}, 0);
        table = new JTable(model);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());
    }

    private void loadData() {
        model.setRowCount(0);
        List<Employee> list = dao.getAll();
        for (Employee e : list) {
            model.addRow(new Object[]{
                    e.getId(),
                    e.getUsername(),
                    e.getFullname()
            });
        }
    }

    private void addEmployee() {
        if (txtUser.getText().isEmpty() || txtPass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Thiếu thông tin");
            return;
        }
        Employee e = new Employee();
        e.setUsername(txtUser.getText());
        e.setFullname(txtName.getText());
        e.setPassword(HashUtil.sha256(new String(txtPass.getPassword())));
        dao.insert(e);
        loadData();
        clearForm();
    }

    private void deleteEmployee() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        if (JOptionPane.showConfirmDialog(this, "Xóa nhân viên?") == 0) {
            dao.delete(id);
            loadData();
            clearForm();
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtId.setText(model.getValueAt(row, 0).toString());
            txtUser.setText(model.getValueAt(row, 1).toString());
            txtUser.setEnabled(false);
            txtName.setText(model.getValueAt(row, 2).toString());
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtUser.setText("");
        txtUser.setEnabled(true);
        txtName.setText("");
        txtPass.setText("");
        table.clearSelection();
    }
}