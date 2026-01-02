package util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.RowFilter;

public class SimpleFilter implements DocumentListener {

    private JTextField txt;
    private TableRowSorter<DefaultTableModel> sorter;

    public SimpleFilter(JTextField txt, TableRowSorter<DefaultTableModel> sorter) {
        this.txt = txt;
        this.sorter = sorter;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        filter();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        filter();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        filter();
    }

    private void filter() {
        String key = txt.getText();
        if (key.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + key));
        }
    }
}
