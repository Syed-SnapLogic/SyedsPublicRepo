package com.snaplogic.snaps.oracledemo;

import com.google.inject.Inject;
import com.snaplogic.account.api.capabilities.Accounts;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.ExpressionProperty;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@General(title = "Oracle Select", purpose = "To execute Oracle Select queries")
@Inputs(min = 0, max = 1, accepts = { ViewType.DOCUMENT })
@Version(snap = 1)
@Accounts(provides = OracleAccount.class)
@Outputs(min = 1, max = 1, offers = { ViewType.DOCUMENT })
@Category(snap = SnapCategory.READ)
public class OracleSelect extends SimpleSnap {
    private final static Logger LOGGER = LoggerFactory.getLogger(OracleSelect.class);
    private final static String KEY_QUERY = "query";

    private String query;
    private ExpressionProperty queryExpression;
    @Inject
    private OracleAccount account;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(KEY_QUERY, "Query", "Select statement to be executed")
                .required()
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .defaultValue("Select 1")
                .add();
    }

    @Override
    public void configure(final PropertyValues propertyValues) {
        queryExpression = propertyValues.getAsExpression(KEY_QUERY);
    }

    @Override
    public void process(Document document, String inputView) {
        query = queryExpression.eval(document);
        executeQuery(query, document);
    }

    private void executeQuery(String query, Document document) {
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Connection connection = account.connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            int colCount = metaData.getColumnCount();
            String[] columns = new String[colCount];
            for (int i = 1; i <= colCount; ++i) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            while (resultSet.next()) {
                Map<String, Object> record = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    record.put(columns[i - 1], resultSet.getObject(i));
                }
                outputViews.write(documentUtility.newDocument(record));
            }
        } catch (SQLException e1) {
            LOGGER.warn("Exception occurred while executing query {}", query, e1);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e2) {
                // NO OP
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e2) {
                // NO OP
            }
        }
    }

    @Override
    public void cleanup() {
        account.disconnect();
    }
}
