package com.liferay.cli.shell.jline.osgi;

import java.net.URL;
import java.util.Collection;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import com.liferay.cli.shell.ExecutionStrategy;
import com.liferay.cli.shell.Parser;
import com.liferay.cli.shell.jline.JLineShell;
import com.liferay.cli.support.osgi.OSGiUtils;

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
//    @Reference private UrlInputStreamService urlInputStreamService;

    private ComponentContext context;

    protected void activate(final ComponentContext context) {
        this.context = context;
        final Thread thread = new Thread(this, "Liferay CLI JLine Shell");
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

    @Override
    protected Parser getParser() {
        return parser;
    }
}