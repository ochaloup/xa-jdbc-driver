package util;

import javax.sql.XAConnection;

public class SybaseXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.sybase.jdbc4.jdbc.SybDriver";

    public static String serverNameSybase157 = "db05.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "5000";

    public SybaseXAConnectionUtil() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameSybase157, defaultPort)
            .dbType(DbType.MARIADB);
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
            com.sybase.jdbc4.jdbc.SybXADataSource ds = new com.sybase.jdbc4.jdbc.SybXADataSource();
            ds.setUser(data.user());
            ds.setPassword(data.pass());
            ds.setServerName(data.server());
            ds.setPortNumber(data.portAsInt());
            ds.setDatabaseName(data.db());
            return ds.getXAConnection();
        } catch (Exception e) {
            new RuntimeException(getCreateXAConnectionErrorString(data), e);
        }
        return null;
    }
}
