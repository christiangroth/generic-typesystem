package de.chrgroth.generictypesystem.model;

/**
 * A simple POJO representing a unit based value.
 *
 * @author Christian Groth
 */
public final class UnitValue {

    private Long unitsId;
    private Long unitId;
    private GenericValue<?> value;

    public UnitValue() {
        this(null, null, null);
    }

    public UnitValue(Long unitsId, Long unitId, GenericValue<?> value) {
        this.unitsId = unitsId;
        this.unitId = unitId;
        this.value = value;
    }

    public Long getUnitsId() {
        return unitsId;
    }

    public void setUnitsId(Long unitsId) {
        this.unitsId = unitsId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public GenericValue<?> getValue() {
        return value;
    }

    public void setValue(GenericValue<?> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UnitValue [unitsId=" + unitsId + ", unitId=" + unitId + ", value=" + value + "]";
    }
}
