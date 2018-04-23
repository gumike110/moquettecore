
package person.catcircle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.entity.Device;
import person.catcircle.repo.DeviceRepo;
import person.catcircle.service.DeviceService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepo deviceRepo;

    @Override
    public Device save(Device device) {
        return deviceRepo.save(device);
    }

    @Override
    public List<Device> batchAdd(Iterable<Device> devices) {
        List<Device> list = new ArrayList<>();
        deviceRepo.save(devices).forEach(r -> list.add(r));
        return list;
    }

    @Override
    public Device update(Device device) {
        Device device1 = deviceRepo.findOne(device.getSn());
        deviceRepo.delete(device1);
        return deviceRepo.save(device);
    }

    @Override
    public void deleteBySn(String sn) {
        deviceRepo.delete(sn);
    }

    @Override
    public void batchDeleteBySn(Iterable<String> sn) {
        for(String s : sn){
            deviceRepo.delete(s);
        }

    }

    @Override
    public Device findBySn(String sn) {
        return deviceRepo.findOne(sn);
    }

    @Override
    public Device onLine(String connectNode,String sn) {
        Device device = deviceRepo.findOne(sn);
        device.setConnectNode(connectNode);
        return this.update(device);
    }

    @Override
    public Device offLine(String sn) {
        Device device = deviceRepo.findOne(sn);
        device.setConnectNode("");
        return this.update(device);
    }
}
