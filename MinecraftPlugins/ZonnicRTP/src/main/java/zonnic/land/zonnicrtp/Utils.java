package zonnic.land.zonnicrtp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import zonnic.land.zonnicrtp.commands.RTPCommand;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    ZonnicRTP plugin = ZonnicRTP.getPlugin();
    public static HashMap<UUID, Integer> teleportiongPlayers = new HashMap<>();
    public static HashMap<Player, Boolean> locationFound = new HashMap<>();
    public static HashMap<Player, Location> generatedLocationFinal = new HashMap<>();

    public void teleportToRandomLocation(Player player) {
        player.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.GREEN + "Looking for location...");
        generateLocation(player);

    }

    private void generateLocation(Player player) {
        int minX = plugin.getConfig().getInt("range.x-min"); int minZ = plugin.getConfig().getInt("range.z-min");
        int maxX = plugin.getConfig().getInt("range.x-max"); int maxZ = plugin.getConfig().getInt("range.z-max");
        int minXfromSpawn = plugin.getConfig().getInt("range.x-min-from-spawn"); int minZfromSpawn = plugin.getConfig().getInt("range.z-min-from-spawn");
        int maxXfromSpawn = plugin.getConfig().getInt("range.x-max-from-spawn"); int maxZfromSpawn = plugin.getConfig().getInt("range.z-max-from-spawn");

        locationFound.put(player, false);
        final Thread thread = new Thread(
                () -> {
                    Location generatedLocation = new Location(player.getWorld(), 0, 100, 0);
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    while(!locationFound.get(player)){
                        int randX =  random.nextInt(minX, maxX);
                        int randZ = random.nextInt(minZ, maxZ);
                        int highestY;
                        generatedLocation.setX(randX); generatedLocation.setZ(randZ);
                        highestY = generatedLocation.getWorld().getHighestBlockYAt(generatedLocation) + 1;
                        generatedLocation.setY(highestY);
                        if (isLocationSafe(generatedLocation)) {
                            if ((generatedLocation.getBlockX() >= maxXfromSpawn || generatedLocation.getBlockX() <= minXfromSpawn)) {
                                if ((generatedLocation.getBlockZ() >= maxZfromSpawn || generatedLocation.getBlockZ() <= minZfromSpawn)) {
                                    locationFound.replace(player, true);
                                    break;
                                }
                            }
                        }
                    }

                    generatedLocationFinal.put(player, generatedLocation);
                    if (generatedLocation.getBlockY() != -101) {
                        player.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.GREEN + "Location found!");
                        int graceTime = plugin.getConfig().getInt("grace_time");
                        player.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.GREEN + "Teleporting in " + graceTime + " seconds, please do not move.");
                        int id = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(generatedLocation);
                                player.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.GREEN + "You were teleported!");
                                if (!player.isOp()) RTPCommand.rtpcooldown.put(player.getUniqueId(), System.currentTimeMillis());
                                teleportiongPlayers.remove(player.getUniqueId());
                                generatedLocationFinal.remove(player);
                                locationFound.remove(player);
                            }
                        }.runTaskLater(plugin, (graceTime * 20)).getTaskId();
                        teleportiongPlayers.put(player.getUniqueId(), id);
                    } else {
                        player.sendMessage(ChatColor.GOLD + "[ZonnicRTP] " + ChatColor.RED + "Failed to find location, please try again.");
                    }
                }
        );
        thread.start();
    }

    private boolean isLocationSafe (Location location) {
        List<Material> illegalMaterial = new ArrayList<>();
        illegalMaterial.add(Material.LAVA); illegalMaterial.add(Material.FIRE); illegalMaterial.add(Material.WATER);
        illegalMaterial.add(Material.CACTUS);

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block block = Objects.requireNonNull(location.getWorld()).getBlockAt(x, y, z);
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        Block above = location.getWorld().getBlockAt(x, y + 1, z);

        return !(illegalMaterial.contains(below.getType()) || (block.getType().isSolid()) || (above.getType().isSolid()));
    }

}
