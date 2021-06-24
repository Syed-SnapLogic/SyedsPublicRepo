/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syed.test.bctest;

import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public enum ObjectType {
    STRING("String"),
    BIG_INTEGER("BigInteger"),
    BIG_DECIMAL("BigDecimal"),
    INTEGER("Integer"),
    DOUBLE("Double"),
    SHORT("Short"),
    FLOAT("Float"),
    LONG("Long"),
    NULL("null"),
    MAP("Map"),
    BOOLEAN("Boolean"),
    LIST("List"),
    DATE("Date"),
    LOCAL_DATE("LocalDate"),
    LOCAL_TIME("LocalTime"),
    LOCAL_DATETIME("LocalDateTime"),
    PATH_REFERENCE("PathReference"),
    REGEXP("RegExp"),
    BYTE_ARRAY("byte[]"),
    FUNCTION("Function"),
    OTHER("Other"),
    CLASS("Class");

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT_LOCAL = ThreadLocal.withInitial(() -> {
        DecimalFormat retval = new DecimalFormat("0");
        retval.setMaximumFractionDigits(340);
        return retval;
    });
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[_a-zA-Z][a-zA-Z_0-9]*$");
    private final String name;
    private static final Map<Class, ObjectType> CLASS_TO_TYPE = 
            ImmutableMap.<Class,ObjectType> builder()
                    .put(BigInteger.class, BIG_INTEGER)
                    .put(BigDecimal.class, BIG_DECIMAL)
                    .put(Integer.class, INTEGER)
                    .put(Double.class, DOUBLE)
                    .put(Short.class, SHORT)
                    .put(Float.class, FLOAT)
                    .put(Long.class, LONG)
                    .put(String.class, STRING)
                    .put(Boolean.class, BOOLEAN)
                    .put(LocalDate.class, LOCAL_DATE)
                    .put(LocalTime.class, LOCAL_TIME)
                    .put(LocalDateTime.class, LOCAL_DATETIME)
                    .put(byte[].class, BYTE_ARRAY)
                    .put(HashMap.class, MAP)
                    .put(LinkedHashMap.class, MAP)
                    .put(ArrayList.class, LIST)
                    .build();

    private ObjectType(String name) {
        this.name = name;
    }

    public static final ObjectType objectToType(Object obj) {
        if (obj == null) {
            return NULL;
        } else {
            ObjectType type = (ObjectType)CLASS_TO_TYPE.get(obj.getClass());
            if (type != null) {
                return type;
            } else if (obj instanceof Map) {
                return MAP;
            } else if (obj instanceof List) {
                return LIST;
            } 
        }
        return null;
    }

    public static Object attemptToConvertToBigDecimal(Object obj) {
        String num;
        switch(objectToType(obj)) {
        case NULL:
            obj = BigDecimal.ZERO;
            break;
        case FLOAT:
            Float fl = (Float)obj;
            if (fl.isInfinite()) {
                if (fl == 1.0F / 0.0) {
                    obj = 1.0D / 0.0;
                } else {
                    obj = -1.0D / 0.0;
                }
            } else if (fl.isNaN()) {
                obj = 0.0D / 0.0;
            } else {
                obj = new BigDecimal(obj.toString());
            }
            break;
        case DOUBLE:
            Double d = (Double)obj;
            if (!d.isInfinite() && !d.isNaN()) {
                obj = new BigDecimal(obj.toString());
            }
        case BIG_DECIMAL:
            break;
        case BYTE_ARRAY:
            try {
                num = new String((byte[])obj, StandardCharsets.UTF_8);
                if (NumUtils.isNumber(num)) {
                    obj = new BigDecimal(num);
                }
            } catch (Exception var6) {
            }
            break;
        case BIG_INTEGER:
            obj = new BigDecimal((BigInteger)obj);
            break;
        case INTEGER:
        case SHORT:
        case LONG:
            obj = new BigDecimal(obj.toString());
        case STRING:
            if (obj instanceof BigDecimal) {
            return obj;
        }
            num = (String)obj;

            try {
                obj = tryToConvertString(num);
            } catch (Exception var5) {
            }
            break;
        case BOOLEAN:
            obj = (Boolean)obj ? BigDecimal.ONE : BigDecimal.ZERO;
            break;
        default:
            obj = 0.0D / 0.0;
        }

        return obj;
    }
    
    public static BigDecimal tryToConvertString(String str) {
        return StringUtils.isBlank(str) ? BigDecimal.ZERO : new BigDecimal(str);
    }
}