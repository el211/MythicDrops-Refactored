package fr.elias.mythicDrop.commands;

import fr.elias.mythicDrop.quests.QuestGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MQuestsCommand implements CommandExecutor {

    private final QuestGUI questGUI;

    public MQuestsCommand(QuestGUI questGUI) {
        this.questGUI = questGUI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the quest menu.");
            return true;
        }
        questGUI.open(player);
        return true;
    }
}
