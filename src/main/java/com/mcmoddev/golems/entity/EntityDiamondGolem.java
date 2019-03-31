package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityDiamondGolem extends GolemBase {

	public EntityDiamondGolem(final World world) {
		super(EntityDiamondGolem.class, world);
		this.setLootTableLoc(GolemNames.DIAMOND_GOLEM);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.DIAMOND_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
}
