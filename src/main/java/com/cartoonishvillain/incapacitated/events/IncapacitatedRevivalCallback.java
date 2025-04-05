package com.cartoonishvillain.incapacitated.events;

import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.platform.Services;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

/**
 * This event is used for mod integration for the revive system, allowing developers to block revival under new circumstances.
 * parameter revivingPlayer - The player who is attempting to revive another player.
 * parameter downedPlayer - The player who is down
 * Upon Return:
 * REVIVING - The event's newly added checks passed, and will not be blocked from reviving due to the new checks.
 * CAPABLE_OF_REVIVING - Mechanically for event's sake the same as REVIVING.
 * INCAPABLE_OF_REVIVING - The new event checks have failed, and the player is not capable of reviving the other player.
 */
public interface IncapacitatedRevivalCallback {
    Event<IncapacitatedRevivalCallback> EVENT = EventFactory.createArrayBacked(IncapacitatedRevivalCallback.class,
            (listeners) -> (revivingPlayer, downedPlayer) -> {
        for (IncapacitatedRevivalCallback listener : listeners) {
            RevivePlayerState result = listener.interact(revivingPlayer, downedPlayer);

            if (result == RevivePlayerState.INCAPABLE_OF_REVIVING) return result;
        }

        boolean isDown;
        IncapacitatedPlayerData potentialHeroData = Services.getPlayerData(revivingPlayer);
        isDown = potentialHeroData.isIncapacitated();
        if (revivingPlayer.isCrouching() &&  !isDown) return RevivePlayerState.REVIVING;
        else if (!isDown) return RevivePlayerState.CAPABLE_OF_REVIVING;
        else return RevivePlayerState.INCAPABLE_OF_REVIVING;
    });

    RevivePlayerState interact(Player revivingPlayer, Player downedPlayer);
}
