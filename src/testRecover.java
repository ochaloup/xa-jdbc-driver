import java.net.Inet4Address;
import java.sql.*;
import java.util.Random;
import javax.transaction.xa.*;
import javax.sql.*;
import java.util.Arrays;
import com.microsoft.sqlserver.jdbc.*;

public class testRecover {
  
  // Create variables for the connection string.
  private static String prefix = "jdbc:sqlserver://";

  // mssql2008R1
  // private static String serverName = System.getProperty("serverName", "vmg04.mw.lab.eng.bos.redhat.com");
  // mssql2008R2
  private static String serverName = System.getProperty("serverName", "mssql01.mw.lab.eng.bos.redhat.com");
  // mssql2012
  // private static String serverName = System.getProperty("serverName", "db06.mw.lab.eng.bos.redhat.com");
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

         // Create the XA data source and XA ready connection.
         SQLServerXADataSource ds = new SQLServerXADataSource();
         ds.setUser(user);
         ds.setPassword(password);
         ds.setServerName(serverName);
         ds.setPortNumber(Integer.parseInt(portNumber));
         ds.setDatabaseName(databaseName);
         XAConnection xaCon = ds.getXAConnection();
         XAResource xaRes = null;

         // trying to do recover here
         try {
           xaRes = xaCon.getXAResource();
           xaRes.setTransactionTimeout(0);
           Xid[] xids = xaRes.recover(XAResource.TMSTARTRSCAN);
          
           if (xids.length == 0) {
               System.out.println("There is no in-doubt transaction in database"); 
           } else {
               System.out.println("There is " + xids.length + " in-doubt transactions in database: " + Arrays.asList(xids));
           }
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
         xaCon.close();

      } catch (Exception e) {
         // Handle any errors that may have occurred.
         e.printStackTrace();
      }
   }
}
