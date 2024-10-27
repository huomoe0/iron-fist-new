package top.azusall.ironfistnew.entity;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import top.azusall.ironfistnew.IronFistNew;

public record SyncPacket(String json) implements FabricPacket {
    public static final PacketType<SyncPacket> TYPE = PacketType.create(IronFistNew.IRONFISTNEW, SyncPacket::new);

    public SyncPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.json);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}