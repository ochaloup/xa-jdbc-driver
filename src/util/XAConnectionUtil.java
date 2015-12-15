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

            String varcharTypeSpec = "varchar";
            if(data.dbType() == DbType.ORACLE) varcharTypeSpec = "varchar(3000)";
            stmt.executeUpdate("CREATE TABLE " + testTableName + " (f1 int, f2 " + varcharTypeSpec + ")");
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

    private boolean isClassLoaded(String className) throws Exception {
        java.lang.reflect.Method m = ClassLoader.class.getDeclaredMethod(
                "findLoadedClass", new Class[] { String.class });
        m.setAccessible(true);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Object test1 = m.invoke(cl, className);
        return test1 != null;
    }
}
