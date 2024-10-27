package top.azusall.ironfistnew.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author houmo
 */
@Data
@Slf4j
@AllArgsConstructor
public class IronFistPlayer {

    /**
     * 拳头等级
     */
    private int fistLevel;
    /**
     * 拳头经验
     */
    private double fistXp;

    /**
     * 累计
     */
    private float cumulativeWork;
    /**
     * 上次破坏方块时间
     */
    private long lastBreakMillis;
    /**
     * 精力值
     */
    private double energy;


    public IronFistPlayer() {
        // Initialize default data
        this.fistLevel = 1;
        this.fistXp = 0.0D;
        this.energy = 1;
        this.cumulativeWork = 0.0F;
        this.lastBreakMillis = 0L;
    }

}
