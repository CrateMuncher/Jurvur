package net.jurvur.modules;

import net.jurvur.MainBot;
import net.jurvur.Module;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.*;

public class FindReplaceModule extends Module {
    HashMap<String, List<String>> lastMessages;
    public FindReplaceModule() {
        lastMessages = new HashMap<String, List<String>>();
        addPattern("(?:(.+): )?s/([^/]*)/([^/]*)[/]?", "doReplace");
        addPattern(".*", "log");

        addHelpFeature("Find and Replace", "Finds and replaces, with the syntax s/find/replace/.", "<You> This is a tset", "<You> s/tset/test", "What You meant: This is a test");
    }

    public void log(MessageEvent e, List<String> args) {
        if (!lastMessages.containsKey(e.getUser().getNick())) {
            List<String> list = new ArrayList<String>();
            lastMessages.put(e.getUser().getNick(), list);
        }
        if (!e.getMessage().matches("s/([^/]+)/([^/]+)[/]?")) {
            List<String> userMessages = lastMessages.get(e.getUser().getNick());
            userMessages.add(e.getMessage());
            int lookback = Integer.parseInt(MainBot.config.get("find-replace-lookback").toString());
            while(userMessages.size() >= lookback) {
                userMessages.remove(0);
            }
            lastMessages.put(e.getUser().getNick(), userMessages);
        }
    }

    public void doReplace(MessageEvent e, List<String> args) {
        String name = args.get(0);
        String from = args.get(1);
        String to = args.get(2);

        if (name == null) name = e.getUser().getNick();
        if (from == null) from = "";
        if (to == null) to = "";

        if (lastMessages.containsKey(name)) {
            List<String> lastMsg = lastMessages.get(name);
            for (int i = lastMsg.size()-1; i >= 0; i--) {
                String msg = lastMsg.get(i);
                if (msg.contains(from)) {
                    e.getChannel().send().message("What " + name + " meant: " + msg.replaceAll(from, to));
                    return;
                }
            }
        }
    }
}
