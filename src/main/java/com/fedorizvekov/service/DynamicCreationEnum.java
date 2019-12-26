package com.fedorizvekov.service;

import static java.lang.System.arraycopy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import jdk.internal.reflect.ReflectionFactory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DynamicCreationEnum {

    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();


    /**
     * Adds a new enum instance to the specified enum class.
     *
     * @param <T>      the type of the enum (implicit)
     * @param enumType the class of the enum to be modified.
     * @param enumName the name of the new enum instance to be added to the class.
     * @throws RuntimeException if an exception occurs during the operation, including reflection-related issues.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumType, String enumName) {

        try {
            var valuesField = Arrays.stream(enumType.getDeclaredFields())
                    .filter(field -> field.getName().contains("$VALUES"))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchFieldException("$VALUES field not found"));

            AccessibleObject.setAccessible(new Field[]{valuesField}, true);

            var previousValues = (T[]) valuesField.get(enumType);
            var values = new ArrayList<>(Arrays.asList(previousValues));
            var newValue = (T) makeEnum(enumType, enumName.toUpperCase(), values.size(), new Class<?>[]{}, new Object[]{});
            values.add(newValue);

            setFinalStaticField(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);

        } catch (Exception exception) {
            log.error(exception);
            throw new RuntimeException(exception.getMessage(), exception);
        }

    }


    /**
     * Creates a new instance of an enumeration (enum) class with specified values.
     *
     * @param enumClass        The enumeration class for which to create an instance.
     * @param value            The value for the new enumeration instance.
     * @param ordinal          The ordinal value for the new enumeration instance.
     * @param additionalTypes  An array of additional parameter types for the enumeration constructor.
     * @param additionalValues An array of additional parameter values for the enumeration constructor.
     * @return A new instance of the enumeration class with the specified values.
     * @throws InstantiationException    If the creation of the enum instance fails.
     * @throws InvocationTargetException If the constructor invocation fails.
     * @throws NoSuchMethodException     If the constructor for the enum class is not found.
     */
    private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException {

        var types = new Class[additionalTypes.length + 2];
        types[0] = String.class;
        types[1] = int.class;
        arraycopy(additionalTypes, 0, types, 2, additionalTypes.length);

        var constructor = enumClass.getDeclaredConstructor(types);
        constructor.setAccessible(true);

        var params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;
        arraycopy(additionalValues, 0, params, 2, additionalValues.length);

        var constructorAccessor = reflectionFactory.newConstructorAccessor(constructor);
        return enumClass.cast(constructorAccessor.newInstance(params));
    }


    /**
     * Sets the value of a final static field, bypassing its final modifier (non-standard way).
     *
     * @param field  The field to be modified.
     * @param target The object containing the field (null for static fields).
     * @param value  The new value to be set for the field.
     * @throws IllegalAccessException    If access to the field or method is denied.
     * @throws InvocationTargetException If an error occurs while invoking a method.
     * @throws NoSuchMethodException     If the specified method cannot be found.
     */
    private static void setFinalStaticField(Field field, Object target, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        field.setAccessible(true);

        var getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);
        var fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);

        var modifiersField = Arrays.stream(fields)
                .filter(declaredField -> "modifiers".equals(declaredField.getName()))
                .findFirst()
                .orElse(null);

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, modifiersField.getInt(field) & ~Modifier.FINAL);

        var fieldAccessor = reflectionFactory.newFieldAccessor(field, true);
        fieldAccessor.set(target, value);
    }


    /**
     * Cleans the enum cache for the given enum class, ensuring that any cached enum constant information is reset.
     *
     * @param enumClass The class of the enum for which the cache needs to be cleaned.
     * @throws IllegalAccessException    If access to the field or method is denied.
     * @throws InvocationTargetException If an error occurs while invoking a method.
     * @throws NoSuchMethodException     If the specified method cannot be found.
     */
    private static void cleanEnumCache(Class<?> enumClass) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        for (var field : Class.class.getDeclaredFields()) {
            var fieldName = field.getName();
            if (fieldName.contains("enumConstantDirectory") || fieldName.contains("enumConstants")) {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setFinalStaticField(field, enumClass, null);
            }
        }
    }

}
