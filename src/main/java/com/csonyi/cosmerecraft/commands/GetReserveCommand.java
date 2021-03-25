package com.csonyi.cosmerecraft.commands;

import com.csonyi.cosmerecraft.CosmereCraft;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class GetReserveCommand implements Command<CommandSource> {
  private static final GetReserveCommand CMD = new GetReserveCommand();

  public static GetReserveCommand register(CommandDispatcher<CommandSource> dispatcher) {
    return CMD;
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    InvestedMetal metalArg
            = context.getArgument("metal", InvestedMetal.class);
    ServerPlayerEntity player = source.asPlayer();
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    float queriedReserve = allomancy.getReserve(metalArg);

    source.sendFeedback(
            new TranslationTextComponent(
                    CosmereCraft.MOD_ID + ".commands.message.getReserve",
                    player.getDisplayName(),
                    String.valueOf(queriedReserve),
                    metalArg.toTranslationTextComponent()
    ),
    true);

    return 1;
  }
}
