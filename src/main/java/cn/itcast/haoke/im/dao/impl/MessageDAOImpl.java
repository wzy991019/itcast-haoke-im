package cn.itcast.haoke.im.dao.impl;

import cn.itcast.haoke.im.dao.MessageDAO;
import cn.itcast.haoke.im.pojo.Message;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MessageDAOImpl implements MessageDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 实现：查询点对点消息记录
     * @param fromId
     * @param toId
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<Message> findListByFromAndTo(Long fromId, Long toId, Integer page, Integer rows) {

        // 用户A发送给用户B的条件
        Criteria criteriaFrom = new Criteria().andOperator(
                Criteria.where("from.id").is(fromId),
                Criteria.where("to.id").is(toId)
        );

        // 用户B发送给用户A的条件
        Criteria criteriaTo = new Criteria().andOperator(
                Criteria.where("from.id").is(toId),
                Criteria.where("to.id").is(fromId)
        );


        Criteria criteria = new Criteria().orOperator(criteriaFrom, criteriaTo);

        // 分页操作，根据消息发送的时间做一个正向的排序，最早发送的消息在最上面
        PageRequest pageRequest = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "sendDate"));

        // 设置查询条件，分页
        Query query = Query.query(criteria).with(pageRequest);

//        System.out.println(query);

        // 传入查询条件和查询类
        return this.mongoTemplate.find(query, Message.class);
    }

    @Override
    public Message findMessageById(String id) {
        return this.mongoTemplate.findById(new ObjectId(id), Message.class);
    }

    @Override
    public UpdateResult updateMessageState(ObjectId id, Integer status) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("status", status);
        if (status.intValue() == 1) {// 表示已发送的时候
            update.set("send_date", new Date());//将发送时间设置为当前时间
        } else if (status.intValue() == 2) {// 表示已经读取的时候
            update.set("read_date", new Date());//将读取时间设置为当前时间
        }
        return this.mongoTemplate.updateFirst(query, update, Message.class);
    }

    @Override
    public Message saveMessage(Message message) {
        // 写入发送时间
        message.setSendDate(new Date());
        message.setStatus(1);
        message.setId(ObjectId.get());
        return this.mongoTemplate.save(message);
    }

    @Override
    public DeleteResult deleteMessage(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return this.mongoTemplate.remove(query, Message.class);
    }
}
