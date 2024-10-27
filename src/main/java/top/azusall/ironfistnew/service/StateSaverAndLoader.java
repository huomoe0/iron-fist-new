package top.azusall.ironfistnew.service;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
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

    public static HashMap<UUID, IronFistPlayer> players = new HashMap<>();

    /**
     * 写入nbt
     */
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
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

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound players = tag.getCompound("players");
        players.getKeys().forEach((uuid) -> {
            NbtCompound c = players.getCompound(uuid);
            IronFistPlayer ironFistPlayer = new IronFistPlayer(c.getInt("fistLevel"), c.getDouble("fistXp"), c.getFloat("cumulativeWork"), c.getLong("lastBreakMillis"), c.getDouble("energy"));
            UUID uuid1 = UUID.fromString(uuid);
            StateSaverAndLoader.players.put(uuid1, ironFistPlayer);
        });
        return state;
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (注：如需在任意维度生效，请使用 'World.OVERWORLD' ，不要使用 'World.END' 或 'World.NETHER')
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // 当第一次调用了方法 'getOrCreate' 后，它会创建新的 'StateSaverAndLoader' 并将其存储于  'PersistentStateManager' 中。
        //  'getOrCreate' 的后续调用将本地的 'StateSaverAndLoader' NBT 传递给 'StateSaverAndLoader::createFromNbt'。
        StateSaverAndLoader state = persistentStateManager.getOrCreate(StateSaverAndLoader::createFromNbt, StateSaverAndLoader::new, IronFistNew.MOD_ID);

        state.markDirty();

        return state;
    }

    public static IronFistPlayer getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());
        // 根据 UUID 获取对应玩家的状态，如果没有该玩家的数据，就创建一个新的玩家状态。
        IronFistPlayer playerState = players.computeIfAbsent(player.getUuid(), uuid -> new IronFistPlayer());

        return playerState;
    }


    public static void setPlayerState(LivingEntity player, IronFistPlayer playerState) {
        players.put(player.getUuid(), playerState);
    }
}