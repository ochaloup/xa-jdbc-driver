import java.net.Inet4Address;
import java.sql.*;
import java.util.Random;
import javax.transaction.xa.*;
import javax.sql.*;
import org.postgresql.*;
import org.postgresql.xa.*;
import org.postgresql.Driver;

public class testXARollback {
  
  // Create variables for the connection string.
  private static String prefix = "jdbc:postgresql://";

  // localhost
  private static String serverName = System.getProperty("serverName", "localhost");

  private static String portNumber = System.getProperty("portNumber", "5432");
  private static String databaseName = System.getProperty("databaseName", "crashrec"); 
  private static String user = System.getProperty("user", "crashrec"); 
  private static String password = System.getProperty("password", "crashrec");
  private static String connectionUrl = prefix + serverName + ":" + portNumber + "/" + databaseName;

  public static void main(String[] args) throws Exception {

      try {
         System.out.println("Test is going to connect with following connection url:\n'" + connectionUrl +
                "' with username '" + user + "' and password '" + password);
         XAConnectionUtil util = new XAConnectionUtil(connectionUrl)
           .userName(user)
           .password(password)
           .databaseName(databaseName)
           .serverName(serverName)
           .portNumber(portNumber);

         // Create a test table.
         util.createTestTable();

         // XA connection to get xa resource
         XAConnection xaCon = util.getXAConnection();
         Connection con = xaCon.getConnection();

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
         PreparedStatement pstmt = util.insertTestTable(con, 1, xid.toString()); 
         pstmt.executeUpdate();

         // Rollback the transaction.
         xaRes.end(xid,XAResource.TMSUCCESS);
         System.out.println("Rollbacking xid: [" + xid.toString() + "]");
         xaRes.rollback(xid);

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

         // a try if it's possible to run twice rollback on the same xid
         System.out.println("Second rollback of xid: [" + xid.toString() + "]");
         xaRes.rollback(xid);

         // Cleanup.
         con.close();
         xaCon.close();

         con = util.getConnection();
         try {
           ResultSet rs = util.selectTestTable(con);
           rs.next();
           System.out.println("Read -> xid = " + rs.getString(2));
           rs.close();
         } finally {
           if(con != null) con.close();
         }
      } 

      // Handle any errors that may have occurred.
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
