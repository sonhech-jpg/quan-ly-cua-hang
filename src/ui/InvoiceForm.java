package ui;

import dao.ProductDAO;
import model.Product;
import util.SimpleFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import util.Session;

public class InvoiceForm extends JFrame {

    private JTable tblProduct, tblInvoice;
    private DefaultTableModel mProduct, mInvoice;
    private JTextField txtSearch, txtTotal;
    private TableRowSorter<DefaultTableModel> sorter;
    private int employeeId;

    private ProductDAO productDAO = new ProductDAO();

    public InvoiceForm() {
        setTitle("Tạo hóa đơn");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ===== TOP SEARCH =====
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        top.add(new JLabel("Tìm sản phẩm:"));
        top.add(txtSearch);
        add(top, BorderLayout.NORTH);

        // ===== PRODUCT TABLE =====
        mProduct = new DefaultTableModel(
                new String[]{"ID", "Tên", "Giá", "Tồn"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblProduct = new JTable(mProduct);
        sorter = new TableRowSorter<>(mProduct);
        tblProduct.setRowSorter(sorter);

        // ===== INVOICE TABLE =====
        mInvoice = new DefaultTableModel(
                new String[]{"ID", "Tên", "SL", "Giá", "Thành tiền"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblInvoice = new JTable(mInvoice);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(tblProduct),
                new JScrollPane(tblInvoice)
        );
        split.setDividerLocation(500);
        add(split, BorderLayout.CENTER);

        // ===== BOTTOM =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTotal = new JTextField("0", 12);
        txtTotal.setEnabled(false);
        JButton btnPrint = new JButton("IN / THANH TOÁN");

        bottom.add(new JLabel("Tổng tiền:"));
        bottom.add(txtTotal);
        bottom.add(btnPrint);
        add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        txtSearch.getDocument().addDocumentListener(new SimpleFilter(txtSearch, sorter));
        tblProduct.addMouseListener(new ProductClickHandler());
        btnPrint.addActionListener(e -> openPrintForm());
    }

    private void loadProducts() {
        mProduct.setRowCount(0);
        List<Product> list = productDAO.getAll();
        for (Product p : list) {
            mProduct.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity()
            });
        }
    }

    private void addToInvoice(Product p, int qty) {
        for (int i = 0; i < mInvoice.getRowCount(); i++) {
            if ((int) mInvoice.getValueAt(i, 0) == p.getId()) {
                int sl = (int) mInvoice.getValueAt(i, 2) + qty;
                mInvoice.setValueAt(sl, i, 2);
                mInvoice.setValueAt(sl * p.getPrice(), i, 4);
                updateTotal();
                return;
            }
        }

        mInvoice.addRow(new Object[]{
                p.getId(), p.getName(), qty, p.getPrice(), qty * p.getPrice()
        });
        updateTotal();
    }

    private void updateTotal() {
        double sum = 0;
        for (int i = 0; i < mInvoice.getRowCount(); i++) {
            sum += (double) mInvoice.getValueAt(i, 4);
        }
        txtTotal.setText(String.valueOf(sum));
    }

    private void openPrintForm() {
        if (mInvoice.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm");
            return;
        }
        new PrintInvoiceForm(this,mInvoice,Double.parseDouble(txtTotal.getText()),Session.currentEmployee.getId(),1).setVisible(true);

setVisible(false);
    }
    public void clearInvoice() {
    mInvoice.setRowCount(0);
    txtTotal.setText("0");
    setVisible(true);
}
    
    

    // ===== INNER CLASSES =====
    private class ProductClickHandler extends java.awt.event.MouseAdapter {
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getClickCount() == 2) {
                int r = tblProduct.convertRowIndexToModel(tblProduct.getSelectedRow());
                int id = (int) mProduct.getValueAt(r, 0);
                String name = mProduct.getValueAt(r, 1).toString();
                double price = (double) mProduct.getValueAt(r, 2);
                int ton = (int) mProduct.getValueAt(r, 3);

                String s = JOptionPane.showInputDialog("Nhập số lượng:");
                if (s == null) return;

                int qty = Integer.parseInt(s);
                if (qty <= 0 || qty > ton) {
                    JOptionPane.showMessageDialog(null, "Số lượng không hợp lệ");
                    return;
                }

                addToInvoice(new Product(id, name, price, ton), qty);
            }
        }
    }
    public InvoiceForm(int employeeId) {
        this.employeeId = employeeId;
        setTitle("Tạo hóa đơn");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadProducts();
    }

}
