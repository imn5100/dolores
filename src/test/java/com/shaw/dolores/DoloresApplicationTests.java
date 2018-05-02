package com.shaw.dolores;

import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.dao.MetaRepository;
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
    public void testMetaDelete() {
        Meta meta = new Meta();
        meta.setName("test2");
        meta.setCreateTime(System.currentTimeMillis());
        meta.setUpdateTime(System.currentTimeMillis());
        meta.setValue(String.valueOf(System.currentTimeMillis()));
        meta.setExpireTime(System.currentTimeMillis() + 60 * 1000);
        metaRepository.save(meta);

        meta = metaRepository.findMetaByNameAndExpireTimeAfter("test2", System.currentTimeMillis());
        System.out.println(meta);
        metaRepository.deleteByName("test2");
    }

    @Test
    public void testFindMeta() throws InterruptedException {
        boolean roll = true;
        while (roll) {
            long current = System.currentTimeMillis();
            Meta meta = metaRepository.findMetaByNameAndExpireTimeAfter("test", current);
            System.out.println(current);
            System.out.println(meta);
            if (meta == null) {
                roll = false;
            }
            Thread.sleep(1000);
        }
    }


}
