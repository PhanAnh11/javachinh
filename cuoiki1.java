package cuoikijv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class cuoiki1 {
    public cuoiki1() {
        
        JFrame frame = new JFrame("Quản lý giao thông");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);

      
        JPanel panelTrai = new JPanel();
        panelTrai.setPreferredSize(new Dimension(200, 400)); // Tăng chiều rộng của panel bên trái
        panelTrai.setLayout(new BoxLayout(panelTrai, BoxLayout.Y_AXIS));
        panelTrai.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

    
        JButton backButton = new JButton("Quay trở về");
        backButton.setVisible(false); 
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               
                panelTrai.setVisible(true);
                backButton.setVisible(false);
                cardLayout.show(cardPanel, "Main");
            }
        });

     
        JPanel panel1 = CameraPanel();
        JPanel panel2 = createViolationPanel();
        JPanel panel3 = Street();
        JPanel panel4 = createVehicleManagementPanel();

      
        cardPanel.add(new JPanel(), "Main"); 
        cardPanel.add(panel1, "Panel 1");
        cardPanel.add(panel2, "Panel 2");
        cardPanel.add(panel3, "Panel 3");
        cardPanel.add(panel4, "Panel 4");

        
        JButton button1 = createMenuButton("Camera", cardPanel, cardLayout, "Panel 1", panelTrai, backButton);
        JButton button2 = createMenuButton("Lỗi vi phạm", cardPanel, cardLayout, "Panel 2", panelTrai, backButton);
        JButton button3 = createMenuButton("Đường bộ", cardPanel, cardLayout, "Panel 3", panelTrai, backButton);
        JButton button4 = createMenuButton("Quản lý phương tiện", cardPanel, cardLayout, "Panel 4", panelTrai, backButton);

      
        panelTrai.add(button1);
        panelTrai.add(Box.createVerticalStrut(5)); 
        panelTrai.add(button2);
        panelTrai.add(Box.createVerticalStrut(5));
        panelTrai.add(button3);
        panelTrai.add(Box.createVerticalStrut(5));
        panelTrai.add(button4);

      
        frame.add(panelTrai, BorderLayout.WEST);
        frame.add(cardPanel, BorderLayout.CENTER);
        frame.add(backButton, BorderLayout.SOUTH);

       
        frame.setVisible(true);
    }

  
    private JPanel createCardPanel(String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }
//vi phạm
    private JPanel createViolationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

      
        JLabel label = new JLabel("Quản lý lỗi vi phạm", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

       
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID Vi Phạm", "Biển Kiểm Soát", "Ngày Vi Phạm", "Lỗi Vi Phạm", "Xử Lý"});
        table.setModel(tableModel);

      
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

 
        JPanel functionPanel = new JPanel(new BorderLayout());

        //  tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        JTextField txtSearch = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(searchButton);
        functionPanel.add(searchPanel, BorderLayout.NORTH);

        // khác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton resetButton = new JButton("Hiển thị tất cả");
        buttonPanel.add(resetButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        functionPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(functionPanel, BorderLayout.SOUTH);

    
        loadViolationData(tableModel);

       
        DefaultTableModel originalTableModel = (DefaultTableModel) table.getModel();

       
        searchButton.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

           
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.setColumnIdentifiers(new String[]{"ID Vi Phạm", "Biển Kiểm Soát", "Ngày Vi Phạm", "Lỗi Vi Phạm", "Xử Lý"});
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM vipham WHERE idvipham LIKE '%" + keyword + "%' OR bienkiemsoat LIKE '%" + keyword + 
                     "%' OR loivipham LIKE '%" + keyword + "%' OR xuly LIKE '%" + keyword + "%'"
                 )) {

                while (rs.next()) {
                    filteredModel.addRow(new Object[]{
                            rs.getString("idvipham"),
                            rs.getString("bienkiemsoat"),
                            rs.getString("ngayvipham"),
                            rs.getString("loivipham"),
                            rs.getString("xuly")
                    });
                }
                table.setModel(filteredModel);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

  
        resetButton.addActionListener(e -> {
            loadViolationData(originalTableModel);
            table.setModel(originalTableModel);
        });

        // Thêm dữ liệu
        addButton.addActionListener(e -> {
            JTextField txtIdViPham = new JTextField();
            JTextField txtBienSo = new JTextField();
            JTextField txtNgayViPham = new JTextField();
            JTextField txtLoiViPham = new JTextField();
            JTextField txtXuLy = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(5, 2));
            inputPanel.add(new JLabel("ID Vi Phạm:"));
            inputPanel.add(txtIdViPham);
            inputPanel.add(new JLabel("Biển kiểm soát:"));
            inputPanel.add(txtBienSo);
            inputPanel.add(new JLabel("Ngày vi phạm:"));
            inputPanel.add(txtNgayViPham);
            inputPanel.add(new JLabel("Lỗi vi phạm:"));
            inputPanel.add(txtLoiViPham);
            inputPanel.add(new JLabel("Xử lý:"));
            inputPanel.add(txtXuLy);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Thêm lỗi vi phạm", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "INSERT INTO vipham (idvipham, bienkiemsoat, ngayvipham, loivipham, xuly) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtIdViPham.getText());
                    ps.setString(2, txtBienSo.getText());
                    ps.setString(3, txtNgayViPham.getText());
                    ps.setString(4, txtLoiViPham.getText());
                    ps.setString(5, txtXuLy.getText());
                    ps.executeUpdate();
                    loadViolationData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Thêm lỗi vi phạm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sửa dữ liệu
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idViPham = (String) tableModel.getValueAt(selectedRow, 0);
            JTextField txtIdViPham = new JTextField(idViPham);
            JTextField txtBienSo = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField txtNgayViPham = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField txtLoiViPham = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
            JTextField txtXuLy = new JTextField((String) tableModel.getValueAt(selectedRow, 4));

            JPanel inputPanel = new JPanel(new GridLayout(5, 2));
            inputPanel.add(new JLabel("ID Vi Phạm:"));
            inputPanel.add(txtIdViPham);
            inputPanel.add(new JLabel("Biển kiểm soát:"));
            inputPanel.add(txtBienSo);
            inputPanel.add(new JLabel("Ngày vi phạm:"));
            inputPanel.add(txtNgayViPham);
            inputPanel.add(new JLabel("Lỗi vi phạm:"));
            inputPanel.add(txtLoiViPham);
            inputPanel.add(new JLabel("Xử lý:"));
            inputPanel.add(txtXuLy);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Sửa lỗi vi phạm", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "UPDATE vipham SET idvipham = ?, bienkiemsoat = ?, ngayvipham = ?, loivipham = ?, xuly = ? WHERE idvipham = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtIdViPham.getText());
                    ps.setString(2, txtBienSo.getText());
                    ps.setString(3, txtNgayViPham.getText());
                    ps.setString(4, txtLoiViPham.getText());
                    ps.setString(5, txtXuLy.getText());
                    ps.setString(6, idViPham);
                    ps.executeUpdate();
                    loadViolationData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sửa lỗi vi phạm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xóa dữ liệu
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idViPham = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa lỗi vi phạm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "DELETE FROM vipham WHERE idvipham = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, idViPham);
                    ps.executeUpdate();
                    loadViolationData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Xóa lỗi vi phạm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    } 
    
    private void loadViolationData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); 
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
//quản lý phương tiện
    private JPanel createVehicleManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        
        JLabel label = new JLabel("Quản lý phương tiện", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

        
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Biển kiểm soát", "Loại xe", "Màu sắc", "Chủ xe", "Phân khối xe"});
        table.setModel(tableModel);

       
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel functionPanel = new JPanel(new BorderLayout());

        //  kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        JTextField txtSearch = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(searchButton);
        functionPanel.add(searchPanel, BorderLayout.NORTH);

        // khác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton resetButton = new JButton("Hiển thị tất cả");
        buttonPanel.add(resetButton);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        functionPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(functionPanel, BorderLayout.SOUTH);


        loadVehicleData(tableModel);
      
        DefaultTableModel originalTableModel = (DefaultTableModel) table.getModel();
        searchButton.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

           
            int columnCount = tableModel.getColumnCount();
            String[] columnIdentifiers = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnIdentifiers[i] = tableModel.getColumnName(i);
            }

            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.setColumnIdentifiers(columnIdentifiers);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM qlyphuongtien WHERE bienkiemsoat LIKE '%" + keyword + "%' OR loaixe LIKE '%" + keyword + "%' OR mausacxe LIKE '%" + keyword + "%' OR chuxe LIKE '%" + keyword + "%'")) {

                while (rs.next()) {
                    filteredModel.addRow(new Object[]{
                            rs.getString("bienkiemsoat"),
                            rs.getString("loaixe"),
                            rs.getString("mausacxe"),
                            rs.getString("chuxe"),
                            rs.getInt("phankhoixe")
                    });
                }
                table.setModel(filteredModel); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
       
        resetButton.addActionListener(e -> {
            table.setModel(originalTableModel);
        });
        // Thêm dữ liệu
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
                    loadVehicleData(tableModel); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Thêm phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

     // Sửa dữ liệu
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

       
            String oldBienSo = (String) tableModel.getValueAt(selectedRow, 0); 
            JTextField txtBienSo = new JTextField(oldBienSo); 
            JTextField txtLoaiXe = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField txtMauSac = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField txtChuXe = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
            JTextField txtPhanKhoi = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

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

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Sửa thông tin phương tiện", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newBienSo = txtBienSo.getText(); 
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    
                    if (!newBienSo.equals(oldBienSo)) {
                        String checkQuery = "SELECT COUNT(*) FROM qlyphuongtien WHERE bienkiemsoat = ?";
                        PreparedStatement checkPs = conn.prepareStatement(checkQuery);
                        checkPs.setString(1, newBienSo);
                        ResultSet rs = checkPs.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        if (count > 0) {
                            JOptionPane.showMessageDialog(null, "Biển kiểm soát mới đã tồn tại!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                 
                    String updateQuery = "UPDATE qlyphuongtien SET bienkiemsoat = ?, loaixe = ?, mausacxe = ?, chuxe = ?, phankhoixe = ? WHERE bienkiemsoat = ?";
                    PreparedStatement ps = conn.prepareStatement(updateQuery);
                    ps.setString(1, newBienSo); 
                    ps.setString(2, txtLoaiXe.getText());
                    ps.setString(3, txtMauSac.getText());
                    ps.setString(4, txtChuXe.getText());
                    ps.setInt(5, Integer.parseInt(txtPhanKhoi.getText()));
                    ps.setString(6, oldBienSo); 
                    ps.executeUpdate();

                  
                    loadVehicleData(tableModel);
                    JOptionPane.showMessageDialog(null, "Sửa phương tiện thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Phân khối phải là một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sửa phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xóa dữ liệ
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
                    loadVehicleData(tableModel); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Xóa phương tiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        

        return panel;
    }

  
    private void loadVehicleData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); 
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
    //quản lý cam
    private JPanel CameraPanel() {
        JPanel panel = new JPanel(new BorderLayout());

      
        JLabel label = new JLabel("Quản lý camera", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);


        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID Camera", "Vị Trí", "Ngày Kiểm Tra", "Sử Dụng"});
        table.setModel(tableModel);


        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel functionPanel = new JPanel(new BorderLayout());

        //tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        JTextField txtSearch = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(searchButton);
        functionPanel.add(searchPanel, BorderLayout.NORTH);

        // khác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton resetButton = new JButton("Hiển thị tất cả");
        buttonPanel.add(resetButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        functionPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(functionPanel, BorderLayout.SOUTH);

  
        loadCameraData(tableModel);


        DefaultTableModel originalTableModel = (DefaultTableModel) table.getModel();

        // tìm kiếm
        searchButton.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

   
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.setColumnIdentifiers(new String[]{"ID Camera", "Vị Trí", "Ngày Kiểm Tra", "Sử Dụng"});
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM camera WHERE idcamera LIKE '%" + keyword + "%' OR vitri LIKE '%" + keyword + 
                     "%' OR ngaykiemtra LIKE '%" + keyword + "%' OR sudung LIKE '%" + keyword + "%'"
                 )) {

                while (rs.next()) {
                    filteredModel.addRow(new Object[]{
                            rs.getString("idcamera"),
                            rs.getString("vitri"),
                            rs.getString("ngaykiemtra"),
                            rs.getString("sudung")
                    });
                }
                table.setModel(filteredModel);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

       
        resetButton.addActionListener(e -> {
            loadCameraData(originalTableModel);
            table.setModel(originalTableModel);
        });

        addButton.addActionListener(e -> {
            JTextField txtIDCamera = new JTextField();
            JTextField txtViTri = new JTextField();
            JTextField txtNgayKiemTra = new JTextField();
            JTextField txtSuDung = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("ID Camera:"));
            inputPanel.add(txtIDCamera);
            inputPanel.add(new JLabel("Vị trí:"));
            inputPanel.add(txtViTri);
            inputPanel.add(new JLabel("Ngày kiểm tra:"));
            inputPanel.add(txtNgayKiemTra);
            inputPanel.add(new JLabel("Sử dụng:"));
            inputPanel.add(txtSuDung);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Thêm camera", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "INSERT INTO camera (idcamera, vitri, ngaykiemtra, sudung) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtIDCamera.getText());
                    ps.setString(2, txtViTri.getText());
                    ps.setString(3, txtNgayKiemTra.getText());
                    ps.setString(4, txtSuDung.getText());
                    ps.executeUpdate();
                    loadCameraData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Thêm camera thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Sửa dữ liệu
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

     
            String originalIDCamera = (String) tableModel.getValueAt(selectedRow, 0); // ID Camera gốc
            JTextField txtIDCamera = new JTextField(originalIDCamera);
            JTextField txtViTri = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField txtNgayKiemTra = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField txtSuDung = new JTextField((String) tableModel.getValueAt(selectedRow, 3));

            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("ID Camera:"));
            inputPanel.add(txtIDCamera);
            inputPanel.add(new JLabel("Vị trí:"));
            inputPanel.add(txtViTri);
            inputPanel.add(new JLabel("Ngày kiểm tra:"));
            inputPanel.add(txtNgayKiemTra);
            inputPanel.add(new JLabel("Sử dụng:"));
            inputPanel.add(txtSuDung);

      
            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Sửa camera", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String newIDCamera = txtIDCamera.getText();
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    // Kiểm tra nếu ID Camera mới trùng với ID hiện có (trừ ID gốc)
                    String checkQuery = "SELECT COUNT(*) FROM camera WHERE idcamera = ? AND idcamera != ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setString(1, newIDCamera);
                    checkStmt.setString(2, originalIDCamera);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "ID Camera đã tồn tại. Vui lòng nhập ID khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                  
                    String query = "UPDATE camera SET idcamera = ?, vitri = ?, ngaykiemtra = ?, sudung = ? WHERE idcamera = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, newIDCamera);
                    ps.setString(2, txtViTri.getText());
                    ps.setString(3, txtNgayKiemTra.getText());
                    ps.setString(4, txtSuDung.getText());
                    ps.setString(5, originalIDCamera); 
                    ps.executeUpdate();

             
                    loadCameraData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sửa camera thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Xóa dữ liệu
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idCamera = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa camera này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "DELETE FROM camera WHERE idcamera = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, idCamera);
                    ps.executeUpdate();
                    loadCameraData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Xóa camera thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }


    private void loadCameraData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); 
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idcamera, vitri, ngaykiemtra, sudung FROM camera")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("idcamera"),
                        rs.getString("vitri"),
                        rs.getString("ngaykiemtra"),
                        rs.getString("sudung")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    //đường bộ
    private JPanel Street() {
        JPanel panel = new JPanel(new BorderLayout());

        
        JLabel label = new JLabel("Quản lý đường bộ", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.NORTH);

  
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID Đường", "Tên Đường", "Ngày Bảo Dưỡng", "Giao Thông"});
        table.setModel(tableModel);

    
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);


        JPanel functionPanel = new JPanel(new BorderLayout());

//tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        JTextField txtSearch = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchPanel.add(searchLabel);
        searchPanel.add(txtSearch);
        searchPanel.add(searchButton);
        functionPanel.add(searchPanel, BorderLayout.NORTH);

        // khác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton resetButton = new JButton("Hiển thị tất cả");
        buttonPanel.add(resetButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        functionPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(functionPanel, BorderLayout.SOUTH);

   
        loadRoadData(tableModel);


        DefaultTableModel originalTableModel = (DefaultTableModel) table.getModel();

     
        searchButton.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }


            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.setColumnIdentifiers(new String[]{"ID Đường", "Tên Đường", "Ngày Bảo Dưỡng", "Giao Thông"});
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM duongbo WHERE idduong LIKE '%" + keyword + "%' OR tenduong LIKE '%" + keyword + "%' OR giaothong LIKE '%" + keyword + "%'")) {

                while (rs.next()) {
                    filteredModel.addRow(new Object[]{
                            rs.getString("idduong"),
                            rs.getString("tenduong"),
                            rs.getString("ngaybaoduong"),
                            rs.getString("giaothong")
                    });
                }
                table.setModel(filteredModel);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

  
        resetButton.addActionListener(e -> {
            loadRoadData(originalTableModel);
            table.setModel(originalTableModel);
        });

     // Thêm dữ liệu
        addButton.addActionListener(e -> {
            JTextField txtIDDuong = new JTextField();
            JTextField txtTenDuong = new JTextField();
            JTextField txtNgayBaoDuong = new JTextField();
            JTextField txtGiaoThong = new JTextField();

            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("ID Đường:"));
            inputPanel.add(txtIDDuong);
            inputPanel.add(new JLabel("Tên đường:"));
            inputPanel.add(txtTenDuong);
            inputPanel.add(new JLabel("Ngày bảo dưỡng:"));
            inputPanel.add(txtNgayBaoDuong);
            inputPanel.add(new JLabel("Giao thông:"));
            inputPanel.add(txtGiaoThong);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Thêm đường bộ", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "INSERT INTO duongbo (idduong, tenduong, ngaybaoduong, giaothong) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtIDDuong.getText());
                    ps.setString(2, txtTenDuong.getText());
                    ps.setString(3, txtNgayBaoDuong.getText());
                    ps.setString(4, txtGiaoThong.getText());
                    ps.executeUpdate();
                    loadRoadData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Thêm đường bộ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sửa dữ liệu
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField txtIDDuong = new JTextField((String) tableModel.getValueAt(selectedRow, 0));
            JTextField txtTenDuong = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
            JTextField txtNgayBaoDuong = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
            JTextField txtGiaoThong = new JTextField((String) tableModel.getValueAt(selectedRow, 3));

            JPanel inputPanel = new JPanel(new GridLayout(4, 2));
            inputPanel.add(new JLabel("ID Đường:"));
            inputPanel.add(txtIDDuong);
            inputPanel.add(new JLabel("Tên đường:"));
            inputPanel.add(txtTenDuong);
            inputPanel.add(new JLabel("Ngày bảo dưỡng:"));
            inputPanel.add(txtNgayBaoDuong);
            inputPanel.add(new JLabel("Giao thông:"));
            inputPanel.add(txtGiaoThong);

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "Sửa đường bộ", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "UPDATE duongbo SET idduong = ?, tenduong = ?, ngaybaoduong = ?, giaothong = ? WHERE idduong = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, txtIDDuong.getText());
                    ps.setString(2, txtTenDuong.getText());
                    ps.setString(3, txtNgayBaoDuong.getText());
                    ps.setString(4, txtGiaoThong.getText());
                    ps.setString(5, (String) tableModel.getValueAt(selectedRow, 0)); // ID gốc để tìm bản ghi
                    ps.executeUpdate();
                    loadRoadData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sửa đường bộ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Xóa dữ liệu
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idDuong = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa đường này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426")) {
                    String query = "DELETE FROM duongbo WHERE idduong = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, idDuong);
                    ps.executeUpdate();
                    loadRoadData(tableModel);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Xóa đường bộ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void loadRoadData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "513426");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT idduong, tenduong, ngaybaoduong, giaothong FROM duongbo")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("idduong"),
                        rs.getString("tenduong"),
                        rs.getString("ngaybaoduong"),
                        rs.getString("giaothong")
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
        SwingUtilities.invokeLater(cuoiki1::new);
    }
}