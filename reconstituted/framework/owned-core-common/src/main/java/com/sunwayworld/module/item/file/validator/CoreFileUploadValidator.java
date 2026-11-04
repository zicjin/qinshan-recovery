/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.validator;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class CoreFileUploadValidator
implements DataValidator {
    private static final BigDecimal byteUnitNumber = new BigDecimal(0x100000);

    @Override
    public boolean doValid(Object ... args) {
        MultipartFile file = Objects.requireNonNull((MultipartFile)args[1]);
        if (file == null || file.isEmpty()) {
            this.addConstraintViolation("GIKAM.FILE.TIP.NOTFILEUPLOAD");
            return false;
        }
        String fileExt = FileUtils.getFileExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(fileExt)) {
            this.addConstraintViolation("GIKAM.FILE.TIP.NOTFILEEXTENSION");
            return false;
        }
        String validFileExtensions = ApplicationContextHelper.getConstantValue("FILE-EXTENSIONS");
        if (!Arrays.asList(StringUtils.split(validFileExtensions, ",")).contains(fileExt.toLowerCase())) {
            this.addConstraintViolation("GIKAM.FILE.TIP.UNSUPPORTEDEXTENSION");
            return false;
        }
        double spaceLimit = NumberUtils.parseNumber(ApplicationContextHelper.getConstantValue("FILE-SPACELIMIT"), Double.TYPE);
        if (spaceLimit == 0.0) {
            spaceLimit = 100.0;
        }
        BigDecimal spaceLimitNumber = new BigDecimal(spaceLimit).multiply(byteUnitNumber);
        BigDecimal fileSizeNumber = new BigDecimal(file.getSize());
        if (!file.isEmpty() && fileSizeNumber.compareTo(spaceLimitNumber) == 1) {
            this.addConstraintViolation("GIKAM.FILE.TIP.FILELIMITEXCEEDED");
            return false;
        }
        return true;
    }
}
