package com.IceCreamQAQ.YuQ.platform.software.QQ;

import com.IceCreamQAQ.YuQ.entity.Message;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.meowy.cqp.jcq.entity.Anonymous;

@Getter
@ToString
public class QQMessage extends Message {

    private Integer id;

    private Long qq;
    private Long group;
    private Anonymous noName;

    private String msg;
    private String[] texts;

    private QQMessage(){
        super();
    }

    /***
     * 不建议直接使用本方法，在之后版本会根据内容随时进行调整。
     * @param id 消息id
     * @param qq 发送的QQ号
     * @param group 群号
     * @param noName 匿名消息
     * @param texts 内容
     * @return 构建好的message对象。
     */
    @Deprecated
    public static QQMessage buildMessage(Integer id, Long qq, Long group, Anonymous noName, String[] texts, String msg){
        val message=new QQMessage();
        message.id=id;
        message.qq=qq;
        message.group=group;
        message.noName=noName;
        message.texts=texts;
        message.msg=msg;
        return message;
    }

    public static class Builder{

        private QQMessage message;
        private StringBuilder msg;

        public Builder(){
            message=new QQMessage();
            msg=new StringBuilder();
        }

        public Builder(String text){
            this();
            msg.append(text);
        }

        public Builder setQQ(Long qq){
            message.qq=qq;
            return this;
        }

        public Builder setGroup(Long group){
            message.group=group;
            return this;
        }

        public Builder append(String text){
            msg.append(text);
            return this;
        }

        public QQMessage build(){
            message.msg=msg.toString();
            return message;
        }
    }
}
