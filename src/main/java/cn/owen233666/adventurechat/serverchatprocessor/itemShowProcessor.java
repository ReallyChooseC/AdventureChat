package cn.owen233666.adventurechat.serverchatprocessor;

import cn.owen233666.adventurechat.utils.Cache.ItemShowCache;
import cn.owen233666.adventurechat.utils.DataType.ItemData;
import cn.owen233666.adventurechat.utils.ItemStackToSNBT;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class itemShowProcessor {
    public static String processItemShow(ServerPlayer player, String message) {
        Pattern p = Pattern.compile("%i(?!nv)");
        Matcher matcher = p.matcher(message);
        HolderLookup.Provider registries = null;
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            registries = ServerLifecycleHooks.getCurrentServer().registryAccess();
        }


        if (!matcher.find()) return message;
        UUID uuid = UUID.randomUUID();
        ItemData itemData = new ItemData().setItem(player.getMainHandItem()).setPlayer(player);
        ItemStack heldItem = player.getMainHandItem();
        Rarity rarity = heldItem.getRarity();
        String count = String.valueOf(heldItem.getCount());
        String color = switch (rarity){
            case UNCOMMON -> "<yellow>";
            case RARE -> "<aqua>";
            case EPIC -> "<light_purple>";
            default -> "<white>";
        };
        String snbt = ItemStackToSNBT.toSNBT(heldItem, registries);

        ItemShowCache.cache.put(uuid, itemData);

        String name = heldItem.getItem().getDescriptionId();

        String itemDisplay = color + "[<click:run_command:'/adventurechat previewitem " + uuid + "'>" + "<lang:" + name + ">" + "]<reset>";
        String processed = p.matcher(message).replaceAll(itemDisplay);
        return processed;
    }
}
