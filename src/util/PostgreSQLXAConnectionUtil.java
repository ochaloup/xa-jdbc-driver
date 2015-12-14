package util;

import javax.sql.XAConnection;

import org.postgresql.xa.PGXADataSource;

public class PostgreSQLXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "org.postgresql.Driver";

    public PostgreSQLXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static PostgreSQLXAConnectionUtil instance() {
        ConnectionData conData = new ConnectionData.Builder("localhost", "5432").postgresql();
        return new PostgreSQLXAConnectionUtil(conData);
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
            String msgerr = String.format(
                    "Can't create XA connection to: %s:%s %s/%s/%s", data.server(),
                    data.port(), data.db(), data.user(), data.pass());
            new RuntimeException(msgerr, e);
        }
        return null;
    }
}
