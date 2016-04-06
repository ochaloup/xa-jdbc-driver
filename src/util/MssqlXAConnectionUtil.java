package util;

import javax.sql.XAConnection;

import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;

public class MssqlXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static String serverNameMssql2008R1 = "vmg04.mw.lab.eng.bos.redhat.com";
    public static String serverNameMssql2008R2 = "mssql01.mw.lab.eng.bos.redhat.com";
    public static String serverNameMssql2012 = "db06.mw.lab.eng.bos.redhat.com";
    public static String serverNameMssql2014 = "db18.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "1433";

    public MssqlXAConnectionUtil() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder().dbType(DbType.MSSQL);
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
            SQLServerXADataSource ds = new SQLServerXADataSource();
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
