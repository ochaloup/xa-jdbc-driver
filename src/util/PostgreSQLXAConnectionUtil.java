package util;

import javax.sql.XAConnection;

import org.postgresql.xa.PGXADataSource;

public class PostgreSQLXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "org.postgresql.Driver";

    public static String serverNamePostgreSQLLocalhost = "localhost";
    public static String serverNamePostgreSQL93 = "db20.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "5432";

    public PostgreSQLXAConnectionUtil() {
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
            PGXADataSource ds = new PGXADataSource();
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
