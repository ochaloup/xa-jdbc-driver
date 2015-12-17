package util;

import javax.sql.XAConnection;

public class MySQLXAConnectionUtil extends XAConnectionUtil {
    private static final String driverClass = "com.mysql.jdbc.Driver";

    public static String serverNameMysql55 = "db-02.rhev-ci-vms.eng.rdu2.redhat.com";
    public static String serverNameMysql57 = "db19.mw.lab.eng.bos.redhat.com";
    public static String defaultPort = "3306";

    private MySQLXAConnectionUtil(ConnectionData data) {
        super(data);
    }

    public static MySQLXAConnectionUtil instance(ConnectionData.Builder dataBuilder) {
        return new MySQLXAConnectionUtil(dataBuilder.mysql());
    }

    public static MySQLXAConnectionUtil instance() {
        ConnectionData.Builder dataBuilder = new ConnectionData.Builder(serverNameMysql57, defaultPort);
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
            com.mysql.jdbc.jdbc2.optional.MysqlXADataSource ds = new com.mysql.jdbc.jdbc2.optional.MysqlXADataSource();
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
