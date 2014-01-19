package net.cratemuncher.jurvur.modules;

import net.cratemuncher.jurvur.Module;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;

public class TestModule extends Module {
    public TestModule() {
        addCommand("test", "handleTest");
        addPattern(".*test.*", "handleMsg");

        addHelpCommand("test", "Basically like pinging, but with test.", "!test");
        addHelpFeature("Test feature", "Does random test crap that pretty much just annoys people", "You probably already know how to trigger this.");
    }

    public void handleTest(MessageEvent e, List<String> args) {
        e.respond("Test received!");
    }

    public void handleMsg(MessageEvent e, List<String> args) {
        e.respond("Testing testing event handling, please ignore");
    }
}
