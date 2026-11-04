/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import java.util.List;

public interface CorePasswordPolicyService
extends GenericService<CorePasswordPolicyBean, Long>,
GenericSuspendableService<CorePasswordPolicyBean, Long>,
GenericSelectableService<CorePasswordPolicyBean, Long> {
    @Override
    public Long insert(RestJsonWrapperBean var1);

    public List<CorePasswordPolicyBean> selectEffectedList(String var1);

    public void checkPassword(String var1, String var2);

    public CorePasswordPolicyBean selectMatchingPasswordPolicy();

    public CorePasswordPolicyBean selectMatchingPasswordPolicy(String var1);

    public Long selectPasswordExpireRemainingDays();

    public Long selectPasswordExpireRemainingDays(List<CorePasswordPolicyBean> var1);

    public Long selectPasswordExpireRemainingDays(CoreUserBean var1, List<CorePasswordPolicyBean> var2);

    public Long selectPasswordExpiredMaxUse(List<CorePasswordPolicyBean> var1);
}
