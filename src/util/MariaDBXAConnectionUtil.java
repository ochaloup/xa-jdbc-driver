package util;

import javax.sql.XAConnection;

public class MariaDBXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "org.mariadb.jdbc.Driver";

    public static String serverNameMariaDB55 = "db22.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "3306";

    public MariaDBXAConnectionUtil() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder().dbType(DbType.MARIADB);
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
            org.mariadb.jdbc.MySQLDataSource ds = new org.mariadb.jdbc.MySQLDataSource();
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
