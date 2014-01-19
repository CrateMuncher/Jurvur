package net.jurvur.modules;

import net.jurvur.HelpManager;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

import java.util.List;

public class HelpModule extends Module {
    public HelpModule() {
        addCommand("help", "handleHelp");

        addHelpCommand("help", "Shows this.", "!help <command>");
    }

    public void handleHelp(MessageEvent e, List<String> args) {
        if (args.size() == 0) {
            e.respond("I have PM'd you all my commands and features");
            OutputUser output = e.getUser().send();

            output.message("==========JURVUR==========");
            output.message("Commands:");
            if (HelpManager.commandsHelp.size() > 0) {
                for (HelpManager.HelpEntry entry : HelpManager.commandsHelp) {
                    output.message("!" + entry.getName() + ": " + entry.getDescription());
                }
            } else {
                output.message("None");
            }
            output.message(" ");
            output.message("Other features:");
            if (HelpManager.featuresHelp.size() > 0) {
                for (HelpManager.HelpEntry entry : HelpManager.featuresHelp) {
                    output.message(entry.getName() + ": " + entry.getDescription());
                }
            } else {
                output.message("None");
            }
            output.message("==========JURVUR==========");
        } else {
            OutputChannel output = e.getChannel().send();
            String argsText = StringUtils.join(args, " ");

            for (HelpManager.HelpEntry entry : HelpManager.commandsHelp) {
                if (entry.getName().toLowerCase().trim().equalsIgnoreCase(argsText.toLowerCase().trim())) { // Do all kinds of stuff to maximize good-ability.
                    output.message("Command: " + "!" + entry.getName());
                    output.message("Description: " + entry.getDescription());

                    output.message("Examples: ");
                    for (String example : entry.getExamples()) {
                        output.message(example);
                    }
                    return;
                }
            }

            for (HelpManager.HelpEntry entry : HelpManager.featuresHelp) {
                if (entry.getName().toLowerCase().trim().replaceAll(" ", "").contentEquals(argsText.toLowerCase().trim().replaceAll(" ", ""))) { // Do all kinds of stuff to maximize good-ability.
                    output.message("Feature: " + entry.getName());
                    output.message("Description " + entry.getDescription());

                    output.message("Examples: ");
                    for (String example : entry.getExamples()) {
                        output.message(example);
                    }
                    return;
                }
            }
        }
    }
}
