package de.chrgroth.generictypesystem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * The main service dealing with generic typesystem. Most of the functionality is delegated to {@link ValidationService} and {@link PersistenceService} which
 * may be implemented to fit your own needs.
 *
 * @author Christian Groth
 */
// TODO add security / visibility service and some kind of context ... might be placed in persistence service??
public class GenericTypesystemService {

    private static final Logger LOG = LoggerFactory.getLogger(GenericTypesystemService.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ValidationService validation;
    private final PersistenceService persistence;

    /**
     * Creates a new service instance with the given validation and persistence services.
     *
     * @param validation
     *            validation service , null will create a new instance of {@link DefaultValidationService}
     * @param persistence
     *            persistence service , null will create a new instance of {@link InMemoryPersistenceService}
     */
    public GenericTypesystemService(ValidationService validation, PersistenceService persistence) {
        this.validation = validation != null ? validation : new DefaultValidationService(new DefaultValidationServiceEmptyHooks());
        this.persistence = persistence != null ? persistence : new InMemoryPersistenceService(new InMemoryItemsQueryService(DEFAULT_PAGE_SIZE), new InMemoryValueProposalService());
    }

    /**
     * Returns all type groups as defined by {@link PersistenceService#typeGroups()}.
     *
     * @return type groups
     */
    public Set<String> typeGroups() {
        return persistence.typeGroups();
    }

    /**
     * Returns all types as defined by {@link PersistenceService#types()}.
     *
     * @return type
     */
    public Set<GenericType> types() {
        return persistence.types();
    }

    /**
     * Ensures an unique id for all type attributes, validates the type and if the type is valid {@link PersistenceService#type(GenericType)} will be invoked.
     *
     * @param type
     *            type to be handled
     * @return validation result
     */
    public ValidationResult<GenericType> type(GenericType type) {

        // ensure all type attributes have an id
        if (type != null && type.getAttributes() != null) {
            Set<GenericAttribute> allAttributes = type.attributes();
            for (GenericAttribute attribute : allAttributes) {
                if (attribute.getId() == null || attribute.getId().longValue() < 1) {

                    // get next unique id
                    long nextTypeAttributeId = nextTypeAttributeId(allAttributes);

                    // set id
                    attribute.setId(nextTypeAttributeId);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("attribute " + attribute.getName() + " id resolved to " + nextTypeAttributeId);
                    }
                }
            }
        }

        // validate
        ValidationResult<GenericType> validationResult = validation.validate(type);

        // save / update
        if (validationResult.isValid()) {
            persistence.type(type);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid type " + (type != null ? type.getId() : null));
        }

        // done
        return validationResult;
    }

    private long nextTypeAttributeId(Set<GenericAttribute> allAttributes) {
        return allAttributes.stream().filter(a -> a.getId() != null).mapToLong(a -> a.getId()).max().orElse(0) + 1;
    }

    /**
     * Returns the query result as defined by {@link PersistenceService#query(long, ItemsQueryData)}.
     *
     * @param typeId
     *            type id
     * @param data
     *            query data
     * @return query result
     */
    public ItemQueryResult query(long typeId, ItemsQueryData data) {
        return persistence.query(typeId, data);
    }

    /**
     * Returns the item as defined by {@link PersistenceService#item(long, long)}.
     *
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return item
     */
    public GenericItem item(long typeId, long id) {
        return persistence.item(typeId, id);
    }

    /**
     * Ensures {@link GenericItem#getTypeId()} is set to given type, validates the type and item and if the result is valid
     * {@link PersistenceService#item(GenericType, GenericItem)} will be invoked.
     *
     * @param typeId
     *            type id
     * @param item
     *            optional template item
     * @return validation result
     */
    public ValidationResult<GenericItem> item(long typeId, GenericItem item) {

        // get type
        GenericType type = persistence.type(typeId);

        // ensure generic type id
        if (type != null && item != null) {
            item.setTypeId(typeId);
        }

        // validate
        ValidationResult<GenericItem> validationResult = validation.validate(type, item);

        // save / update
        if (validationResult.isValid()) {
            persistence.item(type, item);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid item " + typeId + "/" + item.getId());
        }

        // done
        return validationResult;
    }

    /**
     * Returns the value proposals as defined by {@link PersistenceService#values(long, GenericItem)}.
     *
     * @param typeId
     *            type id
     * @param template
     *            optional template item
     * @return value proposals
     */
    public Map<String, List<?>> values(long typeId, GenericItem template) {
        return persistence.values(typeId, template);
    }

    /**
     * Deletes the item as defined by {@link PersistenceService#removeItem(long, long)}.
     *
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return true if successful, false otherwise
     */
    public boolean removeItem(long typeId, long id) {
        return persistence.removeItem(typeId, id);
    }

    /**
     * Deletes the type as defined by {@link PersistenceService#removeType(long)}.
     *
     * @param typeId
     *            type id
     * @return true if successful, false otherwise
     */
    public boolean removeType(long typeId) {
        return persistence.removeType(typeId);
    }
}
