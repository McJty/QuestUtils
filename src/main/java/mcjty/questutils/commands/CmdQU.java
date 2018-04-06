package mcjty.questutils.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.questutils.data.QUData;
import mcjty.questutils.data.QUEntry;
import mcjty.questutils.json.JsonPersistance;
import mcjty.questutils.proxy.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
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

    private static final Map<String, ICommandHandler> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("list", CmdQU::list);
        COMMANDS.put("save", CmdQU::save);
        COMMANDS.put("load", CmdQU::load);
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

    private static void save(ICommandSender sender, String[] args) {
        String filename = args[1];
        File file = new File(CommonProxy.modConfigDir.getPath(), filename);
        Predicate<String> matcher = getMatcher(args, 2);
        JsonPersistance.write(file, matcher);
    }

    private static void load(ICommandSender sender, String[] args) {
        String filename = args[1];
        File file = new File(CommonProxy.modConfigDir.getPath(), filename);
        if (!file.exists()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Cannot load file!"));
            return;
        }
        Predicate<String> matcher = getMatcher(args, 2);
        JsonPersistance.read(file, matcher);
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
