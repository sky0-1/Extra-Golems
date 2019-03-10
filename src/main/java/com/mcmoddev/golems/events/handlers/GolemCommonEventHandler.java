package com.mcmoddev.golems.events.handlers;


import com.mcmoddev.golems.entity.base.GolemBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles events added specifically from this mod.
 **/
public class GolemCommonEventHandler {
	
	/*
	// TODO find out when we can handle this
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		////// Spawn some basic golems in villages //////
		// percent chance that each chunk will contain a golem
		final int GOLEM_CHANCE = Config.getVillageGolemSpawnChance();
		if(GOLEM_CHANCE > 0 && event.isHasVillageGenerated() && event.getRand().nextInt(100) < GOLEM_CHANCE) {
			BlockPos pos = new BlockPos(event.getChunkX() * 16, 100, event.getChunkZ() * 16);
			Village village = event.getWorld().villageCollection.getNearestVillage(pos, 32);
			if(village != null) {
				// spawn a golem based on the village biome
				Class<? extends GolemBase> golemClazz = getGolemForBiome(event.getWorld().getBiome(pos).getCategory(), event.getRand());
				GolemBase golemInstance = golemClazz != null ? GolemLookup.getGolem(event.getWorld(), golemClazz) : null;
				if(golemInstance != null) {	
					BlockPos spawn = getSafeSpawnPos(golemInstance, pos.add(8, 0, 8));
					if(spawn != null) {
						// spawn the golem
						golemInstance.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
						golemInstance.setPlayerCreated(false);
						event.getWorld().spawnEntity(golemInstance);
						// randomize texture if applicable
						if(golemInstance instanceof GolemMultiTextured) {
							byte texture = (byte)event.getRand().nextInt(((GolemMultiTextured)golemInstance).getNumTextures());
							((GolemMultiTextured)golemInstance).setTextureNum(texture);
						} else if(golemInstance instanceof GolemColorizedMultiTextured) {
							byte texture = (byte)event.getRand().nextInt(((GolemColorizedMultiTextured)golemInstance).getColorArray().length);
							((GolemColorizedMultiTextured)golemInstance).setTextureNum(texture);
						}
					}
				}
			}
		}
	}
	*/
	private static BlockPos getSafeSpawnPos(final EntityLivingBase entity, final BlockPos near) {
		final int radius = 6;
		final int maxTries = 24;
		BlockPos testing;
		for(int i = 0; i < maxTries; i++) {
			// get a random position near the passed BlockPos
			int x = near.getX() + entity.getEntityWorld().rand.nextInt(radius * 2) - radius;
			int z = near.getZ() + entity.getEntityWorld().rand.nextInt(radius * 2) - radius;
			int y = 128;
			testing = new BlockPos(x, y, z);
			// make sure to end up with a solid block
			while(entity.getEntityWorld().isAirBlock(testing) && testing.getY() > 0) {
				testing = testing.down(1);
			}
			// check if golem can spawn there
			IBlockState iblockstate = entity.getEntityWorld().getBlockState(testing);
			if(iblockstate.canEntitySpawn(entity)) {
				return testing.up(1);
			}
		}
		
		return null;
	}
	
//	/**
//	 * This method makes a list of golems to pick from based on the biome passed,
//	 * then returns a random member of that list.
//	 * @param biome The biome that this golem is in.
//	 * @param rand the random number generator.
//	 * @return a Golem Class based on the biome and random chance.
//	 */
//	private static Class<? extends GolemBase> getGolemForBiome(final Biome.Category biome, final Random rand) {
//		List<Class<? extends GolemBase>> options = new ArrayList<>();
//		// the following will be added to the options in certain biomes:
//		if(biome == Biome.Category.DESERT) {
//			// use the config to get desert-type golems
//			options.addAll(Config.getDesertGolems());
//		} else if(biome == Biome.Category.PLAINS || biome == Biome.Category.SAVANNA
//				|| biome == Biome.Category.TAIGA) {
//			// use the config to get plains-type golems
//			options.addAll(Config.getPlainsGolems());
//		} else if(biome == Biome.Category.MESA) {
//			// mesa-type golems
//			////options.add(EntityHardenedClayGolem.class);
//			////options.add(EntityStainedClayGolem.class);
//		} else if(biome == Biome.Category.JUNGLE) {
//			// jungle-type golems
//			////options.add(EntityLeafGolem.class);
//		} else if(biome == Biome.Category.ICY) {
//			// snow-type golems
//			options.add(EntityIceGolem.class);
//			////options.add(EntityWoolGolem.class);
//		} else if(biome == Biome.Category.SWAMP) {
//			// swamp-type golems
//			////options.add(EntityWoodenGolem.class);
//			options.add(EntitySlimeGolem.class);
//			////options.add(EntityLeafGolem.class);
//			options.add(EntityClayGolem.class);
//		}
//		// add some rare and semi-rare golems
//		final int clay = 3, crafting = 3, obsidian = 6, glowstone = 5, books = 4;
//		if(rand.nextInt(clay) == 0) {
//			options.add(EntityClayGolem.class);
//		}
//		if(rand.nextInt(crafting) == 0) {
//			options.add(EntityCraftingGolem.class);
//		}
//		if(rand.nextInt(obsidian) == 0) {
//			options.add(EntityObsidianGolem.class);
//		}
//		if(rand.nextInt(glowstone) == 0) {
//			options.add(EntityGlowstoneGolem.class);
//		}
//		if(rand.nextInt(books) == 0) {
//			options.add(EntityBookshelfGolem.class);
//		}
//		// choose a random golem from the list
//		return options.isEmpty() ? null : options.get(rand.nextInt(options.size()));
//	}
//



//	/**
//	 * Basically, this handler allows pumpkins to be placed anywhere
//	 * (as long as it's done by a player). Then upon placement, we try
//	 * to spawn a golem based on the blocks the pumpkin is on.
//	 *
//	 * Note:  This seems to be called twice on client and twice on server.
//	 * May have problems with dedicated server, or it might be fine.
//	 */
//	@SubscribeEvent
//	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
//		ItemStack stack = event.getItemStack();
//		float hitX = (float) event.getHitVec().x;
//		float hitY = (float) event.getHitVec().y;
//		float hitZ = (float) event.getHitVec().z;
//		// check qualifications for running this event...
//		//TODO: reimpl config
//		if(false && !event.isCanceled()
//				&& !stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
//			Block heldBlock = ((ItemBlock)stack.getItem()).getBlock();
//			// if player is holding pumpkin or lit pumpkin, try to place the block
//			if(heldBlock instanceof BlockCarvedPumpkin) {
//				// update the location to place block
//				BlockPos pumpkinPos = event.getPos();
//				IBlockState clicked = event.getWorld().getBlockState(pumpkinPos);
//				if (!clicked.isReplaceable(
//						new BlockItemUseContext(event.getWorld(), event.getEntityPlayer(), stack, pumpkinPos, event.getFace(), hitX, hitY, hitZ))) {
//		            pumpkinPos = pumpkinPos.offset(event.getFace());
//				}
//				// now we're ready to place the block
//				if(event.getEntityPlayer().canPlayerEdit(pumpkinPos, event.getFace(), stack)) {
//					IBlockState pumpkin = heldBlock.getDefaultState().with(BlockHorizontal.HORIZONTAL_FACING,
//							event.getEntityPlayer().getHorizontalFacing().getOpposite());
//					// set block and trigger golem-checking
//					if(event.getWorld().setBlockState(pumpkinPos, pumpkin)) {
//						event.setCanceled(true);
//						BlockGolemHead.trySpawnGolem(event.getWorld(), pumpkinPos);
//						// reduce itemstack
//						if(!event.getEntityPlayer().isCreative()) {
//							event.getItemStack().shrink(1);
//						}
//					}
//				}
//			}
//		}
//	}
	
	/*
	@SubscribeEvent
	public void onItemUse(LivingEntityUseItemEvent.Finish event) {
		// check if the item used was a pumpkin block, and if it was NOT placed
		if(Config.doesPumpkinBuildGolem() &&
			(event.getItem().getItem() instanceof ItemBlock 
			&& ((ItemBlock)event.getItem().getItem()).getBlock() == Blocks.PUMPKIN) 
			//&& (event.getResultStack().getCount() == event.getItem().getCount())
			) {
			// find out where to spawn the golem
			BlockPos placePos = new BlockPos(event.getEntityLiving().getLookVec());
			if(BlockGolemHead.trySpawnGolem(event.getEntityLiving().getEntityWorld(), placePos)) {
				event.getResultStack().shrink(1);
			}
		}
	}
	*/
	@SubscribeEvent
	public void onLivingSpawned(final EntityJoinWorldEvent event) {
		// add custom 'attack golem' AI to hostile mobs. They already have this for regular iron golems
		if(event.getEntity() instanceof EntityCreature) {
			final EntityCreature creature = (EntityCreature) event.getEntity();
			if (creatureAttacksGolems(creature)) {
				for (final EntityAITasks.EntityAITaskEntry entry : creature.targetTasks.taskEntries) {
					if (entry.action instanceof EntityAIAttackGolem) {
						return;
					}
				}
				creature.targetTasks.addTask(3, new EntityAIAttackGolem(creature));
			}
		// add custom 'chase golem' AI to hostile entities that do not inherit from EntityCreature
		// (currently just EntitySlime)
		} else if(event.getEntity() instanceof EntityLiving) {
			final EntityLiving living = (EntityLiving) event.getEntity();
			if (livingAttacksGolems(living)) {
				living.targetTasks.addTask(3, new EntityAIFindEntityNearest(living, GolemBase.class));
			}
		}
	}
	
	/** Returns true if this entity is an EntityCreature AND normally attacks Iron Golems **/
	private static boolean creatureAttacksGolems(EntityCreature e) {
		return e instanceof AbstractSkeleton || e instanceof EntitySpider 
				|| e instanceof AbstractIllager
				|| (e instanceof EntityZombie && !(e instanceof EntityPigZombie));
	}
	
	/** Returns true if this entity is any EntityLivingBase AND chases after Iron Golems **/
	private static boolean livingAttacksGolems(EntityLivingBase e) {
		return e instanceof EntitySlime;
	}

	private static final class EntityAIAttackGolem extends EntityAINearestAttackableTarget<GolemBase> {
		private EntityAIAttackGolem(final EntityCreature creature) {
			super(creature, GolemBase.class, true);
		}
	}
}