package ovclub.thespongeeconomy;

import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.util.Objects;
import java.util.ResourceBundle;

public class Commands {
    ResourceBundle bundle;
    EconomyImplementer eco;
    Yaml configFile;

    public Commands(ResourceBundle bundle, EconomyImplementer eco, Yaml configFile) {
        this.bundle = bundle;
        this.eco = eco;
        this.configFile = configFile;
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender) {
        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString();
        Util.sendMessageToPlayer(String.format(bundle.getString("info.balance"), (int) eco.getBalance(uuid), eco.bank.getPlayerBank().get(uuid), eco.converter.getInventoryValue(player)), player);
    }

    @CommandHook("pay")
    public void pay(CommandSender commandSender, OfflinePlayer target, int amount) {
        Player sender = (Player) commandSender;
        String senderuuid = sender.getUniqueId().toString();
        String targetuuid = target.getUniqueId().toString();

        if (amount == 0) {
            Util.sendMessageToPlayer(bundle.getString("error.zero"), sender);
            return;
        }

        if (amount < 0) {
            Util.sendMessageToPlayer(bundle.getString("error.negative"), sender);
            return;
        }

        if (amount > eco.bank.getTotalPlayerBalance(senderuuid)) {
            Util.sendMessageToPlayer(bundle.getString("error.notenough"), sender);
            return;
        } else if (senderuuid.equals(targetuuid)){
            Util.sendMessageToPlayer(bundle.getString("error.payyourself"), sender);
            return;
        } else if (Util.isOfflinePlayer(target.getName()) == null) {
            Util.sendMessageToPlayer(bundle.getString("error.noplayer"), sender);
            return;
        }

        eco.withdrawPlayer(sender, amount);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.sendmoneyto"), amount, target.getName()), sender);
        if (target.isOnline()) {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.moneyreceived"), amount, sender.getName()), Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())));
            eco.bank.setBalance(target.getUniqueId().toString(), eco.bank.getTotalPlayerBalance(targetuuid) + amount);
        } else {
            eco.depositPlayer(target, eco.bank.getTotalPlayerBalance(targetuuid) + amount);
        }
    }

    @CommandHook("deposit")
    public void deposit(CommandSender commandSender, String sponges){
        Player player = (Player) commandSender;

        if (sponges == null) {
            Util.sendMessageToPlayer(bundle.getString("help.deposit"), player);
            return;
        }

        if (sponges.equals("all")) {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), eco.converter.getInventoryValue((Player) commandSender)), player);
            eco.converter.depositAll((Player) commandSender);
        } else if (Integer.parseInt(sponges) == 0) {
            Util.sendMessageToPlayer(bundle.getString("error.zero"), player);
        } else if (Integer.parseInt(sponges) < 0) {
            Util.sendMessageToPlayer(bundle.getString("error.negative"), player);
        } else if (Integer.parseInt(sponges) > eco.converter.getInventoryValue(player)) {
            Util.sendMessageToPlayer(bundle.getString("error.notenough"), player);
        } else {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), Integer.parseInt(sponges)), player);
            eco.converter.deposit((Player) commandSender, Integer.parseInt(sponges));
        }

    }

    @CommandHook("withdraw")
    public void withdraw(CommandSender commandSender, String sponges){
        Player player = (Player) commandSender;

        if (sponges == null) {
            Util.sendMessageToPlayer(bundle.getString("help.withdraw"), player);
        } else if (sponges.equals("all")) {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), eco.bank.getAccountBalance(player.getUniqueId().toString())), player);
            eco.converter.withdrawAll((Player) commandSender);
        } else if (Integer.parseInt(sponges) == 0) {
            Util.sendMessageToPlayer(bundle.getString("error.zero"), player);
        } else if (Integer.parseInt(sponges) < 0) {
            Util.sendMessageToPlayer(bundle.getString("error.negative"), player);
        } else if (Integer.parseInt(sponges) > eco.bank.getAccountBalance(player.getUniqueId().toString())) {
            Util.sendMessageToPlayer(bundle.getString("error.notenough"), player);
        } else {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), Integer.parseInt(sponges)), player);
            eco.converter.withdraw((Player) commandSender, Integer.parseInt(sponges));
        }

    }

    @CommandHook("set")
    public void set(CommandSender commandSender, OfflinePlayer target, int sponge){
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.moneyset"), target.getName(), sponge), player);
        }

        eco.bank.setBalance(target.getUniqueId().toString(), sponge);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.moneyset"), sponge), Bukkit.getPlayer(target.getUniqueId()));

    }

    @CommandHook("add")
    public void add(CommandSender commandSender, OfflinePlayer target, int sponge){
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.addmoney"), sponge, target.getName()), player);
        }

        eco.depositPlayer(target, sponge);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.addmoney"), sponge), Bukkit.getPlayer(target.getUniqueId()));
    }

    @CommandHook("remove")
    public void remove(CommandSender commandSender, OfflinePlayer target, int sponge) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.remove"), sponge, target.getName()), player);
        }

        eco.withdrawPlayer(target, sponge);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.remove"), sponge), Bukkit.getPlayer(target.getUniqueId()));
    }
}


