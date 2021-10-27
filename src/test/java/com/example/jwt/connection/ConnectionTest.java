package com.example.jwt.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionTest {

    private final String DRIVER = "org.mariadb.jdbc.Driver";
    private final String URL = "jdbc:mariadb://localhost:3306/jwt_example";
    private final String USER = "jwt_user";
    private final String PW = "jwt_pw";

    @Test
    public void DB_연결_테스트() throws Exception{

        Class.forName(DRIVER);

        try(Connection con = DriverManager.getConnection(URL, USER, PW)){
            System.out.println(con);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
