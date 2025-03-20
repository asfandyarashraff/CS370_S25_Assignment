import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBTest_Demo {

    public int testconnection_mysql(int hr_offset) {
        String connection_host = "3.141.101.113"; 
        String db_name = "db_repo"; 
        String db_user = "malik"; 
        String db_password = "Ahmedyar1!"; 
        Connection connect = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        int flag = 0;

        try {
            // Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with MySQL EC2 instance (No SSL)
            connect = DriverManager.getConnection(
                "jdbc:mysql://" + connection_host + ":3306/" + db_name + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                db_user, 
                db_password
            );

            String qry1a = "SELECT CURDATE() + " + hr_offset;
            preparedStatement = connect.prepareStatement(qry1a);
            ResultSet r1 = preparedStatement.executeQuery();

            if (r1.next()) {
                String nt = r1.getString(1);
                System.out.println(hr_offset + " hour(s) ahead of MySQL Server at " + connection_host + " is: " + nt);
            }
            r1.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
            flag = -1;
        } finally {
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connect != null) connect.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return flag;
    }

    public static void main(String[] args) {
        DBTest_Demo dbTest = new DBTest_Demo();
        dbTest.testconnection_mysql(0);
    }
}


