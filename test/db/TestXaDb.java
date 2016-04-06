package db;

import org.junit.Test;

import util.ConnectionData;
import util.Db2XAConnectionUtil;
import util.FactoryXAConnectionUtil;
import util.MariaDBXAConnectionUtil;
import util.MssqlXAConnectionUtil;
import util.MySQLXAConnectionUtil;
import util.OracleXAConnectionUtil;
import util.PostgresPlusXAConnectionUtil;
import util.SybaseXAConnectionUtil;

public class TestXaDb {
    public static void presetPostgreSQL() {
        ConnectionData.Builder builder = new ConnectionData.Builder("localhost", "5432");
        FactoryXAConnectionUtil.usePostgreSQL();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }

    public static void presetPostgresPlus() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                PostgresPlusXAConnectionUtil.serverNamePostgresPlus94, PostgresPlusXAConnectionUtil.defaultPort);
        FactoryXAConnectionUtil.usePostgresPlus();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }
    
    public static void presetMssql() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MssqlXAConnectionUtil.serverNameMssql2014, MssqlXAConnectionUtil.defaultPort);
        FactoryXAConnectionUtil.useMssql();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }
    
    public static void presetOracle() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                OracleXAConnectionUtil.serverNameOracle12c, OracleXAConnectionUtil.defaultPort)
                .db("qaora12");
        FactoryXAConnectionUtil.useOracle();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }

    public static void presetSybase() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                SybaseXAConnectionUtil.serverNameSybase157, SybaseXAConnectionUtil.defaultPort)
                .db("crashrec2")
                .user("crashrec2")
                .pass("crashrec2");
        FactoryXAConnectionUtil.useSybase();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }
    
    public static void presetDb2() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
            Db2XAConnectionUtil.serverNameDb2105, Db2XAConnectionUtil.defaultPort)
            .db("jbossqa");
        FactoryXAConnectionUtil.useDb2();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }

    public static void presetMysql() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MySQLXAConnectionUtil.serverNameMysql57, MySQLXAConnectionUtil.defaultPort);
        FactoryXAConnectionUtil.useMysql();
        FactoryXAConnectionUtil.setDataBuilder(builder);
    }

    public static void presetMariaDB() {
        ConnectionData.Builder builder = new ConnectionData.Builder(
                MariaDBXAConnectionUtil.serverNameMariaDB55, MariaDBXAConnectionUtil.defaultPort);
        FactoryXAConnectionUtil.useMariaDb();
        FactoryXAConnectionUtil.setDataBuilder(builder);
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
        run.testXA.main(new String[]{});
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
    public void testRecover() throws Exception {
        preset();
        run.testRecover.main(new String[]{});
    }
}
