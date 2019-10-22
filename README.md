#### 基础框架库

```groovy
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
implementation 'com.android.support:multidex:1.0.3'
implementation "com.android.support:design:${support_library_version}"
implementation "com.android.support:recyclerview-v7:${support_library_version}"
implementation "com.android.support:appcompat-v7:${support_library_version}"
implementation "com.android.support:cardview-v7:${support_library_version}"
implementation 'com.android.support.constraint:constraint-layout:1.1.3'

implementation 'io.reactivex.rxjava2:rxjava:2.2.8' // 线程调度
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1' // 线程调度
implementation 'com.lzy.net:okgo:3.0.4' // 网络访问
implementation 'org.greenrobot:eventbus:3.1.1' // 事件总线
implementation 'com.orhanobut:logger:2.1.1' // 日志
implementation 'com.google.code.gson:gson:2.7' // json解析
implementation 'com.gyf.barlibrary:barlibrary:2.3.0' // 状态栏
implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.46' // recyclerView adapter
implementation 'com.blankj:utilcode:1.25.9' // 工具包
implementation 'com.zhihu.android:matisse:0.5.2-beta4' // 图片选择
implementation 'com.github.chrisbanes:PhotoView:2.1.3' // 图片手势缩放
implementation('com.github.hotchemi:permissionsdispatcher:2.4.0') { // 运行时权限
    exclude module: "support-v13"
}
kapt 'com.github.hotchemi:permissionsdispatcher-processor:3.2.0' // 运行时权限
implementation 'cn.carbswang.android:NumberPickerView:1.1.0' // 滚动控件
implementation 'com.github.promeg:tinypinyin:1.0.0' // 汉字转拼音
implementation 'com.king.zxing:zxing-lite:1.0.5' // 二维码扫描
implementation 'cn.pedant.safewebviewbridge:library:1.4'// js交互
implementation 'com.github.abel533:ECharts:3.0.0.6' // echarts
implementation 'com.github.smarxpan:NotchScreenTool:0.0.1'// 刘海屏适配
implementation 'com.jakewharton.rxbinding2:rxbinding:2.2.0' // rxbinding
implementation 'com.github.bumptech.glide:glide:3.7.0' // 图片加载
implementation 'com.ts.lib_dimens:lib_dimens:1.0.0' // dimens资源
```

#### JS交互方法

| 方法名                           | 功能             | 参数                                          | 返回值类型和解释                    |
|:-----------------------------:|:--------------:|:-------------------------------------------:|:---------------------------:|
| ***toggleScreenOrientation*** | 切换横竖屏          | 无                                           | 无                           |
| ***screenIsLandscape***       | 屏幕方向判断         | 无                                           | boolean，屏幕是横向为true，纵向为false |
| sign                          | 手写签名           | 无                                           | String，android手机本机图片路径      |
| print                         | 打印             | String jsonArray                            | boolean，打印结果                |
| enableSensorRotate            | 打开/关闭重力感应切换横竖屏 | boolean sensorRotate，true打开，false关闭，默认为关闭状态 | 无                           |
| closeWebView                  | 关闭当前native页面   | 无                                           | 无                           |

#### 打印功能详解，print(json)

| 字段名         | 类型      | 功能             | 取值范围                                          | 所属type    |
|:-----------:|:-------:|:--------------:|:---------------------------------------------:|:---------:|
| type        | String  | 打印类型           | text,line,blank,enter,stamp,signature，默认为text | 无         |
| text        | String  | 打印的文本          | 不可为空，默认为null                                  | text      |
| textSize    | int     | 字体大小           | 0(16号)，1(24号)，2(32号)，3(48号)，4(64号)，默认为1(24号)  | text,signature      |
| underLine   | boolean | 是否带下划线         | true,false，默认为false                           | text,signature      |
| bold        | boolean | 是否加粗           | true,false，默认为false                           | text,signature      |
| align       | String  | 对齐方式           | left,right,center，默认为left                     | text      |
| enter       | boolean | 是否换行           | true,false 默认为false                            | text      |
| blankHeight | Int     | 空白高度           | 不可为负，默认为0                                     | blank     |
| lineTip     | String  | 切割线提示          | 任意字符串，默认为：请沿此处撕开                              | line      |
| stamp       | String  | 印章             | 任意字符串，为空不打印（打印格式需要单独适配，不需要则不要使用），默认为null      | stamp     |
| signTip     | String  | 签名提示信息，在签名图片之前 | 任意字符串，为空不打印，默认为null                           | signature |
| signPath    | String  | 签名图片，在签名信息之后   | android手机本机图片地址（暂不支持网络图片打印），默认为null           | signature |

#### 网页测试代码

```javascript
<!DOCTYPE html>
<html>
<body>

<script>
    var count = 0
    function toggleScreenOrientation() {
        window.plus.toggleScreenOrientation();
        document.getElementById("change").innerHTML = "已切换屏幕方向"
    }
    function screenIsLandscape() {
        window.plus.screenIsLandscape(function(bool) {
            document.getElementById("screen").innerHTML = bool
        });
    }
    function sign() {
        window.plus.sign(function(string) {
            document.getElementById("sign").innerHTML = "签名文件路径为：" + string
        });
    }
    function print() {
        window.plus.print('[{"blankHeight":20,"type":"blank"},{"align":"left","bold":true,"text":"苏州xxxx有限公司：","textSize":2,"underLine":false,"type":"text"},{"blankHeight":20,"type":"blank"},{"align":"left","bold":false,"text":"因你公司违反了xxxx规定，现对你司进行如下处罚：","textSize":1,"underLine":false,"type":"text"},{"blankHeight":20,"type":"blank"},{"align":"left","bold":false,"text":" 从今日起至","textSize":1,"underLine":false,"type":"text"},{"align":"left","bold":true,"text":"2020年1月1日","textSize":2,"underLine":true,"type":"text"},{"align":"left","bold":false,"text":"不得开门营业，直到整改结束。","textSize":1,"underLine":false,"type":"text"},{"blankHeight":100,"type":"blank"}]', function(bool) {
            if (bool) {
                document.getElementById("print").innerHTML = "打印成功" + (count++)
            }
        });
    }
    function enableSensorRotate(sensorRotate) {
        window.plus.enableSensorRotate(sensorRotate);
    }
</script>
<button type="button" onclick="toggleScreenOrientation()">
    切换屏幕方向
</button>
<p id="change"></p>
<button type="button" onclick="screenIsLandscape()">
    屏幕是否为横屏
</button>
<p id="screen"></p>
<button type="button" onclick="sign()">
    手写签名
</button>
<p id="sign"></p>
<button type="button" onclick="print()">
    打印
</button>
<p id="print"></p>
<button type="button" onclick="enableSensorRotate(true)">
    打开重力感应
</button>
<button type="button" onclick="enableSensorRotate(false)">
    关闭重力感应
</button>
</body>
</html>
```

#### 其他注意事项

1. 打印类型为text，设置对齐方式为center、right，默认自动换行，设置为left，如需要换行，请自行添加enter或者blank类型元素，否则顺序打印，参考demo中”打印测试 --> 打印测试3“ ，"{"align":"left","bold":false,"text":" 从今日起至","textSize":1,"underLine":false,"type":"text"},{"align":"left","bold":true,"text":"2020年1月1日","textSize":2,"underLine":true,"type":"text"},{"align":"left","bold":false,"text":"不得开门营业，直到整改结束。","textSize":1,"underLine":false,"type":"text"}"

2. 打印完签名图片后自动换行

3. 印章不是通用，如无需要请不要调用（印章信息是姑苏区的，以后会做通用适配）

4. 字号只支持上表所列5种，打印机限制无法适配其他字号

5. 打印类型为enter，不需要任何参数

6. 交互方法测试参考demo中“测试网页”，网页代码在上面

7. 旧版打印交互方法以后不再支持，请切换至新版打印

#### 打印json示例（demo中打印测试2，可以进行添加签名操作）

```json
[
{
"blankHeight":20,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"监督意见书",
"textSize":2,
"underLine":false,
"type":"text"
},
{
"type":"enter"
},
{
"align":"center",
"bold":true,
"text":"第2019000001号",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":20,
"type":"blank"
},
{
"align":"left",
"bold":false,
"text":"被监督人：姑苏区陆二私房菜馆\n法定代表人（负责人）：陆叶君\n地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n联系电话：13800000000\n监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n以上意见限于 日内改正。",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":100,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"监督意见书",
"textSize":2,
"underLine":false,
"type":"text"
},
{
"blankHeight":10,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"第2019000001号",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":20,
"type":"blank"
},
{
"align":"left",
"bold":false,
"text":"被监督人：姑苏区陆二私房菜馆\n法定代表人（负责人）：陆叶君\n地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n联系电话：13800000000\n监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n以上意见限于 日内改正。",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":50,
"type":"blank"
},
{
"type":"line"
},
{
"blankHeight":50,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"监督意见书",
"textSize":2,
"underLine":false,
"type":"text"
},
{
"type":"enter"
},
{
"align":"center",
"bold":true,
"text":"第2019000001号",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":20,
"type":"blank"
},
{
"align":"left",
"bold":false,
"text":"被监督人：姑苏区陆二私房菜馆\n法定代表人（负责人）：陆叶君\n地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n联系电话：13800000000\n监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n以上意见限于 日内改正。",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":100,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"监督意见书",
"textSize":2,
"underLine":false,
"type":"text"
},
{
"blankHeight":10,
"type":"blank"
},
{
"align":"center",
"bold":true,
"text":"第2019000001号",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":20,
"type":"blank"
},
{
"align":"left",
"bold":false,
"text":"被监督人：姑苏区陆二私房菜馆\n法定代表人（负责人）：陆叶君\n地    址：江苏省苏州市姑苏区双塔街道凤凰街390号\n联系电话：13800000000\n监督意见：\n\n禁止生产经营《食品安全法》第三十四条所列的禁止生产经营的食品、食品添加剂、食品相关产品。\n\n以上意见限于 日内改正。",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":50,
"type":"blank"
},
{
"align":"right",
"bold":false,
"text":"2019-08-05",
"textSize":1,
"underLine":false,
"type":"text"
},
{
"blankHeight":50,
"type":"blank"
}
]
```
