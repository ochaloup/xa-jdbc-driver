import java.net.Inet4Address;
import java.sql.*;
import java.util.Random;
import javax.transaction.xa.*;
import javax.sql.*;
import org.postgresql.*;
import org.postgresql.xa.*;
import org.postgresql.Driver;

public class XAConnectionUtil {
  private static final String driverClass = "org.postgresql.Driver";
  private static final String testTableName = "XA_MIN";

  private String url, user, pass, db, server, port;

  public XAConnectionUtil(String connectionUrl) {
    url = connectionUrl;
  }

  public XAConnectionUtil connectionUrl(String connectionUrl) {
    this.url = connectionUrl;
    return this;
  }
  public XAConnectionUtil userName(String userName) {
    this.user = userName;
    return this;
  }
  public XAConnectionUtil password(String password) {
    this.pass = password;
    return this;
  }
  public XAConnectionUtil databaseName(String databaseName) {
    this.db = databaseName;
    return this;
  }
  public XAConnectionUtil serverName(String serverName) {
    this.server = serverName;
    return this;
  }
  public XAConnectionUtil portNumber(String portNumber) {
    this.port = portNumber;
    return this;
  }

  public Connection getConnection() {
    try {
      synchronized(XAConnectionUtil.class) {
        if(!isClassLoaded(driverClass)) {
          Class.forName(driverClass);
        }
      }
      return DriverManager.getConnection(url, user, pass);
    } catch (Exception e) {
      throw new RuntimeException("Can't get connection of " + url + " [" + user + "," + pass + "]", e);
    }
  }

  public boolean isDriverClassLoaded() throws Exception {
    return isClassLoaded(driverClass);
  }

  public void createTestTable() {
    Connection con = getConnection();

    try {
      Statement stmt = con.createStatement();

      try {
        stmt.executeUpdate("DROP TABLE " + testTableName); 
      }
      catch (Exception e) {
        // when table does not exist we ignore failure from DROP
      }

      stmt.executeUpdate("CREATE TABLE " + testTableName + " (f1 int, f2 varchar)");
    } catch (Exception e) {
      String msgerr = String.format("Can't create table %s", testTableName);
      throw new RuntimeException(msgerr, e);
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

  public ResultSet selectTestTable(Connection con) {
    try {
      // Open a new connection and read back the record to verify that it worked.
      return con.createStatement().executeQuery("SELECT * FROM " + testTableName);
    } catch (Exception e) {
      String msgerr = String.format("Can't do select of table %s on connection %s",
        testTableName, con);
      throw new RuntimeException(msgerr, e);
    }
  }

  public PreparedStatement insertTestTable(Connection con, int index, String value) {
    try {
      PreparedStatement pstmt = 
        con.prepareStatement("INSERT INTO " + testTableName + " (f1,f2) VALUES (?, ?)");
      pstmt.setInt(1,index);
      pstmt.setString(2, value);
      return pstmt;
    } catch (Exception e) {
      String msgerr = String.format("Can't create prepared statement for values '%s', '%s' on connection %s",
        index, value, con);
      throw new RuntimeException(msgerr, e);
    }
  }

  public XAConnection getXAConnection() {
    try {
      // Create the XA data source and XA ready connection.
      PGXADataSource ds = new PGXADataSource();
      ds.setUser(user);
      ds.setPassword(pass);
      ds.setServerName(server);
      ds.setPortNumber(Integer.parseInt(port));
      ds.setDatabaseName(db);
      return ds.getXAConnection();
    } catch (Exception e) {
      String msgerr = String.format("Can't create XA connection to: %s:%s %s/%s/%s", server, port, db, user, pass);
      new RuntimeException(msgerr, e);
    }
    return null;
  }

  private boolean isClassLoaded(String className) throws Exception {
    java.lang.reflect.Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] { String.class });
    m.setAccessible(true);
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    Object test1 = m.invoke(cl, className);
    return test1 != null;
  }
}
