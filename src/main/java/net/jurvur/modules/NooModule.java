package net.jurvur.modules;

import net.jurvur.Module;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public class NooModule extends Module {
    public NooModule() {
        addPattern("![n]+o[o]+", "nooo");
    }

    public void nooo(MessageEvent e, List<String> args) {
        e.respond("http://nooooooooooooooo.com/");
    }
}
