
package person.catcircle.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.entity.common.Packet;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
public class PacketUtils {
    @Autowired
    private ObjectMapper objectMapper;

    private static PacketUtils packetUtils;

    private SecureRandom r = new SecureRandom();

    private AtomicLong sequence = new AtomicLong(Math.abs(r.nextLong()/10));

    private Long getNextId(){
        long id = sequence.incrementAndGet();
        if(id < 0 || id == Long.MAX_VALUE){
            sequence.compareAndSet(id,Math.abs(r.nextLong())/10);
        }
        return id;
    }


    public static final int HEAD_LEN = 64;

    public static PacketUtils getInstance(){
        return packetUtils;
    }

    @PostConstruct
    public void init(){
        packetUtils = this;
    }

    public Packet.Head buildResponseHead(Packet.Head req,int type){
        return new Packet.Head(req.getId(),req.getCode(),type);
    }

    public Packet.Head buildRequestHead(int code){
        return new Packet.Head(getNextId(),code, Packet.Head.Type.REQUEST);
    }

    public Packet.Head buildNotifyHead(int code){
        return new Packet.Head(getNextId(),code, Packet.Head.Type.NOTIFY);
    }

    public Packet.Head parseHead(JsonParser p) throws IOException{
        p.nextToken();
        assertToken(JsonToken.START_OBJECT,p);
        p.nextToken();//head
        p.nextToken();
        Packet.Head h = objectMapper.readValue(p,Packet.Head.class);
        return h;
    }

    private void assertToken(JsonToken expect,JsonParser p) throws JsonParseException{
        if(p.getCurrentToken() != expect){
            throw new JsonParseException(p,
                    String.format("Invalid token, expected %s and actually %s", expect, p.getCurrentToken()),
                    p.getTokenLocation());
        }
    }

    public Packet.Head parseHead(ByteBuffer byteBuffer){
        Packet.Head head = new Packet.Head();
        head.setVersion(byteBuffer.getInt());
        head.setId(byteBuffer.getLong());
        head.setCode(byteBuffer.getInt());
        head.setType(byteBuffer.getInt());
        return head;
    }

    public <T> T parseBody(Object o,Class<T> type) throws IOException{
        if(byte[].class.isAssignableFrom(o.getClass())){
            return null;
        }else if(JsonParser.class.isAssignableFrom(o.getClass())){
            JsonParser p = (JsonParser)o;
            return parseJsonBody(p,type);
        }else {
            return null;
        }
    }

    private <T> T parseJsonBody(JsonParser p, Class<T> type) throws IOException{
        if(type == null || type == Void.class){
            return null;
        }
        p.nextToken();//body
        if(p.hasCurrentToken() && "body".equals(p.getCurrentName())){
            p.nextToken();//JSONTOken.START_OBJECT
            return objectMapper.readValue(p,type);
        }
        return null;
    }

    public JsonParser createParser(byte[] bytes) throws IOException{
        JsonParser p = objectMapper.getFactory().createParser(bytes);
        return p;
    }

    public byte[] serizable(Packet<?> p){
        try {
            return objectMapper.writeValueAsBytes(p);
        }catch (IOException e){
            return null;
        }
    }

    public String getAddr(String model,String sn){
        return "/d" + "/" + model + "/" + sn;
    }

    public String getServerAddr(String model){
        return "/s" + "/" + model;
    }
}
