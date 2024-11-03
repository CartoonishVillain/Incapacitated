package com.cartoonishvillain.incapacitated.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ReviveCheckEvent extends Event implements ICancellableEvent {
    private Player revivingPlayer;
    private Player downedPlayer;

    public ReviveCheckEvent(Player revivingPlayer, Player downedPlayer) {
        this.revivingPlayer = revivingPlayer;
        this.downedPlayer = downedPlayer;
    }

    public Player getRevivingPlayer() {
        return revivingPlayer;
    }

    public Player getDownedPlayer() {
        return downedPlayer;
    }
}
