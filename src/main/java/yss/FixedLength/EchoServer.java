package yss.FixedLength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

	public static void main(String[] args) throws Exception {
		int port=8081;
		if(args !=null && args.length>0) {
			try{
				port = Integer.valueOf(args[0]);
			}catch (NumberFormatException e) {
				//采用默认值
				// TODO: handle exception
			}
		}
		
		new EchoServer().bind(port);
	}

	public void bind(int port) throws Exception {
		//用于服务端接受客户端的连接
		EventLoopGroup bossgroup = new NioEventLoopGroup();
		//用于进行socketchannel的网络读写
		EventLoopGroup workGroup = new NioEventLoopGroup();
	try {
		//netty 用于启动NIO服务端的辅助启动类
		ServerBootstrap b = new ServerBootstrap();
		//将两个NIO线程组作为参数传递到serverbootstrap中
		b.group(bossgroup, workGroup)
		//设置创建的channel为NioServerSocketChannel
		.channel(NioServerSocketChannel.class)
		//配置NioServerSocketChannel的TCP参数
		.option(ChannelOption.SO_BACKLOG, 1024)
		//设置日志处理handler，级别为INFO
		.handler(new LoggingHandler(LogLevel.INFO))
		//绑定I/O事件处理类ChildchannelHandler
		.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel arg0) throws Exception {
				// TODO Auto-generated method stub
//				ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
//				ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
				//固定长度发送
				arg0.pipeline().addLast(new FixedLengthFrameDecoder(10));
				arg0.pipeline().addLast(new StringDecoder());
				arg0.pipeline().addLast(new EchoServerHandler());
			}
		});
		
		//绑定端口，同步等待成功
		ChannelFuture f = b.bind(port).sync();
		
		//等待服务监听端口关闭
		f.channel().closeFuture().sync();
		// TODO Auto-generated method stub
	} finally {
		bossgroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
	}
}
