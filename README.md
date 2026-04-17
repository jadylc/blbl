# blbl-android

一个面向大屏场景的第三方哔哩哔哩 Android 客户端，兼容触屏与遥控器操作，适用于平板、TV、车机等设备。

## 项目特点

- 支持 Android 5.0 及以上
- 适配横屏与大屏交互场景
- 同时支持触屏与遥控器 / D-Pad 操作
- 支持普通启动器与 Leanback 启动器
- 内置扫码登录、播放设置、弹幕设置与更新能力

## 界面预览

**推荐页**
![推荐页](./example-pic/推荐页.png)

**分类页**
![分类页](./example-pic/分类页.png)

**动态页**
![动态页](./example-pic/动态页.png)

**直播页**
![直播页](./example-pic/直播页.png)

**我的页**
![我的页](./example-pic/我的页.png)

**搜索页**
![搜索页](./example-pic/搜索页.png)

**追番**
![追番](./example-pic/追番.png)

**视频播放页**
![视频播放页](./example-pic/视频播放页.png)

## 功能一览

### 导航与页面

- 侧边栏导航：搜索、推荐、分类、动态、直播、我的
- 支持按配置启用自定义页
- 支持设置默认启动页
- 首页与直播页采用标签页结构，便于大屏快速切换

### 账号与个人内容

- 支持扫码登录
- 持久化登录态与 Cookie
- 我的页面支持：
  - 历史记录
  - 收藏夹
  - 追番
  - 追剧
  - 稍后再看
  - 点赞记录

### 播放体验

- 基于 Media3 ExoPlayer
- 集成 IjkPlayer 插件能力
- 支持 HLS 播放
- 支持分辨率、音轨、视频编码偏好切换
- 支持倍速播放、播放模式切换、音频平衡调节
- 支持底部常驻进度条与调试信息显示

### 字幕与弹幕

- 字幕开关、语言切换
- 支持字幕字号、底部间距、背景透明度调节
- 支持弹幕开关、速度、不透明度、字号、显示区域调节
- 支持弹幕描边粗细、字重、轨道密度调节
- 支持顶部 / 底部 / 滚动 / 彩色 / 特殊弹幕筛选
- 支持跟随 B 站屏蔽规则与 AI 弹幕屏蔽等级

### 设置与个性化

- 提供通用设置、页面设置、播放设置、弹幕设置、关于应用、设备信息、其他设置
- 支持主题预设切换
- 支持 UI 缩放
- 支持 IPv4 only、UA、自定义设备标识等偏好项
- 支持配置导入 / 导出
- 支持日志导出 / 上传

### 更新能力

- 内置版本检查与 APK 下载更新流程
- 下载完成后可直接拉起系统安装
- 当前测试阶段支持通过远程 `version.json` 获取更新信息

## 安装说明

### 直接安装

1. 从 Release 页面下载 APK
2. 将 APK 传到目标设备后安装
3. 如果系统提示安全限制，请允许安装未知来源应用
4. 首次进入后可在“我的”页使用哔哩哔哩 App 扫码登录

### 应用内更新

- 可在设置页检查新版本
- 检测到新版本后，应用会下载 APK 并调用系统安装器
- 测试阶段更新信息默认来自远程 `version.json`

### 适用设备

- Android 5.0+
- 平板
- Android TV / 电视盒子
- 车机或其他横屏大屏设备
- 支持触屏，也支持遥控器 / D-Pad 操作

## 构建说明

环境要求：

- JDK 17
- Android SDK 36

调试构建：

```bash
./gradlew assembleDebug
```

发布构建：

```bash
./gradlew assembleRelease
```

自定义版本号：

```bash
./gradlew assembleRelease -PversionName=0.1.1 -PversionCode=2
```

生成产物命名格式：

```text
blbl-<version>-debug.apk
blbl-<version>-release.apk
```

## 签名配置

Release 构建默认读取以下签名信息：

- `keystore/release.keystore`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

其中密码和别名可通过 Gradle 属性或环境变量传入。

## CI / 发布流程

仓库内包含 Android Release 工作流，支持：

- 自动构建 Release APK
- 上传 GitHub Release
- 可选上传 Cloudflare R2
- 可选同步 Gitee 更新文件

需要的 Secrets：

- `RELEASE_KEYSTORE_BASE64`
- `RELEASE_STORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`
- `GITEE_TOKEN`
- `GITEE_USERNAME`
- `R2_ACCESS_KEY_ID`
- `R2_SECRET_ACCESS_KEY`
- `R2_ACCOUNT_ID`
- `R2_BUCKET`

可选 Variables / Secrets：

- `R2_PREFIX`
- `GITEE_REPO`
- `GITEE_BRANCH`
- `GITEE_UPDATES_DIR`

## 技术栈

- Kotlin
- AndroidX
- ViewBinding
- Media3 ExoPlayer
- OkHttp
- Protobuf-lite
- Material Components
- RecyclerView / ViewPager2

## 常见问题

### 1. 这是什么设备定位的应用？

这是一个偏大屏场景的第三方 B 站客户端，重点适配平板、TV、车机，以及需要横屏和遥控器操作的设备。

### 2. 最低支持哪个 Android 版本？

当前最低支持 Android 5.0（API 21）。

### 3. 怎么登录？

打开“我的”页后进入扫码登录，用手机上的哔哩哔哩 App 扫码并确认即可。

### 4. 为什么建议从 Release 页面下载？

因为当前测试阶段仍保留了内置更新链路，直接从 Release 下载更直观，也更方便确认安装包来源。

### 5. 更新失败怎么办？

可以优先检查网络连通性、安装权限，以及目标设备是否允许安装未知来源应用；如果仍失败，建议直接手动下载最新 APK 覆盖安装。

### 6. 这个项目适合手机日常使用吗？

当前项目更偏向横屏和大屏交互设计，不是以手机竖屏体验为主要目标。

## 注意事项

当前版本仍包含测试阶段使用的内置更新链路，便于覆盖安装和更新验证；如果你更在意来源可控性，建议直接从 Release 页面下载 APK。

## TODO

- 完善操作逻辑
- 统一样式大小计算规则

## 感谢

- https://github.com/SocialSisterYi/bilibili-API-collect B 站 API 收集整理
- https://github.com/xiaye13579/BBLL 优秀的页面设计和操作逻辑，本项目大量页面和交互参考 BBLL
- https://github.com/bggRGjQaUbCoE/PiliPlus 部分关键功能参考了 PiliPlus
- 开源第三方 B 站客户端
- 群友们的详细测试与反馈

## 免责声明

> 不得利用本项目进行任何非法活动，不得干扰 B 站正常运营，不得传播恶意软件或病毒。

1. 禁止在官方平台（如 B 站、B 站官方账号相关区域）宣传本项目
2. 禁止在微信公众号平台宣传本项目
3. 禁止利用本项目牟利，本项目无任何盈利行为，第三方盈利与本项目无关
