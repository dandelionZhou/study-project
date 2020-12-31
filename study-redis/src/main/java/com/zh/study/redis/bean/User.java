package com.zh.study.redis.bean;

import org.springframework.stereotype.Repository;

/**
 * @date 2020/12/31
 */
@Repository
public class User {
    private long id;
    private String userName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
