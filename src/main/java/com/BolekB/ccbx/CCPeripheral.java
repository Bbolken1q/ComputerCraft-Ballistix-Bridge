package com.BolekB.ccbx;

import ballistix.common.tile.TileMissileSilo;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Our peripheral class, this is the class where we will register functions for our block.
 */
public class CCPeripheral implements IPeripheral {

    /**
     * A list of all our connected computers. We need this for event usages.
     */
    List<IComputerAccess> connectedComputers = new ArrayList<>();

    /**
     * This is our tile entity, we set the tile entity when we create a new peripheral. We use this tile entity to access the block or the world
     */
    private final CCTileEntity tileEntity;

    /**
     *
     * @param tileEntity the tile entity of this peripheral
     */
    public CCPeripheral(CCTileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    /**
     * We use getType to set the name for our peripheral. A modem would wrap our block as "test_n"
     *
     * @return the name of our peripheral
     */
    @Nonnull
    @Override
    public String getType() {
        return "silocontroller";
    }

    /**
     * CC use this method to check, if the peripheral in front of the modem is our peripheral
     */
    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return this == iPeripheral;
    }

    /**
     * Will be called when a computer disconnects from our block
     */
    @Override
    public void detach(@Nonnull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    /**
     * Will be called when a computer connects to our block
     */
    @Override
    public void attach(@Nonnull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    public CCTileEntity getTileEntity() {
        return tileEntity;
    }

    public static TileMissileSilo getMissileSilo(World level, BlockPos pos) {
        if (level == null) return null;

        TileEntity blockEntity = level.getBlockEntity(pos.above());

        if (blockEntity == null) return null;

        if (blockEntity instanceof TileMissileSilo) {
            return (TileMissileSilo) blockEntity;
        }

        return null;
    }

    /**
     * To register functions for our block, wee need to create final methods with the {@link LuaFunction} annotation
     * This function will send a message to every player on the Server
     */
//    @LuaFunction
//    public final void sendMessage(String message) {
//        //Used to get the current server and all online players.
//        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
//            //Now, send the message
//            player.sendStatusMessage(new StringTextComponent(message), false);
//        });
//    }

    /**
     * Because we want to access the world, we need to run this function on the main thread.
     */
    @LuaFunction(mainThread = true)
    public final boolean launch() {

        TileMissileSilo tileMissileSilo = getMissileSilo(this.getTileEntity().getLevel(), tileEntity.getBlockPos());

        if (tileMissileSilo == null) return false;

        tileMissileSilo.shouldLaunch = true;

        BlockPos targetPosition = tileMissileSilo.target.toBlockPos();

        for (IComputerAccess computerAccess : connectedComputers) {
            computerAccess.queueEvent("ccbx_launch", targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
        }

        return true;
    }

    @LuaFunction(mainThread = true)
    public final void setPosition(int x, int y, int z) {
        TileMissileSilo tileMissileSilo = getMissileSilo(this.getTileEntity().getLevel(), tileEntity.getBlockPos());

        if (tileMissileSilo == null) return;

        tileMissileSilo.target.set(x, y, z);

        for (IComputerAccess computerAccess : connectedComputers) {
            computerAccess.queueEvent("ccbx_update_position", x, y, z);
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean launchWithPosition(int x, int y, int z) {
        setPosition(x, y, z);
        return launch();
    }

}