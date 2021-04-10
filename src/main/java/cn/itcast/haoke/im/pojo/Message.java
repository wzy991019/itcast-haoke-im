package cn.itcast.haoke.im.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message") // 指定表的名称
@Builder // 可以更方便的构造对象，通过build的方式
public class Message {

    @Id// 设置为id字段
    private ObjectId id;
    private String msg;
    /**
     * 消息状态，1-未读，2-已读
     */
    @Indexed//创建索引
    private Integer status;
//    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "UTC")
    @Field("send_date")
    @Indexed
    private Date sendDate;
//    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "UTC")
    @Field("read_date")
    private Date readDate;
    @Indexed
    private User from;
    @Indexed
    private User to;

}
