package org.jfantasy.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.commons.lang3.tuple.Pair;
import org.jfantasy.rpc.codec.RpcDecoder;
import org.jfantasy.rpc.codec.RpcEncoder;
import org.jfantasy.rpc.dto.RpcRequest;
import org.jfantasy.rpc.dto.RpcResponse;
import org.jfantasy.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/dozer47528/AutoReconnectNettyExample/blob/master/src/main/java/cc/dozer/netty/example/TcpClient.java
 */
public class NettyClient implements IClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private Channel channel;

    private ClientRpcHandler clientRpcHandler = new ClientRpcHandler();

    private volatile boolean closed = false;

    private static final int WORKER_GROUP_THREADS = 5;

    @Override
    public void connect(final InetSocketAddress socketAddress) {
        try {
            workerGroup = new NioEventLoopGroup(WORKER_GROUP_THREADS);
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //处理失败重连
                                    .addFirst(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            super.channelInactive(ctx);
                                            ctx.channel().eventLoop().schedule(new Runnable() {
                                                @Override
                                                public void run() {
                                                    doConnect(socketAddress);
                                                }
                                            }, 1, TimeUnit.SECONDS);
                                        }
                                    })
                                    //处理分包传输问题
                                    .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast("encoder", new LengthFieldPrepender(4, false))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(clientRpcHandler);
                        }
                    });
            doConnect(socketAddress);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void doConnect(final InetSocketAddress socketAddress) {
        logger.info("trying to connect server:{}", socketAddress);
        if (closed) {
            return;
        }

        ChannelFuture future = bootstrap.connect(socketAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    logger.info("connected to {}", socketAddress);
                } else {
                    logger.info("connected to {} failed", socketAddress);
                    f.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect(socketAddress);
                        }
                    }, 1, TimeUnit.SECONDS);
                }
            }
        });

        channel = future.syncUninterruptibly()
                .channel();
    }

    @Override
    public RpcResponse syncSend(RpcRequest request) throws InterruptedException {
        logger.debug("send request:" + request);
        channel.writeAndFlush(request).sync();
        return clientRpcHandler.send(request, null);
    }

    @Override
    public RpcResponse asyncSend(RpcRequest request, Pair<Long, TimeUnit> timeout) throws InterruptedException {
        channel.writeAndFlush(request);
        return clientRpcHandler.send(request, timeout);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!(remoteAddress instanceof InetSocketAddress)) {
            throw new RpcException("Get remote address error, should be InetSocketAddress");
        }
        return (InetSocketAddress) remoteAddress;
    }

    public boolean isClosed() {
        return closed;
    }

    @PreDestroy
    @Override
    public void close() {
        logger.info("destroy client resources");
        if (null == channel) {
            logger.error("channel is null");
            return;
        }
        closed = true;
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        workerGroup = null;
        channel = null;
    }
}
