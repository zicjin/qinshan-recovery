package com.sunwayworld.framework;

import com.sunwayworld.framework.converter.exception.ConvertFailedException;
import com.sunwayworld.framework.converter.number.NumberToNumberConverterFactory;
import com.sunwayworld.framework.converter.sql.ClobToPrimitiveCharacterArrayConverter;
import com.sunwayworld.framework.converter.string.StringToNumberConverterFactory;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;

/** Direct public-seam checks documenting supplier-compatible converter quirks. */
public final class ReviewedValueContractTest {
    private static void equal(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but got " + actual);
        }
    }

    private static void historicalEmptyByte() {
        equal(null, StringToNumberConverterFactory.INSTANCE.getConverter(Byte.class).convert(""),
                "nullable Byte empty conversion");
        try {
            StringToNumberConverterFactory.INSTANCE.getConverter(byte.class).convert("");
            throw new AssertionError("primitive byte empty conversion must retain supplier failure");
        } catch (ClassCastException expected) {
            // Supplier's Character zero is an observed compatibility behavior.
        }
    }

    private static void historicalNumberPrecision() {
        NumberToNumberConverterFactory factory = NumberToNumberConverterFactory.INSTANCE;
        BigDecimal source = new BigDecimal("12345678901234567890.123456789000");
        equal(new BigDecimal("1.2345678901234567E+19"), factory.getConverter(BigDecimal.class).convert(source),
                "BigDecimal conversion follows supplier double path");
        BigInteger large = new BigInteger("123456789012345678901234567890");
        equal(new BigInteger("-4362896299872285998"), factory.getConverter(BigInteger.class).convert(large),
                "BigInteger conversion follows supplier long path");
    }

    private static void historicalClobMetadata() {
        final SQLException failure = new SQLException("synthetic length failure");
        Clob clob = (Clob) Proxy.newProxyInstance(ReviewedValueContractTest.class.getClassLoader(),
                new Class<?>[] {Clob.class}, (proxy, method, args) -> { throw failure; });
        try {
            ClobToPrimitiveCharacterArrayConverter.INSTANCE.convert(clob);
            throw new AssertionError("expected Clob conversion failure");
        } catch (ConvertFailedException expected) {
            equal("Converting from type [java.sql.Blob] to type [[B]", expected.getMessage(),
                    "supplier LOB error metadata");
            if (expected.getCause() != failure) throw new AssertionError("original JDBC cause must be retained");
        }
    }

    public static void main(String[] args) {
        historicalEmptyByte();
        historicalNumberPrecision();
        historicalClobMetadata();
        System.out.println("reviewed value compatibility contracts: PASS");
    }
}
