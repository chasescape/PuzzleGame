package escape.ui;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class MySqlTest {
        public static void main(String[] args) {
            String url = "jdbc:mysql://localhost:3306/userinformation";
            String user = "root";
            String password = "123456";

            try {
                // 注册 JDBC 驱动程序
                Class.forName("com.mysql.cj.jdbc.Driver");

                // 打开连接
                Connection connection = DriverManager.getConnection(url, user, password);

                // 执行查询
                Statement statement = connection.createStatement();
                String sql = "SELECT uid, password, email FROM 用户信息";
                ResultSet resultSet = statement.executeQuery(sql);

                // 展示数据
                while (resultSet.next()) {
                    int uid = resultSet.getInt("uid");
                    String name = resultSet.getString("password");
                    System.out.println("ID: " + uid + ",password: " + name);
                }

                // 关闭资源
                resultSet.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

