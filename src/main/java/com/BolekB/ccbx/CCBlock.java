package com.BolekB.ccbx;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class CCBlock extends Block {

    public CCBlock() {
        super(Properties.of(Material.METAL).harvestLevel(3).harvestTool(ToolType.PICKAXE));
    }

    //Create a new tile entity with our registry object
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Registration.CC_TILEENTITY.get().create();
    }

    //Say minecraft, our CCBlock has a tile entity.
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}