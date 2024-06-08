package ovclub.thespongeeconomy.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BalanceChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final double amount;

    public BalanceChangeEvent(Player player, double amount) {
        this.player = player;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
