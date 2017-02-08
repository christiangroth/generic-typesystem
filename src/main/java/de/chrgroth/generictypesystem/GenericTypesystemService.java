package de.chrgroth.generictypesystem;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.context.impl.NullGenericTypesystemContext;
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
import de.chrgroth.generictypesystem.validation.impl.DefaultValidationServiceMessageKey;

/**
 * The main service dealing with generic typesystem. Most of the functionality is delegated to {@link ValidationService} and {@link PersistenceService} which
 * may be implemented to fit your own needs.
 *
 * @author Christian Groth
 */
// TODO add createItem - also considering default values
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
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public Set<String> typeGroups() {
        return typeGroups(new NullGenericTypesystemContext());
    }

    /**
     * Returns all type groups as defined by {@link PersistenceService#typeGroups(GenericTypesystemContext)}.
     *
     * @param context
     *            current context
     * @return type groups
     */
    public Set<String> typeGroups(GenericTypesystemContext context) {
        return persistence.typeGroups(context);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public Set<GenericType> types() {
        return types(new NullGenericTypesystemContext());
    }

    /**
     * Returns all types as defined by {@link PersistenceService#types(GenericTypesystemContext)}.
     *
     * @param context
     *            current context
     * @return type
     */
    public Set<GenericType> types(GenericTypesystemContext context) {
        return persistence.types(context);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public ValidationResult<GenericType> type(GenericType type) {
        return type(new NullGenericTypesystemContext(), type);
    }

    /**
     * Ensures an unique id for all type attributes, validates the type and if the type is valid
     * {@link PersistenceService#type(GenericTypesystemContext, GenericType)} will be invoked.
     *
     * @param context
     *            current context
     * @param type
     *            type to be handled
     * @return validation result
     */
    public ValidationResult<GenericType> type(GenericTypesystemContext context, GenericType type) {

        // check type accessibility
        if (!context.isTypeAccessible(type)) {
            final ValidationResult<GenericType> validationResult = new ValidationResult<>(type);
            validationResult.error("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return validationResult;
        }

        // ensure all type attributes have an id
        if (type != null && type.getAttributes() != null) {
            List<GenericAttribute> allAttributes = type.attributes();
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
            persistence.type(context, type);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid type " + (type != null ? type.getId() : null));
        }

        // done
        return validationResult;
    }

    private long nextTypeAttributeId(List<GenericAttribute> allAttributes) {
        return allAttributes.stream().filter(a -> a.getId() != null).mapToLong(a -> a.getId()).max().orElse(0) + 1;
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public ItemQueryResult query(long typeId, ItemsQueryData data) {
        return query(new NullGenericTypesystemContext(), typeId, data);
    }

    /**
     * Returns the query result as defined by {@link PersistenceService#query(GenericTypesystemContext, long, ItemsQueryData)}.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param data
     *            query data
     * @return query result
     */
    public ItemQueryResult query(GenericTypesystemContext context, long typeId, ItemsQueryData data) {
        return persistence.query(context, typeId, data);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public GenericItem item(long typeId, long id) {
        return item(new NullGenericTypesystemContext(), typeId, id);
    }

    /**
     * Returns the item as defined by {@link PersistenceService#item(GenericTypesystemContext, long, long)}.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return item
     */
    public GenericItem item(GenericTypesystemContext context, long typeId, long id) {
        return persistence.item(context, typeId, id);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public ValidationResult<GenericItem> item(long typeId, GenericItem item) {
        return item(new NullGenericTypesystemContext(), typeId, item);
    }

    /**
     * Ensures {@link GenericItem#getTypeId()} is set to given type, validates the type and item and if the result is valid
     * {@link PersistenceService#item(GenericTypesystemContext, GenericType, GenericItem)} will be invoked.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param item
     *            optional template item
     * @return validation result
     */
    public ValidationResult<GenericItem> item(GenericTypesystemContext context, long typeId, GenericItem item) {

        // get type
        GenericType type = persistence.type(context, typeId);

        // check type accessibility
        if (!context.isTypeAccessible(type)) {
            final ValidationResult<GenericItem> validationResult = new ValidationResult<>(null);
            validationResult.error("", DefaultValidationServiceMessageKey.GENERAL_TYPE_NOT_PROVIDED);
            return validationResult;
        }

        // check item accessibility
        if (!context.isItemAccessible(type, item)) {
            final ValidationResult<GenericItem> validationResult = new ValidationResult<>(null);
            validationResult.error("", DefaultValidationServiceMessageKey.GENERAL_ITEM_NOT_PROVIDED);
            return validationResult;
        }

        // ensure generic type id
        if (type != null && item != null) {
            item.setTypeId(typeId);
        }

        // validate
        ValidationResult<GenericItem> validationResult = validation.validate(type, item);

        // save / update
        if (validationResult.isValid()) {
            persistence.item(context, type, item);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid item " + typeId + "/" + (item != null ? item.getId() : null));
        }

        // done
        return validationResult;
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public Map<String, List<?>> values(long typeId, GenericItem template) {
        return values(new NullGenericTypesystemContext(), typeId, template);
    }

    /**
     * Returns the value proposals as defined by {@link PersistenceService#values(GenericTypesystemContext, long, GenericItem)}.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param template
     *            optional template item
     * @return value proposals
     */
    public Map<String, List<?>> values(GenericTypesystemContext context, long typeId, GenericItem template) {
        return persistence.values(context, typeId, template);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public boolean removeItem(long typeId, long id) {
        return removeItem(new NullGenericTypesystemContext(), typeId, id);
    }

    /**
     * Deletes the item as defined by {@link PersistenceService#removeItem(GenericTypesystemContext, long, long)}.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @param id
     *            item id
     * @return true if successful, false otherwise
     */
    public boolean removeItem(GenericTypesystemContext context, long typeId, long id) {
        return persistence.removeItem(context, typeId, id);
    }

    /**
     * @deprecated Please use overloaded method with {@link GenericTypesystemContext} as first parameter, this method will be removed in next release!
     */
    @Deprecated
    public boolean removeType(long typeId) {
        return removeType(new NullGenericTypesystemContext(), typeId);
    }

    /**
     * Deletes the type as defined by {@link PersistenceService#removeType(GenericTypesystemContext, long)}.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @return true if successful, false otherwise
     */
    public boolean removeType(GenericTypesystemContext context, long typeId) {
        return persistence.removeType(context, typeId);
    }
}
