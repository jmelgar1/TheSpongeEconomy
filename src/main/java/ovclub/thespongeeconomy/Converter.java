package ovclub.thespongeeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.ResourceBundle;

public class Converter {

    EconomyImplementer eco;
    ResourceBundle bundle;

    public Converter(EconomyImplementer economyImplementer, ResourceBundle bundle) {
        this.eco = economyImplementer;
        this.bundle = bundle;
    }

    public int getValue(Material material) {
        if (material.equals(Material.SPONGE)) return 1;
        if (material.equals(Material.WET_SPONGE)) return 1;

        return 0;
    }

    public boolean isNotSponge(Material material) {
        switch(material) {
            case SPONGE:
            case WET_SPONGE:
                return false;
            default:
                return true;
        }
    }

    public int getInventoryValue(Player player){
        int value = 0;

        // calculating the value of all the sponge in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotSponge(material)) continue;

            value += (getValue(material) * item.getAmount());

        }
        return value;
    }

    public void remove(Player player, int amount){
        int value = 0;

        // calculating the value of all the sponge in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotSponge(material)) continue;

            value += (getValue(material) * item.getAmount());
        }

        // Checks if the Value of the items is greater than the amount to deposit
        if (value < amount) return;

        // Deletes all sponge items
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (isNotSponge(item.getType())) continue;

            item.setAmount(0);
            item.setType(Material.AIR);
        }

        int newBalance = value - amount;
        give(player, newBalance);
    }

    public void give(Player player, int value){
        boolean warning = false;

        HashMap<Integer, ItemStack> blocks = player.getInventory().addItem(new ItemStack(Material.SPONGE, value));
        for (ItemStack item : blocks.values()) {
            if (item != null && item.getType() == Material.SPONGE && item.getAmount() > 0) {
                player.getWorld().dropItem(player.getLocation(), item);
                warning = true;
            }
        }

        if (warning) Util.sendMessageToPlayer(String.format(bundle.getString("warning.drops")), player);
    }


    public void withdrawAll(Player player){
        String uuid = player.getUniqueId().toString();

        // searches in the Hashmap for the balance, so that a player can't withdraw sponge from Inventory
        int value = eco.bank.getAccountBalance(player.getUniqueId().toString());
        eco.bank.setBalance(uuid, (0));

        give(player, value);
    }

    public void withdraw(Player player, int sponges){
        String uuid = player.getUniqueId().toString();
        int oldbalance = eco.bank.getAccountBalance(player.getUniqueId().toString());

        // Checks balance in HashMap
        if (sponges > eco.bank.getPlayerBank().get(player.getUniqueId().toString())) {
            Util.sendMessageToPlayer(bundle.getString("error.notenoughmoneywithdraw"), player);
            return;
        }
        eco.bank.setBalance(uuid, (oldbalance - sponges));

        give(player, sponges);

    }

    public void depositAll(Player player){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = 0;

        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotSponge(material)) continue;

            value = value + (getValue(material) * item.getAmount());
            item.setAmount(0);
            item.setType(Material.AIR);
        }

        eco.depositPlayer(op, value);

    }

    public void deposit(Player player, int sponges){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());

        remove(player, sponges);
        eco.depositPlayer(op, sponges);
    }
}
