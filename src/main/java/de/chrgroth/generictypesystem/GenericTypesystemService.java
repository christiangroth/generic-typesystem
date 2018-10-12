package de.chrgroth.generictypesystem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.generictypesystem.context.GenericTypesystemContext;
import de.chrgroth.generictypesystem.model.DefaultGenericAttributeType;
import de.chrgroth.generictypesystem.model.GenericAttribute;
import de.chrgroth.generictypesystem.model.GenericItem;
import de.chrgroth.generictypesystem.model.GenericStructure;
import de.chrgroth.generictypesystem.model.GenericType;
import de.chrgroth.generictypesystem.model.GenericUnit;
import de.chrgroth.generictypesystem.model.GenericUnits;
import de.chrgroth.generictypesystem.model.GenericValue;
import de.chrgroth.generictypesystem.model.UnitValue;
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
public class GenericTypesystemService {

    private static final Logger LOG = LoggerFactory.getLogger(GenericTypesystemService.class);

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PersistenceService persistence;
    private final ValidationService validation;

    /**
     * Creates a new service instance with the given validation and persistence services.
     *
     * @param validation
     *            validation service , null will create a new instance of {@link DefaultValidationService}
     * @param persistence
     *            persistence service , null will create a new instance of {@link InMemoryPersistenceService}
     */
    public GenericTypesystemService(ValidationService validation, PersistenceService persistence) {
        this.persistence = persistence != null ? persistence : new InMemoryPersistenceService(new InMemoryItemsQueryService(DEFAULT_PAGE_SIZE), new InMemoryValueProposalService());
        this.validation = validation != null ? validation : new DefaultValidationService(unitsId -> persistence.units(null, unitsId), new DefaultValidationServiceEmptyHooks());
    }

    /**
     * Returns all units as defined by {@link PersistenceService#units(GenericTypesystemContext)}.
     *
     * @param context
     *            current context
     * @return units
     */
    public Set<GenericUnits> units(GenericTypesystemContext context) {
        return persistence.units(context);
    }

    /**
     * Ensures an unique id for all sub units, validates the units and if the resulz is valid
     * {@link PersistenceService#units(GenericTypesystemContext, GenericUnits)} will be invoked.
     *
     * @param context
     *            current context
     * @param units
     *            units to be handled
     * @return validation result
     */
    public ValidationResult<GenericUnits> units(GenericTypesystemContext context, GenericUnits units) {

        // ensure all type attributes have an id
        if (units != null && units.getUnits() != null) {
            Set<GenericUnit> allUnits = units.getUnits();
            for (GenericUnit unit : allUnits) {
                if (unit.getId() == null || unit.getId().longValue() < 1) {

                    // get next unique id
                    long nextUnitsUnitId = nextUnitsUnitId(allUnits);

                    // set id
                    unit.setId(nextUnitsUnitId);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("unit " + unit.getName() + " id resolved to " + nextUnitsUnitId);
                    }
                }
            }
        }

        // validate
        ValidationResult<GenericUnits> validationResult = validation.validate(units);

        // save / update
        if (validationResult.isValid()) {
            persistence.units(context, units);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid units " + (units != null ? units.getId() : null));
        }

        // done
        return validationResult;
    }

    private long nextUnitsUnitId(Set<GenericUnit> allUnits) {
        return allUnits.stream().filter(u -> u.getId() != null).mapToLong(u -> u.getId()).max().orElse(0) + 1;
    }

    /**
     * Converts the given value to the target value. The return value is wrapped into a validation result an in case the target unit does not exist, the result
     * will be not valid. The type of the converted value will be as follows
     * <table summary="Type mapping">
     * <tr>
     * <th>Input type</th>
     * <th>Output type</th>
     * </tr>
     * <tr>
     * <td>Integer</td>
     * <td>Integer if no rounding required, Double otherwise</td>
     * </tr>
     * <tr>
     * <td>Long</td>
     * <td>Long if no rounding required, Double otherwise</td>
     * </tr>
     * <tr>
     * <td>Float</td>
     * <td>Float</td>
     * </tr>
     * <tr>
     * <td>Double</td>
     * <td>Double</td>
     * </tr>
     * </table>
     *
     * @param context
     *            current context
     * @param value
     *            value to be converted
     * @param targetUnitId
     *            id of target unit from same units
     * @return result of conversion
     */
    public ValidationResult<UnitValue> convert(GenericTypesystemContext context, UnitValue value, long targetUnitId) {
        final ValidationResult<UnitValue> validationResult = new ValidationResult<>(value);

        // null guard
        if (value == null || value.getValue() == null || value.getValue().getValue() == null) {
            validationResult.error("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_NOT_AVAILABLE);
            return validationResult;
        }

        // type check
        final Class<?> inputType = value.getValue().getType();
        if (!DefaultGenericAttributeType.DOUBLE.getTypeClasses().contains(inputType)) {
            validationResult.error("", DefaultValidationServiceMessageKey.UNITS_CONVERT_VALUE_TYPE_NOT_SUPPORTED);
            return validationResult;
        }

        // ensure units
        final Long unitsId = value.getUnitsId();
        GenericUnits units = unitsId == null ? null : persistence.units(context, unitsId);
        if (units == null) {
            validationResult.error("unitsId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNITS_NOT_AVAILABLE);
            return validationResult;
        }

        // ensure units valid
        ValidationResult<GenericUnits> unitsValidationResult = validation.validate(units);
        if (!unitsValidationResult.isValid()) {
            validationResult.error("unitsId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNITS_NOT_VALID);
            return validationResult;
        }

        // ensure source and target unit
        GenericUnit source = value.getUnitId() == null ? null : units.unit(value.getUnitId());
        if (source == null) {
            validationResult.error("unitId", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNIT_SOURCE_NOT_AVAILABLE);
            return validationResult;
        }
        GenericUnit target = units.unit(targetUnitId);
        if (target == null) {
            validationResult.error("", DefaultValidationServiceMessageKey.UNITS_CONVERT_UNIT_TARGET_NOT_AVAILABLE);
            return validationResult;
        }

        // calculate new value
        BigDecimal convertedValue = BigDecimal.valueOf(((Number) value.getValue().getValue()).doubleValue());
        if (!source.isBase()) {
            convertedValue = convertedValue.divide(BigDecimal.valueOf(source.getFactor()));
        }
        if (!target.isBase()) {
            convertedValue = convertedValue.multiply(BigDecimal.valueOf(target.getFactor()));
        }

        // create resulting value
        final GenericValue<? extends Number> resultingValue;
        if (Integer.class.equals(inputType) || Long.class.equals(inputType)) {

            // check if rounding is required
            boolean roundingRequired;
            try {
                convertedValue = convertedValue.setScale(0);
                roundingRequired = false;
            } catch (ArithmeticException e) {
                roundingRequired = true;
            }

            // convert
            if (!roundingRequired) {
                if (Integer.class.equals(inputType)) {
                    resultingValue = new GenericValue<>(Integer.class, convertedValue.intValue());
                } else {
                    resultingValue = new GenericValue<>(Long.class, convertedValue.longValue());
                }
            } else {

                // fallback - stay with double value
                resultingValue = new GenericValue<>(Double.class, convertedValue.doubleValue());
            }
        } else if (Float.class.equals(inputType)) {
            resultingValue = new GenericValue<>(Float.class, convertedValue.floatValue());
        } else {
            resultingValue = new GenericValue<>(Double.class, convertedValue.doubleValue());
        }

        // done
        return new ValidationResult<>(new UnitValue(unitsId, targetUnitId, resultingValue));
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
     * Returns all types as defined by {@link PersistenceService#types(GenericTypesystemContext)}.
     *
     * @param context
     *            current context
     * @return types
     */
    public Set<GenericType> types(GenericTypesystemContext context) {
        return persistence.types(context);
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
     * Create a new item for given type. Configured default values are already set in returned item.
     *
     * @param context
     *            current context
     * @param typeId
     *            type id
     * @return new item with pre-configured default values or null if type is unknown or not accessible
     */
    public GenericItem create(GenericTypesystemContext context, long typeId) {

        // get type
        GenericType type = persistence.type(context, typeId);
        if (type == null) {
            return null;
        }

        // create item
        GenericItem item = new GenericItem();
        item.setTypeId(typeId);
        item.setOwner(context.currentUser());

        // fill default values
        create(item, "", type);

        // done
        return item;
    }

    private void create(GenericItem item, String pathPrefix, GenericStructure structure) {

        // null guard
        if (structure == null || structure.getAttributes() == null) {
            return;
        }

        // walk all attributes
        for (GenericAttribute attribute : structure.getAttributes()) {

            // recurse
            if (attribute.getStructure() != null) {
                create(item, pathPrefix + attribute.getName() + ".", attribute.getStructure());
                continue;
            }

            // set default value
            if (attribute.getDefaultValue() != null && attribute.getDefaultValue().getValue() != null) {
                item.set(pathPrefix + attribute.getName(), attribute.getDefaultValue().getValue());
            }
        }
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
     * Ensures {@link GenericItem#getTypeId()} is set to given type, validates the type and item and if the result is valid
     * {@link PersistenceService#item(GenericTypesystemContext, long, GenericItem)} will be invoked.
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
            persistence.item(context, typeId, item);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("skip persisting invalid item " + typeId + "/" + (item != null ? item.getId() : null));
        }

        // done
        return validationResult;
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

    /**
     * Deletes the units as defined by {@link PersistenceService#removeUnits(GenericTypesystemContext, long)}.
     *
     * @param context
     *            current context
     * @param unitsId
     *            units id
     * @return true if successful, false otherwise
     */
    public boolean removeUnits(GenericTypesystemContext context, long unitsId) {
        return persistence.removeUnits(context, unitsId);
    }
}
