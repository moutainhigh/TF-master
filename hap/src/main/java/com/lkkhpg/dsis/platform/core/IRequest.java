/*
 * Copyright LKK Health Products Group Limited
 */

package com.lkkhpg.dsis.platform.core;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * service 请求上下文.
 * <p>
 * 一些与 session 相关的数据可以放在这里面,一起传给 service 的方法.
 * <p>
 * 还可以通过一个 map 附加一些额外的属性.<br>
 * 注意,这个 map 初始为 null,当首次调用 {@code setAttribute(String)} 时初始化.
 * 
 * @author shengyang.zhou@hand-china.com
 */
public interface IRequest extends Serializable {

    String FIELD_ACCOUNTID = "accountId";
    String FIELD_LOCALE = "locale";
    String FIELD_ROLEID = "roleId";

    String MDC_PREFIX = "MDC.";

    Long getAccountId();

    /**
     * 获取扩展属性.
     * 
     * @param name
     *            扩展属性名
     * @param <T>
     *            返回类型,自动匹配
     * @return 扩展属性,如果 attributeMap 尚未初始化,返回 null
     */
    <T> T getAttribute(String name);

    /**
     * 获取属性Map.
     * 
     * @return 一个包含扩展属性的 map,有可能返回 null.(初始为 null)
     */
    Map<String, Object> getAttributeMap();

    /**
     * 获取属性Name.
     * 
     * @return 属性名 Enumeration
     */
    Set<String> getAttributeNames();

    /**
     * 获取公司ID.
     * 
     * @return 公司 id
     */
    Long getCompanyId();

    /**
     * 获取语言环境.
     * 
     * @return lang code
     */
    String getLocale();

    /**
     * 获取角色ID.
     * 
     * @return 角色 id
     */
    Long getRoleId();

    /**
     * 设置角色ID.
     * 
     * @param accountId
     *            账户 id
     */
    void setAccountId(Long accountId);

    /**
     * 设置扩展属性.
     * <p>
     * 首次调用该方法时,初始化 attributeMap
     * 
     * @param name
     *            属性名
     * @param value
     *            属性值
     */
    void setAttribute(String name, Object value);

    /**
     * 设置公司ID.
     * 
     * @param companyId
     *            公司 id
     */
    void setCompanyId(Long companyId);

    /**
     * 设置语言环境.
     * 
     * @param locale
     *            语言 code
     */
    void setLocale(String locale);

    /**
     * 设置角色ID.
     * 
     * @param roleId
     *            角色 id
     */
    void setRoleId(Long roleId);
}
