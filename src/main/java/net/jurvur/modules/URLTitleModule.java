package net.jurvur.modules;

import com.github.kevinsawicki.http.HttpRequest;
import net.jurvur.Module;
import org.pircbotx.hooks.events.MessageEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLTitleModule extends Module {
    Pattern urlTitle = Pattern.compile("<title>(.+)</title>");

    public URLTitleModule() {
        addPattern(".*", "fetchTitle");

        addHelpFeature("URL Title", "Finds the title of any URL you post.");
    }

    public void fetchTitle(MessageEvent e, List<String> args) {
        try {
            URL url = new URL(e.getMessage());

            String body = HttpRequest.get(url).followRedirects(true).body();

            Matcher m = urlTitle.matcher(body);

            if (m.find()) {
                String title = m.group(1);
                e.getChannel().send().message(e.getUser().getNick() + "'s URL: " + title);
            }
        } catch (MalformedURLException e1) {
            // No valid URL in message
        }
    }
}
