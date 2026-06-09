package fr.elias.mythicDrop.quests.events;

import fr.elias.mythicDrop.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a player completes a quest and all base rewards have been granted.
 * Other systems (effects, broadcasts, etc.) can listen to this event.
 */
public class PlayerQuestCompleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Quest quest;

    public PlayerQuestCompleteEvent(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    public Player getPlayer() { return player; }
    public Quest getQuest()   { return quest; }

    @Override
    public @NotNull HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
