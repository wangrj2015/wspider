package com.rjay.wspider.service.leader;

import java.io.Serializable;

/**
 * 任务分发者
 */
public class Leader implements Serializable {

    private String host;

    private boolean active = false;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
