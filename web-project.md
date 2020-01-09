地址：http://renlian.yishanzhi.com/
 
返回值约定
{
    "data": ,  // 接口数据
    "code": null,
    "success": true,  // 是否执行成功
    "message": null,  // 异常信息
    "tracer": null
}


接口一：发送验证码，POST
/api/user/sendVerifyCode?phone=${phone}

接口二：注册， POST
/api/user/register
参数：
{
	"referencePhone":"18611692636",  // 推荐人手机号
	"phone":"16619876037",  // 手机号
	"verifyCode":"178847",  // 验证码
	"password":"ycy08680", // 手机号
	"name":"杨崇园", // 姓名
	"province":"山东省",  // 省份
	"city":"济南市",  // 城市
	"wechatId":"123454"  // 微信ID，暂时写一个固定值
}

接口三：登录， POST
/api/user/login
参数：
{
	"phone":"18611692636",  // 手机号
	"password":"ycy08680"  // 密码
}

接口四：个人信息，GET
/api/user/detail
参数: 无
返回值：
{
    "data": {
        "id": 2,
        "wechatId": "12345",
        "phone": "18611692636",
        "name": "杨崇园",
        "gender": null,
        "province": "山东省",
        "city": "济南市",
        "address": null,
        "avatar": null,
        "updateAt": 1578453790422,
        "createAt": 1578453790422
    },
    "code": null,
    "success": true,
    "message": null,
    "tracer": null
}

接口五：我的推荐列表 GET
/api/user/myRecommended
参数：无
返回值：
{
    "data": [
        {
            "id": 6,
            "wechatId": "123454",
            "phone": "16619876037",
            "name": "杨崇园",
            "gender": null,
            "province": "山东省",
            "city": "济南市",
            "address": null,
            "avatar": null,
            "updateAt": 1578453974128,
            "createAt": 1578453974128
        }
    ],
    "code": null,
    "success": true,
    "message": null,
    "tracer": null
}

