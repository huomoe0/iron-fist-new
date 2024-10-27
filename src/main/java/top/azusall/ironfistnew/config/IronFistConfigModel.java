package top.azusall.ironfistnew.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author houmo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IronFistConfigModel {
    private int energyRecoveryFactor = 100000;
    private int speedMultiple = 4;
    private int millisecondsHardnessOne = 1000;
    private float energyThreshold = 0.2F;
    private float damageAmount = 2.0F;
    private float minHealth = 0.1F;
    private HashMap<Integer, ArrayList<String>> level;


}