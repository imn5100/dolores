package com.shaw.dolores.listener;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;
import java.util.stream.Collectors;

@Log4j
public class SQLiteListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
        ConfigurableEnvironment configurableEnvironment = applicationPreparedEvent.getApplicationContext().getEnvironment();
        Properties properties = null;
        try {
            String databaseName = configurableEnvironment.getProperty("dolores.database.name");
            properties = importSql(databaseName);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        configurableEnvironment.getPropertySources().addFirst(new PropertiesPropertySource(this.getClass().getName(), properties));
    }


    private static Properties importSql(String databaseName) throws IOException, SQLException {
        Properties props = new Properties();
        try {
            String path = System.getProperty("user.dir") + "/" + databaseName;
            String url = "jdbc:sqlite://" + path;
            props.setProperty("spring.datasource.url", url);
            log.info("load sqlite database path:" + path);
            log.info("load sqlite database src:" + url);
            Connection con = DriverManager.getConnection(url);
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT count(*) FROM sqlite_master WHERE type='table'");
            int count = rs.getInt(1);
            if (count == 0) {
                Resource resource = new ClassPathResource("init_schema.sql");
                InputStreamReader isr = new InputStreamReader(resource.getInputStream(), "UTF-8");
                String sql = new BufferedReader(isr).lines().collect(Collectors.joining("\n"));
                statement.executeUpdate(sql);
            }
            rs.close();
            statement.close();
            con.close();
        } catch (Exception e) {
            log.error("initialize database fail", e);
            throw e;
        }
        return props;
    }
}
