package xdsei.wycg.autoExecuteProgram.service;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZPww
 * @since 2021/4/18
 */
@Service
@Slf4j
public class ConnectedClientService {

    /**
     * 已经连接的客户端 ChannelHandler, 用于向指定客户端发消息 还得知道客户端对应的 key 或者群发有 channelGroup
     */
    private static final Map<String, ChannelHandlerContext> CONNECTED_CLIENT_MAP = new ConcurrentHashMap<>(16);

    public static void addClient(String id, ChannelHandlerContext ctx){
        CONNECTED_CLIENT_MAP.put(id, ctx);
    }

    public static Map<String, ChannelHandlerContext> getClients(){
        return CONNECTED_CLIENT_MAP;
    }

    public static ChannelHandlerContext getClient(String id){
        return CONNECTED_CLIENT_MAP.get(id);
    }

    public static void removeClient(String id){
        CONNECTED_CLIENT_MAP.remove(id);
    }


    /**
     * @return 是否有至少一个客户端连接 是：true
     *                                 否：false
     */
    public static boolean channelConnectedStatus() {
        Map<String, ChannelHandlerContext> clients = getClients();
        for(Map.Entry<String, ChannelHandlerContext> entry: clients.entrySet()) {
            if(entry.getValue().channel().isActive()) {
               return true;
            }
        }
        return false;
    }


}
