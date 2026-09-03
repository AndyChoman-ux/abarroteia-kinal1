/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.java.com.vyorg.abarroteria.kinal.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import main.java.com.vyorg.abarroteria.kinal.config.Credentials;
/**
 *
 * @author informatica
 */
public class DataBaseConnection {
    private static Connection connection;   
   
    private DataBaseConnection(){
    };
    
    public static Connection getDataBaseConnection() throws SQLException{
        if (connection == null || connection.isClosed()){
            connection= DriverManager.getConnection(Credentials.URL_DB,Credentials.USER_DB,Credentials.PASS_DB);
        }
    return connection;
    }
    
    
}
