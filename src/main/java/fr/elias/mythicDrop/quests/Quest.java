package fr.elias.mythicDrop.quests;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Pure data object representing a single quest configuration.
 */
@Getter
public class Quest {

    private final String questID;
    private final String mobName;
    private final int requiredKills;

    // Quest item appearance in GUI
    private final String questItemMaterial;
    private final int modelData;
    private final String displayName;
    private final List<String> questLore;
    private final int guiSlot;

    private final String completionMessage;

    // Keyed by LuckPerms group name (e.g. "default", "vip")
    private final Map<String, RewardSection> rewardSections;

    public Quest(String questID, String mobName, int requiredKills,
                 String questItemMaterial, int modelData, String displayName,
                 List<String> questLore, int guiSlot, String completionMessage,
                 Map<String, RewardSection> rewardSections) {
        this.questID = questID;
        this.mobName = mobName;
        this.requiredKills = requiredKills;
        this.questItemMaterial = questItemMaterial;
        this.modelData = modelData;
        this.displayName = displayName;
        this.questLore = questLore;
        this.guiSlot = guiSlot;
        this.completionMessage = completionMessage;
        this.rewardSections = rewardSections;
    }
}
