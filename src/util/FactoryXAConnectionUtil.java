package util;

public class FactoryXAConnectionUtil {
    private static String DB_TYPE_STRING = System.getProperty(ConnectionData.DBTYPE_PARAM, DbType.POSTGRESQL.name());
    private static DbType DB_TYPE = DbType.valueOf(DB_TYPE_STRING.toUpperCase());
    private static ConnectionData connectionData;

    public static XAConnectionUtil getInstance() {
        if(connectionData != null) DB_TYPE = connectionData.dbType();
        return buildInstance(connectionData);
    }

    public static XAConnectionUtil getInstance(String host, String port) {
        ConnectionData cd = connectionData;
        if(cd == null) {
            cd = buildInstance(null).getConnectionData();
        }

        cd = new ConnectionData.Builder(host, port)
            .db(cd.db())
            .dbType(cd.dbType())
            .user(cd.user())
            .pass(cd.pass())
            .build();
        return buildInstance(cd);
    }

    public static void setConnectionData(ConnectionData connectionData) {
        FactoryXAConnectionUtil.connectionData = connectionData;
    }

    public static void unsetDataBuilder() {
        FactoryXAConnectionUtil.connectionData = null;
    }

    public static Class<? extends XAConnectionUtil> getXAConnectionUtilClass(DbType dbType) {
        switch (dbType) {
            case MSSQL:
                return MssqlXAConnectionUtil.class;
            case POSTGRESQL:
                return PostgreSQLXAConnectionUtil.class;
            case POSTGRESPLUS:
                return PostgresPlusXAConnectionUtil.class;
            case ORACLE:
                return OracleXAConnectionUtil.class;
            case SYBASE:
                return SybaseXAConnectionUtil.class;
            case DB2:
                return Db2XAConnectionUtil.class;
            case MYSQL:
                return MySQLXAConnectionUtil.class;
            case MARIADB:
                return MariaDBXAConnectionUtil.class;
            default:
                throw new IllegalStateException("Not known dbtype" + DB_TYPE.name());
        }
    }

    private static XAConnectionUtil buildInstance(ConnectionData connectionData) {
        Class<? extends XAConnectionUtil> clazz = getXAConnectionUtilClass(DB_TYPE);
        try {
            XAConnectionUtil xaConnectionUtil = clazz.newInstance();
            if(connectionData != null) {
                xaConnectionUtil.setConnectionData(connectionData);
            }
            return xaConnectionUtil;
        } catch (Exception e) {
            throw new IllegalStateException("Can't create instance of " + clazz.getName(), e);
        }
    }
}
