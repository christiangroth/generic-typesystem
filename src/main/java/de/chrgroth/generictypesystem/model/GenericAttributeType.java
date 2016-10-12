package de.chrgroth.generictypesystem.model;

/**
 * The base interface for all attribute types.
 *
 * @author Christian Groth
 */
public interface GenericAttributeType {

    /**
     * Checks if the type is numeric.
     *
     * @return true if the attribute type is numeric, false otherwise
     */
    boolean isNumeric();

    /**
     * Checks if the type is text.
     *
     * @return true if the attribute type is text, false otherwise
     */
    boolean isText();

    /**
     * Checks if the type is capable of min and max bounds.
     *
     * @return true if the attribute type is min and max capable, false otherwise
     */
    boolean isMinMaxCapable();

    /**
     * Checks if the type is capable of step configuration.
     *
     * @return true if the attribute type is step capable, false otherwise
     */
    boolean isStepCapable();

    /**
     * Checks if the type is capable of pattern validation.
     *
     * @return true if the attribute type is pattern capable, false otherwise
     */
    boolean isPatternCapable();

    /**
     * Checks if the type is capable of value proposal dependencies.
     *
     * @return true if the attribute type is value proposal dependencies capable, false otherwise
     */
    boolean isValueProposalDependenciesCapable();

    /**
     * Checks if the type is capable of units.
     *
     * @return true if the attribute type is unit capable, false otherwise
     */
    boolean isUnitCapable();

    /**
     * Checks if the type is capable of default values.
     *
     * @return true if the attribute type is default valuecapable, false otherwise
     */
    boolean isDefaultValueCapable();

    /**
     * Checks if the type is a list type.
     *
     * @return true if the attribute type represents a list, false otherwise
     */
    boolean isList();

    /**
     * Checks if the type is a structured type.
     *
     * @return true if the attribute type represents a structure, false otherwise
     */
    boolean isStructure();

    /**
     * Checks if the given class is assignable for the attribute type.
     *
     * @param actualClass
     *            class to be checked
     * @return true if assignable, false otherwise
     */
    boolean isAssignableFrom(Class<?> actualClass);

    /**
     * Converts the given value from any class returning true for {@link #isAssignableFrom(Class)} to the java main type. Unfortunately the main type can't b
     * expressed as generic on enumerations because of java language specification boundaries, so we need to return object. Sorry for that!! If
     * {@link #isDefaultValueCapable()} returns fale this method will always return null!
     *
     * @param value
     *            value to be converted
     * @return converted value of null is {@link #isAssignableFrom(Class)} returns false
     */
    Object convert(Object value);
}
