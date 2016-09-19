package de.chrgroth.generictypesystem.persistence.values.impl;

import java.util.ArrayList;
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

        // null guard
        GenericAttribute attribute = type != null ? type.attribute(attributePath) : null;
        if (attribute == null) {
            return new ArrayList<>();
        }

        // validate type
        if (!attribute.getType().isValueProposalDependenciesCapable()) {
            return new ArrayList<>();
        }

        // check for value proposal dependencies
        if (template != null && attribute.getValueProposalDependencies() != null && !attribute.getValueProposalDependencies().isEmpty()) {

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
        }

        // filter all null and empty values
        Stream<Object> valuesStream = items.stream().map(i -> i.get(attributePath));
        Stream<String> nonEmptyValuesStream = valuesStream.filter(Objects::nonNull).map(s -> s.toString().trim()).filter(s -> s.length() > 0);

        // create list of ordered distinct values
        return nonEmptyValuesStream.sorted((s1, s2) -> s1.compareToIgnoreCase(s2)).distinct().collect(Collectors.toList());
    }
}
