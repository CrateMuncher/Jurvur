package net.cratemuncher.jurvur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpManager {
    public static List<HelpEntry> commandsHelp = new ArrayList<HelpEntry>();
    public static List<HelpEntry> featuresHelp = new ArrayList<HelpEntry>();

    public static class HelpEntry {
        private String name;
        private String description;
        private List<String> examples;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getExamples() {
            return examples;
        }

        public HelpEntry(String name, String description, String... examples) {
            this.name = name;
            this.description = description;
            this.examples = Arrays.asList(examples);
        }
    }
}
