package mcjty.questutils.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.questutils.data.QUData;
import mcjty.questutils.data.QUEntry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CmdQU extends CommandBase {

    @Override
    public String getName() {
        return "qu";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "qu <command> [ <args> ]";
    }

    private final static Map<String, ICommandHandler> COMMANDS = new HashMap();

    static {
        COMMANDS.put("list", (sender, args) -> list(sender, args));
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Too few arguments!"));
            return;
        }

        String cmd = args[0].toLowerCase();
        ICommandHandler handler = COMMANDS.get(cmd);
        if (handler == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown subcommand!"));
            return;
        }

        handler.execute(sender, args);
    }

    private static void list(ICommandSender sender, String[] args) {
        Predicate<String> matcher = getMatcher(args, 1);
        for (Map.Entry<String, QUEntry> entry : QUData.getData().getEntries().entrySet()) {
            if (matcher.test(entry.getKey())) {
                sender.sendMessage(new TextComponentString("Id: " + entry.getKey() + " at " + BlockPosTools.toString(entry.getValue().getPos()) + " (" + entry.getValue().getDimension() + ")"));
            }
        }
    }

    private static Predicate<String> getMatcher(String[] args, int index) {
        if (index >= args.length) {
            return s -> true;
        }
        String re = args[index].replace(".", "[.]").replace("*", ".*");
        Pattern pattern = Pattern.compile(re);
        return s -> pattern.matcher(s).matches();
    }
}
