package net.jurvur.modules;

import net.jurvur.HelpManager;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

import java.util.HashMap;
import java.util.List;

public class FindReplaceModule extends Module {
    HashMap<String, String> lastMessages;
    public FindReplaceModule() {
        lastMessages = new HashMap<String, String>();
        addPattern("(?:(.+): )?s/([^/]*)/([^/]*)[/]?", "doReplace");
        addPattern(".*", "log");

        addHelpFeature("Find and Replace", "Finds and replaces, with the syntax s/find/replace/.", "<You> This is a tset", "<You> s/tset/test", "What You meant: This is a test");
    }

    public void log(MessageEvent e, List<String> args) {
        if (!e.getMessage().matches("s/([^/]+)/([^/]+)[/]?")) {
            lastMessages.put(e.getUser().getNick(), e.getMessage());
        }
    }

    public void doReplace(MessageEvent e, List<String> args) {
        if (lastMessages.containsKey(e.getUser().getNick())) {
            String name = args.get(0);
            String from = args.get(1);
            String to = args.get(2);

            if (name == null) name = e.getUser().getNick();
            if (from == null) from = "";
            if (to == null) to = "";

            String lastMsg = lastMessages.get(name);

            String replaced = lastMsg.replaceAll(from, to);
            e.getChannel().send().message("What " + name + " meant: " + replaced);
        }
    }
}
