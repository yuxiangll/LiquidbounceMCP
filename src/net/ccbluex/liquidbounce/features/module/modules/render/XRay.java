package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ArrayUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.List;

@ModuleInfo(name = "XRay", description = "Allows you to see ores through walls.", category = ModuleCategory.RENDER)
public class XRay extends Module {
    private static XRay instance;

	public static XRay getInstance(){
		return instance;
	}

    public List<Block> xrayBlocks = ArrayUtils.getAsArray(Blocks.coal_ore,
		    Blocks.iron_ore,
		    Blocks.gold_ore,
		    Blocks.redstone_ore,
		    Blocks.lapis_ore,
		    Blocks.diamond_ore,
		    Blocks.emerald_ore,
		    Blocks.quartz_ore,
		    Blocks.clay,
		    Blocks.glowstone,
		    Blocks.crafting_table,
		    Blocks.torch,
		    Blocks.ladder,
		    Blocks.tnt,
		    Blocks.coal_block,
		    Blocks.iron_block,
		    Blocks.gold_block,
		    Blocks.diamond_block,
		    Blocks.emerald_block,
		    Blocks.redstone_block,
		    Blocks.lapis_block,
		    Blocks.fire,
		    Blocks.mossy_cobblestone,
		    Blocks.mob_spawner,
		    Blocks.end_portal_frame,
		    Blocks.enchanting_table,
		    Blocks.bookshelf,
		    Blocks.command_block,
		    Blocks.lava,
		    Blocks.flowing_lava,
		    Blocks.water,
		    Blocks.flowing_water,
		    Blocks.furnace,
		    Blocks.lit_furnace
    );

    public XRay(){
        instance = this;
		LiquidBounce.commandManager.registerCommand(new Command("xray", new String[]{}) {
			@Override
			public void execute(String[] args) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("add")) {
						if (args.length > 2) {
							try {
								Block block;
								try {
									block = Block.getBlockById(Integer.parseInt(args[2]));
								} catch (NumberFormatException exception) {
									Block tmpBlock = Block.getBlockFromName(args[2]);

									if (Block.getIdFromBlock(tmpBlock) <= 0 || tmpBlock == null) {
										chat("§7Block §8${args[2]}§7 does not exist!");
										return;
									}

									block = tmpBlock;
								}

								if (xrayBlocks.contains(block)) {
									chat("This block is already on the list.");
									return;
								}

								xrayBlocks.add(block);
								LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig);
								chat("§7Added block §8${block.localizedName}§7.");
								playEdit();
							} catch (NumberFormatException exception) {
								chatSyntaxError();
							}

							return;
						}

						chatSyntax("xray add <block_id>");
						return;
					}

					if (args[1].equalsIgnoreCase("remove")) {
						if (args.length > 2) {
							try {
								Block block;

								try {
									block = Block.getBlockById(Integer.parseInt(args[2]));
								} catch (NumberFormatException exception) {
									block = Block.getBlockFromName(args[2]);

									if (Block.getIdFromBlock(block) <= 0) {
										chat("§7Block §8${args[2]}§7 does not exist!");
										return;
									}
								}

								if (!xrayBlocks.contains(block)) {
									chat("This block is not on the list.");
									return;
								}

								xrayBlocks.remove(block);
								LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig);
								chat("§7Removed block §8${block.localizedName}§7.");
								playEdit();
							} catch (NumberFormatException exception) {
								chatSyntaxError();
							}

							return;
						}
						chatSyntax("xray remove <block_id>");
						return;
					}

					if (args[1].equalsIgnoreCase("list")) {
						chat("§8Xray blocks:");
						for (Block x : xrayBlocks) {
							chat("§8" + x.getLocalizedName() + " §7-§c " + Block.getIdFromBlock(x));
						}
						return;
					}
				}

				chatSyntax("xray <add, remove, list>");
			}
		});
    }

	@Override
    public void onToggle(boolean state) {
        mc.renderGlobal.loadRenderers();
    }
}
