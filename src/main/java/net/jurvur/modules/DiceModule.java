package net.jurvur.modules;

import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiceModule extends Module {
    Pattern p = Pattern.compile("(\\d+)?d(\\d+)(?:\\+(\\d+))?");
    public DiceModule() {
        addCommand("roll", "doRoll");
    }

    public void doRoll(MessageEvent e, List<String> args) {
        if (args.size() == 0) {
            int roll = (int) (Math.random()*6+1);
            e.respond("Rolled " + roll);
        } else {
            String arg = args.get(0);

            Matcher m = p.matcher(arg);
            if (!m.matches()) {
                return;
            }

            String amountStr = m.group(1);
            String sidesStr = m.group(2);
            String addStr = m.group(3);

            int amount = amountStr == null ? 1 : Integer.parseInt(amountStr);
            int sides = sidesStr == null ? 6 : Integer.parseInt(sidesStr);
            int add = addStr == null ? 0 : Integer.parseInt(addStr);

            List<Integer> nums = new ArrayList<Integer>();
            for (int r = 0; r < amount; r++) {
                int i = (int) Math.floor(Math.random()*sides)+1;
                i += add;
                if (nums.size() < 50) {
                    nums.add(i);
                }
            }

            e.respond("Rolled " + StringUtils.join(nums, ", "));
        }
    }
}
