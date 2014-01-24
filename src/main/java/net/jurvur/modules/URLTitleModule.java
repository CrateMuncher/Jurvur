package net.jurvur.modules;

import com.github.kevinsawicki.http.HttpRequest;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
        List<String> urls = new ArrayList<String>();
        for (String message : e.getMessage().split(" ")) {
            try {
                URL url = new URL(message);

                String body = HttpRequest.get(new URL(url.toURI().toString())).followRedirects(true).body();

                Matcher m = urlTitle.matcher(body);

                if (m.find()) {
                    String title = m.group(1);
                    urls.add(title);
                }
            } catch (MalformedURLException e1) {
                // No valid URL in message
            } catch (URISyntaxException e1) {
                // Also no valid URL in message
            } catch (HttpRequest.HttpRequestException e1) {
                // There was an error doing shit
                urls.add("[ERROR]");
            }
        }

        if (urls.size() > 0) {
            if (urls.size() == 1) {
                e.getChannel().send().message(e.getUser().getNick() + "'s URL: " + StringUtils.join(urls, ", "));
            } else {
                e.getChannel().send().message(e.getUser().getNick() + "'s URLs: " + StringUtils.join(urls, ", "));
            }
        }
    }
}
