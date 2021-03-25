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

import java.util.Set;

public class GetMetalsCommand implements Command<CommandSource> {
  private static final GetMetalsCommand CMD = new GetMetalsCommand();

  public static GetMetalsCommand register(CommandDispatcher<CommandSource> dispatcher) {
    return CMD;
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    ServerPlayerEntity player = source.asPlayer();
    IAllomancy allomancy = CapabilityAllomancy.getAllomancy(player);
    Set<InvestedMetal> metals = allomancy.getMetals();
    StringBuilder sb = new StringBuilder();

    for(InvestedMetal metal : metals) {
      sb.append(metal.name()).append(", ");
    }

    sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",") + 1);

    source.sendFeedback(
            new TranslationTextComponent(
                    CosmereCraft.MOD_ID + ".commands.message.getMetals",
                    player.getDisplayName(),
                    sb.toString()
            ),
            true
    );
    return 1;
  }
}
