package person.catcircle.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.annotation.DeviceCommand;
import person.catcircle.annotation.Message;
import person.catcircle.entity.Device;
import person.catcircle.entity.common.Packet;
import person.catcircle.service.DeviceService;

/**
 * Created by catcircle on 2018/4/23.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
@DeviceCommand
public class ModelHandler {

    @Autowired
    private DeviceService deviceService;


    /**
     * online
     * @param clientId
     */
    @DeviceCommand.Command(code = Packet.Head.Code.CONNECT,type = Packet.Head.Type.NOTIFY)
    public void online(@Message.MessageParam(Message.MessageParam.ParamType.clientId)String clientId){
        Device device = deviceService.findBySn(clientId);

    }

    @DeviceCommand.Command(code = Packet.Head.Code.DISCONNECT,type = Packet.Head.Type.NOTIFY)
    public void offline(@Message.MessageParam(Message.MessageParam.ParamType.clientId)String clientId){

    }
}
