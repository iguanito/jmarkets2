/*
 * Copyright (C) 2005-2006, <a href="http://www.ssel.caltech.edu">SSEL</a>
 * <a href="http://www.cassel.ucla.edu">CASSEL</a>, Caltech/UCLA
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 *
 * Project Authors: Raj Advani, Walter M. Yuan, and Peter Bossaerts
 * Email: jmarkets@ssel.caltech.edu
 */

/*
 * DBConnector.java
 *
 * Created on March 17, 2004, 6:03 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.server.data;

import java.util.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;


/**
 *
 * @author  Raj Advani
 */
public class DBConnector {
    
    /** Creates a new instance of DBConnector */
    public DBConnector(Properties props) {
        log.debug("A new Database Connector has been instantiated");
        
        if (props != null) {
            //dbName = props.getProperty("dbName");
            dbPassword = props.getProperty("dbPassword", "");
            dbUser = props.getProperty("dbUser");
            //dbPort = props.getProperty("dbPort");
            dbURL = props.getProperty("dbURL");
            try{
                this.maxNumConn = Integer.parseInt(props.getProperty("maxActive"));
            }catch(Exception e){
                this.maxNumConn = Max_Active;
            }

            try{
                this.minNumConn = Integer.parseInt(props.getProperty("minActive"));
            }catch(Exception e){
                this.minNumConn = Min_Active;
            }

            try{
                this.maxWait = Long.parseLong(props.getProperty("maxWait"));
            }catch(Exception e){
                this.maxWait = Max_Wait;
            }

            try{
                this.autoCommit = Boolean.parseBoolean(props.getProperty("autoCommit"));
            }catch(Exception e){
                this.autoCommit = Default_AutoCommit;
            }

            try{
                this.readOnly = Boolean.parseBoolean(props.getProperty("readOnly"));
            }catch(Exception e){
                this.readOnly = Default_ReadOnly;
            }
        }
        log.info("Database Info (I): " + dbURL + ", username " + dbUser);
        log.info("Database Info (II): max conn " + this.maxNumConn + ", min conn " + this.minNumConn + ", auto commit " + this.autoCommit + ", read only " + this.readOnly);
    }
    
    /** Initialize the database */
    public static void main(String args[]) {
        Connection conn= null;
        ResultSet rs =null;
        try {
            DBConnector dbc = new DBConnector(null);
            dbc.dbURL = "jdbc:mysql://localhost:3306/jmarkets2?autoReconnect=true";
            //dbc.dbPort = "3306";
            dbc.dbUser = "root";
            dbc.dbPassword = "";
            //dbc.dbName = "jmarkets";
            
            conn = dbc.getConnection();
            String query = "select * from jm_user";
            Object[] results = dbc.executeQuery(query, conn);
            rs = (ResultSet) results[0];
            System.out.println("" + rs.next());
            
            System.out.println(rs.getString("email"));
            
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try { rs.close(); } catch(Exception e) { }
            try { conn.close(); } catch(Exception e) { }
        }
    }
    
    /** Initialize the connection pool */
    public void connect() {
        try {
            ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.jdbc.Driver");
            ds.setUsername(dbUser);
            ds.setPassword(dbPassword);
            ds.setUrl(dbURL);
            //ds.setUrl("jdbc:mysql://localhost:3306/jmarkets");
            ds.setMaxActive(this.maxNumConn);
            ds.setDefaultAutoCommit(this.autoCommit);
            ds.setDefaultReadOnly(this.readOnly);
            
            //conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassword);
            //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jmarkets2?user=root&password=");//?user=blah&password=blah");
            log.info("Successfully connected to the jMarkets database");
            
        }catch(Exception e) {
            log.fatal("Failed to connect to the jMarkets database: " + e);
        }
    }
    
    /** Get a connection from the pool */
    public Connection getConnection() throws SQLException {
        if (ds == null)
            connect();

        Connection conn= ds.getConnection();
        openConnCount++;
        this.logConnectionStatus(true);
        return conn;
    }
    
    public void logConnectionStatus(boolean opening) {
        Throwable t = new Throwable();
        StackTraceElement[] elements = t.getStackTrace();

        StringBuffer stack = new StringBuffer();
        for (int i = 3; i > 0; i--) {
            String callerMethodName = elements[i].getMethodName();
            String callerClassName = elements[i].getClassName();
            stack.append(i + ": CallerClassName=" + callerClassName + " , Caller method name: " + callerMethodName + " -->");
        }

        log.info(stack.toString());

        if (opening) {
            log.info("Opening connection. Number of Connections: " + openConnCount);
        } else {
            log.info("Closing connection. Number of Connections left: " + openConnCount);
        }
    }

    /** Start a database transaction */
    public void startTransaction(Connection conn) {
        try {
            conn.setAutoCommit(false);
        }catch(Exception e) {
            log.error("Failed to set database to auto-commit false mode", e);
        }
    }
    
    /** Commit the current database transaction */
    public void commit(Connection conn) throws SQLException {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /** Rollback all changes made in the current transaction */
    public void rollback(Connection conn) {
        try {
            conn.rollback();
            conn.setAutoCommit(true);
        }catch(Exception e) {
            log.error("Failed to rollback the last transaction", e);
        }
    }
    
    /** Executes the given query on the Mysql database, returning an object array with the
     *  result set and the statement */
    public Object[] executeQuery(String query, Connection conn) throws SQLException {
        try {
            log.debug("DBConnector is executing query: " + query);
            Statement stmt = conn.createStatement();
            ResultSet RS = stmt.executeQuery(query);
            
            Object[] results = {RS, stmt};
            return results;
        }catch(SQLException e) {
            log.error("Failed to execute the query: " + query, e);
            throw e;
        }
    }
    
    /** Executes the given update on the Mysql database. The ResultSet returned contains
     *  the generated keys */
    public Object[] executeUpdate(String query, Connection conn) throws SQLException {
        try {
            log.debug("DBConnector is executing update: " + query);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet keys = stmt.getGeneratedKeys();
            
            Object[] results = {keys, stmt};
            return results;
        }catch(SQLException e) {
            log.error("Failed to execute the update: " + query, e);
            throw e;
        }
    }
    
    /** Given an object array containing the result set and statement (of the form returned by
     *  the executeQuery method, this simply closes those two connections */
    public void closeQuery(Object[] results) {
        try {
            if (results == null)
                return;
            
            ResultSet RS = (ResultSet) results[0];
            Statement stmt = (Statement) results[1];
            
            stmt.close();
            RS.close();
            
        }catch(SQLException e) {
            log.error("Failed to close DB results object", e);
        }
    }
    
    /** Given an object array containing the result set and statement (of the form returned by
     *  the executeQuery method, this simply closes those two connections */
    public void closeQuery(Object[] results, Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                openConnCount--;
                this.logConnectionStatus(false);
            }

        } catch (Exception e) {
            log.error("Failed to close connection object", e);
        }

        try {
            if (results != null) {
                ResultSet RS = (ResultSet) results[0];
                Statement stmt = (Statement) results[1];

                stmt.close();
                RS.close();
            }
        } catch (Exception e) {
            log.error("Failed to close results objects", e);
        }
    }
    
    public void closeQuery(Connection conn) {
        try {
            if(conn != null){
                conn.close();
                openConnCount--;
                this.logConnectionStatus(false);
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    /** Insert the given values into the given table */
    public Object[] insert(String table, String[] values, Connection conn) throws SQLException {
        StringBuffer query = new StringBuffer();
        
        query.append("insert into ").append(table).append(" values(");
        for (int i=0; i<values.length-1; i++)
            query.append("'").append(values[i]).append("'").append(", ");
        query.append("'").append(values[values.length-1]).append("'").append(")");
        
        return executeUpdate(query.toString(), conn);
    }
    
    public Object[] insert(String table, String [] colNames, String[] values, Connection conn) throws SQLException {
        StringBuffer query = new StringBuffer();
        query.append("insert into ").append(table).append(" (");
        for (int i=0; i<colNames.length-1; i++)
            query.append(colNames[i]).append(", ");
        query.append(colNames[colNames.length-1]).append(")");
        
        query.append(" values(");
        for (int i=0; i<values.length-1; i++)
            query.append("'").append(values[i]).append("'").append(", ");
        query.append("'").append(values[values.length-1]).append("'").append(")");
        
        return executeUpdate(query.toString(), conn);
    }
    
    /** Update the given table. Change the value of each setNames[i] to setValues[i] whenever
     *  matchNames[i] == matchValues[i]. For example if:
     *
     *  table = people
     *
     *  setNames[0] = name, setNames[1] = age
     *  setValues[0] = Amy, setValues[1] = 22
     *
     *  matchNames[0] = sex, matchValues[0] = female
     *  matchNames[1] = job, matchValues[1] = student
     *
     *  Then the following query will be constructed:
     *
     *  update people set name=Amy, age=22 where sex=female and job=student
     */
    public Object[] update(String table, String[] setNames, String[] setValues, String[] matchNames, String[] matchValues, Connection conn) throws SQLException {
        if (setNames.length != setValues.length || matchNames.length != matchValues.length)
            return null;
        
        StringBuffer query = new StringBuffer();
        query.append("update ").append(table).append(" set ");
        
        for (int i=0; i<setNames.length-1; i++)
            query.append(setNames[i]).append("=").append("'").append(setValues[i]).append("'").append(", ");
        query.append(setNames[setNames.length-1]).append("=").append("'").append(setValues[setValues.length-1]).append("'").append(" where ");
        
        for (int i=0; i<matchNames.length-1; i++)
            query.append(matchNames[i]).append("=").append("'").append(matchValues[i]).append("'").append(" and ");
        query.append(matchNames[matchNames.length-1]).append("=").append("'").append(matchValues[matchValues.length-1]).append("'");
        
        return executeUpdate(query.toString(), conn);
    }
    
    /** Select the values from the given table that satisfy the conditions specified
     *  by the arrays names and values. For example, if names[0] = age, values[0] = 12
     *  names[1] = sex values[1] = female, table = people, display[0]= name and
     *  display[1] = job then the query will look like
     *
     * select name, job from people where age=12 and sex=female
     *
     *  WARNING: string values must be enclosed in ' ' before being passed into the display,
     *  values, and names arrays!
     */
    public Object[] select(String table, String[] display, String[] names, String[] values, Connection conn) throws SQLException {
        if (names.length != values.length)
            return null;
        
        StringBuffer query = new StringBuffer();
        query.append("select ");
        
        for (int i=0; i<display.length-1; i++)
            query.append(display[i]).append(", ");
        query.append(display[display.length-1]).append(" from ").append(table).append(" where ");
        
        for (int i=0; i<names.length-1; i++)
            query.append(names[i]).append("=").append(values[i]).append(" and ");
        query.append(names[names.length-1]).append("=").append(values[values.length-1]);
        
        Object[] results = executeQuery(query.toString(), conn);
        return results;
    }
    
    /** Simpler select query that does not use the display array -- instead it uses the * wildcard */
    public Object[] select(String table, String[] names, String[] values, Connection conn) throws SQLException {
        String[] display = {"*"};
        return select(table, display, names, values, conn);
    }
    
    /** Select all query */
    public Object[] select(String table, Connection conn) throws SQLException {
        Object[] results = executeQuery("select * from " + table, conn);
        return results;
    }
    
    /** Checks if name='value' exists in the given table. If it does, returns its id. If not
     *  inserts it into the table and returns the generated id. Works only for tables that
     *  generate IDs, only for VARCHAR values, and only for tables that have only two columns:
     *  ID and some VARCHAR column (given by name) */
    public int insertIfDNE(String table, String name, String value) {
        Connection conn =null;
        Object[] results = null;
        try {
            conn = getConnection();
            
            String query = "select id from " + table + " where " + name + "='" + value + "'";
            results = executeQuery(query, conn);
            int id = -1;
            
            //Check if name=value is there -- if so, return its id
            if (results != null) {
                ResultSet rs = (ResultSet) results[0];
                if (!rs.wasNull()) {
                    rs.last();
                    int size = rs.getRow();
                    if (size > 0) {
                        id = rs.getInt("id");
                        
                        return id;
                    }
                }
            }
            
            //If the security was not found then add it and return the generated id
            String update = "insert into " + table + " values(0, '" + value + "')";
            results = executeUpdate(update, conn);
            ResultSet rs = (ResultSet) results[0];
            rs.next();
            id = rs.getInt(1);
            
            return id;
            
        }catch(SQLException e) {
            log.error("Failed to perform a 'add if does not exist' table insert: " + e);
            return -1;
        }finally{
            closeQuery(results, conn);
        }
    }
    
    private BasicDataSource ds;
    private Properties props;
    private String dbURL, dbUser, dbPassword;
    private int minNumConn;
    private int maxNumConn; 
    private boolean autoCommit; 
    private boolean readOnly;
    private long maxWait;

    public static int openConnCount =0;

    protected static final String DB_Host="localhost";
    protected static final String DB_User="jmarkets2";
    protected static final String DB_Name="jmarkets2";
    protected static final String DB_Password="banana";
    protected static final int DB_Port=3306;
    protected static final int Max_Active=30;
    protected static final int Min_Active=10;
    protected static final long Max_Wait=GenericObjectPool.DEFAULT_MAX_WAIT;
    protected static final boolean Default_AutoCommit=true;
    protected static final boolean Default_ReadOnly=false;
    
    public static Log log = LogFactory.getLog(DBConnector.class);
}
