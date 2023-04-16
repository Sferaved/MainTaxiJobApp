package com.taxieasyua.job.driver_app;

import java.util.List;

public interface Postman {
    void fragmentMailInfo(List<String> infoList);

    void fragmentMailAuto(List<String> autoList);
    void fragmentMailService(List<String> servicesList);
}
