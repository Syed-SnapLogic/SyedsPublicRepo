package com.snaplogic.snaps.oracledemo;

import com.google.api.client.util.Lists;
import com.snaplogic.account.api.Account;
import com.snaplogic.account.api.AccountType;
import com.snaplogic.account.api.ValidatableAccount;
import com.snaplogic.account.api.capabilities.AccountCategory;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Version;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@General(title = "Oracle Account")
@Version(snap = 1)
@AccountCategory(type = AccountType.DATABASE)
public class OracleAccount implements Account<Connection>, ValidatableAccount<Connection> {
    private final static String KEY_DB_HOST = "dbHost";
    private final static String KEY_DB_PORT = "dbPort";
    private final static String KEY_DB_NAME = "dbName";
    private final static String KEY_DB_USER = "dbUser";
    private final static String KEY_DB_PASSWORD = "dbPassword";
    private final static String KEY_DRIVER_CLASS = "driverClass";
    private final static String DEFAULT_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    private final static String JDBC_URL = "jdbc:oracle:thin:@%s:%d/%s";
    private final static Logger LOGGER = LoggerFactory.getLogger(OracleAccount.class);

    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String driverClass;
    private List<Connection> connectionList = Lists.newArrayList();
    private String jdbcUrl;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(KEY_DB_HOST, "Database Host/Server",
                "IP Address or domain name of the oracle db server")
                .required()
                .add();
        propertyBuilder.describe(KEY_DB_PORT, "Database Port",
                "Port number of the oracle db server")
                .required()
                .add();
        propertyBuilder.describe(KEY_DB_NAME, "Database Name",
                "Name of the database to connect to on oracle db server")
                .required()
                .add();
        propertyBuilder.describe(KEY_DB_USER, "Database User",
                "Name of the database user to be used to login")
                .required()
                .add();
        propertyBuilder.describe(KEY_DB_PASSWORD, "Password", "User's password to login")
                .required()
                .obfuscate()
                .add();
        propertyBuilder.describe(KEY_DRIVER_CLASS, "Driver class", "JDBC Driver class for Oracle")
                .defaultValue(DEFAULT_DRIVER_CLASS)
                .add();
    }

    @Override
    public void configure(final PropertyValues propertyValues) {
        dbHost = propertyValues.get(KEY_DB_HOST);
        dbPort = Integer.parseInt(propertyValues.get(KEY_DB_PORT));
        dbName = propertyValues.get(KEY_DB_NAME);
        dbUser = propertyValues.get(KEY_DB_USER);
        dbPassword = propertyValues.get(KEY_DB_PASSWORD);
        driverClass = propertyValues.get(KEY_DRIVER_CLASS);

        if (dbPort <= 0) {
            throw new ConfigurationException("Configuration error")
                    .withReason("Invalid port " + dbPort)
                    .withResolution("Ensure the port number is a positive integer");
        }
        if (StringUtils.isBlank(driverClass)) {
            driverClass = DEFAULT_DRIVER_CLASS;
        }
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e1) {
            throw new ConfigurationException(e1, "Configuration error")
                    .withReason("Unable to load the driver class " + driverClass)
                    .withResolution("Ensure the driver class provided is valid");
        }
        jdbcUrl = String.format(JDBC_URL, dbHost, dbPort, dbName);
    }

    @Override
    public Connection connect() {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            connectionList.add(connection);
            return connection;
        } catch (SQLException e1) {
            throw new ExecutionException(e1, "Unable to connect to the database")
                    .withReason(e1.getMessage())
                    .withResolution("Ensure the connection details provided are valid");
        }
    }

    @Override
    public void disconnect() {
        for (Connection connection : connectionList) {
            try {
                connection.close();
            } catch (SQLException e1) {
                LOGGER.warn("Error closing connection");
            }
        }
    }
}