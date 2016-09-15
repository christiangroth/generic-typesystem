package de.chrgroth.generictypesystem.model;

/**
 * The base interface for all attribute types.
 *
 * @author Christian Groth
 */
public interface GenericAttributeType {

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
}
