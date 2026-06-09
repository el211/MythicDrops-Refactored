package fr.elias.mythicDrop.quests;

import lombok.Getter;

/**
 * A single reward entry: a console command, a roll chance, and an optional player message.
 */
@Getter
public class Reward {

    /** Console command with {@code %player%} placeholder. */
    private final String command;

    /** Probability this reward fires (0.0–1.0). Ignored for guaranteed rewards. */
    private final double chance;

    /** Message sent to the player after execution. {@code null} means no message. */
    private final String message;

    public Reward(String command, double chance, String message) {
        this.command = command;
        this.chance = chance;
        this.message = message;
    }
}
