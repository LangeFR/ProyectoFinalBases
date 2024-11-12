/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Controlador;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Consulta {
    private Connection conn;
    private String baseTable;
    private StringBuilder joins;
    private StringBuilder filters;
    private String error;

    public Consulta(Connection conn, String baseTable) {
        this.conn = conn;
        this.baseTable = baseTable;
        this.joins = new StringBuilder();
        this.filters = new StringBuilder();
        this.error = "";
    }

    public void addJoin(String join) {
        joins.append(" ").append(join);
    }

    public boolean hasJoin() {
        return joins.length() > 0;
    }

    public void addFilter(String filter) {
        if (filters.length() > 0) {
            filters.append(" ").append(filter);
        } else {
            filters.append(" WHERE ").append(filter);
        }
    }

    public void clear() {
        joins.setLength(0);
        filters.setLength(0);
        error = "";
    }

    public String buildQuery() {
        return "SELECT * FROM " + baseTable + joins.toString() + filters.toString();
    }

    public ResultSet execute() {
        String query = buildQuery();
        try {
            return conn.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            error = ex.getMessage();
            ex.printStackTrace();
            return null;
        }
    }

    public String getError() {
        return error;
    }

    public StringBuilder getFilters() {
        return filters;
    }
}
