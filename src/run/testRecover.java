package run;

import java.util.Arrays;
import java.util.List;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

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
	  List<String> argsList = Arrays.asList(args);

	  boolean isDropTable = argsList.contains("-Ddrop.table") || System.getProperty("drop.table") != null;
	  boolean isRollback = argsList.contains("-Drollback") || System.getProperty("rollback") != null;

	  Xid[] xids = new Xid[] {};

      try {
          XAConnectionUtil util = FactoryXAConnectionUtil.getInstance();
          System.out.println("Test is going to connect with following data: "
                  + util.getConnectionData().toString());

          // Drop and create a test table
          if(isDropTable)
        	  util.createTestTable();

          // XA connection to get xa resource
          XAConnection xaCon = util.getXAConnection();

          // Get a unique Xid object for testing.
          XAResource xaRes = null;

         // trying to do recover here
         try {
           xaRes = xaCon.getXAResource();
           xaRes.setTransactionTimeout(0);
           xids = xaRes.recover(XAResource.TMSTARTRSCAN);
          
           if (xids.length == 0) {
               System.out.println("There is no in-doubt transaction in database"); 
           } else {
               System.out.printf("There is %s in-doubt transactions in database.%nListing: %s%n",
            		   xids.length, Arrays.asList(xids));
           }
         } catch (XAException e) {
        	 System.err.println("Can't recover at connection " + util.getConnectionData());
             e.printStackTrace();
         } finally {
           try {
        	   if(xaRes != null)
        		   xaRes.recover(XAResource.TMENDRSCAN);
           }
           catch (Exception e1) {
        	   System.err.println("Can't end recovery scan with TMENDRSCAN at " + util.getConnectionData());
        	   e1.printStackTrace();
           }
         }

         // we will try to rollback all transaction which were recovered
         if(isRollback) {
	         for(Xid xid: xids) {
	        	 try {
		        	 System.out.printf("Rollbacking: %s%n", xid);
		        	 xaRes.rollback(xid);
	        	 } catch (Exception e) {
	        		 System.err.printf("Can't rollback Xid: %s%n", xid);
	        		 e.printStackTrace();
	        	 }
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
;