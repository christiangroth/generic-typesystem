package de.chrgroth.generictypesystem;

import java.util.ArrayList;
import java.util.Collection;
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
import de.chrgroth.generictypesystem.persistence.impl.InMemoryPersistenceService;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationServiceEmptyHooks;

// TODO extract values(...) to service similar to query service
// TODO add security / visibility service and some kind of context ... might be placed in persistence service??
public class GenericTypesystemService {

    // TODO private static final Logger LOG = LoggerFactory.getLogger(GenericTypesystemService.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ValidationService validation;
    private final PersistenceService persistence;

    public GenericTypesystemService(ValidationService validation, PersistenceService persistence) {
        this.validation = validation != null ? validation : new DefaultValidationService(new DefaultValidationServiceEmptyHooks());
        this.persistence = persistence != null ? persistence : new InMemoryPersistenceService(DEFAULT_PAGE_SIZE);
    }

    public Set<String> typeGroups() {
        return persistence.typeGroups();
    }

    public Set<GenericType> types() {
        return persistence.types();
    }

    public ValidationResult<GenericType> type(GenericType type) {

        // ensure all type attributes have an id
        if (type != null && type.getAttributes() != null) {
            Set<GenericAttribute> allAttributes = type.attributes();
            for (GenericAttribute attribute : allAttributes) {
                if (attribute.getId() == null || attribute.getId().longValue() < 1) {
                    attribute.setId(nextTypeAttributeId(allAttributes));
                }
            }
        }

        // validate
        ValidationResult<GenericType> validationResult = validation.validate(type);

        // save / update
        if (validationResult.isValid()) {
            persistence.type(type);
        }

        // done
        return validationResult;
    }

    private long nextTypeAttributeId(Set<GenericAttribute> allAttributes) {
        return allAttributes.stream().filter(a -> a.getId() != null).mapToLong(a -> a.getId()).max().orElse(0) + 1;
    }

    public ItemQueryResult query(Long typeId, ItemsQueryData data) {
        return persistence.query(typeId, data);
    }

    public GenericItem item(Long typeId, long id) {
        return persistence.item(typeId, id);
    }

    public ValidationResult<GenericItem> item(Long typeId, GenericItem item) {

        // get type
        GenericType type = persistence.type(typeId);

        // ensure generic type id
        item.setTypeId(typeId);

        // validate
        ValidationResult<GenericItem> validationResult = validation.validate(type, item);

        // save / update
        if (validationResult.isValid()) {
            persistence.item(type, item);
        }

        // done
        return validationResult;
    }

    // TODO test with template!!
    public Map<String, List<?>> values(Long typeId, GenericItem template) {
        return values(persistence.type(typeId), template);
    }

    // TODO allow to be served by persistence service??
    private Map<String, List<?>> values(GenericType type, GenericItem template) {

        // null guard
        if (type == null) {
            return null;
        }

        // collect all paths
        Set<String> paths = new HashSet<>();
        paths.addAll(collectValuePaths(type, ""));

        // collect value proposals
        Map<String, List<?>> valueProposals = new HashMap<>();
        paths.forEach(p -> valueProposals.put(p, values(type, p, template)));

        // done
        return valueProposals;
    }

    private Collection<String> collectValuePaths(GenericStructure structure, String pathPrefix) {
        Collection<String> paths = new HashSet<>();

        // recurse into all structures
        structure.getAttributes().stream().filter(a -> a.getType().isStructure() || a.getType().isList() && a.getValueType().isStructure())
                .forEach(a -> paths.addAll(collectValuePaths(a.getStructure(), buildAttributePath(pathPrefix, a))));

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

    private List<?> values(GenericType type, String attributePath, GenericItem template) {

        // null guard
        GenericAttribute attribute = type != null ? type.attribute(attributePath) : null;
        if (attribute == null) {
            return new ArrayList<>();
        }

        // validate type
        if (!attribute.getType().isValueProposalDependenciesCapable()) {
            return new ArrayList<>();
        }

        // collect all items
        Set<GenericItem> allItems = persistence.items(type.getId());

        // check for value proposal dependencies
        if (template != null && attribute.getValueProposalDependencies() != null && !attribute.getValueProposalDependencies().isEmpty()) {

            // filter items by dependent attributes and their template values
            Stream<GenericItem> itemsStream = allItems.stream();
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
            allItems = itemsStream.collect(Collectors.toSet());
        }

        // filter all null and empty values
        Stream<Object> valuesStream = allItems.stream().map(i -> i.get(attributePath));
        Stream<String> nonEmptyValuesStream = valuesStream.filter(Objects::nonNull).map(s -> s.toString().trim()).filter(s -> s.length() > 0);

        // create list of ordered distinct values
        return nonEmptyValuesStream.sorted((s1, s2) -> s1.compareToIgnoreCase(s2)).distinct().collect(Collectors.toList());
    }

    public boolean deleteItem(Long typeId, long id) {
        return persistence.removeItem(typeId, id);
    }

    public boolean deleteType(long typeId) {
        return persistence.removeType(typeId);
    }
}
