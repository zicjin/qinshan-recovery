package com.sunwayworld.framework.netty.core.client;

import com.sunwayworld.framework.netty.core.NettyStarter;
import com.sunwayworld.framework.netty.core.channel.NettyChannelHolder;
import com.sunwayworld.framework.utils.NetUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientStarter implements NettyStarter {
   private static final Logger logger = LoggerFactory.getLogger(NettyClientStarter.class);
   private EventLoopGroup workerGroup = new NioEventLoopGroup();
   private AtomicBoolean connecting = new AtomicBoolean(false);
   private final NettyClientConfig config;

   private NettyClientStarter(NettyClientConfig nettyClientConfig) {
      this.config = nettyClientConfig;
   }

   public static NettyClientStarter of(NettyClientConfig nettyClientConfig) {
      return new NettyClientStarter(nettyClientConfig);
   }

   @Override
   public void start() {
      if (!this.connecting.getAndSet(true)) {
         try {
            if (!NettyChannelHolder.isRemotePresent(this.config.getServerAddress())) {
               int count = 0;

               while (this.getConfig().getMaxAttempts() > count) {
                  Bootstrap bootstrap = new Bootstrap();
                  bootstrap.group(this.workerGroup)
                     .channel(NioSocketChannel.class)
                     .option(ChannelOption.SO_KEEPALIVE, this.getConfig().isKeepAlive())
                     .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.getConfig().getConnectTimeoutMillis());
                  if (this.getConfig().getChannelHandler() != null) {
                     bootstrap.handler(this.getConfig().getChannelHandler());
                  }

                  try {
                     logger.error("Try start netty client for server: " + this.getConfig().getServerAddress().toString() + " -- count: " + ++count);
                     ChannelFuture future = bootstrap.connect(this.getConfig().getServerAddress()).sync().await();
                     NettyChannelHolder.add(future.channel());
                     logger.error("Netty client started for server: " + this.getConfig().getServerAddress().toString());
                     return;
                  } catch (Exception var9) {
                     logger.error("Unable start netty client for server: " + this.getConfig().getServerAddress().toString() + " due to: " + var9.getMessage());

                     try {
                        Thread.sleep(2000L);
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
      this.getWorkerGroup().shutdownGracefully().syncUninterruptibly();
      logger.info("Netty server stopped for server: " + this.getConfig().getServerAddress().toString());
   }

   @Override
   public boolean match(InetSocketAddress socketAddress) {
      return this.getConfig().getServerAddress().equals(socketAddress);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         return !(obj instanceof NettyClientStarter)
            ? false
            : NetUtils.equals(((NettyClientStarter)obj).getConfig().getServerAddress(), this.getConfig().getServerAddress());
      }
   }

   @Override
   public int hashCode() {
      return this.getConfig().getServerAddress() == null ? 42 : this.getConfig().getServerAddress().hashCode();
   }

   public EventLoopGroup getWorkerGroup() {
      return this.workerGroup;
   }

   public AtomicBoolean getConnecting() {
      return this.connecting;
   }

   public NettyClientConfig getConfig() {
      return this.config;
   }
}
