package top.azusall.ironfistnew.common;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import top.azusall.ironfistnew.IronFistNew;
import top.azusall.ironfistnew.entity.IronFistPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author houmo
 */
public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID, IronFistPlayer> players = new HashMap<>();

    /**
     * 写入nbt
     * @param nbt
     * @param registryLookup
     * @return
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach(((uuid, ironFistPlayer) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putInt("fistLevel", ironFistPlayer.getFistLevel());
            playerNbt.putDouble("fistXp", ironFistPlayer.getFistXp());
            playerNbt.putFloat("cumulativeWork", ironFistPlayer.getCumulativeWork());
            playerNbt.putDouble("energy", ironFistPlayer.getEnergy());
            playerNbt.putLong("lastBreakMillis", ironFistPlayer.getLastBreakMillis());

            playersNbt.put(uuid.toString(), playerNbt);
        }));
        nbt.put("players", playersNbt);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound players = tag.getCompound("players");
        players.getKeys().forEach((uuid) -> {
            NbtCompound c = players.getCompound(uuid);
            IronFistPlayer ironFistPlayer = new IronFistPlayer(c.getInt("fistLevel"), c.getDouble("fistXp"), c.getFloat("cumulativeWork"), c.getLong("lastBreakMillis"), c.getDouble("energy"));
            UUID uuid1 = UUID.fromString(uuid);
            state.players.put(uuid1, ironFistPlayer);
        });
        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
             // 若不存在 'StateSaverAndLoader' 则创建
            StateSaverAndLoader::new,
             // 若存在 'StateSaverAndLoader' NBT, 则调用 'createFromNbt' 传入参数
            StateSaverAndLoader::createFromNbt,
            // 此处理论上应为 'DataFixTypes' 的枚举，但我们直接传递为空(null)也可以
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (注：如需在任意维度生效，请使用 'World.OVERWORLD' ，不要使用 'World.END' 或 'World.NETHER')
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // 当第一次调用了方法 'getOrCreate' 后，它会创建新的 'StateSaverAndLoader' 并将其存储于  'PersistentStateManager' 中。
        //  'getOrCreate' 的后续调用将本地的 'StateSaverAndLoader' NBT 传递给 'StateSaverAndLoader::createFromNbt'。
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, IronFistNew.MOD_ID);

        // 若状态未标记为脏(dirty)，当 Minecraft 关闭时， 'writeNbt' 不会被调用，相应地，没有数据会被保存。
        // 从技术上讲，只有在事实上发生数据变更时才应当将状态标记为脏(dirty)。
        // 但大多数开发者和模组作者会对他们的数据未能保存而感到困惑，所以不妨直接使用 'markDirty' 。
        // 另外，这只将对应的布尔值设定为 TRUE，代价是文件写入磁盘时模组的状态不会有任何改变。(这种情况非常少见)
        state.markDirty();

        return state;
    }

    public static IronFistPlayer getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        // 根据 UUID 获取对应玩家的状态，如果没有该玩家的数据，就创建一个新的玩家状态。
        IronFistPlayer playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new IronFistPlayer());

        return playerState;
    }
}