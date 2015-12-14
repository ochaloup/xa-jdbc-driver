package util;

public class ConnectionData {
    public static String SERVER_PARAM    = "host";
    public static String PORT_PARAM      = "port";
    public static String DATABASE_PARAM  = "database";
    public static String USER_PARAM      = "user";
    public static String PASSWORD_PARAM  = "password";

    private static String postgresqlUrlPrefix = "jdbc:postgresql://";
    
    private final String url, user, pass, db, server, port;

    /**
     * Use method {@link #url()} to instantiate this class.
     */
    private ConnectionData(String connectionUrl, Builder builder) {
        this.url = connectionUrl;
        this.user = builder.user;
        this.pass = builder.pass;
        this.db = builder.db;
        this.server = builder.server;
        this.port = builder.port;
    }

    public String url() {
        return url;
    }
    
    public String user() {
        return user;
    }
    
    public String pass() {
        return pass;
    }
    
    public String db() {
        return db;
    }
    
    public String server() {
        return server;
    }
    
    public String port() {
        return port;
    }
    
    public int portAsInt() {
        return Integer.parseInt(port);
    }

    public String toString() {
        return String.format("jdbc url: '%s', connection props: %s:%s %s/%s", url, server, port, user, pass);
    }

    public static class Builder {
        private final String server, port;
        private String db = System.getProperty(ConnectionData.DATABASE_PARAM, "crashrec"); 
        private String user = System.getProperty(ConnectionData.USER_PARAM, "crashrec");
        private String pass = System.getProperty(ConnectionData.PASSWORD_PARAM, "crashrec");

        public Builder() {
            this.server = System.getProperty(ConnectionData.SERVER_PARAM);
            this.port = System.getProperty(ConnectionData.PORT_PARAM);

            if(server == null || port == null) {
                throw new NullPointerException("host or port is not defined");
            }
        }

        /**
         * If system properties for server and port is not defined then default params are used.
         */
        public Builder(String defaultServer, String defaultPort) {
            this.server = System.getProperty(ConnectionData.SERVER_PARAM, defaultServer);
            this.port = System.getProperty(ConnectionData.PORT_PARAM, defaultPort);
        }
        
        public Builder user(String userName) {
            this.user = userName;
            return this;
        }
    
        public Builder pass(String password) {
            this.pass = password;
            return this;
        }
    
        public Builder db(String databaseName) {
            this.db = databaseName;
            return this;
        }

        public ConnectionData postgresql() {
            String connectionUrl = postgresqlUrlPrefix + server + ":" + port  + "/" + db;
            return new ConnectionData(connectionUrl, this);
        }
    }

}
