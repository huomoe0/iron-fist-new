package top.azusall.ironfistnew.entity;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import top.azusall.ironfistnew.IronFistNew;

public record MyC2SSyncPayload(byte[] value) implements MyPayloadBase {
  public static final Id<MyC2SSyncPayload> ID = IronFistNew.IRONFISTNEW_ID;
  public static final PacketCodec<RegistryByteBuf, MyC2SSyncPayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(MyC2SSyncPayload::new, MyC2SSyncPayload::value).cast();


    @Override
  public Id<MyC2SSyncPayload> getId() {
    return ID;
  }

  @Override
  public byte[] getValue() {
    return value;
  }
}