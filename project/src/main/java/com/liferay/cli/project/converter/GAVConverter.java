package com.liferay.cli.project.converter;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import com.liferay.cli.shell.Completion;
import com.liferay.cli.shell.Converter;
import com.liferay.cli.shell.MethodTarget;

import com.liferay.cli.project.GAV;

/**
 * A {@link Converter} for {@link GAV}s
 * 
 * @author Andrew Swan
 * @since 1.2.0
 */
@Component
@Service
public class GAVConverter implements Converter<GAV> {

    public GAV convertFromText(final String value, final Class<?> targetType,
            final String optionContext) {
        return GAV.getInstance(value);
    }

    public boolean getAllPossibleValues(final List<Completion> completions,
            final Class<?> targetType, final String existingData,
            final String optionContext, final MethodTarget target) {
        // Currently (i.e. with no multi-module support), we can't offer any
        // completions as we don't know what GAVs are valid for the user and
        // the "this" alias (representing the current project or module) isn't
        // implemented yet either.
        // TODO offer the GAVs of the project, its modules, and any parent POMs
        // of either
        return true;
    }

    public boolean supports(final Class<?> type, final String optionContext) {
        return GAV.class.isAssignableFrom(type);
    }
}