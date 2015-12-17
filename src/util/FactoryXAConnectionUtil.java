package util;

public class FactoryXAConnectionUtil {
    private static String DB_TYPE_STRING = System.getProperty(ConnectionData.DBTYPE_PARAM, DbType.POSTGRESQL.name());
    private static DbType DB_TYPE = DbType.valueOf(DB_TYPE_STRING.toUpperCase());
    private static ConnectionData.Builder dataBuilder;

    public static XAConnectionUtil getInstance() {
        if(dataBuilder == null) return defaultInstance();
        return builderInstance(FactoryXAConnectionUtil.dataBuilder);
    }
    
    private static XAConnectionUtil defaultInstance() {
        switch (DB_TYPE) {
            case MSSQL:
                return MssqlXAConnectionUtil.instance();
            case POSTGRESQL:
                return PostgreSQLXAConnectionUtil.instance();
            case POSTGRESPLUS:
                return PostgresPlusXAConnectionUtil.instance();
            case ORACLE:
                return OracleXAConnectionUtil.instance();
            case SYBASE:
                return SybaseXAConnectionUtil.instance();
            case MYSQL:
                return MySQLXAConnectionUtil.instance();
            case MARIADB:
                return MariaDBXAConnectionUtil.instance();
            default:
                throw new IllegalStateException("Not known dbtype" + DB_TYPE.name());
        }
    }

    private static XAConnectionUtil builderInstance(ConnectionData.Builder dataBuilder) {
        switch (DB_TYPE) {
            case MSSQL:
                return MssqlXAConnectionUtil.instance(dataBuilder);
            case POSTGRESQL:
                return PostgreSQLXAConnectionUtil.instance(dataBuilder);
            case POSTGRESPLUS:
                return PostgresPlusXAConnectionUtil.instance(dataBuilder);
            case ORACLE:
                return OracleXAConnectionUtil.instance(dataBuilder);
            case SYBASE:
                return SybaseXAConnectionUtil.instance(dataBuilder);
            case MYSQL:
                return MySQLXAConnectionUtil.instance(dataBuilder);
            case MARIADB:
                return MariaDBXAConnectionUtil.instance(dataBuilder);
            default:
                throw new IllegalStateException("Not known dbtype" + DB_TYPE.name());
        }
    }

    public static void useMssql() {
        DB_TYPE = DbType.MSSQL;
    }
    
    public static void usePostgreSQL() {
        DB_TYPE = DbType.POSTGRESQL;
    }
    
    public static void usePostgresPlus() {
        DB_TYPE = DbType.POSTGRESPLUS;
    }
    
    public static void useOracle() {
        DB_TYPE = DbType.ORACLE;
    }

    public static void useSybase() {
        DB_TYPE = DbType.SYBASE;
    }
    
    public static void useMysql() {
        DB_TYPE = DbType.MYSQL;
    }
    
    public static void useMariaDb() {
        DB_TYPE = DbType.MARIADB;
    }

    public static void setDataBuilder(ConnectionData.Builder builder) {
        FactoryXAConnectionUtil.dataBuilder = builder; 
    }

    public static void unsetDataBuilder() {
        FactoryXAConnectionUtil.dataBuilder = null;
    }
}
