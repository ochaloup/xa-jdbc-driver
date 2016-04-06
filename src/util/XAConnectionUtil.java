package util;

import java.sql.*;
import javax.sql.*;

public abstract class XAConnectionUtil {
    // static
    private static final String testTableName = "XA_MIN";
    // instance
    protected final ConnectionData data;

    // abstract
    public abstract String getDriverClassName();
    public abstract XAConnection getXAConnection();

    public XAConnectionUtil(ConnectionData data) {
        this.data = data;
    }

    public ConnectionData getConnectionData() {
        return this.data;
    }

    public Connection getConnection() {
        try {
            synchronized (XAConnectionUtil.class) {
                if (!isClassLoaded(getDriverClassName())) {
                    System.out.println("Loading driver class " + getDriverClassName());
                    Class.forName(getDriverClassName());
                }
            }
            return DriverManager.getConnection(data.url(), data.user(), data.pass());
        } catch (Exception e) {
            throw new RuntimeException("Can't get connection of " + data.url() + " ["
                    + data.user() + "," + data.pass() + "]", e);
        }
    }

    public boolean isDriverClassLoaded() throws Exception {
        return isClassLoaded(getDriverClassName());
    }

    public void createTestTable() {
        Connection con = getConnection();

        try {
            Statement stmt = con.createStatement();

            try {
                stmt.executeUpdate("DROP TABLE " + testTableName);
            } catch (Exception e) {
                // when table does not exist we ignore failure from DROP
            }


            stmt.executeUpdate("CREATE TABLE " + testTableName + " (f1 int, f2 " + getVarCharTypeSpec() + ")");
        } catch (Exception e) {
            String msgerr = String.format("Can't create table %s",
                    testTableName);
            throw new RuntimeException(msgerr, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public void createTestTableWithDrop(String tableName) {
        tableName = tableName == null || tableName.isEmpty() ? testTableName : tableName;

        Connection con = getConnection();
        try {
           // Create a test table.
           Statement stmt = con.createStatement();
           try {
              System.out.println("Dropping table '" + tableName + "'");
              stmt.executeUpdate("DROP TABLE " + tableName); 
           }
           catch (Exception e) {
              System.out.println("Dropping table " + tableName + " ended with error: " + e.getMessage());
           }

           System.out.println("Creating table '" + tableName + "'");
           stmt.executeUpdate("CREATE TABLE " + tableName + " (id int, value " + getVarCharTypeSpec() + ")");
           stmt.close();
        } catch (Exception e) {
           // Handle any errors that may have occurred.
           e.printStackTrace();
           throw new RuntimeException(e);
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    public String getVarCharTypeSpec() {
        switch(data.dbType()) {
            case ORACLE:
            case SYBASE:
            case DB2:
                return "varchar(3000)";
            case MSSQL:
                return "varchar(max)";
            case MYSQL:
            case MARIADB:
                return "text";
            default:
                return "varchar";
                        
        }
    }

    public ResultSet selectTestTable(Connection con) {
        try {
            // Open a new connection and read back the record to verify that it
            // worked.
            return con.createStatement().executeQuery(
                    "SELECT * FROM " + testTableName);
        } catch (Exception e) {
            String msgerr = String.format(
                    "Can't do select of table %s on connection %s",
                    testTableName, con);
            throw new RuntimeException(msgerr, e);
        }
    }

    public PreparedStatement insertTestTable(Connection con, int index,
            String value) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO "
                    + testTableName + " (f1,f2) VALUES (?, ?)");
            pstmt.setInt(1, index);
            pstmt.setString(2, value);
            return pstmt;
        } catch (Exception e) {
            String msgerr = String
                    .format("Can't create prepared statement for values '%s', '%s' on connection %s",
                            index, value, con);
            throw new RuntimeException(msgerr, e);
        }
    }

    protected String getCreateXAConnectionErrorString(ConnectionData connectionData) {
        return String.format("Can't create XA connection to: %s:%s %s/%s/%s", connectionData.server(),
            connectionData.port(), connectionData.db(), connectionData.user(), connectionData.pass());
    }

    private boolean isClassLoaded(String className) throws Exception {
        java.lang.reflect.Method m = ClassLoader.class.getDeclaredMethod(
                "findLoadedClass", new Class[] { String.class });
        m.setAccessible(true);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Object test1 = m.invoke(cl, className);
        return test1 != null;
    }
}
