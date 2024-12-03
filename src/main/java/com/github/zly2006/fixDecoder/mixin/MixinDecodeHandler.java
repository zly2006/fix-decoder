package com.github.zly2006.fixDecoder.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistryPacketHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.NetworkState;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.List;

@Mixin(DecoderHandler.class)
public class MixinDecodeHandler {
    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private NetworkState<?> state;

    @Inject(
            method = "decode",
            at = @At(value = "INVOKE", target = "Ljava/io/IOException;<init>(Ljava/lang/String;)V"),
            cancellable = true
    )
    private void onError(ChannelHandlerContext context, ByteBuf buf, List<Object> objects, CallbackInfo ci,
                         @Local Packet<?> packet) {
        buf.skipBytes(buf.readableBytes());
        PacketType<?> packetType = packet.getPacketId();
        String var10002 = this.state.id().getId();
        LOGGER.error("Packet {}/{} ({}) was larger than I expected, found {} bytes extra whilst reading packet {}", var10002, packetType, packet.getClass().getSimpleName(), buf.readableBytes(), packetType);
        if (packet instanceof CustomPayloadS2CPacket(CustomPayload payload)) {
            LOGGER.error("Payload id: {}", payload.getId().id());
        }
        ci.cancel();
    }
}
