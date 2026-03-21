package com.cts.passiton;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dc = new DatabaseConnection();

        try {
            dc.stat = dc.con.createStatement();
            dc.rst = dc.stat.executeQuery("SELECT * FROM tblusers");

            while(dc.rst.next()) {
                System.out.print(dc.rst.getString("user_id"));
                System.out.print(" ");
                System.out.print(dc.rst.getString("email"));
                System.out.print(" ");
                System.out.print(dc.rst.getString("first_name"));
                System.out.print(" ");
                System.out.print(dc.rst.getString("last_name"));
                System.out.println(" ");

            }
        } catch(SQLException e) {
            System.exit(0);
        }
    }
}