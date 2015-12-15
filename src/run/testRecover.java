package run;

import javax.transaction.xa.*;
import javax.sql.*;
import java.util.Arrays;
import util.FactoryXAConnectionUtil;
import util.XAConnectionUtil;

/**
 * Trying to call function <code>recover</code> to see
 *   1. if works
 *   2. it returns information about some in-doubt transactions
 *
 */
public class testRecover {

  public static void main(String[] args) throws Exception {

      try {
          XAConnectionUtil util = FactoryXAConnectionUtil.getInstance();
          System.out.println("Test is going to connect with following data: "
                  + util.getConnectionData().toString());

          // Create a test table.
          util.createTestTable();

          // XA connection to get xa resource
          XAConnection xaCon = util.getXAConnection();

          // Get a unique Xid object for testing.
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
