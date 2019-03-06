package mcjty.questutils.commands;

import mcjty.lib.varia.BlockPosTools;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.data.QUData;
import mcjty.questutils.data.QUEntry;
import mcjty.questutils.json.JsonPersistance;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CmdQU extends CommandBase {

    @Override
    public String getName() {
        return "qu";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "qu help: for more info";
    }

    private static final Map<String, ICommandHandler> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("help", CmdQU::help);
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

    private static void help(ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "qu list [ <matcher> [ <positional> ] ]"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "qu load <file> [ <matcher> [ <positional> ] ]"));
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "qu save <file> [ <matcher> [ <positional> ] ]"));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + ""));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "<matcher> is a wildcard string (using . and *)"));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "<positional> is either a radius or <x>,<y>,<z>[,<dim>]@radius"));
    }

    private static void list(ICommandSender sender, String[] args) {
        Predicate<String> matcher = getMatcher(args, 1);
        BiPredicate<Integer, BlockPos> radiusMatcher = getRadiusMatcher(sender, args, 2);
        if (radiusMatcher == null) {
            return;
        }
        for (Map.Entry<String, QUEntry> entry : QUData.getData().getEntries().entrySet()) {
            if (matcher.test(entry.getKey())) {
                QUEntry qu = entry.getValue();
                if (radiusMatcher.test(qu.getDimension(), qu.getPos())) {
                    sender.sendMessage(new TextComponentString("Id: " + entry.getKey() + " at " + BlockPosTools.toString(qu.getPos()) + " (" + qu.getDimension() + ")"));
                }
            }
        }
    }

    private static void save(ICommandSender sender, String[] args) {
        String filename = args[1];
        File file = new File(QuestUtils.setup.getModConfigDir().getPath(), filename);
        Predicate<String> matcher = getMatcher(args, 2);
        BiPredicate<Integer, BlockPos> radiusMatcher = getRadiusMatcher(sender, args, 3);
        if (radiusMatcher == null) {
            return;
        }
        JsonPersistance.write(file, matcher, sender, radiusMatcher);
    }

    private static void load(ICommandSender sender, String[] args) {
        String filename = args[1];
        File file = new File(QuestUtils.setup.getModConfigDir().getPath(), filename);
        if (!file.exists()) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Cannot load file!"));
            return;
        }
        Predicate<String> matcher = getMatcher(args, 2);
        BiPredicate<Integer, BlockPos> radiusMatcher = getRadiusMatcher(sender, args, 3);
        if (radiusMatcher == null) {
            return;
        }
        JsonPersistance.read(file, matcher, sender, radiusMatcher);
    }

    private static Predicate<String> getMatcher(String[] args, int index) {
        if (index >= args.length) {
            return s -> true;
        }
        String re = args[index].replace(".", "[.]").replace("*", ".*");
        Pattern pattern = Pattern.compile(re);
        return s -> pattern.matcher(s).matches();
    }

    private static BiPredicate<Integer, BlockPos> getRadiusMatcher(ICommandSender sender, String[] args, int index) {
        if (index >= args.length) {
            return (dimension, pos) -> true;
        }
        try {
            final int senderDim = sender.getEntityWorld().provider.getDimension();
            final BlockPos senderPos = sender.getPosition();
            int radius = Integer.parseInt(args[index]);
            int sqRadius = radius * radius;
            return (dimension, pos) -> senderDim == dimension && senderPos.distanceSq(pos) <= sqRadius;
        } catch (NumberFormatException e) {
            String[] split = StringUtils.split(args[index], '@');
            if (split.length != 2) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Badly formed radius!"));
                return null;
            }
            String[] possplit = StringUtils.split(split[0], ',');
            if (possplit.length < 3 || possplit.length > 4) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Badly formed radius!"));
                return null;
            }
            BlockPos argPos = new BlockPos(Integer.parseInt(possplit[0]), Integer.parseInt(possplit[1]), Integer.parseInt(possplit[2]));
            int argDim = sender.getEntityWorld().provider.getDimension();
            if (possplit.length == 4) {
                argDim = Integer.parseInt(possplit[3]);
            }
            int radius = Integer.parseInt(split[1]);
            int sqRadius = radius * radius;
            int finalArgDim = argDim;
            return (dimension, pos) -> finalArgDim == dimension && argPos.distanceSq(pos) <= sqRadius;
        }
    }
}
