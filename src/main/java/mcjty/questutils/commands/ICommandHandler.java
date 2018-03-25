package mcjty.questutils.commands;

import net.minecraft.command.ICommandSender;

public interface ICommandHandler {

    void execute(ICommandSender sender, String[] args);
}
