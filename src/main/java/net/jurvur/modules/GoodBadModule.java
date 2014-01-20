package net.jurvur.modules;

import net.jurvur.HelpManager;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

import java.util.List;

public class GoodBadModule extends Module {
    public GoodBadModule() {
        addPattern(".*good jurvur.*", "good");
        addPattern(".*bad jurvur.*", "bad");
    }

    public void good(MessageEvent e, List<String> args) {
        e.respond("<3");
    }

    public void bad(MessageEvent e, List<String> args) {
        e.respond("grrrrrr");
    }
}
