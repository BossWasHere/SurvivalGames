/*
 * BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 * Copyright (C) 2019-2022 BackwardsNode/BossWasHere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames;

import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.message.JsonMessage;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

//    /**
//     * Sends a raw JSON message input string to the client of an online player
//     * @param player The target player entity
//     * @param message The raw JSON message to send
//     * @return True if the message was dispatched successfully, false if not
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public static boolean sendRawMessage(Player player, String message) {
//        try {
//
//            Class<?> packetClass = Class.forName(VersionGetter.getPackageHeader() + ".Packet");
//            Class<?> packetChatClass = Class.forName(VersionGetter.getPackageHeader() + ".PacketPlayOutChat");
//            Class<?> chatBaseClass = Class.forName(VersionGetter.getPackageHeader() + ".IChatBaseComponent");
//            Class<?> chatSerializerClass = Class.forName(VersionGetter.getPackageHeader() + ".IChatBaseComponent$ChatSerializer");
//            Class<?> craftPlayerClass = Class.forName(VersionGetter.getBukkitPackageHeader() + ".entity.CraftPlayer");
//            Class<?> entityPlayerClass = Class.forName(VersionGetter.getPackageHeader() + ".EntityPlayer");
//            Class<?> playerConnectionClass = Class.forName(VersionGetter.getPackageHeader() + ".PlayerConnection");
//
//            Method jsonSerializer = chatSerializerClass.getMethod("a", String.class);
//            Object outputMessage = jsonSerializer.invoke(null, message);
//
//            Constructor<?> packetConstructor;
//            Object packet;
//            if (VersionGetter.isNewerOrEqualTo("v1_16_R1")) {
//                Class<?> chatMessageTypeClass = Class.forName(VersionGetter.getPackageHeader() + ".ChatMessageType");
//                packetConstructor = packetChatClass.getDeclaredConstructor(chatBaseClass, chatMessageTypeClass, UUID.class);
//                packet = packetConstructor.newInstance(outputMessage, Enum.valueOf((Class<Enum>) chatMessageTypeClass, "SYSTEM"), new UUID(0, 0));
//            } else {
//                packetConstructor = packetChatClass.getDeclaredConstructor(chatBaseClass);
//                packet = packetConstructor.newInstance(outputMessage);
//            }
//
//            Object craftPlayer = craftPlayerClass.cast(player);
//            Method getHandle = craftPlayerClass.getMethod("getHandle", new Class<?>[] {});
//            Object handle = getHandle.invoke(craftPlayer, new Object[] {});
//            Field playerConnectionField = entityPlayerClass.getField("playerConnection");
//            playerConnectionField.setAccessible(true);
//            Object playerConnection = playerConnectionField.get(handle);
//
//            Method sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
//            sendPacket.invoke(playerConnection, packet);
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * Sends a JsonMessage object to the client of an online player
     * @param player The target player entity
     * @param message The {@link JsonMessage} formatted object
     */
    public static void sendJsonMessage(Player player, JsonMessage message) {
        Preconditions.checkNotNull(message);
        player.spigot().sendMessage(message.toBungeeChatComponent());
        //return sendRawMessage(player, message.build());
    }

    /**
     * Sends multiple JsonMessage objects in a concatenated string to the client of an online player
     * @param player The target player entity
     * @param compoundMessage The {@link JsonMessage.CompoundJsonMessage} formatted object
     */
    public static void sendJsonMessage(Player player, JsonMessage.CompoundJsonMessage compoundMessage) {
        Preconditions.checkNotNull(compoundMessage);
//        StringBuilder builder = new StringBuilder();
//        builder.append('[');
//        for (JsonMessage msg : compoundMessage.messages) {
//            builder.append(',');
//            builder.append(msg.build());
//        }
//        builder.append(']');
//        return sendRawMessage(player, builder.toString());
        BaseComponent[] components = new BaseComponent[compoundMessage.messages.size()];
        int i = 0;
        for (JsonMessage msg : compoundMessage.messages) {
            components[i++] = msg.toBungeeChatComponent();
        }
        player.spigot().sendMessage(components);
    }

    /**
     * Adds a custom display name and custom lore entries to the item metadata of a given {@link ItemStack}
     * @param item The {@link ItemStack} to add the meta to
     * @param name The custom display name to set
     * @param lore The custom lore lines to set
     */
    public static ItemStack addNameAndLore(ItemStack item, String name, String... lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (name != null) {
            itemMeta.setDisplayName(name);
        }
        if (lore != null) {
            itemMeta.setLore(Arrays.asList(lore));
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * Serializes a location object to a comma-separated string in the format:
     * <blockquote>x,y,z,world</blockquote>
     * @param location The location in the world to serialize
     * @param chunkRef If the y-value should be omitted
     * @param toXYZInt If xyz coordinates should be rounded
     * @return A string representation of the location
     */
    public static String stringFromLocation(Location location, boolean chunkRef, boolean toXYZInt) {
        World world = location.getWorld();
        String worldName = world == null ? null : world.getName();

        if (toXYZInt) {
            if (chunkRef) {
                return String.format("%d,%d,%s", location.getBlockX(), location.getBlockZ(), worldName);
            }
            return String.format("%d,%d,%d,%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), worldName);
        }

        if (chunkRef) {
            return String.format("%e,%e,%s", location.getX(), location.getZ(), worldName);
        }
        return String.format("%e,%e,%e,%s", location.getX(), location.getY(), location.getZ(), worldName);
    }

    /**
     * Deserializes a comma-separated string in the format:
     * <blockquote>x,y,z,world</blockquote>
     * To a location within a loaded bukkit world
     * @param location The string to deserialize
     * @param chunkRef If the y-value is omitted
     * @return A Location object representing the position of the string
     */
    public static Location locationFromString(String location, boolean chunkRef) {
        int index = 0;
        double x,y=0,z;
        float yaw=0, pitch=0;
        String name;
        String[] spl = location.split(",");
        try {
            x = Double.parseDouble(spl[index++]);
            if (!chunkRef) {
                y = Double.parseDouble(spl[index++]);
            }
            z = Double.parseDouble(spl[index++]);
            name = spl[index];
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
        World world = Bukkit.getWorld(name);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Finds the most appropriate multiple of 9 to set as the inventory size for a basic player inventory
     * The number returned will be the same or a greater multiple of 9
     * @param itemLength The number of items given, not necessarily a multiple of 9
     * @return A multiple of 9 large enough to generate an inventory container for all items
     */
    public static int getPreferredSize(int itemLength) {
        return itemLength + 8 - (itemLength - 1) % 9;
    }

    /**
     * Gets an array of UNIQUE randomly generated numbers below the maximum value
     * @param count the number of values to return
     * @param max exclusive, the maximum value which can be randomised
     * @return an array of values
     * @throws IllegalArgumentException if max < count
     */
    public static int[] getRandomSlots(int count, int max) throws IllegalArgumentException {
        if (max < count) {
            throw new IllegalArgumentException("The maximum value must be greater than the number of values");
        }
        int[] a = new int[count];
        Random r = new Random();
        int next;
        for (int b = 0; b < count; b++) {
            do {
                next = r.nextInt(max);
            } while (anyMatch(a, next));
            a[b] = next;
        }
        return a;
    }

    public static boolean fillChest(Chest chest, Collection<ItemSet> allItemSets, Collection<String> permittedItemSets) {
        Set<ItemSet> selector = allItemSets.stream().filter(x -> permittedItemSets.equals(x.name)).collect(Collectors.toSet());

        List<ItemModel> assorted = new ArrayList<>();
        for (ItemSet select : selector) {
            assorted.addAll(select.items);
        }

        chest.setCustomName("Loot Chest");
        chest.update(true, false);
        Inventory blockInv = chest.getBlockInventory();
        blockInv.clear();

        Random r = new Random();
        int availableItems = assorted.size();
        int m = Math.min(availableItems, r.nextInt(4) + 2);
        int[] slots = getRandomSlots(m, 27);

        boolean isOkay = true;
        for (int i = 0; i < m && availableItems > 0; i++) {
            ItemModel model = assorted.remove(r.nextInt(availableItems--));
            ItemStack item = model.getEquivalent();
            if (item == null) {
                Bukkit.getLogger().warning("Unknown item [" + model.id + "], you should check the config file");
                isOkay = false;
                m++;
                continue;
            }
            item.setAmount(r.nextInt(model.count) + 1);
            blockInv.setItem(slots[i], item);
        }

        return isOkay;
    }

    /**
     * Reads the entire contents from a file
     * @param file The file to read from
     * @param encoding The encoding of the file
     * @return A string object containing the file's contents
     * @throws IOException if the file cannot be read
     */
    public static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    /**
     * Gets a timestamp in the format YYYYMMDD-HHMMSS
     * @return The string representation of the current timestamp
     */
    public static String timestamp() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    /**
     * Appends a given string to a timestamp in the format YYYYMMDD-HHMMSS
     * @param append The string to append
     * @return The string with the timestamp prepended to it
     */
    public static String timestamp(String append) {
        return timestamp() + "-" + append;
    }

    /**
     * Selects a random enum constant from a given enum class
     * @param enumeration The enum class to target
     * @return A pseudorandom constant from the given class
     */
    public static <T extends Enum<?>> T getRandomConstant(Class<T> enumeration) {
        int r = new Random().nextInt(enumeration.getEnumConstants().length);
        return enumeration.getEnumConstants()[r];
    }
    public static <T extends Enum<?>> String capitalizeEnumName(T e) {
        return WordUtils.capitalizeFully(e.toString().replace('_', ' '));
    }

    private static boolean anyMatch(final int[] array, final int value) {
        return Arrays.stream(array).anyMatch(x -> x == value);
    }

    public static void spawnRandomFirework(Location l) {
        Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fm = f.getFireworkMeta();

        Random r = new Random();

        FireworkEffect fe = FireworkEffect.builder().flicker(r.nextBoolean()).trail(r.nextBoolean()).withColor(Color.fromRGB(r.nextInt(16777216)))
                .withFade(Color.fromRGB(r.nextInt(16777216))).with(getRandomConstant(FireworkEffect.Type.class)).build();
        fm.addEffect(fe);
        f.setFireworkMeta(fm);

    }

    public static String secondsToString(long pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        fis.close();

        byte[] bytes = digest.digest();

        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for(int i = 0; i< bytes.length; i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
