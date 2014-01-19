package net.jurvur;

import net.jurvur.modules.HelpModule;
import net.jurvur.modules.RelevantModule;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.InputStream;
import java.io.InputStreamReader;
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
