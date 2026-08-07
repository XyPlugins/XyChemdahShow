package xy.xychemdahshow.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import xy.xychemdahshow.XyChemdahShow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MainCommand implements CommandExecutor, TabCompleter {

    private final XyChemdahShow plugin;

    public MainCommand(XyChemdahShow plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("xychemdahshow.admin")) {
                XyChemdahShow.log(sender, "你没有权限执行该命令");
                return true;
            }
            plugin.reloadInternal(true);
            XyChemdahShow.log(sender, "重载完成，已刷新在线玩家 HUD");
            return true;
        }

        if ("refresh".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                XyChemdahShow.log(sender, "该命令只能由玩家执行");
                return true;
            }
            plugin.getHudService().updateHud((Player) sender, false);
            XyChemdahShow.log(sender, "已刷新你的任务视图");
            return true;
        }

        if ("nav".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                XyChemdahShow.log(sender, "该命令只能由玩家执行");
                return true;
            }
            Player player = (Player) sender;
            if (args.length >= 2) {
                int displayIndex;
                try {
                    displayIndex = Integer.parseInt(args[1]) - 1;
                } catch (NumberFormatException ignored) {
                    XyChemdahShow.playerLog(player, "任务导航序号无效，请刷新任务视图后重试");
                    return true;
                }

                String questId = plugin.getHudService().getDisplayedQuestId(player, displayIndex);
                if (questId == null) {
                    XyChemdahShow.playerLog(player, "该任务已不在当前视图中，请刷新后重试");
                    return true;
                }
                plugin.getNavigationService().toggleNavigation(player, questId);
                return true;
            }

            plugin.getNavigationService().toggleNavigation(player);
            return true;
        }

        sendHelp(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return null;
        }

        List<String> options = new ArrayList<String>(Arrays.asList("refresh", "nav"));
        if (sender.hasPermission("xychemdahshow.admin")) {
            options.add("reload");
        }

        List<String> result = new ArrayList<String>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(args[0].toLowerCase())) {
                result.add(option);
            }
        }
        return result;
    }

    private void sendHelp(CommandSender sender) {
        XyChemdahShow.log(sender, "XyChemdahShow 命令帮助");
        XyChemdahShow.log(sender, "&6/xychshow refresh &f- 刷新自己的任务 HUD");
        XyChemdahShow.log(sender, "&6/xychshow nav [任务序号] &f- 开始、切换或停止任务导航");
        if (sender.hasPermission("xychemdahshow.admin")) {
            XyChemdahShow.log(sender, "&6/xychshow reload &f- 重载配置并刷新在线玩家");
        }
    }
}
