/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.snowflake;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gaian
 */
public class Runner {

    public static void main(String[] str) {
        try {
            Class.forName("com.snowflake.client.jdbc.SnowflakeDriver");
            System.out.println("driver loaded");
            try (Connection c = DriverManager.getConnection("jdbc:snowflake://snaplogic.snowflakecomputing.com:443?"
                    + "ssl=on&warehouse=SNAPQA&db=SNAPQADB&schema=PUBLIC", "Danila Mikhaylutsa", "<put password here>")) {
                try (Statement s = c.createStatement()) {
                    s.execute("create table SNAPQADB.PRASANNA.\"autocommitoff\" (id int, name varchar(30), joiningdate date);"
                            + " insert into SNAPQADB.PRASANNA.\"autocommitoff\" values (1,'prasanna','2017-09-09');");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("Error state: " + e.getSQLState());
            System.out.println("Error msg: " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
