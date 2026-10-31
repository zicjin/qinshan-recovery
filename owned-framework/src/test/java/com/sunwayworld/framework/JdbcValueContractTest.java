package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.data.JdbcValue;
import javax.sql.rowset.serial.SerialClob;

/** Guards the Oracle CLOB-to-JSON conversion used by original project pages. */
public final class JdbcValueContractTest {
    public static void main(String[] args) throws Exception {
        Object value = JdbcValue.normalize(new SerialClob("组织甲".toCharArray()));
        if (!(value instanceof String) || !"组织甲".equals(value)) {
            throw new AssertionError("CLOB was not converted to its text value: " + value);
        }
        if (JdbcValue.normalize(null) != null) throw new AssertionError("null normalization changed");
        System.out.println("jdbc value contract checks passed");
    }
}
