package ui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class ProductForm extends JFrame {

    private ProductDAO dao = new ProductDAO();

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtId, txtName, txtPrice, txtQty, txtSearch;

    private JButton btnAdd, btnUpdate, btnDelete, btnMain;

    public ProductForm() {
        setTitle("Quản lý sản phẩm");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        initUI();
        loadData();
        addEvent();
    }

    // ================= UI =================
    private void initUI() {

        // ===== FORM INPUT =====
        JPanel pForm = new JPanel(new GridLayout(2, 4, 5, 5));

        txtId = new JTextField();
        txtId.setEnabled(false); // ID auto

        txtName = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();

        pForm.add(new JLabel("ID"));
        pForm.add(new JLabel("Tên"));
        pForm.add(new JLabel("Giá"));
        pForm.add(new JLabel("Số lượng"));

        pForm.add(txtId);
        pForm.add(txtName);
        pForm.add(txtPrice);
        pForm.add(txtQty);

        add(pForm, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"ID", "Tên", "Giá", "SL"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTON + SEARCH =====
        JPanel pBottom = new JPanel(new BorderLayout());

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnMain = new JButton("Main");

        pBtn.add(btnAdd);
        pBtn.add(btnUpdate);
        pBtn.add(btnDelete);
        pBtn.add(btnMain);

        pBottom.add(pBtn, BorderLayout.WEST);

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtSearch = new JTextField(15);
        pSearch.add(new JLabel("Search"));
        pSearch.add(txtSearch);

        pBottom.add(pSearch, BorderLayout.EAST);

        add(pBottom, BorderLayout.SOUTH);
    }

    // ================= LOAD DATA =================
    private void loadData() {
        model.setRowCount(0);
        List<Product> list = dao.getAll();
        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getQuantity()
            });
        }
    }

    // ================= EVENT =================
    private void addEvent() {

        // CLICK TABLE → ĐỔ FORM
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.convertRowIndexToModel(table.getSelectedRow());
                txtId.setText(model.getValueAt(r, 0).toString());
                txtName.setText(model.getValueAt(r, 1).toString());
                txtPrice.setText(model.getValueAt(r, 2).toString());
                txtQty.setText(model.getValueAt(r, 3).toString());
            }
        });

        // ADD
        btnAdd.addActionListener(e -> {
            try {
                Product p = new Product(
                        0,
                        txtName.getText(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtQty.getText())
                );
                dao.insert(p);
                loadData();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            }
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;

            try {
                Product p = new Product(
                        Integer.parseInt(txtId.getText()),
                        txtName.getText(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtQty.getText())
                );
                dao.update(p);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi update");
            }
        });

        // DELETE
        btnDelete.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            int c = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm?");
            if (c == JOptionPane.YES_OPTION) {
                dao.delete(new Product(Integer.parseInt(txtId.getText()), null, 0, 0));
                loadData();
                clearForm();
            }
        });

        // SEARCH
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String key = txtSearch.getText();
                sorter.setRowFilter(
                        key.isEmpty() ? null : RowFilter.regexFilter("(?i)" + key)
                );
            }
        });

        // MAIN
        btnMain.addActionListener(e -> {
            new MainForm().setVisible(true);
            dispose();
        });
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtQty.setText("");
    }
}
