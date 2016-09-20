package de.chrgroth.generictypesystem.persistence.values.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;

/**
 * A naive value proposal implementation independent from persistence layer.
 *
 * @author Christian Groth
 */
public class InMemoryValueProposalService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryValueProposalService.class);

    /**
     * Computes all value proposals for given type and items and optional template item. See {@link PersistenceService#values(long, GenericItem)} for more
     * details.
     *
     * @param type
     *            type definition
     * @param items
     *            all items to be considered
     * @param template
     *            optional template item for value proposals
     * @return all value proposals, never null
     */
    public Map<String, List<?>> values(GenericType type, Set<GenericItem> items, GenericItem template) {

        // null guard
        if (type == null || items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        // collect all paths
        Set<String> paths = new HashSet<>();
        paths.addAll(collectValuePaths(type, ""));
        if (paths.isEmpty()) {
            return Collections.emptyMap();
        }

        // collect value proposals
        if (LOG.isDebugEnabled()) {
            LOG.debug("computing valueproposals for " + type + " based on " + items.size() + " items and paths " + paths);
        }
        Map<String, List<?>> valueProposals = new HashMap<>();
        paths.forEach(p -> valueProposals.put(p, values(type, items, p, template)));

        // done
        return valueProposals;
    }

    private Collection<String> collectValuePaths(GenericStructure structure, String pathPrefix) {
        Collection<String> paths = new HashSet<>();

        // recurse into all structures
        structure.getAttributes().stream().filter(a -> a.isStructure()).forEach(a -> paths.addAll(collectValuePaths(a.getStructure(), buildAttributePath(pathPrefix, a))));

        // add all string attributes
        structure.getAttributes().stream().filter(a -> a.getType().isValueProposalDependenciesCapable()).forEach(a -> paths.add(buildAttributePath(pathPrefix, a)));

        // done
        return paths;
    }

    private String buildAttributePath(String pathPrefix, GenericAttribute a) {

        // add prefix
        StringBuilder sb = new StringBuilder();
        sb.append(pathPrefix);
        if (StringUtils.isNotBlank(pathPrefix)) {
            sb.append(".");
        }

        // add name
        sb.append(a.getName());

        // done
        return sb.toString();
    }

    private List<?> values(GenericType type, Set<GenericItem> items, String attributePath, GenericItem template) {

        // check for value proposal dependencies and reduce items to the ones with matching values
        GenericAttribute attribute = type.attribute(attributePath);
        if (template != null && attribute.getValueProposalDependencies() != null && !attribute.getValueProposalDependencies().isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("reducing to matching items dependeing on template item for " + attributePath);
            }

            // filter items by dependent attributes and their template values
            Stream<GenericItem> itemsStream = items.stream();
            for (Long dependencyId : attribute.getValueProposalDependencies()) {
                String dependecyAttributePath = type.attributePath(dependencyId);
                if (StringUtils.isNotBlank(dependecyAttributePath)) {
                    Object templateValue = template.get(dependecyAttributePath);
                    if (templateValue != null && StringUtils.isNotBlank(templateValue.toString())) {
                        itemsStream = itemsStream.filter(i -> Objects.equals(i.get(dependecyAttributePath), templateValue));
                    }
                }
            }

            // filter
            items = itemsStream.collect(Collectors.toSet());
            if (LOG.isDebugEnabled()) {
                LOG.debug("reduced to " + items.size() + " items for " + attributePath);
            }
        }

        // filter all null and empty values
        Stream<Object> valuesStream = items.stream().map(i -> i.get(attributePath));
        Stream<String> nonEmptyValuesStream = valuesStream.filter(Objects::nonNull).map(s -> s.toString().trim()).filter(s -> s.length() > 0);

        // create list of ordered distinct values
        List<String> values = nonEmptyValuesStream.sorted((s1, s2) -> s1.compareToIgnoreCase(s2)).distinct().collect(Collectors.toList());
        if (LOG.isDebugEnabled()) {
            LOG.debug("found " + values.size() + " values for " + attributePath);
        }

        // done
        return values;
    }
}
