import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class QuanlySinhVien {
    private final AtomicReference<Connection> connection = new AtomicReference<>();
    private List<String> cache;
    private int MaSinhVien;

    public QuanlySinhVien() throws SQLException, IOException {
        this.MaSinhVien = 0;
        this.connection.set(DatabaseConnection.getInstance().getConnection());
        this.cache = null;
    }


    public List<String> fetchAllStudents() throws SQLException {
        if (this.cache == null) {
            Statement stmt = connection.get().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM sinhvien");
            List<String> students = new ArrayList<>();
            while (rs.next()) {
                String student = String.format("MaSinhVien: %d, HoTen: %s, GioiTinh: %b, NgaySinh: %s",
                        rs.getInt("MaSinhVien"),
                        rs.getString("HoTen"),
                        rs.getBoolean("GioiTinh"),
                        rs.getDate("NgaySinh").toString());
                students.add(student);
            }
            rs.close();
            stmt.close();
            this.cache = students;
        }
        return this.cache;
    }

    public void addStudent(String HoTen, boolean GioiTinh, String NgaySinh) throws SQLException {
        String query = "INSERT INTO students (MaSinhVien,HoTen, GioiTinh, NgaySinh) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.get().prepareStatement(query);
        pstmt.setInt(1, MaSinhVien);
        pstmt.setString(2, HoTen);
        pstmt.setBoolean(3, GioiTinh);
        pstmt.setDate(4, Date.valueOf(NgaySinh));
        pstmt.executeUpdate();
        pstmt.close();
        this.cache = null;
    }

    public void printStudents() throws SQLException {
        List<String> students = fetchAllStudents();
        for (String student : students) {
            System.out.println(student);
        }
    }

    public static void main(String[] args) {
        try {
            QuanlySinhVien manager = new QuanlySinhVien();
            System.out.println("Danh sách sinh viên hiện tại:");
            manager.printStudents();

            manager.addStudent("Tran Thanh Dat", true, "2003-03-20");
            System.out.println("\nSau khi thêm sinh viên mới:");
            manager.printStudents();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}