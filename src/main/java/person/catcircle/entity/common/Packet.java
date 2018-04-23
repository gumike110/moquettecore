
package person.catcircle.entity.common;

import lombok.Data;

/**
 * 消息包
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Data
public class Packet<T> {

    private Head head;

    private T body;

    public Packet(Head head) {
        this.head = head;
    }

    public Packet(Head head, T body) {
        this.head = head;
        this.body = body;
    }

    public Packet(){

    }

    /**
     * 由code & type 构成的消息头，统一交由handle 处理，id 用作消息调用的chain
     */
    // TODO: 2018/4/20 完善消息call chain 处理
    @Data
    public static final class Head {
        private int version;

        private long id;

        private int code;

        private int type;

        public Head() {
        }

        public Head(long id, int code, int type) {
            setId(id);
            setCode(code);
            setType(type);
        }

        public static final class Code{
            public static final int CONNECT = 0;
            public static final int DISCONNECT = 1;
            public static final int REMOTE_MESSAGE = 2;

            //自定义扩展消息
            public static final int RAW_MESSAGE = 1000;
            //系统升级
            public static final int UPGRADE_FILE = 1001;
        }

        public static final class Type{
            public static final int NOTIFY = 0;
            public static final int REQUEST = 1;
            public static final int RESPONSE_OK = 2;
            public static final int RESPONSE_BAD_REQ = 3;
            public static final int RESPONSE_TIMEOUT = 4;
            public static final int RESPONSE_PROCESS_FAIL = 5;

            public static boolean isResponse(int type) {
                return type >= RESPONSE_OK;
            }

            public static boolean isResponseOk(Packet<?> p) {
                return p.getHead().getType() == RESPONSE_OK;
            }
        }
    }
}


