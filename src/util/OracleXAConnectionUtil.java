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
            if(data.url() != null && !data.url().isEmpty()) {
                ds.setURL(data.url());
            } else {
                ds.setServerName(data.host());
                ds.setPortNumber(data.portAsInt());
                ds.setDatabaseName(data.db());
            }

            ds.setUser(data.user());
            ds.setPassword(data.pass());
            return ds.getXAConnection();
        } catch (Exception e) {
            new RuntimeException(getCreateXAConnectionErrorString(data), e);
        }
        return null;
    }
}
