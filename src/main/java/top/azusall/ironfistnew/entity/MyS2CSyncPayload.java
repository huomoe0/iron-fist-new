package top.azusall.ironfistnew.entity;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import top.azusall.ironfistnew.IronFistNew;

public record MyS2CSyncPayload(byte[] value) implements MyPayloadBase {
  public static final CustomPayload.Id<MyS2CSyncPayload> ID = IronFistNew.IRONFISTNEW_ID;
  public static final PacketCodec<RegistryByteBuf, MyS2CSyncPayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(MyS2CSyncPayload::new, MyS2CSyncPayload::value).cast();


    @Override
  public CustomPayload.Id<MyS2CSyncPayload> getId() {
    return ID;
  }

  @Override
  public byte[] getValue() {
    return value;
  }
}