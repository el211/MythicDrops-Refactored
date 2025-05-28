package fr.elias.mythicDrop.rewards;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;

public interface Reward {
    void send(Player player, ActiveMob mob);
}
