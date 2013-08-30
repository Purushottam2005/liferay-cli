package com.liferay.cli.shell.jline.osgi;

import static com.liferay.cli.support.util.AnsiEscapeCode.FG_CYAN;
import static com.liferay.cli.support.util.AnsiEscapeCode.FG_GREEN;
import static com.liferay.cli.support.util.AnsiEscapeCode.FG_MAGENTA;
import static com.liferay.cli.support.util.AnsiEscapeCode.FG_YELLOW;
import static com.liferay.cli.support.util.AnsiEscapeCode.REVERSE;
import static com.liferay.cli.support.util.AnsiEscapeCode.UNDERSCORE;
import static com.liferay.cli.support.util.AnsiEscapeCode.decorate;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.osgi.service.component.ComponentContext;
import com.liferay.cli.shell.ExecutionStrategy;
import com.liferay.cli.shell.Parser;
import com.liferay.cli.shell.jline.JLineShell;
import com.liferay.cli.support.osgi.OSGiUtils;
import com.liferay.cli.url.stream.UrlInputStreamService;

/**
 * OSGi component launcher for {@link JLineShell}.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(immediate = true)
@Service
public class JLineShellComponent extends JLineShell {

    @Reference private ExecutionStrategy executionStrategy;
    @Reference private Parser parser;
    @Reference private UrlInputStreamService urlInputStreamService;

    private ComponentContext context;

    protected void activate(final ComponentContext context) {
        this.context = context;
        final Thread thread = new Thread(this, "Spring Roo JLine Shell");
        thread.start();
    }

    protected void deactivate(final ComponentContext context) {
        this.context = null;
        closeShell();
    }

    @Override
    protected Collection<URL> findResources(final String path) {
        // For an OSGi bundle search, we add the root prefix to the given path
        return OSGiUtils.findEntriesByPath(context.getBundleContext(),
                OSGiUtils.ROOT_PATH + path);
    }

    @Override
    protected ExecutionStrategy getExecutionStrategy() {
        return executionStrategy;
    }

    private String getLatestFavouriteTweet() {
        // Access Twitter's REST API
        final String string = sendGetRequest(
                "http://api.twitter.com/1/favorites.json",
                "id=SpringRoo&count=5");
        if (StringUtils.isBlank(string)) {
            return null;
        }
        // Parse the returned JSON. This is a once off operation so we can used
        // JSONValue.parse without penalty
        final JSONArray object = (JSONArray) JSONValue.parse(string);
        if (object == null) {
            return null;
        }
        int index = 0;
        if (object.size() > 4) {
            index = new Random().nextInt(5);
        }
        final JSONObject jsonObject = (JSONObject) object.get(index);
        if (jsonObject == null) {
            return null;
        }
        final String screenName = (String) ((JSONObject) jsonObject.get("user"))
                .get("screen_name");
        String tweet = (String) jsonObject.get("text");
        // We only want one line
        tweet = tweet.replace(IOUtils.LINE_SEPARATOR, " ");
        final List<String> words = Arrays.asList(tweet.split(" "));
        final StringBuilder sb = new StringBuilder();
        // Add in Roo's twitter account to give context to the notification
        sb.append(decorate("@" + screenName + ":",
                SystemUtils.IS_OS_WINDOWS ? FG_YELLOW : REVERSE));
        sb.append(" ");

        // We want to colourise certain words. The codes used here should be
        // moved to a ShellUtils and include a few helper methods
        // This is a basic attempt at pattern identification, it should be
        // adequate in most cases although may be incorrect for URLs.
        // For example url.com/ttym: is valid by may mean "url.com/ttym" + ":"
        for (final String word : words) {
            if (word.startsWith("http://") || word.startsWith("https://")) {
                // It's a URL
                if (SystemUtils.IS_OS_WINDOWS) {
                    sb.append(decorate(word, FG_GREEN));
                }
                else {
                    sb.append(decorate(word, FG_GREEN, UNDERSCORE));
                }
            }
            else if (word.charAt(0) == '@') {
                // It's a Twitter username
                sb.append(decorate(word, FG_MAGENTA));
            }
            else if (word.charAt(0) == '#') {
                // It's a Twitter hash tag
                sb.append(decorate(word, FG_CYAN));
            }
            else {
                // All else default
                sb.append(word);
            }
            // Add back separator
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    protected Parser getParser() {
        return parser;
    }

    @Override
    public String getStartupNotifications() {
        try {
            return getLatestFavouriteTweet();
        }
        catch (final Exception e) {
            return null;
        }
    }

    // TODO: This should probably be moved to a HTTP service of some sort - JTT
    // 29/08/11
    private String sendGetRequest(final String endpoint,
            final String requestParameters) {
        if (!(endpoint.startsWith("http://") || endpoint.startsWith("https://"))) {
            return null;
        }

        // Send a GET request to the servlet
        InputStream inputStream = null;
        try {
            // Send data
            String urlStr = endpoint;
            if (StringUtils.isNotBlank(requestParameters)) {
                urlStr += "?" + requestParameters;
            }
            // Get the response
            final URL url = new URL(urlStr);
            inputStream = urlInputStreamService.openConnection(url);
            return IOUtils.toString(inputStream);
        }
        catch (final Exception e) {
            return null;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}