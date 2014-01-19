package net.jurvur.modules;

import net.jurvur.Module;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public class RelevantModule extends Module {
    public RelevantModule() {
        addCommand("relevant", "dundundun");

        addHelpCommand("relevant", "DUN DUN DUUUUUUUUUN", "!test");
    }

    public void dundundun(MessageEvent e, List<String> args) {
        e.respond("http://www.youtube.com/watch?v=bW7Op86ox9g");
    }
}
