/*
 *
 */
package com.lkkhpg.dsis.platform.dto.system;

import com.lkkhpg.dsis.platform.dto.BaseDTO;

/**
 * 短信账号 vo
 * @author Clerifen Li
 */
public class MessageSmsAccountVo extends BaseDTO {
 
    private static final long serialVersionUID = -682617301774945684L;

    private Long accountId;

    private String accountCode;

    private String host;

    private String port;

    private Long tryTimes;

    private String userName;

    private String password;

    private Long marketId;

    private String marketName;
    
    private String description;

    private String organization;

    private String useWhiteList;
    
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Long getTryTimes() {
        return tryTimes;
    }

    public void setTryTimes(Long tryTimes) {
        this.tryTimes = tryTimes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getUseWhiteList() {
        return useWhiteList;
    }

    public void setUseWhiteList(String useWhiteList) {
        this.useWhiteList = useWhiteList;
    }
    
}