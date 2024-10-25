package top.azusall.ironfistnew.entity;

import net.minecraft.network.packet.CustomPayload;

/**
 * @Author: liumingda
 * @Date: 2024/10/25 05:45
 * @Description:
 */
public interface MyPayloadBase extends CustomPayload {

    /**
     * 获取record value
     * @return
     */
    byte[] getValue();

}
