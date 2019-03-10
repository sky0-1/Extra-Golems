package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntityMushroomGolem extends GolemMultiTextured {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Mushrooms";
	public static final String FREQUENCY = "Mushroom Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public static final String SHROOM_PREFIX = "shroom";
	public static final String[] SHROOM_TYPES = {"red", "brown"};
	public final IBlockState[] mushrooms = {Blocks.BROWN_MUSHROOM.getDefaultState(),
		Blocks.RED_MUSHROOM.getDefaultState()};
	protected static final Block[] soils = {Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM};

	public EntityMushroomGolem(final World world) {
		super(EntityMushroomGolem.class, world, SHROOM_PREFIX, SHROOM_TYPES);
		this.setCanSwim(true);
		final boolean allowed = container.canUseSpecial;
		//TODO: reimpl
		int freq = allowed ? 420 : -100;
		freq += this.rand.nextInt(Math.max(10, freq / 2));
		this.tasks.addTask(2,
			new EntityAIPlaceRandomBlocksStrictly(this, freq, mushrooms, soils, allowed));
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}


	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GRASS_STEP;
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// heals randomly, but only at night
		//TODO: reimpl config

		//Note how it goes from least expensive to most expensive
		if(this.container.canUseSpecial && !this.getEntityWorld().isDaytime() && rand.nextInt(450) == 0) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 2));
		}
	}

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		// use block type to give this golem the right texture (defaults to brown mushroom)
		byte textureNum = body.getBlock() == Blocks.RED_MUSHROOM_BLOCK ? (byte) 0
			: (byte) 1;
		textureNum %= this.getNumTextures();
		this.setTextureNum(textureNum);
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (container.canUseSpecial) {
			list.add(TextFormatting.DARK_GREEN + trans("entitytip.plants_shrooms"));
		}
		//TODO: reimpl config
		if(container.canUseSpecial) {
			String sHeals = TextFormatting.LIGHT_PURPLE + trans("entitytip.heals");
			list.add(sHeals);
		}
		return list;
	}
}