package com.liferay.cli.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;

/**
 * Allows a {@link MetadataProvider} or other class to track hash codes of
 * {@link MetadataItem}s and only invoke
 * {@link MetadataDependencyRegistry#notifyDownstream(String)} if there has been
 * an actual change since the last notification.
 * <p>
 * IMPORTANT: Before subclassing this class, ensure the {@link MetadataItem}s
 * that you will be presenting are all of the same type AND they provide a
 * reliable {@link Object#hashCode()} method. Failure to observe this
 * requirement will result in erroneous notifications.
 * 
 * @author Ben Alex
 * @since 1.1
 */
@Component(componentAbstract = true)
public abstract class AbstractHashCodeTrackingMetadataNotifier {

    private final Map<String, Integer> hashes = new HashMap<String, Integer>();
    @Reference protected MetadataDependencyRegistry metadataDependencyRegistry;

    @Reference protected MetadataService metadataService;

    /**
     * Notifies downstream dependencies of a change if and only if the passed
     * metadata item has a different hash code than the existing metadata item.
     * This is aimed at reducing needless notifications if nothing has actually
     * changed since the last notification.
     * 
     * @param metadataItem the potentially-updated metadata item (required; must
     *            be a metadata item of the same class as all other items
     *            presented to this class)
     */
    protected void notifyIfRequired(final MetadataItem metadataItem) {
        final String instanceId = MetadataIdentificationUtils
                .getMetadataInstance(metadataItem.getId());
        final Integer existing = hashes.get(instanceId);
        final int newHash = metadataItem.hashCode();
        if (existing != null && newHash == existing) {
            // No need to notify
            return;
        }
        // To get this far, we need to notify and replace/add the metadata
        // item's hash for future reference
        hashes.put(instanceId, newHash);

        // Eagerly insert into the cache to so any recursive gets for this
        // metadata item will be returned successfully
        metadataService.put(metadataItem);

        metadataDependencyRegistry.notifyDownstream(metadataItem.getId());
    }
}