package com.honghuang.community.service;

import com.honghuang.community.entity.Message;

import java.util.List;

public interface MessageService {
    //查询当前用户会话列表,针对每个会话只返回一条最新的私信
    List<Message> findConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int findConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> findLetters(String conversationId,int offset,int limit);

    //查询某个会话所包含的私信数量
    int findLetterCount(String conversationId);

    //查询未读的私信数量
    int findLetterUnreadCount(int userId,String conversationId);

    //增加私信
    int addMessage(Message message);

    //更改私信状态(未读-->已读)
    int readMessage(List<Integer> ids);
}
