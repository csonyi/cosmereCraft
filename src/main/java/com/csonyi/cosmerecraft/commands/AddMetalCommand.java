package com.csonyi.cosmerecraft.commands;

import com.csonyi.cosmerecraft.common.capabilities.allomancy.InvestedMetal;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.CapabilityAllomancy;
import com.csonyi.cosmerecraft.common.capabilities.allomancy.IAllomancy;
import com.csonyi.cosmerecraft.common.networking.NetworkHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.command.EnumArgument;

public class AddMetalCommand implements Command<CommandSource> {
  private static final AddMetalCommand CMD = new AddMetalCommand();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.argument(
            "metal",
            EnumArgument.enumArgument(InvestedMetal.class)
    ).executes(CMD);
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    CommandSource source = context.getSource();

    InvestedMetal addedMetal
            = context.getArgument("metal", InvestedMetal.class);
    ServerPlayerEntity player = source.asPlayer();
    IAllomancy allomancy = player.getCapability(CapabilityAllomancy.ALLOMANCY_CAPABILITY).orElse(null);
    allomancy.addMetal(addedMetal);

    Minecraft mc = Minecraft.getInstance();
    if(mc.getConnection() != null) NetworkHandler.syncAllomancy(player);

    source.sendFeedback(
            new TranslationTextComponent(
                    "cosmerecraft.commands.message.addMetal",
                    addedMetal.toTranslationTextComponent(),
                    player.getDisplayName()
            ),
            true
    );
    return 1;
  }
}
