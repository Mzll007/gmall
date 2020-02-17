package com.mzll.gmall.service;


import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;


public interface UmsMemberService {
    List<UmsMember> getAll();

    UmsMember verify(String token);

    UmsMember login(UmsMember umsMember);

    void putToken(String token, UmsMember umsMemberFromDb);

    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressByUserId(String userId);

    UmsMember addVloginUser(UmsMember umsMember);

    void sendHadLogin(String id, String nickname);
}
