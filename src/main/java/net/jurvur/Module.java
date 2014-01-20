package net.jurvur;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Module extends ListenerAdapter {
    private List<PatternHandler> patterns;

    public Module() {
        patterns = new ArrayList<PatternHandler>();
    }

    protected void addCommand(String command, String methodName) {
        Method m;
        try {
            m = this.getClass().getMethod(methodName, MessageEvent.class, List.class);
            patterns.add(new PatternHandler(Pattern.compile("!"+command), m, true));
        } catch (NoSuchMethodException e) {
            // Do something here, eventually, idk.
        }
    }

    protected void addPattern(String regex, String methodName) {
        Method m;
        try {
            m = this.getClass().getMethod(methodName, MessageEvent.class, List.class);
            patterns.add(new PatternHandler(Pattern.compile(regex), m, false));
        } catch (NoSuchMethodException e) {
            // Do something here, eventually, idk.
        }
    }

    protected void addHelpCommand(String name, String description, String... examples) {
        HelpManager.commandsHelp.add(new HelpManager.HelpEntry(name, description, examples));
    }

    protected void addHelpFeature(String name, String description, String... examples) {
        HelpManager.featuresHelp.add(new HelpManager.HelpEntry(name, description, examples));
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        String msg = event.getMessage();
        for (PatternHandler ph : patterns) {
            Pattern p = ph.pattern;
            Matcher m = p.matcher(msg);

            List<String> groups = new ArrayList<String>();
            if (ph.command) {
                if (msg.startsWith(p.pattern())) {
                    List<String> splittedList = Arrays.asList(msg.split(" "));
                    for (String arg : splittedList.subList(1, splittedList.size())) {
                        groups.add(arg);
                    }
                    ph.handler.invoke(this, event, groups);
                }
            } else {
                while (m.find()) {
                    for (int g = 1; g <= m.groupCount(); g++) {
                        String group = m.group(g);
                        if (group != null) {
                            groups.add(m.group(g));
                        }
                    }
                }

                if (m.matches()) {
                    ph.handler.invoke(this, event, groups);
                }
            }
        }
    }

    private class PatternHandler {
        private Pattern pattern;
        private Method handler;
        private boolean command;

        private PatternHandler(Pattern pattern, Method handler, boolean command) {
            this.pattern = pattern;
            this.handler = handler;
            this.command = command;
        }

    }
}
