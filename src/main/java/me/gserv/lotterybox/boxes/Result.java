package me.gserv.lotterybox.boxes;

import java.util.HashMap;

public class Result {

    private final HashMap<String, Object> reward;
    private final boolean success;

    private final Reason reason;
    private final String name;

    public Result(String name) {
        this.name = name;
        this.success = false;
        this.reward = null;
        this.reason = Reason.FAILED;
    }

    public Result(String name, Reason reason) {
        this.name = name;
        this.success = false;
        this.reward = null;
        this.reason = reason;
    }

    public Result(String name, Reason reason, HashMap<String, Object> reward) {
        this.name = name;
        this.success = true;
        this.reward = reward;
        this.reason = reason;
    }

    public String getName() {
        return this.name;
    }

    public Reason getReason() {
        return this.reason;
    }

    public HashMap<String, Object> getReward() {
        return this.reward;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
