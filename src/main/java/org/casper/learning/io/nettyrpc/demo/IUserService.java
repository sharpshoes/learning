package org.casper.learning.io.nettyrpc.demo;

import org.casper.learning.io.nettyrpc.client.Callback;
import org.casper.learning.io.nettyrpc.client.annotation.RpcApi;
import org.casper.learning.io.nettyrpc.client.annotation.RpcService;

import java.util.List;


@RpcService(namespace = "")
public interface IUserService {

    @RpcApi(callback = UserCreateCallback.class)
    public void createUser(UserInfo userInfo);

    @RpcApi(callback = UserListCallback.class)
    public void listUser();

    @RpcApi()
    public void listUser(Callback<List<UserInfo>> callback);

    public static class UserCreateCallback implements Callback<UserInfo> {


        @Override
        public void action(UserInfo value, Exception ex) {

        }
    }

    public static class UserListCallback implements Callback<List<UserInfo>> {

        @Override
        public void action(List<UserInfo> value, Exception ex) {

        }
    }

}
