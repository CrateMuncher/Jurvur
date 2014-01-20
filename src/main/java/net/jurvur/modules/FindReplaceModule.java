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
        addPattern("s/([^/]+)/([^/]+)[/]?", "doReplace");
        addPattern(".*", "log");

        addHelpCommand("help", "Shows this.", "!help", "!help relevant");
    }

    public void log(MessageEvent e, List<String> args) {
        if (!e.getMessage().matches("s/([^/]+)/([^/]+)[/]?")) {
            lastMessages.put(e.getUser().getNick(), e.getMessage());
        }
    }

    public void doReplace(MessageEvent e, List<String> args) {
        String lastMsg = lastMessages.get(e.getUser().getNick());
        String from = args.get(0);
        String to = args.get(1);

        String replaced = lastMsg.replaceAll(from, to);
        e.getChannel().send().message("What " + e.getUser().getNick() + " meant: " + replaced);
    }
}
