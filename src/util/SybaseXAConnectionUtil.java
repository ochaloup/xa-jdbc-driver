package util;

import javax.sql.XAConnection;

public class SybaseXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.sybase.jdbc4.jdbc.SybDriver";

    public static String serverNameSybase157 = "db05.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "5000";

    private SybaseXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static SybaseXAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new SybaseXAConnectionUtil(dataBuilder.sybase());
    }

    public static SybaseXAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameSybase157, defaultPort);
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
            com.sybase.jdbc4.jdbc.SybXADataSource ds = new com.sybase.jdbc4.jdbc.SybXADataSource();
            ds.setUser(data.user());
            ds.setPassword(data.pass());
            ds.setServerName(data.server());
            ds.setPortNumber(data.portAsInt());
            ds.setDatabaseName(data.db());
            return ds.getXAConnection();
        } catch (Exception e) {
            String msgerr = String.format(
                    "Can't create XA connection to: %s:%s %s/%s/%s", data.server(),
                    data.port(), data.db(), data.user(), data.pass());
            new RuntimeException(msgerr, e);
        }
        return null;
    }
}
