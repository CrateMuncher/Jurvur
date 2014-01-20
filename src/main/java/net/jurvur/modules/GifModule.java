package net.jurvur.modules;

import com.github.kevinsawicki.http.HttpRequest;
import net.jurvur.MainBot;
import net.jurvur.Module;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class GifModule extends Module {
    public GifModule() {
        addCommand("gif", "postGif");

        addHelpCommand("gif", "Posts a random gif with a given tag", "!gif", "!gif popcorn");
    }

    public void postGif(MessageEvent e, List<String> args) {
        String url = "http://api.giphy.com/v1/gifs/random?api_key=dc6zaTOxFJmzC";
        if (args.size() > 0) {
            try {
                String tag = URLEncoder.encode(StringUtils.join(args, " "), "UTF-8");
                url = url + "&tag=" + tag;
            } catch (UnsupportedEncodingException e1) {
                return;
            }
        }

        String content = HttpRequest.get(url).body();
        JSONObject respJson = (JSONObject) JSONValue.parse(content);

        if (respJson.get("data") instanceof JSONObject) {
            JSONObject data = (JSONObject) respJson.get("data");
            String imageURL = (String) data.get("image_original_url");
            if ((Boolean) MainBot.config.get("shorten-gif-url")) {
                String googlContent = HttpRequest.post("https://www.googleapis.com/urlshortener/v1/url").contentType("application/json").send("{\"longUrl\": \"" + imageURL + "\"}").body();
                String shortenedUrl = (String) ((JSONObject) JSONValue.parse(googlContent)).get("id");
                e.respond(shortenedUrl);
            } else {
                e.respond(imageURL);
            }
        } else {
            e.respond("Not found :(");
        }
    }
}
