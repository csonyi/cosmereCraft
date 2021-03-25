package com.csonyi.cosmerecraft.commands;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CosmereCraftCommands {
  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    LiteralCommandNode<CommandSource> cosmereCraftCommands = dispatcher.register(
      Commands.literal(CosmereCraft.MOD_ID)
        .then(Commands.literal("metals")
          .then(Commands.literal("get")
            .executes(GetMetalsCommand.register(dispatcher))
          )
          .then(Commands.literal("add")
            .then(AddMetalCommand.register(dispatcher))
          )
          .then(Commands.literal("reserve")
            .then(Commands.literal("add")
              .then(AddReserveCommand.register(dispatcher))
            )
          )
        )
    );
  }
}
