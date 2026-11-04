/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.specialcharacter.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.specialcharacter.controller.CoreSpecialCharacterController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreSpecialCharacterControllerImpl
implements CoreSpecialCharacterController {
    @Override
    public String editListPage() {
        return "module/item/special-characters/core-special-character-edit-list";
    }
}
