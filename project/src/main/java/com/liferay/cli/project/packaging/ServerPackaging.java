
package com.liferay.cli.project.packaging;

import com.liferay.cli.model.JavaPackage;
import com.liferay.cli.project.Path;
import com.liferay.cli.project.ProjectOperations;

import java.util.Collection;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * The Maven "server" {@link PackagingProvider}
 *
 * @author Gregory Amerson
 */
@Component
@Service
public class ServerPackaging extends AbstractCorePackagingProvider
{

    public static final String NAME = "server";

    /**
     * Constructor
     */
    public ServerPackaging()
    {
        super( NAME, "pom", "server-pom-template.xml" );
    }

    @Override
    protected void createOtherArtifacts(
        final JavaPackage topLevelPackage, final String module, final ProjectOperations projectOperations )
    {
        // No artifacts are applicable for Server modules
    }

    public Collection<Path> getPaths()
    {
        return null;
    }
}
