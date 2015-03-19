
import java.net.Inet4Address;
import java.sql.*;
import java.util.Random;
import javax.transaction.xa.*;
import javax.sql.*;
import com.microsoft.sqlserver.jdbc.*;

public class testConnectionCrash {

  // mssql2014
  private static String serverName = System.getProperty("serverName", "db18.mw.lab.eng.bos.redhat.com");
  // mssql2012
  private static String serverName = System.getProperty("serverName", "db06.mw.lab.eng.bos.redhat.com");
  private static String portNumber = System.getProperty("portNumber", "1433");
  private static String databaseName = System.getProperty("databaseName", "crashrec"); 
  private static String user = System.getProperty("user", "crashrec"); 
  private static String password = System.getProperty("password", "crashrec");
  private static String connectionUrl = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s",
       prefix, serverName, portNumber, databaseName, user, password);

  public static void main(String[] args) throws Exception {

      System.out.println("Going to connect with following connection url: " + connectionUrl);

      createTable();

      // Get a unique Xid object for testing.
      XAResource xaRes = null;
      Xid xid = null;
      xid = XidImpl.getUniqueXid(1);

      {
          XAConnection xaConnection = ds.getXAConnection();
          XAResource xaResource = xaConnection.getXAResource();
          xaResource.start(xid, XAResource.TMNOFLAGS);
          Connection connection = xaConnection.getConnection();
          connection.createStatement().execute("insert into foo (id) values (1)");
          xaResource.end(xid, XAResource.TMSUCCESS);
          assertEquals(xaResource.prepare(xid), XAResource.XA_OK);

          // kill the connection now
          // this is just closing but for our internal testing we simulate
          // killing connection by proxying the connection and then hitting the proxy
          connection.close();
          
          try {
              xaResource.commit(xid, false);
          } catch (XAException e) {
              int outcome = e.errorCode;
              // http://docs.oracle.com/javase/7/docs/api/javax/transaction/xa/XAException.html#errorCode
             System.out.println("Outcome error code of the exception is: " + outcome);
             // e.printStackTrace();
          }
      }

      {
          // restore the database now...
          XAConnection xaConnection = getDs.getXAConnection();
          XAResource xaResource = xaConnection.getXAResource();
          Xid[] recover = xaResource.recover(XAResource.TMSTARTRSCAN);
          // As per spec, if the commit returns XAException.XAER_RMERR then there should be no elements here
          assert (recover == null || recover.length == 0);
          System.out.println("Number of elements got after recover: " + recover.length);
          xaResource.recover(XAResource.TMENDRSCAN);
          xaConnection.close();
      }
   }

   private XADataSource getDs() {
     try {
        // Create the XA data source and XA ready connection.
        SQLServerXADataSource ds = new SQLServerXADataSource();
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServerName(serverName);
        ds.setPortNumber(Integer.parseInt(portNumber));
        ds.setDatabaseName(databaseName);
     } catch (Exception e) {
        int outcome = e.errorCode; 
        System.out.println("Outcome error code of the exception is: " + outcome);
     }
   }

   private void createTable() {
      try {
         // Establish the connection.
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         Connection con = DriverManager.getConnection(connectionUrl);

         // Create a test table.
         Statement stmt = con.createStatement();
         try {
            stmt.executeUpdate("DROP TABLE foo"); 
         }
         catch (Exception e) {
         }
         stmt.executeUpdate("CREATE TABLE foo (id int, value varchar(max))");
         stmt.close();
         con.close();
      } catch (Exception e) {
         // Handle any errors that may have occurred.
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }
}
