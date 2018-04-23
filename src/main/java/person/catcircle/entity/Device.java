
package person.catcircle.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Data
@RedisHash("device")
public class Device implements Serializable{

    private static final long serialVersionUID = 6298659607552057753L;

    @Id
    private String sn;

    private String name;

    private String appKey;

    private String model;

    private boolean online;

    private boolean actived;

    private String connectNode;


}
