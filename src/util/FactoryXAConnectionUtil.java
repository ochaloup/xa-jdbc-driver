package util;

public class FactoryXAConnectionUtil {
    private static String DB_TYPE_STRING = System.getProperty("dbtype", DbType.POSTGRESQL.name());
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

    public static void setDataBuilder(ConnectionData.Builder builder) {
        FactoryXAConnectionUtil.dataBuilder = builder; 
    }

    public static void unsetDataBuilder() {
        FactoryXAConnectionUtil.dataBuilder = null;
    }
}
