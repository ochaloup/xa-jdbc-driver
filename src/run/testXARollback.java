package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import util.FactoryXAConnectionUtil;
import util.XAConnectionUtil;
import util.XidImpl;

/**
 * Simple XA transaction started but then rollbacked.
 */
public class testXARollback {

    public static void main(String[] args) throws Exception {

        try {
            XAConnectionUtil util = FactoryXAConnectionUtil.getInstance();
            System.out.println("Test is going to connect with following data: "
                    + util.getConnectionData().toString());

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
            xaRes.start(xid, XAResource.TMNOFLAGS);
            PreparedStatement pstmt = util.insertTestTable(con, 1,
                    xid.toString());
            pstmt.executeUpdate();

            // Rollback the transaction.
            xaRes.end(xid, XAResource.TMSUCCESS);
            System.out.println("Rollbacking xid: [" + xid.toString() + "]");
            xaRes.rollback(xid);

            // trying to do recover here
            try {
                xaRes.recover(XAResource.TMSTARTRSCAN);
            } catch (XAException e) {
                e.printStackTrace();
                try {
                    xaRes.recover(XAResource.TMENDRSCAN);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            try {
                // a try if it's possible to run twice rollback on the same xid
                System.out.println("Second rollback of xid: [" + xid.toString()
                        + "]");
                xaRes.rollback(xid);
            } catch (XAException xae) {
                if (xae.errorCode == XAException.XAER_NOTA) {
                    System.out.println("[ERROR] Second rollback failed with exception: " + xae.getMessage()
                            + " with correct error code " + xae.errorCode);
                } else {
                    System.out.println("[ERROR] Second rollback failed with exception: " + xae.getMessage()
                        + " with incorrect error code " + xae.errorCode + "expected XAException.XAER_NOTA: " + XAException.XAER_NOTA);
                    throw xae;
                }
            }

            // Cleanup.
            con.close();
            xaCon.close();

            con = util.getConnection();
            try {
                ResultSet rs = util.selectTestTable(con);
                boolean isResultSet = rs.next();
                if (!isResultSet)
                    System.out
                            .println("[OK] There is no data in test table as txn was rollbacked");
                rs.close();
            } finally {
                if (con != null)
                    con.close();
            }
        }

        // Handle any errors that may have occurred.
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
