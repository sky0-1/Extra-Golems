package com.golems.events.handlers;

import com.golems.entity.*;
import com.golems.events.GolemBuildEvent;
import com.golems.main.Config;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Handles events added specifically from this mod. **/
public class GolemCommonEventHandler {

	@SubscribeEvent
	public void onBuildGolem(final GolemBuildEvent event) {
		// if it has not already been set
		if (event.isGolemNull()) {
			if (event.blockBelow == Blocks.DIAMOND_BLOCK) {
				event.setGolem(new EntityDiamondGolem(event.worldObj), Config.DIAMOND.canSpawn());
			} else if (event.blockBelow == Blocks.EMERALD_BLOCK) {
				event.setGolem(new EntityEmeraldGolem(event.worldObj), Config.EMERALD.canSpawn());
			} else if (event.blockBelow == Blocks.GOLD_BLOCK) {
				event.setGolem(new EntityGoldGolem(event.worldObj), Config.GOLD.canSpawn());
			} else if (event.blockBelow == Blocks.LAPIS_BLOCK) {
				event.setGolem(new EntityLapisGolem(event.worldObj), Config.LAPIS.canSpawn());
			} else if (event.blockBelow == Blocks.TNT) {
				event.setGolem(new EntityTNTGolem(event.worldObj), Config.TNT.canSpawn());
			} else if (event.blockBelow == Blocks.SPONGE) {
				event.setGolem(new EntitySpongeGolem(event.worldObj), Config.SPONGE.canSpawn());
			} else if (event.blockBelow == Blocks.SANDSTONE) {
				event.setGolem(new EntitySandstoneGolem(event.worldObj),
						Config.SANDSTONE.canSpawn());
			} else if (event.blockBelow == Blocks.HARDENED_CLAY) {
				event.setGolem(new EntityHardenedClayGolem(event.worldObj),
						Config.HARD_CLAY.canSpawn());
			} else if (event.blockBelow == Blocks.OBSIDIAN) {
				event.setGolem(new EntityObsidianGolem(event.worldObj), Config.OBSIDIAN.canSpawn());
			} else if (event.blockBelow == Blocks.BOOKSHELF) {
				event.setGolem(new EntityBookshelfGolem(event.worldObj),
						Config.BOOKSHELF.canSpawn());
			} else if (event.blockBelow == Blocks.GLASS) {
				event.setGolem(new EntityGlassGolem(event.worldObj), Config.GLASS.canSpawn());
			} else if (event.blockBelow == Blocks.PACKED_ICE
					|| (Config.ICE.getBoolean(EntityIceGolem.CAN_USE_REGULAR_ICE)
							&& event.blockBelow == Blocks.ICE)) {
				event.setGolem(new EntityIceGolem(event.worldObj), Config.ICE.canSpawn());
			} else if (event.blockBelow instanceof BlockLog) {
				GolemMultiTextured golem = new EntityWoodenGolem(event.worldObj);
				// use block metadata to give this golem the right texture
				final int meta = event.blockBelow.getMetaFromState(
						event.blockState.withProperty(BlockLog.LOG_AXIS, EnumAxis.NONE));
				byte textureNum = event.blockBelow == Blocks.LOG2 ? (byte) (meta + 4) : (byte) meta;
				textureNum %= golem.getNumTextures();
				golem.setTextureNum(textureNum);
				// actually set the golem
				event.setGolem(golem, Config.WOOD.canSpawn());
			} else if (event.blockBelow == Blocks.CLAY) {
				event.setGolem(new EntityClayGolem(event.worldObj), Config.CLAY.canSpawn());
			} else if (event.blockBelow == Blocks.HAY_BLOCK) {
				event.setGolem(new EntityStrawGolem(event.worldObj), Config.STRAW.canSpawn());
			} else if (event.blockBelow == Blocks.NETHER_BRICK
					|| event.blockBelow == Blocks.RED_NETHER_BRICK) {
				event.setGolem(new EntityNetherBrickGolem(event.worldObj),
						Config.NETHERBRICK.canSpawn());
			} else if (event.blockBelow == Blocks.GLOWSTONE) {
				event.setGolem(new EntityGlowstoneGolem(event.worldObj),
						Config.GLOWSTONE.canSpawn());
			} else if (event.blockBelow == Blocks.END_STONE) {
				event.setGolem(new EntityEndstoneGolem(event.worldObj), Config.ENDSTONE.canSpawn());
			} else if (event.blockBelow == Blocks.QUARTZ_BLOCK) {
				event.setGolem(new EntityQuartzGolem(event.worldObj), Config.QUARTZ.canSpawn());
			} else if (event.blockBelow == Blocks.COAL_BLOCK) {
				event.setGolem(new EntityCoalGolem(event.worldObj), Config.COAL.canSpawn());
			} else if (event.blockBelow == Blocks.MELON_BLOCK) {
				event.setGolem(new EntityMelonGolem(event.worldObj), Config.MELON.canSpawn());
			} else if (event.blockBelow == Blocks.SLIME_BLOCK) {
				event.setGolem(new EntitySlimeGolem(event.worldObj), Config.SLIME.canSpawn());
			} else if (event.blockBelow instanceof BlockLeaves) {
				event.setGolem(new EntityLeafGolem(event.worldObj), Config.LEAF.canSpawn());
			} else if (event.blockBelow == Blocks.PRISMARINE) {
				event.setGolem(new EntityPrismarineGolem(event.worldObj),
						Config.PRISMARINE.canSpawn());
			} else if (event.blockBelow == Blocks.BROWN_MUSHROOM_BLOCK
					|| event.blockBelow == Blocks.RED_MUSHROOM_BLOCK) {
				final GolemMultiTextured golem = new EntityMushroomGolem(event.worldObj);
				// use block metadata to give this golem the right texture
				byte textureNum = event.blockBelow == Blocks.RED_MUSHROOM_BLOCK ? (byte) 0
						: (byte) 1;
				textureNum %= golem.getNumTextures();
				golem.setTextureNum(textureNum);
				// actually set the golem
				event.setGolem(golem, Config.MUSHROOM.canSpawn());
			} else if (event.blockBelow == Blocks.RED_SANDSTONE) {
				event.setGolem(new EntityRedSandstoneGolem(event.worldObj),
						Config.RED_SANDSTONE.canSpawn());
			} else if (event.blockBelow == Blocks.SEA_LANTERN) {
				event.setGolem(new EntitySeaLanternGolem(event.worldObj),
						Config.SEA_LANTERN.canSpawn());
			} else if (event.blockBelow == Blocks.REDSTONE_BLOCK) {
				event.setGolem(new EntityRedstoneGolem(event.worldObj), Config.REDSTONE.canSpawn());
			} else if (event.blockBelow == Blocks.WOOL) {
				final GolemMultiTextured golem = new EntityWoolGolem(event.worldObj);
				// use block metadata to give this golem the right texture
				final int meta = event.blockBelow.getMetaFromState(event.blockState)
						% golem.getTextureArray().length;
				golem.setTextureNum((byte) meta);
				// actually set the golem
				event.setGolem(golem, Config.WOOL.canSpawn());
			} else if (event.blockBelow == Blocks.STAINED_HARDENED_CLAY) {
				final GolemColorizedMultiTextured golem = new EntityStainedClayGolem(event.worldObj);
				// use block metadata to give this golem the right texture
				final int meta = event.blockBelow.getMetaFromState(event.blockState)
						% golem.getColorArray().length;
				golem.setTextureNum((byte) (golem.getColorArray().length - meta - 1));
				// actually set the golem
				event.setGolem(golem, Config.STAINED_CLAY.canSpawn());
			} else if (event.blockBelow == Blocks.STAINED_GLASS) {
				final GolemColorizedMultiTextured golem = new EntityStainedGlassGolem(event.worldObj);
				// use block metadata to give this golem the right texture
				final int meta = event.blockBelow.getMetaFromState(event.blockState)
						% golem.getColorArray().length;
				golem.setTextureNum((byte) (golem.getColorArray().length - meta - 1));
				// actually set the golem
				event.setGolem(golem, Config.STAINED_GLASS.canSpawn());
			} else if (event.blockBelow == Blocks.MAGMA) {
				event.setGolem(new EntityMagmaGolem(event.worldObj), Config.MAGMA.canSpawn());
			} else if (event.blockBelow == Blocks.NETHER_WART_BLOCK) {
				event.setGolem(new EntityNetherWartGolem(event.worldObj),
						Config.NETHERWART.canSpawn());
			} else if (event.blockBelow == Blocks.BONE_BLOCK) {
				event.setGolem(new EntityBoneGolem(event.worldObj), Config.BONE.canSpawn());
			} else if (event.blockBelow instanceof BlockWorkbench) {
				event.setGolem(new EntityCraftingGolem(event.worldObj), Config.CRAFTING.canSpawn());
			}
		}
	}

	@SubscribeEvent
	public void onLivingSpawned(final EntityJoinWorldEvent event) {
		// add custom 'attack golem' AI to zombies. They already have this for regular iron golems
	    if (event.getEntity() instanceof EntityZombie && !(event.getEntity() instanceof EntityPigZombie)) {
	        final EntityZombie zombie = (EntityZombie) event.getEntity();
	        for (final EntityAITasks.EntityAITaskEntry entry : zombie.targetTasks.taskEntries) {
	            if (entry.action instanceof EntityAIAttackGolem) {
	                return;
	            }
	        }
	        zombie.targetTasks.addTask(3, new EntityAIAttackGolem(zombie));
	    }
	}
	


	private static final class EntityAIAttackGolem extends EntityAINearestAttackableTarget<GolemBase> {
	    private EntityAIAttackGolem(final EntityCreature creature) {
	        super(creature, GolemBase.class, true);
	    }
	}
}
