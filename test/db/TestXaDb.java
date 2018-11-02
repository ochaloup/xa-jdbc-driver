package db;

import org.junit.Test;

import util.ConnectionData;
import util.Db2XAConnectionUtil;
import util.DbType;
import util.FactoryXAConnectionUtil;
import util.MariaDBXAConnectionUtil;
import util.MssqlXAConnectionUtil;
import util.MySQLXAConnectionUtil;
import util.OracleXAConnectionUtil;
import util.PostgresPlusXAConnectionUtil;
import util.SybaseXAConnectionUtil;

public class TestXaDb {
    public static void presetPostgreSQL() {
        ConnectionData.Builder builder = new ConnectionData.Builder("localhost", "5432")
            .dbType(DbType.POSTGRESQL);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetPostgresPlus() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                PostgresPlusXAConnectionUtil.serverNamePostgresPlus94, PostgresPlusXAConnectionUtil.defaultPort)
                .dbType(DbType.POSTGRESPLUS);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetMssql() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MssqlXAConnectionUtil.serverNameMssql2012, MssqlXAConnectionUtil.defaultPort)
                .dbType(DbType.MSSQL);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetOracle() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                OracleXAConnectionUtil.serverNameOracle12c, OracleXAConnectionUtil.defaultPort)
                .db("qaora12")
                .dbType(DbType.ORACLE);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetSybase() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                SybaseXAConnectionUtil.serverNameSybase157, SybaseXAConnectionUtil.defaultPort)
                .db("crashrec2")
                .user("crashrec2")
                .pass("crashrec2")
                .dbType(DbType.SYBASE);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetDb2() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
            Db2XAConnectionUtil.serverNameDb2105, Db2XAConnectionUtil.defaultPort)
            .db("jbossqa")
            .dbType(DbType.DB2);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetMysql() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MySQLXAConnectionUtil.serverNameMysql57, MySQLXAConnectionUtil.defaultPort)
                .dbType(DbType.MYSQL);
        FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void presetMariaDB() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MariaDBXAConnectionUtil.serverNameMariaDB55, MariaDBXAConnectionUtil.defaultPort)
                .dbType(DbType.MARIADB);
            FactoryXAConnectionUtil.setConnectionData(builder.build());
    }

    public static void preset() {
        // presetPostgresPlus();
        // presetMssql();
        // presetPostgreSQL();
        // presetOracle();
        // presetSybase();
        // presetMysql();
        // presetMariaDB();
        presetDb2();
    }

    @Test
    public void testXA() throws Exception {
        preset();
        run.test2PC.main(new String[]{});
    }

    @Test
    public void testXARollback() throws Exception {
        preset();
        run.testXARollback.main(new String[]{});
    }

    @Test
    public void testConnectionClose() throws Exception {
        preset();
        run.testConnectionClose.main(new String[]{});
    }

    @Test
    public void test1PCConnectionClose() throws Exception {
        preset();
        run.test1PCConnectionClose.main(new String[]{});
    }

    @Test
    public void test1PCConnectionProxy() throws Exception {
        preset();
        run.test1PCConnectionProxy.main(new String[]{});
    }

    @Test
    public void testRecover() throws Exception {
        preset();
        run.testRecover.main(new String[]{});
    }
}
