package de.chrgroth.generictypesystem;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.InMemoryPersistenceService;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.query.ItemFilterData;
import de.chrgroth.generictypesystem.query.ItemPagingData;
import de.chrgroth.generictypesystem.query.ItemQueryResult;
import de.chrgroth.generictypesystem.query.ItemSortData;
import de.chrgroth.generictypesystem.query.ItemsQueryData;
import de.chrgroth.generictypesystem.util.CascadingAttributeComparator;
import de.chrgroth.generictypesystem.validation.DefaultValidationService;
import de.chrgroth.generictypesystem.validation.NullDefaultValidationServiceHooks;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;

// TODO add security / visibility service
// TODO extract query service or move to data service??
// TODO unit test coverage
public class GenericTypesystemService {

    private static final Logger LOG = LoggerFactory.getLogger(GenericTypesystemService.class);

    private final ValidationService validation;
    private final PersistenceService persistence;

    public GenericTypesystemService(ValidationService validation, PersistenceService persistence) {
        this.validation = validation != null ? validation : new DefaultValidationService(new NullDefaultValidationServiceHooks());
        this.persistence = persistence != null ? persistence : new InMemoryPersistenceService();
    }

    public Set<String> typeGroups() {
        return persistence.typeGroups();
    }

    public Set<GenericType> types() {
        // TODO context / visibility handling
        return persistence.types();
    }

    public ValidationResult<GenericType> createOrUpdate(GenericType type) {

        // ensure all type attributes have an id
        if (type != null && type.getAttributes() != null) {
            List<GenericAttribute> allAttributes = collectAllTypeAttributes(type);
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

    private List<GenericAttribute> collectAllTypeAttributes(GenericStructure structure) {
        List<GenericAttribute> allAttributes = new ArrayList<>();

        // add all attributes
        allAttributes.addAll(structure.getAttributes());

        // recurse into all structures
        structure.getAttributes().stream()
                .filter(a -> a.getType() == GenericAttributeType.STRUCTURE || a.getType() == GenericAttributeType.LIST && a.getValueType() == GenericAttributeType.STRUCTURE)
                .forEach(a -> allAttributes.addAll(collectAllTypeAttributes(a.getStructure())));

        return allAttributes;
    }

    private long nextTypeAttributeId(List<GenericAttribute> allAttributes) {
        return allAttributes.stream().filter(a -> a.getId() != null).mapToLong(a -> a.getId()).max().orElse(0) + 1;
    }

    public ItemQueryResult items(Long typeId, ItemsQueryData data) {

        // query
        // TODO unit test query, change return object
        return items(typeId, data.getFilter(), data.getSorts(), data.getPaging());
    }

    // TODO allow to be served by persistence service??
    // TODO error handling
    private ItemQueryResult items(long typeId, ItemFilterData filter, List<ItemSortData> sorts, ItemPagingData paging) {
        List<GenericItem> items = new ArrayList<>();

        // get item store
        Set<GenericItem> data = persistence.items(typeId);
        if (data == null) {
            return new ItemQueryResult(items, false);
        }

        // add all
        boolean moreAvailable = false;
        items.addAll(data);

        // TODO filter and test
        // filter
        if (filter != null) {
            LOG.warn("filtering not implemented yet!!");
        }

        // sorting
        // TODO case insensitive string compare + test
        Collections.sort(items, new CascadingAttributeComparator(sorts));

        // paging
        // TODO error tests
        if (paging != null) {
            // TODO validate paging parameters
            int pageSize = paging.getSize();
            int page = paging.getPage();

            // compute beginning
            int firstIdx = 0;
            if (page > 0) {
                firstIdx = pageSize * (page - 1);
            }

            // compute end
            int lastIdx = firstIdx + pageSize;

            // slice
            if (items.size() >= firstIdx + 1) {
                moreAvailable = items.size() > lastIdx;
                items = items.subList(firstIdx, items.size() > lastIdx ? lastIdx : items.size());
            } else {
                // TODO error handling
                items.clear();
            }
        }

        // done
        return new ItemQueryResult(items, moreAvailable);
    }

    public GenericItem item(Long typeId, long id) {
        return persistence.item(typeId, id);
    }

    public ValidationResult<GenericItem> createOrUpdate(Long typeId, GenericItem item) {

        // get type
        GenericType type = persistence.type(typeId);

        // ensure generic type id
        item.setGenericTypeId(typeId);

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
        structure.getAttributes().stream()
                .filter(a -> a.getType() == GenericAttributeType.STRUCTURE || a.getType() == GenericAttributeType.LIST && a.getValueType() == GenericAttributeType.STRUCTURE)
                .forEach(a -> paths.addAll(collectValuePaths(a.getStructure(), buildAttributePath(pathPrefix, a))));

        // add all string attributes
        structure.getAttributes().stream().filter(a -> a.getType() == GenericAttributeType.STRING).forEach(a -> paths.add(buildAttributePath(pathPrefix, a)));

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
        if (attribute.getType() != GenericAttributeType.STRING) {
            return new ArrayList<>();
        }

        // collect all items
        Set<GenericItem> allItems = persistence.items(type.getId());

        // check for value proposal dependencies
        if (template != null && attribute.getValueProposalDependencies() != null && !attribute.getValueProposalDependencies().isEmpty()) {

            // filter items by dependent attributes and their template values
            Stream<GenericItem> itemsStream = allItems.stream();
            for (Long dependencyId : attribute.getValueProposalDependencies()) {
                String dependecyAttributePath = type.attribute(dependencyId);
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

        // drop storage
        // TODO move to default impl boolean success = persistence.dropItemStore(typeId);
        boolean success = true;
        if (success) {

            // remove type definition from types storage
            success = persistence.removeType(typeId);
        }

        // done
        return success;
    }
}
