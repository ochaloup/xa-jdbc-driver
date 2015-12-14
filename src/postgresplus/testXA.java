package postgresplus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import util.*;

public class testXA {

    public static void main(String[] args) throws Exception {

        try {
            XAConnectionUtil util = PostgresPlusXAConnectionUtil.instance();
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

            // Commit the transaction.
            xaRes.end(xid, XAResource.TMSUCCESS);
            xaRes.commit(xid, true);

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
