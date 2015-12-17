package util;

import javax.sql.XAConnection;

public class MariaDBXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "org.mariadb.jdbc.Driver";

    public static String serverNameMariaDB55 = "db22.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "3306";

    private MariaDBXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static MariaDBXAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new MariaDBXAConnectionUtil(dataBuilder.mariadb());
    }

    public static MariaDBXAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameMariaDB55, defaultPort);
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
            org.mariadb.jdbc.MySQLDataSource ds = new org.mariadb.jdbc.MySQLDataSource();
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
