package komok.org.example.mazetour.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class candyWarCompliter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> args_ = new ArrayList<>();
            args_.add("start");
            args_.add("stop");
            return args_;
        }
        return null;
    }
}
