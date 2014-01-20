package net.jurvur;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MainBot extends ListenerAdapter {
    public static List<Module> modules;

    public static JSONObject config;

    public static void main(String[] args) throws Exception {
        InputStream configStream = ClassLoader.getSystemClassLoader().getResourceAsStream("config.json");
        config = (JSONObject) JSONValue.parse(new InputStreamReader(configStream));

        modules = new ArrayList<Module>();

        for (Object moduleName : (JSONArray) config.get("modules")) {
            registerModule((Class<? extends Module>) Class.forName(moduleName.toString()));
        }

        Configuration.Builder<PircBotX> conf = new Configuration.Builder<PircBotX>()
                .setName((String) config.get("name"))
                .setServerHostname("irc.freenode.net")
                .addAutoJoinChannel("#jurvur-test")
                .setAutoNickChange(true)
                .setAutoReconnect(true)
                .setNickservPassword((String) config.get("password"));

        for (Object chnl : (JSONArray) config.get("channels")) {
            conf.addAutoJoinChannel(chnl.toString());
        }

        for (Module module : modules) {
            conf.addListener(module);
        }

        ServerConfig dbConfig = new ServerConfig();
        dbConfig.setName("jurvur-db");

        JSONObject dbConfigJson = (JSONObject) config.get("database");

        DataSourceConfig db = new DataSourceConfig();
        if ((Boolean) dbConfigJson.get("heroku")) {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));
            db.setDriver("org.postgres.Driver");
            db.setUsername(dbUri.getUserInfo().split(":")[0]);
            db.setPassword(dbUri.getUserInfo().split(":")[1]);
            db.setUrl("jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath(););
        } else {
            db.setDriver((String) dbConfigJson.get("driver"));
            db.setUsername((String) dbConfigJson.get("username"));
            db.setPassword((String) dbConfigJson.get("password"));
            db.setUrl("jdbc:" + (String) dbConfigJson.get("type") + "://" + dbConfigJson.get("ip") + ":" + dbConfigJson.get("port") + "/" + dbConfigJson.get("database"));
        }
        db.setHeartbeatSql("SELECT * FROM note");
        dbConfig.setDataSourceConfig(db);

        dbConfig.setDdlGenerate(true);
        dbConfig.setDdlRun(true);

        dbConfig.setDefaultServer(true);
        dbConfig.setRegister(true);

        EbeanServer server = EbeanServerFactory.create(dbConfig);

        PircBotX bot;
        try {
            bot = new PircBotX(conf.buildConfiguration());
            bot.startBot();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void registerModule(Class<? extends Module> module) {
        try {
            modules.add(module.newInstance());
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        }
    }
}
