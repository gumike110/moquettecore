package person.catcircle.moquette.server.netty;

import io.moquette.server.netty.NettyUtils;
import io.moquette.spi.impl.ProtocolProcessor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

@Sharable
public class NettyMQTTHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(io.moquette.server.netty.NettyMQTTHandler.class);
    private final ProtocolProcessor m_processor;

    public NettyMQTTHandler(ProtocolProcessor processor) {
        m_processor = processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        MqttMessage msg = (MqttMessage) message;
        MqttMessageType messageType = msg.fixedHeader().messageType();
        LOG.debug("Processing MQTT message, type={}", messageType);
        try {
            switch (messageType) {
                case CONNECT:
                    m_processor.processConnect(ctx.channel(), (MqttConnectMessage) msg);
                    break;
                case SUBSCRIBE:
                    m_processor.processSubscribe(ctx.channel(), (MqttSubscribeMessage) msg);
                    break;
                case UNSUBSCRIBE:
                    m_processor.processUnsubscribe(ctx.channel(), (MqttUnsubscribeMessage) msg);
                    break;
                case PUBLISH:
                    m_processor.processPublish(ctx.channel(), (MqttPublishMessage) msg);
                    break;
                case PUBREC:
                    m_processor.processPubRec(ctx.channel(), msg);
                    break;
                case PUBCOMP:
                    m_processor.processPubComp(ctx.channel(), msg);
                    break;
                case PUBREL:
                    m_processor.processPubRel(ctx.channel(), msg);
                    break;
                case DISCONNECT:
                    m_processor.processDisconnect(ctx.channel());
                    break;
                case PUBACK:
                    m_processor.processPubAck(ctx.channel(), (MqttPubAckMessage) msg);
                    break;
                case PINGREQ:
                    MqttFixedHeader pingHeader = new MqttFixedHeader(
                            MqttMessageType.PINGRESP,
                            false,
                            AT_MOST_ONCE,
                            false,
                            0);
                    MqttMessage pingResp = new MqttMessage(pingHeader);
                    ctx.writeAndFlush(pingResp);
                    break;
                default:
                    LOG.error("Unkonwn MessageType:{}", messageType);
                    break;
            }
        } catch (Throwable ex) {
            LOG.error("Exception was caught while processing MQTT message, " + ex.getCause(), ex);
            ctx.fireExceptionCaught(ex);
            ctx.close();
        } finally {
          //ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientID = NettyUtils.clientID(ctx.channel());
        if (clientID != null && !clientID.isEmpty()) {
            LOG.info("Notifying connection lost event. MqttClientId = {}.", clientID);
            m_processor.processConnectionLost(clientID, ctx.channel());
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("An unexpected exception was caught while processing MQTT message. Closing Netty channel. CId={}, " +
            "cause={}, errorMessage={}", NettyUtils.clientID(ctx.channel()), cause, cause.getMessage());
        LOG.error("MQTT Handler Error", cause);
        ctx.close();
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            m_processor.notifyChannelWritable(ctx.channel());
        }
        ctx.fireChannelWritabilityChanged();
    }

}
