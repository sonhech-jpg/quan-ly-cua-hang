package ui;

import javax.swing.*;
import dao.EmployeeDAO;
import model.Employee;
import util.Session;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginForm() {
        setTitle("Login");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        txtUser = new JTextField(15);
        txtPass = new JPasswordField(15);
        JButton btn = new JButton("Login");

        p.add(new JLabel("User:"));
        p.add(txtUser);
        p.add(new JLabel("Pass:"));
        p.add(txtPass);
        p.add(btn);

        add(p);

        btn.addActionListener(e -> login());
    }

    private void login() {
        String username = txtUser.getText().trim();
        String password = String.valueOf(txtPass.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đầy đủ thông tin");
            return;
        }

        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.authenticate(username, password);

        if (emp != null) {
            Session.currentEmployee = emp;
            new MainForm().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
        }
    }
}
