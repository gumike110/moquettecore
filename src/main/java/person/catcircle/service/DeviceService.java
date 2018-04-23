
package person.catcircle.service;

import person.catcircle.entity.Device;

import java.util.List;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
public interface DeviceService {
    Device save(Device device);

    List<Device> batchAdd(Iterable<Device> devices);

    Device update(Device device);

    void deleteBySn(String sn);

    void batchDeleteBySn(Iterable<String> sn);

    Device findBySn(String sn);

    Device onLine(String connectNode,String sn);

    Device offLine(String sn);
}
