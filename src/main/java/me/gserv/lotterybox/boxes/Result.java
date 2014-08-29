package me.gserv.lotterybox.boxes;

import java.util.HashMap;

public class Result {

    private final HashMap<String, Object> reward;
    private final boolean success;
    private final Reason reason;

    public Result() {
        this.success = false;
        this.reward = null;
        this.reason = Reason.FAILED;
    }

    public Result(Reason reason) {
        this.success = false;
        this.reward = null;
        this.reason = reason;
    }

    public Result(Reason reason, HashMap<String, Object> reward) {
        this.success = true;
        this.reward = reward;
        this.reason = reason;
    }

    public HashMap<String, Object> getReward() {
        return reward;
    }

    public boolean isSuccess() {
        return success;
    }
}
