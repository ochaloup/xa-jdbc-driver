package util;

import javax.sql.XAConnection;
import oracle.jdbc.xa.client.OracleXADataSource;

public class OracleXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "oracle.jdbc.driver.OracleDriver";

    public static String serverNameOracle12c = "dev151.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "1521";

    public OracleXAConnectionUtil() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder().dbType(DbType.ORACLE);
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
