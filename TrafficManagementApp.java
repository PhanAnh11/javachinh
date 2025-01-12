package cuoikijv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TrafficManagementApp {
    public TrafficManagementApp() {
        // Tạo JFrame
        JFrame frame = new JFrame("Quản lý giao thông");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

        // Tạo panel bên trái (chứa các nút)
        JPanel panelTrai = new JPanel();
        panelTrai.setPreferredSize(new Dimension(200, 400)); // Tăng chiều rộng của panel bên trái
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo CardLayout và panel trung tâm
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // Tạo nút "Quay trở về"
        JButton backButton = new JButton("Quay trở về");
        backButton.setVisible(false); // Ẩn nút "Quay trở về" khi khởi tạo
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiển thị lại panel bên trái
                panelTrai.setVisible(true);
                // Ẩn nút "Quay trở về"
                backButton.setVisible(false);
                // Quay trở lại màn hình chính
                cardLayout.show(cardPanel, "Main");
            }
        });

        // Tạo các panel cho từng mục
        JPanel panel1 = createCardPanel("ĐÈN GIAO THÔNG");
        JPanel panel2 = createViolationPanel();
        JPanel panel3 = createCardPanel("Đường Bộ");
        JPanel panel4 = createVehicleManagementPanel(); // Thay thế bằng panel quản lý phương tiện

        // Thêm các panel vào CardLayout
        cardPanel.add(new JPanel(), "Main"); // Panel trống ban đầu
        cardPanel.add(panel1, "Panel 1");
        cardPanel.add(panel2, "Panel 2");
        cardPanel.add(panel3, "Panel 3");
        cardPanel.add(panel4, "Panel 4");

        // Tạo các nút điều khiển
        JButton button1 = createMenuButton("Đèn giao thông", cardPanel, cardLayout, "Panel 1", panelTrai, backButton);
        JButton button2 = createMenuButton("Lỗi vi phạm", cardPanel, cardLayout, "Panel 2", panelTrai, backButton);
        JButton button3 = createMenuButton("Đường bộ", cardPanel, cardLayout, "Panel 3", panelTrai, backButton);
        JButton button4 = createMenuButton("Quản lý phương tiện", cardPanel, cardLayout, "Panel 4", panelTrai, backButton);

        // Thêm các nút vào panel bên trái
        panelTrai.add(button1);
        panelTrai.add(Box.createVerticalStrut(5)); // Khoảng cách giữa các nút
        panelTrai.add(button2);
        panelTrai.add(Box.createVerticalStrut(5));
        panelTrai.add(button3);
        panelTrai.add(Box.createVerticalStrut(5));
        panelTrai.add(button4);

        // Thêm panel và nút vào frame
        frame.add(panelTrai, BorderLayout.WEST);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(backButton, BorderLayout.SOUTH);

        // Hiển thị frame
        frame.setVisible(true);
    }

    // Phương thức tạo panel nội dung
    private JPanel createCardPanel(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Label hiển thị tiêu đề
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }
///////////////////////////////////////////////////////////////////////////////////////
    private JPanel createViolationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tiêu đề
        JLabel label = new JLabel("Quản lý lỗi vi phạm", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        // Bảng để hiển thị dữ liệu
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID Vi Phạm", "Biển Kiểm Soát", "Ngày Vi Phạm", "Lỗi Vi Phạm", "Xử Lý"});
        table.setModel(tableModel);

        // Cuộn cho bảng
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Tải dữ liệu từ bảng vipham
        loadViolationData(tableModel);

        return panel;
    }

    // Phương thức tải dữ liệu từ bảng "vipham"
    private void loadViolationData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idvipham, bienkiemsoat, ngayvipham, loivipham, xuly FROM vipham")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("idvipham"),
                        rs.getString("bienkiemsoat"),
                        rs.getString("ngayvipham"),
                        rs.getString("loivipham"),
                        rs.getString("xuly")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createVehicleManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Label tiêu đề
        JLabel label = new JLabel("Quản lý phương tiện", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        // Bảng để hiển thị dữ liệu
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Biển kiểm soát", "Loại xe", "Màu sắc", "Chủ xe", "Phân khối xe"});
        table.setModel(tableModel);

        // Cuộn cho bảng
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Kết nối cơ sở dữ liệu và tải dữ liệu vào bảng
        loadVehicleData(tableModel);

        // Xử lý nút "Thêm"
        addButton.addActionListener(e -> {
            JTextField txtBienSo = new JTextField();
            JTextField txtLoaiXe = new JTextField();
            JTextField txtMauSac = new JTextField();
            JTextField txtChuXe = new JTextField();
            JTextField txtPhanKhoi = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(5, 2));
            inputPanel.add(new JLabel("Biển kiểm soát:"));
            inputPanel.add(txtBienSo);
            inputPanel.add(new JLabel("Loại xe:"));
            inputPanel.add(txtLoaiXe);
            inputPanel.add(new JLabel("Màu sắc:"));
            inputPanel.add(txtMauSac);
            inputPanel.add(new JLabel("Chủ xe:"));
            inputPanel.add(txtChuXe);
            inputPanel.add(new JLabel("Phân khối xe:"));
            inputPanel.add(txtPhanKhoi);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Thêm phương tiện", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "INSERT INTO qlyphuongtien (bienkiemsoat, loaixe, mausacxe, chuxe, phankhoixe) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtBienSo.getText());
                    ps.setString(2, txtLoaiXe.getText());
                    ps.setString(3, txtMauSac.getText());
                    ps.setString(4, txtChuXe.getText());
                    ps.setInt(5, Integer.parseInt(txtPhanKhoi.getText()));
                    ps.executeUpdate();
                    loadVehicleData(tableModel); // Làm mới bảng sau khi thêm
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Thêm phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xử lý nút "Sửa"
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bienSo = (String) tableModel.getValueAt(selectedRow, 0);
            JTextField txtLoaiXe = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField txtMauSac = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField txtChuXe = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
            JTextField txtPhanKhoi = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("Loại xe:"));
            inputPanel.add(txtLoaiXe);
            inputPanel.add(new JLabel("Màu sắc:"));
            inputPanel.add(txtMauSac);
            inputPanel.add(new JLabel("Chủ xe:"));
            inputPanel.add(txtChuXe);
            inputPanel.add(new JLabel("Phân khối xe:"));
            inputPanel.add(txtPhanKhoi);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Sửa thông tin phương tiện", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "UPDATE qlyphuongtien SET loaixe = ?, mausacxe = ?, chuxe = ?, phankhoixe = ? WHERE bienkiemsoat = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtLoaiXe.getText());
                    ps.setString(2, txtMauSac.getText());
                    ps.setString(3, txtChuXe.getText());
                    ps.setInt(4, Integer.parseInt(txtPhanKhoi.getText()));
                    ps.setString(5, bienSo);
                    ps.executeUpdate();
                    loadVehicleData(tableModel); // Làm mới bảng sau khi sửa
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sửa phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xử lý nút "Xóa"
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bienSo = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phương tiện này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "DELETE FROM qlyphuongtien WHERE bienkiemsoat = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, bienSo);
                    ps.executeUpdate();
                    loadVehicleData(tableModel); // Làm mới bảng sau khi xóa
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Xóa phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    // Phương thức tải lại dữ liệu từ cơ sở dữ liệu
    private void loadVehicleData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bienkiemsoat, loaixe, mausacxe, chuxe, phankhoixe FROM qlyphuongtien")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("bienkiemsoat"),
                        rs.getString("loaixe"),
                        rs.getString("mausacxe"),
                        rs.getString("chuxe"),
                        rs.getInt("phankhoixe")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    // Phương thức tạo nút menu
    private JButton createMenuButton(String text, JPanel cardPanel, CardLayout cardLayout, String cardName, JPanel panelTrai, JButton backButton) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Đặt kích thước cho nút
        Dimension buttonSize = new Dimension(200, 30); // Kích thước lớn hơn
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ẩn panel bên trái khi nút được nhấn
                panelTrai.setVisible(false);
                // Hiển thị nút "Quay trở về"
                backButton.setVisible(true);
                // Hiển thị panel tương ứng
                cardLayout.show(cardPanel, cardName);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TrafficManagementApp::new);
    }
}