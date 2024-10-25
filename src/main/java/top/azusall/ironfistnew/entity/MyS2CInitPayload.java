package top.azusall.ironfistnew.entity;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import top.azusall.ironfistnew.IronFistNew;

public record MyS2CInitPayload(byte[] value) implements MyPayloadBase {
  public static final Id<MyS2CInitPayload> ID = IronFistNew.INITIAL_SYNC_ID;
  public static final PacketCodec<RegistryByteBuf, MyS2CInitPayload> CODEC = PacketCodecs.BYTE_ARRAY.xmap(MyS2CInitPayload::new, MyS2CInitPayload::value).cast();


    @Override
  public Id<MyS2CInitPayload> getId() {
    return ID;
  }

  @Override
  public byte[] getValue() {
    return value;
  }


}