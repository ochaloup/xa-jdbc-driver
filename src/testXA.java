import java.net.Inet4Address;
import java.sql.*;
import java.util.Random;
import javax.transaction.xa.*;
import javax.sql.*;
import com.microsoft.sqlserver.jdbc.*;

public class testXA {
  
  // Create variables for the connection string.
  private static String prefix = "jdbc:sqlserver://";

  // mssql2008R1
  // private static String serverName = System.getProperty("serverName", "vmg04.mw.lab.eng.bos.redhat.com");
  // mssql2008R2
  // private static String serverName = System.getProperty("serverName", "mssql01.mw.lab.eng.bos.redhat.com");
  // mssql2012
  private static String serverName = System.getProperty("serverName", "db06.mw.lab.eng.bos.redhat.com");
  // mssql2014
  // private static String serverName = System.getProperty("serverName", "db18.mw.lab.eng.bos.redhat.com");


  private static String portNumber = System.getProperty("portNumber", "1433");
  private static String databaseName = System.getProperty("databaseName", "crashrec"); 
  private static String user = System.getProperty("user", "crashrec"); 
  private static String password = System.getProperty("password", "crashrec");
  private static String connectionUrl = prefix + serverName + ":" + portNumber
     + ";databaseName=" + databaseName + ";user=" + user + ";password=" + password;

  public static void main(String[] args) throws Exception {

      try {
         System.out.println("Test is going to connect with following connection url:\n" + connectionUrl);

         // Establish the connection.
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         Connection con = DriverManager.getConnection(connectionUrl);

         // Create a test table.
         Statement stmt = con.createStatement();
         try {
            stmt.executeUpdate("DROP TABLE XAMin"); 
         }
         catch (Exception e) {
         }
         stmt.executeUpdate("CREATE TABLE XAMin (f1 int, f2 varchar(max))");
         stmt.close();
         con.close();

         // Create the XA data source and XA ready connection.
         SQLServerXADataSource ds = new SQLServerXADataSource();
         ds.setUser(user);
         ds.setPassword(password);
         ds.setServerName(serverName);
         ds.setPortNumber(Integer.parseInt(portNumber));
         ds.setDatabaseName(databaseName);
         XAConnection xaCon = ds.getXAConnection();
         con = xaCon.getConnection();

         // Get a unique Xid object for testing.
         XAResource xaRes = null;
         Xid xid = null;
         xid = XidImpl.getUniqueXid(1);

         // Get the XAResource object and set the timeout value.
         xaRes = xaCon.getXAResource();
         xaRes.setTransactionTimeout(0);

         // Perform the XA transaction.
         System.out.println("Write -> xid = " + xid.toString());
         xaRes.start(xid,XAResource.TMNOFLAGS);
         PreparedStatement pstmt = 
         con.prepareStatement("INSERT INTO XAMin (f1,f2) VALUES (?, ?)");
         pstmt.setInt(1,1);
         pstmt.setString(2,xid.toString());
         pstmt.executeUpdate();

         // Commit the transaction.
         xaRes.end(xid,XAResource.TMSUCCESS);
         xaRes.commit(xid,true);

         // trying to do recover here
         try {
           xaRes.recover(XAResource.TMSTARTRSCAN);
         } catch (XAException e) {
           e.printStackTrace();
           try {
             xaRes.recover(XAResource.TMENDRSCAN);
           }
           catch (Exception e1) {
             e1.printStackTrace();
           }
         }

         // Cleanup.
         con.close();
         xaCon.close();

         // Open a new connection and read back the record to verify that it worked.
         con = DriverManager.getConnection(connectionUrl);
         ResultSet rs = con.createStatement().executeQuery("SELECT * FROM XAMin");
         rs.next();
         System.out.println("Read -> xid = " + rs.getString(2));
         rs.close();
         con.close();
      } 

      // Handle any errors that may have occurred.
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
