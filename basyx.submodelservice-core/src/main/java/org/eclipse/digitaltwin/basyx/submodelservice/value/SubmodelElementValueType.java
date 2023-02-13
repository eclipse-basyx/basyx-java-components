package org.eclipse.digitaltwin.basyx.submodelservice.value;

public enum SubmodelElementValueType {
    RANGE,
    MULTI_LANGUAGE_PROPERTY_VALUE,
    PROPERTY,
    FILE;

    public static SubmodelElementValueType fromString(String type) {
        switch(type) {
            case "RangeValue": return RANGE;
            case "MultiLanguagePropertyValue": return MULTI_LANGUAGE_PROPERTY_VALUE;
            case "PropertyValue": return PROPERTY;
            case "FileValue": return FILE;
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
}
