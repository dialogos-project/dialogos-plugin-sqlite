package edu.cmu.lti.dialogos.db.sqlite;

import com.clt.script.exp.Value;
import com.clt.script.exp.values.Undefined;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

public class Database {

    String jdbcURL;
    Connection conn;

    public void setDatabaseURL(String databaseURL) {
        this.jdbcURL = databaseURL;
        closeDatabase();
    }

    void openDatabase() throws SQLException {
        if (conn == null)
            conn = DriverManager.getConnection(jdbcURL);
    }

    void closeDatabase() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        conn = null;
    }

    Value executeStatement(String statement) throws SQLException {
        openDatabase();
        Value v = new Undefined();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            boolean hasResult = stmt.execute(statement);
            if (hasResult) {
                ResultSet rs = stmt.executeQuery(statement);
                v = interpretResultSet(rs);
            }
        } finally {
            if (stmt != null)
                stmt.close();
        }
        return v;
    }

    static Value interpretResultSet(ResultSet rs) throws SQLException {
        JSONArray json = convert(rs);
        //System.err.println(json.toString());
        return Value.fromJson(json);
    }

    // shamelessly plugged from https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json
    // it would of course be more efficient to directly convert the ResultSet to a Value...
    public static JSONArray convert(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();

        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i=1; i<numColumns+1; i++) {
                String column_name = rsmd.getColumnName(i);

                if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                    obj.put(column_name, rs.getArray(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                    obj.put(column_name, rs.getBoolean(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                    obj.put(column_name, rs.getBlob(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                    obj.put(column_name, rs.getDouble(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                    obj.put(column_name, rs.getFloat(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                    obj.put(column_name, rs.getNString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                    obj.put(column_name, rs.getString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
                    obj.put(column_name, rs.getDate(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                    obj.put(column_name, rs.getTimestamp(column_name));
                }
                else{
                    obj.put(column_name, rs.getObject(column_name));
                }
            }
            json.put(obj);
        }
        return json;
    }
}
