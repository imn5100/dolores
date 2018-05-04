package com.shaw.dolores;

import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.dao.MetaRepository;
import com.shaw.dolores.utils.DesUtils;
import com.shaw.dolores.utils.GidUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoloresApplicationTests {

    @Autowired
    private MetaRepository metaRepository;

    @Test
    public void contextLoads() {
    }


    @Test
    public void testMeta() {
        Meta meta = new Meta();
        meta.setName("test2");
        meta.setCreateTime(System.currentTimeMillis());
        meta.setUpdateTime(System.currentTimeMillis());
        meta.setValue(String.valueOf(System.currentTimeMillis()));
        meta.setExpireTime(System.currentTimeMillis() + 60 * 1000);
        metaRepository.save(meta);
    }

    @Test
    public void testFindMeta() throws InterruptedException {
        boolean roll = true;
        while (roll) {
            long current = System.currentTimeMillis();
            Meta meta = metaRepository.findOneMetaByNameAndValid("test2", current);
            System.out.println(current);
            System.out.println(meta);
            if (meta == null) {
                roll = false;
            }
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) throws Exception {
        int i = 10;
        while (i > 0) {
            System.out.println(DesUtils.getDefaultInstance().encrypt((String.valueOf(GidUtil.next()))));
            i--;
        }

    }


}
