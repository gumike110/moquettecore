
package person.catcircle.repo;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import person.catcircle.entity.App;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
public interface AppRepo extends KeyValueRepository<App,String> {
}
