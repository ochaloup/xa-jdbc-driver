package util;

import javax.sql.XAConnection;

public class PostgresPlusXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.edb.Driver";

    public static String serverNamePostgresPlus93 = "db21.mw.lab.eng.bos.redhat.com";
    public static String serverNamePostgresPlus94 = "gen-vm001.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "5432";

    private PostgresPlusXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static PostgresPlusXAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new PostgresPlusXAConnectionUtil(dataBuilder.postgresplus());
    }

    public static PostgresPlusXAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNamePostgresPlus94, defaultPort);
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
            com.edb.xa.PGXADataSource ds = new com.edb.xa.PGXADataSource();
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
