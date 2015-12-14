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

    public MssqlXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static MssqlXAConnectionUtil instance() {
        ConnectionData conData = new ConnectionData.Builder(serverNameMssql2012, defaultPort).postgresql();
        return new MssqlXAConnectionUtil(conData);
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
            String msgerr = String.format(
                    "Can't create XA connection to: %s:%s %s/%s/%s", data.server(),
                    data.port(), data.db(), data.user(), data.pass());
            new RuntimeException(msgerr, e);
        }
        return null;
    }
}
