package pers.acp.spring.boot.socket.base

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil
import pers.acp.core.CommonTools
import pers.acp.core.log.LogFactory
import pers.acp.spring.boot.conf.SocketListenerConfiguration

import java.nio.charset.Charset

/**
 * Socket 报文处理基类
 *
 * @author zhang by 04/03/2019
 * @since JDK 11
 */
abstract class SocketServerHandle(protected var socketListenerConfiguration: SocketListenerConfiguration, protected var socketServerHandle: ISocketServerHandle) : ChannelInboundHandlerAdapter() {

    protected val log = LogFactory.getInstance(this.javaClass)

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        try {
            val byteBuf = beforeReadMessage(msg)
            val recvStr = if (socketListenerConfiguration.hex) {
                ByteBufUtil.hexDump(byteBuf)
            } else {
                byteBuf.toString(charset(socketListenerConfiguration.charset))
            }
            log.debug("socket receive:$recvStr")
            var responseStr = this.socketServerHandle.doResponse(recvStr)
            if (socketListenerConfiguration.responsable) {
                responseStr = if (CommonTools.isNullStr(responseStr)) "" else responseStr
                try {
                    doResponse(ctx, msg, responseStr)
                    log.debug("socket return:$responseStr")
                } catch (e: Exception) {
                    log.error(e.message, e)
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        } finally {
            ReferenceCountUtil.release(msg)
            afterReadMessage(ctx)
        }
    }

    @Throws(Exception::class)
    protected fun doResponse(ctx: ChannelHandlerContext, requestMsg: Any?, responseStr: String) {
        var retStr = responseStr
        if (socketListenerConfiguration.hex) {
            val bts = ByteBufUtil.decodeHexDump(responseStr)
            retStr = String(bts, charset(socketListenerConfiguration.charset))
        }
        val msgPackage = beforeSendMessage(ctx, requestMsg, retStr)
        ctx.writeAndFlush(msgPackage)
        afterSendMessage(ctx)
    }

    protected abstract fun beforeReadMessage(msg: Any): ByteBuf

    protected abstract fun afterReadMessage(ctx: ChannelHandlerContext)

    @Throws(Exception::class)
    protected abstract fun beforeSendMessage(ctx: ChannelHandlerContext, requestMsg: Any?, sendStr: String): Any

    protected abstract fun afterSendMessage(ctx: ChannelHandlerContext)

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error(cause.message, cause)
        ctx.close()
    }

}
