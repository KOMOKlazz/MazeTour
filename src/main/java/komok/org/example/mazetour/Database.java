package komok.org.example.mazetour;

import komok.org.example.mazetour.models.CandyWarsStats;
import org.checkerframework.checker.units.qual.C;

import java.sql.*;

public class Database {
    private Connection connection;
    public Connection getConnection() throws SQLException{

        if (connection != null) {
            return connection;
        }

        String url = "jdbc:mysql://u144378_DHYQNiS2FL:vfX%40Pyk3u7L2.w1UvIUC!Nc%40@mysql2.joinserver.xyz:3306/s144378_maze";
        String user = "u144378_DHYQNiS2FL";
        String password = "vfX@Pyk3u7L2.w1UvIUC!Nc@";

        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        return connection;
    }

    public void initializeDatabase() throws SQLException{
        Statement statement = getConnection().createStatement();

        //Create the candy_wars table
        String sql = "CREATE TABLE IF NOT EXISTS candy_wars (nickname varchar(36) primary key, candies int, kills int, deaths int)";

        statement.execute(sql);

        statement.close();
    }

    public CandyWarsStats findPlayerCandyWarsStatsByNickname(String nickname) throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "SELECT * FROM candy_wars WHERE nickname = " + nickname;
        ResultSet results = statement.executeQuery(sql);

        if (results.next()) {
            int candies = results.getInt("candies");
            int kills = results.getInt("kills");
            int deaths = results.getInt("deaths");
            CandyWarsStats candyWarsStats = new CandyWarsStats(nickname, candies, kills, deaths);

            statement.close();
            return candyWarsStats;
        }

        statement.close();
        return null;
    }
}
