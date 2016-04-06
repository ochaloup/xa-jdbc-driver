package run;

import java.sql.Connection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import util.FactoryXAConnectionUtil;
import util.XAConnectionUtil;
import util.XidImpl;

/**
 * Closing connection during 1PC in progress. It checks how jdbc driver reacts
 * it means what error code of XAException will be returned back. 
 */
public class test1PCConnectionClose {

  private static String tableName = "foo1pc";

  public static void main(String[] args) throws Exception {

      XAConnectionUtil util = FactoryXAConnectionUtil.getInstance();
      System.out.println("Test is going to connect with following data: "
              + util.getConnectionData().toString());

      util.createTestTableWithDrop(tableName);

      // Get a unique Xid object for testing.
      Xid xid = XidImpl.getUniqueXid(1);

      {
          XAConnection xaConnection = util.getXAConnection();
          XAResource xaResource = xaConnection.getXAResource();
          xaResource.start(xid, XAResource.TMNOFLAGS);
          Connection connection = xaConnection.getConnection();
          String insertSql = "insert into " + tableName + " (id) values (1)";
          System.out.println("Running SQL: " + insertSql);
          connection.createStatement().execute(insertSql);
          xaResource.end(xid, XAResource.TMSUCCESS);

          // kill the connection now
          // this is just closing but for our internal testing we simulate
          // killing connection by proxying the connection and then hitting the proxy
          System.out.println("Simulation of trouble on the connection by closing it");
          xaConnection.close();
          
          try {
              xaResource.commit(xid, true); // true means onephase
          } catch (XAException xae) {
              int outcome = xae.errorCode;
              // http://docs.oracle.com/javase/7/docs/api/javax/transaction/xa/XAException.html#errorCode
              // http://docs.oracle.com/javase/7/docs/api/constant-values.html#javax.transaction
              System.out.println("XAER_RMERR is -3, XAER_RMFAIL is -7, XA_RETRY is 4");
              System.out.println("Outcome error code of the exception is: " + outcome + " with message:\n " + xae.getMessage());
              // xae.printStackTrace();

             /* From XA specification
              * ---------------------
              * The resource manager is not able to commit the transaction branch at this time.
              * This value may be returned when a blocking condition exists and TMNOWAIT was set.
              * Note, however, that this value may also be returned even when TMNOWAIT is not set
              * (for example, if the necessary stable storage is currently unavailable).
              * This value cannot be returned if TMONEPHASE is set in flags.
              * All resources held on behalf of xid remain in a prepared state until commitment is possible.
              * The transaction manager should reissue xa_commit() at a later time. 
              */
          }
      }

      {
          // checking database prepared connections now...
          XAConnection xaConnection = util.getXAConnection();
          XAResource xaResource = xaConnection.getXAResource();
          Xid[] recover = xaResource.recover(XAResource.TMSTARTRSCAN);
          // as no prepare happen there should be no elements here
          assert (recover == null || recover.length == 0);
          System.out.println("Number of elements got after recover: " + recover.length);
          xaResource.recover(XAResource.TMENDRSCAN);
          xaConnection.close();
      }
   }
}
