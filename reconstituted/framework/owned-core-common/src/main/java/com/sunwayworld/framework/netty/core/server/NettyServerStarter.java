package com.sunwayworld.framework.netty.core.server;

import com.sunwayworld.framework.netty.core.NettyStarter;
import com.sunwayworld.framework.netty.core.channel.NettyChannelHolder;
import com.sunwayworld.framework.utils.NetUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerStarter implements NettyStarter {
   private static final Logger logger = LoggerFactory.getLogger(NettyServerStarter.class);
   private EventLoopGroup bossGroup = new NioEventLoopGroup();
   private EventLoopGroup workerGroup = new NioEventLoopGroup();
   private AtomicBoolean connecting = new AtomicBoolean(false);
   private final NettyServerConfig config;

   private NettyServerStarter(NettyServerConfig nettyServerConfig) {
      this.config = nettyServerConfig;
   }

   public static NettyServerStarter of(NettyServerConfig nettyServerConfig) {
      return new NettyServerStarter(nettyServerConfig);
   }

   @Override
   public void start() {
      if (!this.connecting.getAndSet(true)) {
         try {
            if (!NettyChannelHolder.isLocalPresent(this.getConfig().getLocalAddress())) {
               ServerBootstrap bootstrap = new ServerBootstrap()
                  .group(this.getBossGroup(), this.getWorkerGroup())
                  .channel(NioServerSocketChannel.class)
                  .option(ChannelOption.SO_BACKLOG, 12)
                  .localAddress(this.getConfig().getLocalAddress())
                  .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.getConfig().getBossConnectTimeoutMillis())
                  .childOption(ChannelOption.SO_KEEPALIVE, this.getConfig().isWorkerKeepAlive())
                  .childHandler(this.getConfig().getWorkerGroupChannelHandler());
               if (this.config.getBossGroupChannelHandler() != null) {
                  bootstrap.handler(this.getConfig().getBossGroupChannelHandler());
               }

               int count = 0;

               while (true) {
                  try {
                     logger.info("Try start netty server on port: " + this.getConfig().getLocalAddress().getPort() + " -- count: " + ++count);
                     ChannelFuture future = bootstrap.bind().sync().await();
                     NettyChannelHolder.add(future.channel());
                     logger.info("Netty server started on port: " + this.getConfig().getLocalAddress().getPort());
                     return;
                  } catch (Exception var9) {
                     logger.error("Unable start netty server on port: " + this.getConfig().getLocalAddress().getPort() + " due to: " + var9.getMessage());

                     try {
                        Thread.sleep(5000L);
                     } catch (InterruptedException var8) {
                     }
                  }
               }
            }
         } finally {
            this.connecting.set(false);
         }
      }
   }

   @Override
   public void destroy() {
      this.getBossGroup().shutdownGracefully().syncUninterruptibly();
      this.getWorkerGroup().shutdownGracefully().syncUninterruptibly();
      logger.info("Netty server stopped on port: " + this.getConfig().getLocalAddress().getPort());
   }

   @Override
   public boolean match(InetSocketAddress socketAddress) {
      return this.getConfig().getLocalAddress().equals(socketAddress);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         return !(obj instanceof NettyServerStarter)
            ? false
            : NetUtils.equals(((NettyServerStarter)obj).getConfig().getLocalAddress(), this.getConfig().getLocalAddress());
      }
   }

   @Override
   public int hashCode() {
      return this.getConfig().getLocalAddress() == null ? 42 : this.getConfig().getLocalAddress().hashCode();
   }

   public EventLoopGroup getBossGroup() {
      return this.bossGroup;
   }

   public EventLoopGroup getWorkerGroup() {
      return this.workerGroup;
   }

   public AtomicBoolean getConnecting() {
      return this.connecting;
   }

   public NettyServerConfig getConfig() {
      return this.config;
   }

   public void setBossGroup(EventLoopGroup bossGroup) {
      this.bossGroup = bossGroup;
   }

   public void setWorkerGroup(EventLoopGroup workerGroup) {
      this.workerGroup = workerGroup;
   }

   public void setConnecting(AtomicBoolean connecting) {
      this.connecting = connecting;
   }
}
