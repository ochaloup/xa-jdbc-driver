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
 * Closing connection during 2PC in progress. It checks how jdbc driver reacts
 * it means what error code of XAException will be returned back.
 */
public class testConnectionClose {

  private static String tableName = "foo";

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
          connection.createStatement().execute("insert into " + tableName + " (id) values (1)");
          xaResource.end(xid, XAResource.TMSUCCESS);
          int outcomeCode = xaResource.prepare(xid);
          assert (outcomeCode == XAResource.XA_OK);
          System.out.println("Outcome code of XAResource.prepare() is: " + outcomeCode);

          // kill the connection now
          // this is just closing but for our internal testing we simulate
          // killing connection by proxying the connection and then hitting the proxy
          System.out.println("Simulation of trouble on the connection by closing it");
          xaConnection.close();

          try {
              xaResource.commit(xid, false);
          } catch (XAException xae) {
              int outcome = xae.errorCode;
              // http://docs.oracle.com/javase/7/docs/api/javax/transaction/xa/XAException.html#errorCode
             // http://docs.oracle.com/javase/7/docs/api/constant-values.html#javax.transaction
             System.out.println("XAER_RMERR is -3, XAER_RMFAIL is -7, XA_RETRY is 4");
             System.out.println("Outcome error code of the exception is: " + outcome + " with message " + xae.getMessage());
          }
      }

      {
          // restore the database now...
          XAConnection xaConnection = util.getXAConnection();
          XAResource xaResource = xaConnection.getXAResource();
          Xid[] recover = xaResource.recover(XAResource.TMSTARTRSCAN);
          // As per spec, if the commit returns XAException.XAER_RMERR then there should be no elements here
          assert (recover == null || recover.length == 0);
          System.out.println("Number of elements got after recover: " + recover.length);
          xaResource.recover(XAResource.TMENDRSCAN);

          for(Xid xidToRollback: recover) {
             System.out.println("Checkig xid to rollback: " + xidToRollback);
             if(xidToRollback.equals(xid)) {
               try {
                 xaResource.rollback(xidToRollback);
               } catch (Exception e) {
                 System.out.println("Can't rollback xid: " + xidToRollback + " for reason " + e.getMessage());
               }
             }
          }

          xaConnection.close();
      }
   }
}
