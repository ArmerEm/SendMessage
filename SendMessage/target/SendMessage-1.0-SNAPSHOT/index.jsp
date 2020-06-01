<%--
  Created by IntelliJ IDEA.
  User: Armer
  Date: 2020/5/28
  Time: 15:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Title</title>
        <script src="/SendMessage/static/jquery/jquery-3.1.0.js"></script>
        <link href="/SendMessage/static/bs/css/bootstrap.min.css" rel="stylesheet"/>
        <script src="/SendMessage/static/bs/js/bootstrap.min.js"></script>
        <script src="/SendMessage/static/layer/layer.js"></script>

    </head>
    <body>
        <div class="container" align="center">
            <div class="row">
                <div id="alertdiv" class="col-md-12">
                    <form class="navbar-form navbar-left" role="search" id="codeform">
                        <div class="form-group">
                            <input type="text" class="form-control" placeholder="填写手机号" name="phone_no">
                            <button type="button" class="btn btn-default" id="sendCode">发送验证码</button>
                            <br> <font id="countdown" color="red"></font> <br>

                            <input type="text" class="form-control" placeholder="填写验证码" name="verify_code">
                            <button type="button" class="btn btn-default" id="verifyCode">确定</button>
                            <font id="result" color="green"></font><font id="error" color="red"></font>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script type="text/javascript">

            var t = 120;//设定倒计时的时间 120秒
            var interval;

            //设置计数函数
            function refer() {
                $("#countdown").text("请于" + t + "秒内填写验证码 "); // 显示倒计时
                t--; // 计数器递减
                if (t <= 0) {
                    clearInterval(interval);
                    $("#countdown").text("验证码已失效，请重新发送！ ");
                }
            }

            $(function () {
                $("#sendCode").click(function () {
                    $.ajax({
                        type: "post",
                        data: $("#codeform").serialize(),
                        url: "/SendMessage/sendCode",
                        success: function (result) {
                            if (result == 1) {
                                t = 120;
                                clearInterval(interval);
                                interval = setInterval("refer()", 1000);//启动1秒定时
                            } else if (result == 2) {
                                //受限3次
                                clearInterval(interval);
                                $("#countdown").text("单日发送超过次数！ ")
                            } else if(result=="-1") {
                                $("#countdown").text("请输入手机号码！ ")
                            }
                        }
                    });
                });
                $("#verifyCode").click(function () {
                    $.ajax({
                        type:"post",
                        data:$("#codeform").serialize(),
                        url:"/SendMessage/handleCode",
                        success:function (result) {
                            if(result=="1"){
                                layer.msg("恭喜,验证成功~")
                            }else{
                                layer.msg("验证失败!")
                            }
                        }
                    });
                });
            });


        </script>
    </body>
</html>
