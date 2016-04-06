package util;

import javax.sql.XAConnection;
import oracle.jdbc.xa.client.OracleXADataSource;

public class OracleXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "oracle.jdbc.driver.OracleDriver";

    public static String serverNameOracle12c = "dev151.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "1521";

    private OracleXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static OracleXAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new OracleXAConnectionUtil(dataBuilder.oracle());
    }

    public static OracleXAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameOracle12c, defaultPort);
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
            OracleXADataSource ds = new OracleXADataSource();
            ds.setURL(data.url());
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
