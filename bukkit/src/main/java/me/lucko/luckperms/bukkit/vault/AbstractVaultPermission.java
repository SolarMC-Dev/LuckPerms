/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.bukkit.vault;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

/**
 * An extended abstraction of the Vault {@link Permission} API.
 *
 * The original Vault API only contained methods to query data by username. Over
 * time, the maintainers added additional methods to query by (Offline)Player, but
 * in order to keep backwards compatibility with implementations which only supported
 * usernames, they implemented the Player query methods and downgraded the requests
 * to get a result using the players username.
 *
 * Whilst this meant the old plugins would still be supported, it made the whole
 * API a total mess. This class reverses this action, and instead upgrades
 * requests to use UUIDs. This makes implementing Vault significantly easier for modern
 * plugins, and because requests are upgraded instead of being downgraded then upgraded,
 * much faster for plugins querying data.
 */
@SuppressWarnings("deprecation")
public abstract class AbstractVaultPermission extends Permission {
    public AbstractVaultPermission() {
        super.plugin = org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("Vault"); // Solar
    }

    @Override
    public final boolean isEnabled() {
        // always return true
        return true;
    }

    @Override
    public final boolean hasSuperPermsCompat() {
        // always return true
        return true;
    }

    @Override
    public final boolean hasGroupSupport() {
        // always return true
        return true;
    }

    // methods subclasses are expected to implement
    public abstract UUID lookupUuid(String player);
    public abstract boolean userHasPermission(String world, UUID uuid, String permission);
    public abstract boolean userAddPermission(String world, UUID uuid, String permission);
    public abstract boolean userRemovePermission(String world, UUID uuid, String permission);
    public abstract boolean userInGroup(String world, UUID uuid, String group);
    public abstract boolean userAddGroup(String world, UUID uuid, String group);
    public abstract boolean userRemoveGroup(String world, UUID uuid, String group);
    public abstract String[] userGetGroups(String world, UUID uuid);
    public abstract String userGetPrimaryGroup(String world, UUID uuid);
    public abstract boolean groupHasPermission(String world, String name, String permission);
    public abstract boolean groupAddPermission(String world, String name, String permission);
    public abstract boolean groupRemovePermission(String world, String name, String permission);

    /* allow implementations to override and implement custom world remapping / handling rules */
    protected String convertWorld(String world) {
        return world;
    }

    private String convertWorld(World world) {
        if (world == null) {
            return null;
        }
        return convertWorld(world.getName());
    }

    private String convertWorld(Player player) {
        if (player == null) {
            return null;
        }
        return convertWorld(player.getWorld());
    }

    @Override
    public final boolean has(String world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userHasPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean has(World world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userHasPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean has(Player player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return player.hasPermission(permission);
    }

    @Override
    public final boolean playerHas(String world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userHasPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerHas(World world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userHasPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerHas(String world, OfflinePlayer player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userHasPermission(convertWorld(world), player.getUniqueId(), permission);
    }

    @Override
    public final boolean playerHas(Player player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return player.hasPermission(permission);
    }

    @Override
    public final boolean playerAdd(String world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userAddPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerAdd(World world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userAddPermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerAdd(String world, OfflinePlayer player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userAddPermission(convertWorld(world), player.getUniqueId(), permission);
    }

    @Override
    public final boolean playerAdd(Player player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userAddPermission(convertWorld(player), ((OfflinePlayer) player).getUniqueId(), permission);
    }

    @Override
    public final boolean playerRemove(String world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userRemovePermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerRemove(String world, OfflinePlayer player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userRemovePermission(convertWorld(world), player.getUniqueId(), permission);
    }

    @Override
    public final boolean playerRemove(World world, String player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userRemovePermission(convertWorld(world), lookupUuid(player), permission);
    }

    @Override
    public final boolean playerRemove(Player player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return userRemovePermission(convertWorld(player), ((OfflinePlayer) player).getUniqueId(), permission);
    }

    @Override
    public final boolean groupHas(String world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupHasPermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean groupHas(World world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupHasPermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean groupAdd(String world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupAddPermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean groupAdd(World world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupAddPermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean groupRemove(String world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupRemovePermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean groupRemove(World world, String group, String permission) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(permission, "permission");
        return groupRemovePermission(convertWorld(world), group, permission);
    }

    @Override
    public final boolean playerInGroup(String world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userInGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerInGroup(World world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userInGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerInGroup(String world, OfflinePlayer player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userInGroup(convertWorld(world), player.getUniqueId(), group);
    }

    @Override
    public final boolean playerInGroup(Player player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userInGroup(convertWorld(player), ((OfflinePlayer) player).getUniqueId(), group);
    }

    @Override
    public final boolean playerAddGroup(String world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userAddGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerAddGroup(World world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userAddGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerAddGroup(String world, OfflinePlayer player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userAddGroup(convertWorld(world), player.getUniqueId(), group);
    }

    @Override
    public final boolean playerAddGroup(Player player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userAddGroup(convertWorld(player), ((OfflinePlayer) player).getUniqueId(), group);
    }

    @Override
    public final boolean playerRemoveGroup(String world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userRemoveGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerRemoveGroup(World world, String player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userRemoveGroup(convertWorld(world), lookupUuid(player), group);
    }

    @Override
    public final boolean playerRemoveGroup(String world, OfflinePlayer player, String group) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(group, "group");
        return userRemoveGroup(convertWorld(world), player.getUniqueId(), group);
    }

    @Override
    public final boolean playerRemoveGroup(Player player, String group) {
        Objects.requireNonNull(player, "player");
        return userRemoveGroup(convertWorld(player), ((OfflinePlayer) player).getUniqueId(), group);
    }

    @Override
    public final String[] getPlayerGroups(String world, String player) {
        Objects.requireNonNull(player, "player");
        return userGetGroups(convertWorld(world), lookupUuid(player));
    }

    @Override
    public final String[] getPlayerGroups(World world, String player) {
        Objects.requireNonNull(player, "player");
        return userGetGroups(convertWorld(world), lookupUuid(player));
    }

    @Override
    public final String[] getPlayerGroups(String world, OfflinePlayer player) {
        Objects.requireNonNull(player, "player");
        return userGetGroups(convertWorld(world), player.getUniqueId());
    }

    @Override
    public final String[] getPlayerGroups(Player player) {
        Objects.requireNonNull(player, "player");
        return userGetGroups(convertWorld(player), ((OfflinePlayer) player).getUniqueId());
    }

    @Override
    public final String getPrimaryGroup(String world, String player) {
        Objects.requireNonNull(player, "player");
        return userGetPrimaryGroup(convertWorld(world), lookupUuid(player));
    }

    @Override
    public final String getPrimaryGroup(World world, String player) {
        Objects.requireNonNull(player, "player");
        return userGetPrimaryGroup(convertWorld(world), lookupUuid(player));
    }

    @Override
    public final String getPrimaryGroup(String world, OfflinePlayer player) {
        Objects.requireNonNull(player, "player");
        return userGetPrimaryGroup(convertWorld(world), player.getUniqueId());
    }

    @Override
    public final String getPrimaryGroup(Player player) {
        Objects.requireNonNull(player, "player");
        return userGetPrimaryGroup(convertWorld(player), ((OfflinePlayer) player).getUniqueId());
    }

}
