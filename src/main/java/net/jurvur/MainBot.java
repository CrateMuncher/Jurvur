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
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainBot extends ListenerAdapter {
    public static List<Module> modules;

    public static JSONObject config;
    public static JSONObject databaseConfig;

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        System.out.println(event.getMessage());
    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        for (Object line : (JSONArray) config.get("run-on-connect")) {
            event.respond(line.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream configStream = ClassLoader.getSystemClassLoader().getResourceAsStream("config.json");
        config = (JSONObject) JSONValue.parse(new InputStreamReader(configStream));

        InputStream databaseConfigStream = ClassLoader.getSystemClassLoader().getResourceAsStream("database.json");
        databaseConfig = (JSONObject) JSONValue.parse(new InputStreamReader(databaseConfigStream));

        for (String k : System.getenv().keySet()) {
            String v = System.getenv(k);
            if (k.startsWith("JURVUR_")) {
                boolean database = false;
                String adjustedK;
                if (k.startsWith("JURVUR_DATABASE_")) {
                    adjustedK = k.substring(16).toLowerCase().replaceAll("_", "-");
                    database = true;
                } else {
                    adjustedK = k.substring(7).toLowerCase().replaceAll("_", "-");
                }


                List<String> pieces;
                if (adjustedK.contains(".")) {
                    pieces = Arrays.asList(adjustedK.split("\\."));
                } else {
                    pieces = Arrays.asList(adjustedK);
                }

                JSONObject obj = database ? databaseConfig : config;
                while (pieces.size() > 1) {
                    obj = (JSONObject) obj.get(pieces.get(0));
                    pieces = pieces.subList(1, pieces.size());
                }
                if (obj != null) {
                    System.out.println("Got override: " + adjustedK + ": " + JSONValue.parse(v));
                    obj.put(pieces.get(0), JSONValue.parse(v));
                }
            }
        }

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
                .addListener(new MainBot())
                .setNickservPassword((String) config.get("password"));

        for (Object chnl : (JSONArray) config.get("channels")) {
            conf.addAutoJoinChannel(chnl.toString());
        }

        for (Module module : modules) {
            conf.addListener(module);
        }

        ServerConfig dbConfig = new ServerConfig();
        dbConfig.setName("jurvur-db");

        DataSourceConfig db = new DataSourceConfig();
        if (System.getenv("DATABASE_URL") != null) {
            URI dbUri = new URI(System.getenv("DATABASE_URL"));
            db.setDriver((String) databaseConfig.get("driver"));
            db.setUsername(dbUri.getUserInfo().split(":")[0]);
            db.setPassword(dbUri.getUserInfo().split(":")[1]);

            String dbType = dbUri.getScheme();
            if (dbType.equalsIgnoreCase("postgres")) {
                dbType = "postgresql";
            }

            db.setUrl("jdbc:" + dbType + "://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath());
        } else {
            db.setDriver((String) databaseConfig.get("driver"));
            db.setUsername((String) databaseConfig.get("username"));
            db.setPassword((String) databaseConfig.get("password"));
            db.setUrl("jdbc:" + (String) databaseConfig.get("type") + "://" + databaseConfig.get("ip") + ":" + databaseConfig.get("port") + "/" + databaseConfig.get("database"));
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
