/*
 *
 */

package com.lkkhpg.dsis.platform.security;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lkkhpg.dsis.platform.dto.BaseDTO;
import com.lkkhpg.dsis.platform.dto.DTOClassInfo;
import com.lkkhpg.dsis.platform.exception.TokenException;

/**
 * 提供用于生成和校验 _token (BaseDTO的防篡改属性)的方法.
 *
 * @author shengyang.zhou@hand-china.com
 */
public final class TokenUtils {
    public static final String SECURITY_KEY = "securityKey";

    private static Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    private TokenUtils() {
    }

    /**
     * 从session中取得securityKey.
     * 
     * @param session
     *            http session. 为null时,返回 null
     * @return securityKey , 有可能是 null
     */
    public static String getSecurityKey(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(SECURITY_KEY);
    }

    /**
     * 生成一个 uuid 形式的 securityKey,并设置到 session 中
     * <p>
     * 如果 session 中已经存在 securityKey,它会被新的值覆盖!
     *
     * @param session
     *            不能为 null
     * @return 生成的 securityKey (uuid 形式,不会是 null)
     */
    public static String setSecurityKey(HttpSession session) {
        String sec_key = UUID.randomUUID().toString();
        session.setAttribute(SECURITY_KEY, sec_key);
        return sec_key;
    }

    /**
     * 使用指定的 securityKey为 dto 生成 _token.
     * <p>
     * 注意:此方法仅仅生成_token,并不会将结果设置到 BaseDTO 中
     *
     * @param securityKey
     *            uuid 形式
     * @param dto
     *            数据
     * @param ids  id
     * @return 生成的token
     */
    public static String generateToken(String securityKey, BaseDTO dto, String[] ids) {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append(securityKey).append(':').append(dto.getClass().getName());
        if (ids == null) {
            for (Field f : DTOClassInfo.getIdFields(dto.getClass())) {
                try {
                    stringBuilder.append(':').append(Objects.toString(f.get(dto), ""));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            for (String v : ids) {
                stringBuilder.append(':').append(v);
            }
        }
        return DigestUtils.md5Hex(stringBuilder.toString());
    }


    /**
     * 生成_token,并设置到 dto.
     *
     * @param securityKey
     *            校验码,一般来自 session
     * @param dto
     *            数据
     * @see #generateToken(String, BaseDTO)
     */
    public static void generateAndSetToken(String securityKey, BaseDTO dto) {
        String token = generateToken(securityKey, dto, null);
        dto.set_token(token);
    }

    /**
     * 批量生成,设置_token.
     *
     * @param securityKey
     *            校验码,一般来自 session
     * @param dtos
     *            一组 dto
     * @see #generateAndSetToken(String, BaseDTO)
     */
    public static void generateAndSetToken(String securityKey, Collection<? extends BaseDTO> dtos) {
        for (BaseDTO dto : dtos) {
            generateAndSetToken(securityKey, dto);
        }
    }

    /**
     * 使用指定的 securityKey 对 BaseDTO 中的_token 进行校验.
     * <p>
     * 此方法通过异常来决定校验是否成功
     *
     * @param securityKey
     *            校验码,一般来自 session
     * @param dto
     *            不能为 null
     * @throws TokenException
     *             _token 为 null 或者 校验结果不匹配
     */
    public static void checkToken(String securityKey, BaseDTO dto) throws TokenException {
        Field[] idFields = DTOClassInfo.getIdFields(dto.getClass());
        if (idFields.length == 0) {
            throw new TokenException(dto);
        }
        String[] ids  = new String[idFields.length];
        for (int i = 0; i < idFields.length; i++) {
            try {
                ids[i] = Objects.toString(idFields[i].get(dto), "");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.join(ids, "").trim().length() == 0) {
            return;
        }
        
        String token = dto.get_token();
        if (token == null) {
            throw new TokenException(TokenException.MSG_NOT_EXISTS, dto);
        }
        if (!token.equalsIgnoreCase(generateToken(securityKey, dto, ids))) {
            if (logger.isDebugEnabled()) {
                logger.debug("token check failed.token:{}, class:{}", dto.get_token(), dto.getClass().getName());
            }
            throw new TokenException(dto);
        }

        for (Field f : DTOClassInfo.getChildrenFields(dto.getClass())) {
            try {
                Object fieldValue = f.get(dto);
                if (fieldValue instanceof BaseDTO) {
                    checkToken(securityKey, (BaseDTO) fieldValue);
                } else if (fieldValue instanceof Collection) {
                    checkToken(securityKey, (Collection) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 从session取得securityKey,调用 {@link #checkToken(String, BaseDTO)}.
     *
     * @param session
     *            HttpSession
     * @param dto
     *            待校验dto
     * @throws TokenException
     *             没有_token,或者不匹配
     */
    public static void checkToken(HttpSession session, BaseDTO dto) throws TokenException {
        String securityKey = getSecurityKey(session);
        if (securityKey == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("check skipped due to securityKey not exists.");
            }
            return;
        }
        checkToken(securityKey, dto);
    }

    /**
     * 循环调用 {@link #checkToken(String, BaseDTO)}.
     * 
     * @param securityKey
     *            校验码,一般来自 session
     * @param baseDTOs
     *            待校验dto集合
     * @throws TokenException
     *             没有_token,或者不匹配
     */
    public static void checkToken(String securityKey, Collection<? extends BaseDTO> baseDTOs) throws TokenException {
        if (baseDTOs != null) {
            for (BaseDTO dto : baseDTOs) {
//                if (!DTOStatus.ADD.equals(dto.get__status())) {
                    checkToken(securityKey, dto);
//                }
            }
        }
    }

    /**
     * 从session取得securityKey,调用 {@link #checkToken(String, Collection)}.
     *
     * @param session
     *            HttpSession
     * @param baseDTOs
     *            待校验dto集合
     * @throws TokenException
     *             没有_token,或者不匹配
     */
    public static void checkToken(HttpSession session, Collection<? extends BaseDTO> baseDTOs) throws TokenException {
        String securityKey = getSecurityKey(session);
        if (securityKey == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("check skipped due to securityKey not exists.");
            }
            return;
        }
        checkToken(securityKey, baseDTOs);
    }

}
