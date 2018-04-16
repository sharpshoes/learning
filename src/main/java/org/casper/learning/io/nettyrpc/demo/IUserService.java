package org.casper.learning.io.nettyrpc.demo;

import org.casper.learning.io.nettyrpc.client.annotation.RpcApi;
import org.casper.learning.io.nettyrpc.client.annotation.RpcService;

@RpcService(namespace = "")
public interface IUserService {

    @RpcApi()
    public void createUser(UserInfo userInfo);

    @RpcApi()
    public void listUser();

}
