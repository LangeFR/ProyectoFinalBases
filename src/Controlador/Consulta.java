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

    private Connection connection;
    private String baseTable;
    private List<String> joins;
    private List<String> filters;
    private String limit;
    private String errorMessage;

    // Constructor
    public Consulta(Connection connection, String baseTable) {
        this.connection = connection;
        this.baseTable = baseTable;
        this.joins = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.limit = "";
        this.errorMessage = "";
    }

    // Métodos de construcción de consulta

    public List<String> getFilters(){
        return filters;
    }
    public void addJoin(String joinClause) {
        this.joins.add(joinClause);
    }

    public void addFilter(String filter) {
        this.filters.add(filter);
    }

    public void setLimit(int limit) {
        this.limit = "LIMIT " + limit;
    }

    public void clear() {
        this.joins.clear();
        this.filters.clear();
        this.limit = "";
        this.errorMessage = "";
    }

    // Método para construir el SQL
    public String buildQuery() {
        StringBuilder query = new StringBuilder("SELECT * FROM " + baseTable);

        for (String join : joins) {
            query.append(" ").append(join);
        }

        if (!filters.isEmpty()) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", filters));
        }

        if (!limit.isEmpty()) {
            query.append(" ").append(limit);
        }

        return query.toString();
    }

    // Método para ejecutar la consulta y obtener los resultados
    public ResultSet execute() {
        String query = buildQuery();
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            this.errorMessage = "Error al ejecutar la consulta: " + e.getMessage();
            e.printStackTrace();
            return null;
        }
    }

    // Método para validar la consulta
    public boolean isValid() {
        return !buildQuery().isEmpty() && errorMessage.isEmpty();
    }

    // Obtener el mensaje de error
    public String getError() {
        return this.errorMessage;
    }

    // Obtener la consulta SQL en formato String para depuración
    public String getSqlQuery() {
        return buildQuery();
    }
}
