package com.xy.xychemdahshow.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import com.xy.xychemdahshow.event.XyChemdahShowReloadEvent;
import com.xy.xychemdahshow.util.InventoryUtil;

import java.util.List;

public class MainCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Bukkit.getPluginManager().callEvent(new XyChemdahShowReloadEvent());
            sender.sendMessage("§a[XyChemdahShow] §f重载成功！");
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            if (sender instanceof Player) {
                InventoryUtil.openQuestUI((Player) sender);
                return true;
            } else {
                sender.sendMessage("§c只有玩家才能打开界面");
                return false;
            }
        }

        sendHelp(sender);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return TabCommand.complete(args[0]);
        }
        return null;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§m========== §aXyChemdahShow §6§m==========");
        sender.sendMessage("§6/xychemshow open   §f- 打开任务界面");
        sender.sendMessage("§6/xychemshow reload §f- 重载配置");
        sender.sendMessage("§6§m===================================");
    }
}