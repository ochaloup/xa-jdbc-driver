package util;

import javax.sql.XAConnection;

public class Db2XAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.ibm.db2.jcc.DB2Driver";

    public static String serverNameDb2105 = "db17.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "50000";

    private Db2XAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static Db2XAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new Db2XAConnectionUtil(dataBuilder.db2());
    }

    public static Db2XAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameDb2105, defaultPort);
        return instance(dataBuilder);
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
            ds.setServerName(data.server());
            ds.setPortNumber(data.portAsInt());
            ds.setDatabaseName(data.db());
            ds.setDriverType(4);
            return ds.getXAConnection();
        } catch (Exception e) {
            new RuntimeException(getCreateXAConnectionErrorString(data), e);
        }
        return null;
    }
}
