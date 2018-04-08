package org.casper.learning.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
//		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
//				CharsetUtil.UTF_8));
//		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//		while (true) {
//			System.out.println("R: ");
//			try {
//				ctx.writeAndFlush(Unpooled.copiedBuffer(reader.readLine(), CharsetUtil.UTF_8));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
		System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}