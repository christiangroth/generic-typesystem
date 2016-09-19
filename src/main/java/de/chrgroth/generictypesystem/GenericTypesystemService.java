package de.chrgroth.generictypesystem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.persistence.PersistenceService;
import de.chrgroth.generictypesystem.persistence.impl.InMemoryPersistenceService;
import de.chrgroth.generictypesystem.persistence.query.ItemQueryResult;
import de.chrgroth.generictypesystem.persistence.query.ItemsQueryData;
import de.chrgroth.generictypesystem.persistence.query.impl.InMemoryItemsQueryService;
import de.chrgroth.generictypesystem.persistence.values.impl.InMemoryValueProposalService;
import de.chrgroth.generictypesystem.validation.ValidationResult;
import de.chrgroth.generictypesystem.validation.ValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationService;
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationServiceEmptyHooks;

// TODO javadocs
// TODO extract values(...) to service similar to query service
// TODO add security / visibility service and some kind of context ... might be placed in persistence service??
// TODO no wrapper classes as id parameters
public class GenericTypesystemService {

    // TODO private static final Logger LOG = LoggerFactory.getLogger(GenericTypesystemService.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ValidationService validation;
    private final PersistenceService persistence;

    public GenericTypesystemService(ValidationService validation, PersistenceService persistence) {
        this.validation = validation != null ? validation : new DefaultValidationService(new DefaultValidationServiceEmptyHooks());
        this.persistence = persistence != null ? persistence : new InMemoryPersistenceService(new InMemoryItemsQueryService(DEFAULT_PAGE_SIZE), new InMemoryValueProposalService());
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

    public ItemQueryResult query(long typeId, ItemsQueryData data) {
        return persistence.query(typeId, data);
    }

    public GenericItem item(long typeId, long id) {
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

    public Map<String, List<?>> values(Long typeId, GenericItem template) {
        return persistence.values(typeId, template);
    }

    public boolean deleteItem(Long typeId, long id) {
        return persistence.removeItem(typeId, id);
    }

    public boolean deleteType(long typeId) {
        return persistence.removeType(typeId);
    }
}
