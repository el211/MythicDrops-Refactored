package fr.elias.mythicDrop.quests;

import lombok.Getter;

import java.util.List;

/**
 * A group-specific reward section.
 * The first {@code guaranteedRewards} entries in {@code rewards} always execute;
 * the remainder each roll their own chance.
 */
@Getter
public class RewardSection {

    private final int guaranteedRewards;
    private final List<Reward> rewards;

    public RewardSection(int guaranteedRewards, List<Reward> rewards) {
        this.guaranteedRewards = guaranteedRewards;
        this.rewards = rewards;
    }
}
