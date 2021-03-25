package com.csonyi.cosmerecraft.common.capabilities.allomancy;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.Set;

public interface IAllomancy {
  Set<InvestedMetal> getMetals();
  void setMetals(Set<InvestedMetal> metals);
  boolean hasMetal(InvestedMetal metal);
  void addMetal(InvestedMetal metal);
  void removeMetal(InvestedMetal metal);


  void setAtium(boolean hasAtium);
  void setAtiumReserve(float amount);
  void changeAtiumReserve(float amount);
  float getAtiumReserve();
  boolean hasAtium();

  void setReserves(Map<InvestedMetal, Float> reserves);
  Map<InvestedMetal, Float> getReserves();
  float getReserve(InvestedMetal metal);
  boolean hasReserve(InvestedMetal metal);
  void setReserve(InvestedMetal metal, float amount);
  void changeReserve(InvestedMetal metal, float amount);

  Set<InvestedMetal> getBurning();
  void startBurning(InvestedMetal metal);
  void stopBurning(InvestedMetal metal);
  void toggleBurning(InvestedMetal metal);
  void setBurning(Set<InvestedMetal> metals);
  boolean isBurning(InvestedMetal metal);

  void burn(InvestedMetal metal);

  boolean isPushing();
  double getPushPower();
  void increasePushPower(float amount);
  void incrementPushPower();
  void decreasePushPower(float amount);
  void decrementPushPower();

  void applyPush(PlayerEntity player);
  void keepFloating(PlayerEntity player);

  void calculateHoverHeight(PlayerEntity player);

  void applyMetalEffect(InvestedMetal metal, PlayerEntity player);
  void tickEffects();


  void update(Set<InvestedMetal> metals, Map<InvestedMetal, Float> reserves,
              Set<InvestedMetal> burningMetals, boolean hasAtium, float atiumReserve
  );
}
