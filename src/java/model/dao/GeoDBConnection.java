/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import model.vo.GeoreferencedInjuries;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.vo.Variable;

/**
 *
 * @author nelson
 */
public class GeoDBConnection {

    private Connection connection;
    //credenciales
    private String host;
    private String port;
    private String databaseName;
    private String user;
    private String passwd;
    private GeoreferencedInjuries injury;
    private Variable variable;

    public GeoDBConnection(String host, String port, String databaseName, String user, String passwd) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.passwd = passwd;
    }

    public List<Variable> listCountbyYears(String initialDate, String endDate, String polygonVertices) {

        String polygonPoints[] = polygonVertices.split(",");

        String query = ""
                + "SELECT \n"
                + "	injury_name,\n"
                + "	extract(year FROM injury_date) AS injury_year,\n"
                + "	count(*) AS score\n"
                + "FROM\n"
                + "	injuries\n"
                + "	JOIN fatal_injuries USING (injury_id)\n"
                + "	JOIN georeferenced_fatal_injuries USING (fatal_injury_id)\n"
                + "WHERE\n"
                + "	injury_date BETWEEN '" + initialDate + "' AND '" + endDate + "'\n"
                + "	AND ST_Contains(ST_SetSRID(ST_MakePolygon(\n"
                + "		ST_MakeLine(\n"
                + "			ARRAY[\n"
                + "				ST_GeomFromText('" + polygonPoints[0] + "'),\n"
                + "				ST_GeomFromText('" + polygonPoints[1] + "'), \n"
                + "				ST_GeomFromText('" + polygonPoints[2] + "'),\n"
                + "				ST_GeomFromText('" + polygonPoints[3] + "'), \n"
                + "				ST_GeomFromText('" + polygonPoints[0] + "')]\n"
                + "		)\n"
                + "	), 3857), ST_Transform(ST_SetSRID(ST_Point(x, y), 4326), 3857)) IS TRUE\n"
                + "GROUP BY\n"
                + "	injury_name,\n"
                + "	injury_year \n"
                + "ORDER BY\n"
                + "	injury_name, injury_year;";

        System.out.println(query);
        List<Variable> list = null;
        
        try {
            this.openConnection();

            PreparedStatement st = this.connection.prepareStatement(query);
            list = new ArrayList();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                variable = new Variable();
                variable.setColumn1(rs.getString("injury_name"));
                variable.setColumn2(rs.getInt("injury_year"));
                variable.setScore(rs.getInt("score"));
                list.add(variable);
            }
            rs.close();
            st.close();
            this.closeConnection();
        } catch (Exception e) {
            System.out.println("ERROR --- " + e.getMessage());
        }
        
        return list;
    }

    public List<GeoreferencedInjuries> listFatalInjuries(int category, String initial, String end) {
        String query = ""
                + "SELECT"
                + "	fatal_injury_id,"
                + "	ST_X(ST_Transform(ST_SetSRID(ST_Point(x,y), 4326), 3857)) AS x, "
                + "	ST_Y(ST_Transform(ST_SetSRID(ST_Point(x,y), 4326), 3857)) AS y "
                + "FROM injuries\n"
                + "	JOIN fatal_injuries USING (injury_id)\n"
                + "	JOIN georeferenced_fatal_injuries USING (fatal_injury_id) "
                + "WHERE ";

        switch (category) {
            case 1:
                break;
            case 12:
                query = query.concat("injury_name LIKE 'HOMICIDIO' AND ");
                break;
            case 19:
                query = query.concat("injury_name LIKE 'SUICIDIO' AND ");
                break;
            case 5:
                query = query.concat("injury_name LIKE '%ACCIDENTAL' AND ");
                break;
            case 26:
                query = query.concat("injury_name LIKE '%TRANSITO' AND ");
                break;
        }
        query = query.concat("injury_date BETWEEN '" + initial + "' AND '" + end + "';");

        System.out.println(query);

        List<GeoreferencedInjuries> list = null;
        try {
            this.openConnection();

            PreparedStatement st = this.connection.prepareStatement(query);
            list = new ArrayList();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                injury = new GeoreferencedInjuries();
                injury.setInjury_id(rs.getInt("fatal_injury_id"));
                injury.setX(rs.getDouble("x"));
                injury.setY(rs.getDouble("y"));
                list.add(injury);
            }
            rs.close();
            st.close();
            this.closeConnection();
        } catch (Exception e) {
            System.out.println("ERROR --- " + e.getMessage());
        }
        return list;
    }
    
    public List<GeoreferencedInjuries> listNonFatalInjuries(int category, String initial, String end) {
        String query = ""
                + "SELECT \n"
                + "	non_fatal_injury_id, \n"
                + "	ST_X(ST_Transform(ST_SetSRID(ST_Point(x,y), 4326), 3857)) AS x,  \n"
                + "	ST_Y(ST_Transform(ST_SetSRID(ST_Point(x,y), 4326), 3857)) AS y  \n"
                + "FROM \n"
                + "	injuries\n"
                + "	JOIN non_fatal_injuries USING (injury_id) \n"
                + "	JOIN georeferenced_non_fatal_injuries USING (non_fatal_injury_id)\n"
                + "WHERE\n";
                

        switch (category) {
            case 3: //CASOS POR LESION FATAL
                query = query.concat("injury_id  NOT IN (56) AND ");
                break;
            case 33: //CASOS POR VIOLENCIA INTERPERSONAL EN FAMILIA
                query = query.concat("injury_id IN (53, 55) AND ");
                break;
            case 40: //CASOS DE LESIONES EN ACCIDENTES DE TRANSITO
                query = query.concat("injury_id = 51 AND ");
                break;
            case 47: //CASOS DE LESIONES NO INTENCIONALES
                query = query.concat("injury_id = 54 AND ");
                break;
            case 54: //CASOS DE VIOLENCIA AUTOINFLINGIDA
                query = query.concat("injury_id = 52 AND ");
                break;
            case 61: //CASOS DE VIOLENCIA INTERPERSONAL EN COMUNIDAD
                query = query.concat("injury_id = 50 AND ");
                break;
        }
        query = query.concat("injury_date BETWEEN '" + initial + "' AND '" + end + "';");

        System.out.println(query);

        List<GeoreferencedInjuries> list = null;
        try {
            this.openConnection();

            PreparedStatement st = this.connection.prepareStatement(query);
            list = new ArrayList();
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                injury = new GeoreferencedInjuries();
                injury.setInjury_id(rs.getInt("non_fatal_injury_id"));
                injury.setX(rs.getDouble("x"));
                injury.setY(rs.getDouble("y"));
                list.add(injury);
            }
            rs.close();
            st.close();
            this.closeConnection();
        } catch (Exception e) {
            System.out.println("ERROR --- " + e.getMessage());
        }
        return list;
    }
    

    //abrir conexion
    protected void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + databaseName, user, passwd);
            Class.forName("org.postgresql.Driver");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("ERROR -- " + e.getMessage());
        }
    }

    //cerrar conexion
    protected void closeConnection() {
        try {
            if (connection != null) {
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR -- " + e.getMessage());
        }
    }
}