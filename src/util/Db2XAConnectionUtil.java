package util;

import javax.sql.XAConnection;

public class Db2XAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.ibm.db2.jcc.DB2Driver";

    public static String serverNameDb2105 = "db17.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "50000";

    public Db2XAConnectionUtil() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder().dbType(DbType.DB2);
        super.setConnectionData(dataBuilder.build());
    }

    @Override
    public String getDriverClassName() {
        return driverClass;
    }

    @Override
    public XAConnection getXAConnection() {
        try {
            // Create the XA data source and XA ready connection.
            com.ibm.db2.jcc.DB2XADataSource ds = new com.ibm.db2.jcc.DB2XADataSource();
            ds.setUser(data.user());
            ds.setPassword(data.pass());
            ds.setServerName(data.host());
            ds.setPortNumber(data.portAsInt());
            ds.setDatabaseName(data.db());
            ds.setDriverType(4);
            return ds.getXAConnection();
        } catch (Exception e) {
            e.printStackTrace();
            new RuntimeException(getCreateXAConnectionErrorString(data), e);
        }
        return null;
    }
}
