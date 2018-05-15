package org.casper.learning.io.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author Casper
 */
@Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
		ByteBuf in = (ByteBuf) msg;
		System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
		ctx.write(Unpooled.copiedBuffer("Response from server. You have input \"" + in.toString(CharsetUtil.UTF_8) + "\"!", CharsetUtil.UTF_8));
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}