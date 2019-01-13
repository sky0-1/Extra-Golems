package com.golems.entity;

import java.util.List;

import com.golems.events.EndGolemTeleportEvent;
import com.golems.main.Config;
import com.golems.main.GolemItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public final class EntityEndstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Teleporting";
	public static final String ALLOW_WATER_HURT = "Can Take Water Damage";

	/** countdown timer for next teleport. **/
	protected int teleportDelay;
	/** Max distance for one teleport; range is 32.0 for endstone golem. **/
	protected double range;
	protected boolean canTeleport;
	protected boolean isHurtByWater;
	protected boolean hasAmbientParticles;

	protected int ticksBetweenIdleTeleports;
	/** Percent chance to teleport away when hurt by non-projectile. **/
	protected int chanceToTeleportWhenHurt;

	/** Default constructor. **/
	public EntityEndstoneGolem(final World world) {
		this(world, Config.ENDSTONE.getBaseAttack(), new ItemStack(Blocks.END_STONE), 32.0D,
				Config.ENDSTONE.getBoolean(ALLOW_SPECIAL),
				Config.ENDSTONE.getBoolean(ALLOW_WATER_HURT), true);
	}

	/**
	 * Flexible constructor to allow child classes to customize.
	 *
	 * @param world
	 *            the worldObj
	 * @param attack
	 *            base attack damage
	 * @param pick
	 *            Creative pick-block return
	 * @param teleportRange
	 *            64.0 for enderman, 32.0 for endstone golem
	 * @param teleportingAllowed
	 *            usually set by the config, checked here
	 * @param ambientParticles
	 *            whether always to display "portal" particles
	 **/
	public EntityEndstoneGolem(final World world, final float attack, final ItemStack pick, final double teleportRange,
			final boolean teleportingAllowed, final boolean hurtByWater, final boolean ambientParticles) {
		super(world, attack, pick);
		this.ticksBetweenIdleTeleports = 200;
		this.chanceToTeleportWhenHurt = 15;
		this.range = teleportRange;
		this.canTeleport = teleportingAllowed;
		this.isHurtByWater = hurtByWater;
		this.hasAmbientParticles = ambientParticles;
	}

	/**
	 * Flexible contructor to allow child classes to customize.
	 *
	 * @param world
	 *            the worldObj
	 * @param attack
	 *            base attack damage
	 * @param teleportRange
	 *            64.0 for enderman, 32.0 for endstone golem
	 * @param teleportingAllowed
	 *            usually set by the config, checked here
	 * @param ambientParticles
	 *            whether to always display "portal" particles
	 **/
	public EntityEndstoneGolem(final World world, final float attack, final double teleportRange,
			final boolean teleportingAllowed, final boolean hurtByWater, final boolean ambientParticles) {
		this(world, attack, new ItemStack(GolemItems.golemHead, 1), teleportRange,
				teleportingAllowed, hurtByWater, ambientParticles);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("end_stone");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.ENDSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		this.addDrop(dropList, Blocks.END_STONE, 0, 2, 2 + lootingLevel, 90);
//		this.addDrop(dropList, Items.ENDER_PEARL, 0, 2, 4 + lootingLevel, 40);
//		this.addDrop(dropList, Items.ENDER_EYE, 0, 1, 1 + lootingLevel, 6);
//	}

	protected boolean teleportRandomly() {
		final double d0 = this.posX + (this.rand.nextDouble() - 0.5D) * range;
		final double d1 = this.posY + (this.rand.nextDouble() - 0.5D) * range * 0.5D;
		final double d2 = this.posZ + (this.rand.nextDouble() - 0.5D) * range;
		return this.teleportTo(d0, d1, d2);
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();

		if (this.isHurtByWater && this.isWet()) {
			this.attackEntityFrom(DamageSource.DROWN, 1.0F);
			for (int i = 0; i < 16; ++i) {
				if (this.teleportRandomly()) {
					break;
				}
			}
		}
		if (this.getRevengeTarget() != null) {
			this.faceEntity(this.getRevengeTarget(), 100.0F, 100.0F);
			if (rand.nextInt(5) == 0) {
				this.teleportToEntity(this.getRevengeTarget());
			}
		} else if (rand.nextInt(this.ticksBetweenIdleTeleports) == 0) {
			this.teleportRandomly();
		}

	}

	@Override
	public void onLivingUpdate() {
		if (this.world.isRemote) {
			for (int i = 0; this.hasAmbientParticles && i < 2; ++i) {
				this.world.spawnParticle(EnumParticleTypes.PORTAL,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						this.posY + this.rand.nextDouble() * (double) this.height - 0.25D,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						(this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
						(this.rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
			}
		}

		if (!this.world.isRemote && this.isEntityAlive()) {
			if (this.getRevengeTarget() != null) {
				if (this.getRevengeTarget() instanceof EntityMob) {
					if (this.getRevengeTarget().getDistanceSq(this) < 16.0D && (rand.nextInt(5) == 0
							|| this.getRevengeTarget().getRevengeTarget() == this)) {
						this.teleportRandomly();
					}

					this.teleportDelay = 0;
				} else if (this.getRevengeTarget().getDistanceSq(this) > 256.0D
						&& this.teleportDelay++ >= 30
						&& this.teleportToEntity(this.getRevengeTarget())) {
					this.teleportDelay = 0;
				}
			} else {
				this.teleportDelay = 0;
			}
		}

		this.isJumping = false;
		super.onLivingUpdate();
	}

	@Override
	public boolean attackEntityFrom(final DamageSource src, final float amnt) {
		if (this.isEntityInvulnerable(src)) {
			return false;
		} else {

			if (src instanceof EntityDamageSourceIndirect) {
				for (int i = 0; i < 32; ++i) {
					if (this.teleportRandomly()) {
						return true;
					}
				}

				return super.attackEntityFrom(src, amnt);
			} else {
				if (rand.nextInt(this.chanceToTeleportWhenHurt) == 0
						|| (this.getRevengeTarget() != null && rand.nextBoolean())) {
					this.teleportRandomly();
				}

				return super.attackEntityFrom(src, amnt);
			}
		}
	}

	/**
	 * Teleport the golem to another entity.
	 **/
	protected boolean teleportToEntity(final Entity entity) {
		Vec3d vec3d = new Vec3d(this.posX - entity.posX,
				this.getEntityBoundingBox().minY + (double) (this.height / 2.0F) - entity.posY
						+ (double) entity.getEyeHeight(),
				this.posZ - entity.posZ);
		vec3d = vec3d.normalize();
		final double d0 = 16.0D;
		final double d1 = this.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * d0;
		final double d2 = this.posY + (double) (this.rand.nextInt(16) - 8) - vec3d.y * d0;
		final double d3 = this.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * d0;
		return this.teleportTo(d1, d2, d3);
	}

	/**
	 * Teleport the golem.
	 **/
	private boolean teleportTo(final double x, final double y, final double z) {
		final EndGolemTeleportEvent event = new EndGolemTeleportEvent(this, x, y, z, 0);
		if (!this.canTeleport || MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		final boolean flag = this.attemptTeleport(event.getTargetX(), event.getTargetY(),
				event.getTargetZ());

		if (flag) {
			this.world.playSound((EntityPlayer) null, this.prevPosX, this.prevPosY, this.prevPosZ,
					SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
			this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// this will only fire for the Endstone Golem, not child classes
		if (this.getClass() == EntityEndstoneGolem.class && Config.ENDSTONE.getBoolean(EntityEndstoneGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.DARK_AQUA + trans("entitytip.can_teleport"));
		return list;
	}
}
