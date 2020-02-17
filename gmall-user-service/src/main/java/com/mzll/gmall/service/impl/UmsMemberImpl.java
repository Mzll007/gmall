package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;
import com.mzll.gmall.mapper.UmsMemberMapper;
import com.mzll.gmall.mapper.UmsMemberReceiveAddressMapper;
import com.mzll.gmall.service.UmsMemberService;
import com.mzll.gmall.util.ActiveMQUtil;
import com.mzll.gmall.util.RedisUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import javax.jms.*;
import java.util.List;


@Service
public class UmsMemberImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Reference
    ActiveMQUtil activeMQUtil;

    @Override
    public List<UmsMember> getAll() {
        return umsMemberMapper.selectAll();
    }

    @Override
    public UmsMember verify(String token) {
        Jedis jedis = redisUtil.getJedis();
        UmsMember umsMember = null;
        String k = "user:" + token + ":token";
        if (StringUtils.isNotBlank(k)) {
            String v = jedis.get(k);
            umsMember = JSON.parseObject(v, UmsMember.class);
            // 重置token的过期时间
            jedis.expire(k, 60 * 60 * 24);
        }
        jedis.close();

        return umsMember;

    }

    @Override
    public UmsMember login(UmsMember umsMember) {

        UmsMember member = umsMemberMapper.selectOne(umsMember);
        return member;
    }

    @Override
    public void putToken(String token, UmsMember umsMemberFromDb) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:"+token+":token",60*60*24, JSON.toJSONString(umsMemberFromDb));

        jedis.close();


    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressByUserId(String userId) {

        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(userId);
        List<UmsMemberReceiveAddress> select = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return select;
    }

    @Override
    public UmsMember addVloginUser(UmsMember umsMember) {


        // 先查询是否登陆过
        UmsMember umsMember1 = new UmsMember();
        umsMember1.setSourceUid(umsMember.getSourceUid());
        UmsMember member1 = umsMemberMapper.selectOne(umsMember1);
        if(member1==null){
            // 没登陆过 保存信息
            int i = umsMemberMapper.insertSelective(umsMember);

            // 返回刚才保存的数据
            member1 = umsMemberMapper.selectOne(umsMember1);
        }
        return member1;
    }

    @Override
    public void sendHadLogin(String id, String nickname) {

        // 创建一个连接工厂
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(true, 0);
            Queue queue = session.createQueue("LOGIN_SUCCESS");
            MessageProducer producer = session.createProducer(queue);

            MapMessage message = new ActiveMQMapMessage();
            message.setString("userId", id);
            message.setString("nickname", nickname);
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);
            session.commit();


            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
