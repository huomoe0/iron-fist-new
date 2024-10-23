package top.azusall.ironfistnew.entity;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import top.azusall.ironfistnew.IronFistNew;

public record S2CInitSyncPayload(byte[] value) implements CustomPayload {
  public static final Id<S2CInitSyncPayload> ID = IronFistNew.INITIAL_SYNC_ID;
  public static final PacketCodec<RegistryByteBuf, S2CInitSyncPayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(S2CInitSyncPayload::new, S2CInitSyncPayload::value).cast();


  @Override
  public Id<S2CInitSyncPayload> getId() {
    return ID;
  }
}