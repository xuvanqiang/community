package com.honghuang.community.service;

import java.util.Date;

public interface DataService {

    void recordUv(String ip);

    long calculateUv(Date start, Date end);


    void recordDau(int userId);


    long calculateDau(Date start,Date end);

}
