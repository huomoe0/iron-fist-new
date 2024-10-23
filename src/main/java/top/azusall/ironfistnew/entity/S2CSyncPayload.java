package top.azusall.ironfistnew.entity;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import top.azusall.ironfistnew.IronFistNew;

public record S2CSyncPayload(byte[] value) implements CustomPayload {
  public static final CustomPayload.Id<S2CSyncPayload> ID = IronFistNew.IRONFISTNEW_ID;
  public static final PacketCodec<RegistryByteBuf, S2CSyncPayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(S2CSyncPayload::new, S2CSyncPayload::value).cast();


  @Override
  public CustomPayload.Id<S2CSyncPayload> getId() {
    return ID;
  }
}