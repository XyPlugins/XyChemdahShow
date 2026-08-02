# 更新记录

## 1.6.6 - 2026-08-02

### 新增

- 默认 `questhud.yml` 新增 `空任务_label` 组件，用于单独显示无任务提示。

### 调整

- 基于 GitHub 最新 `1.6.5` 代码确认并保留最终前缀语义。
- 玩家导航提示使用 XyCore `messages.prefix`，未安装 XyCore 时回退本插件前缀。
- `/xychshow help/refresh/reload` 的管理反馈、权限不足、只能玩家执行、后台日志和配置加载提示继续使用 XyChemdahShow 本地前缀。
- 当 HUD 配置存在 `空任务_label` 时，玩家没有任务时会将 `config.yml -> empty-text` 写入该组件，并清空 `任务标题_label` 与 `任务信息_label`。
- `空任务_label` 的位置、字号、居中方式完全交给 `questhud.yml` 控制；旧 HUD 没有该组件时仍回退到原来的 `任务信息_label` 显示方式。

### 验证

- 已执行 `gradlew.bat clean build` 通过；Chemdah/Kotlin 依赖注解警告不影响构建。

## 1.6.5 - 2026-08-01

### 新增

- 新增 HUD 保活刷新配置 `hud-keep-alive`，用于定期轻量刷新任务栏文本、内部变量和导航按钮显示状态，缓解 DragonCore 任务栏待久后自动消失或内容丢失的问题。

### 调整

- 玩家导航提示前缀改为优先读取 XyCore 0.3.11+ 的统一前缀 API。
- 保持 XyCore 软依赖：未安装、未启用或 API 不可用时，回退使用本插件 `config.yml -> messages.prefix`。
- 控制台日志、插件重载和管理类提示继续使用 XyChemdahShow 自身前缀，便于排查 HUD、Chemdah 事件和导航问题。

### 修复

- 玩家没有可导航任务时，HUD 会自动隐藏 `任务导航按钮`。
- 玩家任务消失或当前任务不再包含导航坐标时，会静默停止残留导航并清理 DragonCore 地面箭头，避免无任务时仍显示导航标志。

### 性能

- 保活默认 `100 tick` 执行一次，只刷新组件文本和按钮状态，不默认重发完整 HUD 配置。
- 新增 `hud-keep-alive.reopen-hud` 作为兜底开关；默认关闭，避免频繁重新打开 HUD 影响玩家收起状态。

## 1.6.4 - 2026-07-26

### 调整

- 默认 `questhud.yml` 同步为开发者最新提供的任务栏配置，保留 `任务导航按钮.tip` 原生悬浮提示。
- 精简默认 HUD 中与当前提示方式无关的旧 `导航按钮提示` 文本组件和临时变量逻辑。
- 新安装时不再自动生成 `Quest` 目录和 `Quest/任务配置.yml` 示例文件；已有 `Quest` 目录仍可兼容读取。

## 1.6.3 - 2026-07-26

### 新增

- 导航启动前会检测玩家是否处于任务导航目标世界；如果不在目标世界，会提示玩家先传送到 `addon.xychshow.location` 对应地点后再开启导航。

### 调整

- 已开启导航后玩家切换到非目标世界时，会自动停止本次导航并清理箭头，避免导航定时器空转。
- 导航提示中的地点名支持 `&` 颜色符号，仍兼容 `§` 颜色符号。

## 1.6.2 - 2026-07-26

### 修复

- 修正默认 HUD 的导航按钮提示配置：`tip` 放回 `任务导航按钮` 图片组件下，避免误写到独立文本组件导致提示不生效。

## 1.6.1 - 2026-07-26

### 修复

- 修复 XyCore 前缀使用范围过宽的问题：玩家导航提示使用 XyCore 前缀，后台日志、插件重载和管理命令恢复使用 `XyChemdahShow` 自身前缀。
- 修复导航按钮悬浮提示可能跑到屏幕外的问题，默认 HUD 改为 `导航按钮提示` 自定义文本提示。

### 调整

- 默认 HUD 新增 `任务标题_label`，插件会把任务第一行标题单独写入该组件。
- `任务信息_label` 改为显示标题下方的类型、地点、目标、详情等内容。
- 导航按钮默认移动到任务标题左侧，避免遮挡第一行任务名。
- 默认 `questhud.yml` 已同步为开发者最新提供的新版 RPG-HUD 任务栏布局，并保留导航按钮、固定提示与导航视觉配置。
- 旧版 HUD 未配置 `任务标题_label` 时，插件仍会退回旧版整段写入 `任务信息_label` 的兼容模式。

## 1.6.0 - 2026-07-26

### 新增

- `questhud.yml` 默认采用开发者提供的 DragonCore 平面箭头导航配置，默认 `render-mode: dragoncore-arrow`。
- 默认 HUD 配置补充导航按钮、箭头贴图、刷新间隔、贴图大小、间距、旋转、透明度、穿墙和发光等中文注释。
- 插件消息前缀统一为 XyCore 风格：安装 XyCore 时读取 `messages.prefix`，未安装时使用默认 `&7[&bXyCore&7]&r`。

### 调整

- `plugin.yml` 版本升至 `1.6.0`，并将 XyCore 加入软依赖。
- DragonCore 箭头默认贴图路径按当前资源包配置为 `任务栏/导航箭头.png`，保留 README 中对纯英文路径兼容方案的说明。

### 性能

- DragonCore 箭头渲染新增上一帧缓存：玩家不动且箭头位置、旋转、贴图参数未变化时，不重复发送同一批 WorldTexture 更新。
- 保留 `dragoncore-arrow.update-interval` 单独控制箭头路线刷新频率，避免为了箭头顺滑而提高粒子路线频率。
- 已复查导航主要消耗点：在线导航玩家数量、`max-points`、`ground-search-up/down` 和 `update-interval` 是主要性能参数。

## 1.5.9 - 2026-07-26

### 优化

- DragonCore 平面箭头路线新增 `dragoncore-arrow.update-interval` 配置，默认 `2 tick`，让玩家移动时箭头位置更新更连贯。
- `/xychshow reload` 或 Chemdah reload 联动重载后，如果箭头刷新间隔发生变化，导航计时器会自动按新配置重启。

### 性能

- 粒子路线仍使用 `navigation.particle-interval`，DragonCore 箭头路线单独使用 `dragoncore-arrow.update-interval`，避免为了贴图顺滑而把所有导航模式的刷新频率一起拉高。

## 1.5.8 - 2026-07-26

### 调整

- DragonCore 平面箭头路线的默认世界贴图路径改为 `xychemdahshow/nav_arrow.png`，避免部分 DragonCore 版本对中文目录 WorldTexture 解析失败导致箭头空白。
- README 补充说明：HUD 按钮贴图可以继续使用 GUI 路径，但 `dragoncore-arrow.texture` 建议使用纯英文资源路径。

## 1.5.7 - 2026-07-26

### 调整

- DragonCore 平面箭头贴图路线的视觉配置迁移到 `questhud.yml` 的 `任务导航按钮.navigation` 下。
- 插件会优先读取 `questhud.yml` 中的导航视觉配置，旧版 `config.yml` 中的 `navigation.render-mode` 与 `navigation.dragoncore-arrow` 仍作为兼容回退。
- 发送 HUD 给 DragonCore 客户端前，会从内存 YAML 中移除 `任务导航按钮.navigation`，避免客户端把该插件专用配置当作 HUD 组件字段解析。

## 1.5.6 - 2026-07-26

### 新增

- 新增 `navigation.render-mode`，支持在 `particle` 粒子路线与 `dragoncore-arrow` 龙核平面箭头贴图路线之间切换。
- 新增 `navigation.dragoncore-arrow` 配置组，可控制箭头贴图、大小、间距、最大数量、透明度、发光、穿墙和旋转角度。
- 新增 `navigation.dragoncore-arrow.rotation-y-offset`，用于修正“路线方向正确但箭头图案朝左/朝右”的贴图方向问题。

### 调整

- 顶部总开关改为 `navigation-enabled`；旧的 `particle-navigation-enabled` 与 `navigation.enabled` 仍保持兼容。

### 性能

- DragonCore 箭头路线只向正在导航的玩家发送个人世界贴图；停止导航、到达目标或切回粒子模式时会清理旧箭头贴图。

## 1.5.5 - 2026-07-26

### 新增

- 新增导航路线贴地功能，粒子会在路线点附近寻找可站立表面，减少悬崖、坡道处悬空的问题。
- 新增 `navigation.ground-follow-enabled`、`navigation.ground-search-up`、`navigation.ground-search-down`、`navigation.ground-offset` 配置。

### 性能

- 贴地搜索以每个路线点附近已加载区块为范围，不主动为导航线路加载远处区块。
- 贴地搜索范围可通过 `ground-search-up/down` 控制；悬崖较深时可调大 `ground-search-down`，但数值越大检查方块越多。

## 1.5.4 - 2026-07-26

### 新增

- `navigation.particle` 支持中文预设名，便于测试不同导航路线粒子效果。
- 新增 `经验球风格` 预设，使用粒子模拟经验球路线，不生成经验球实体。
- 新增配置顶部总开关 `particle-navigation-enabled`，用于直接控制是否开启粒子导航。

### 说明

- `navigation.particle` 仍支持直接填写 Bukkit 1.12.2 粒子枚举名。
- 旧配置中的 `navigation.enabled` 仍然兼容；当 `particle-navigation-enabled` 存在时优先读取新总开关。

## 1.5.3 - 2026-07-25

### 调整

- 导航粒子从“玩家前方短箭头”调整为“沿目标方向铺设的路线箭头”，目标较近时会直接铺到目标附近。
- 新增 `navigation.particle-spacing`、`navigation.arrow-head-length`、`navigation.max-points` 配置，用于控制路线密度、箭头头部和最大粒子点数量。

### 性能

- 路线粒子仍按 `navigation.particle-interval` 间隔刷新，并通过 `navigation.max-points` 限制单次生成数量。

## 1.5.2 - 2026-07-25

### 修复

- 修复导航只读取 `addon.xychshow.nav` 导致已有 Chemdah `addon.track.world/x/y/z` 坐标无法导航的问题。

### 调整

- 导航目标读取顺序调整为优先 `addon.xychshow.nav`，没有专用导航节点时回退读取 `addon.track` 坐标。

## 1.5.1 - 2026-07-25

### 修复

- 修复默认 `questhud.yml` 导航按钮使用了当前 DragonCore 不支持的 `方法.执行命令`，改为通过 `方法.聊天('/xychshow nav')` 触发导航命令。

## 1.5.0 - 2026-07-25

### 新增

- 新增第一版任务导航功能，玩家可通过 `/xychshow nav` 开始或停止当前任务导航。
- 支持从 Chemdah 任务配置 `addon.xychshow.nav.world/x/y/z` 读取导航目标坐标。
- 导航坐标读取兼容数字和字符串两种写法。
- 新增地面粒子箭头指引，玩家进入目标附近后自动停止导航。
- 默认 `questhud.yml` 增加导航按钮组件，点击后执行 `/xychshow nav`。
- 新增 `navigation` 配置节，可调整启用状态、粒子类型、刷新间隔、到达距离与箭头长度。

### 说明

- 当前导航第一版会选择玩家当前进行中第一条带 `addon.xychshow.nav` 的任务。
- 第一版使用服务端粒子指引，不依赖 DragonGPS 或客户端模组。

### 性能

- 导航粒子只对正在导航的玩家定时刷新，无玩家导航时不会保留导航任务。

## 1.4.4 - 2026-07-25

### 修复

- 修复 `questhud.yml` 静态文本组件中 `%xychemdahshow_task_amount%` 等内部变量不刷新的问题。

### 调整

- 插件会扫描 `questhud.yml` 中所有 `texts` 包含 `%xychemdahshow_` 的文本组件，并按原模板替换变量后刷新该组件。
- HUD 标题、组件名、位置、大小、颜色与文本模板仍由 `questhud.yml` 控制，不恢复 `config.yml` 写死标题的旧逻辑。
- README 补充全部内部 HUD 变量说明，便于发布到 GitHub 后查看使用方式。

### 性能

- 变量组件模板只在插件重载时扫描，运行时随已有 HUD 刷新事件一起更新，不新增常驻轮询。
## 1.4.3 - 2026-07-25

### 调整

- `addon.xychshow.detail` 改为显式控制：未配置 `detail` 时不再自动引用 Chemdah `addon.ui.description`。
- 新增 `structured-labels` 配置，可修改结构化任务视图中的 `类型`、`地点`、`目标`、`详情` 前缀文字。
- 新增 `structured-line-format` 配置，可调整结构化行显示格式，例如从 `标签: 内容` 改为 `标签=内容`。

### 记录

- 已记录 `questhud.yml` 静态文本中 `%xychemdahshow_task_amount%` 不自动解析的问题，后续将改为读取 HUD 模板并刷新变量组件。
## 1.4.2 - 2026-07-25

### 调整

- 取消 HUD 标题文本的运行时覆盖，界面标题、背景、坐标与样式全部交给 `questhud.yml` 控制。
- 默认配置移除 `title-text` 与 `title-component`，避免误以为标题仍由插件配置接管。
- `addon.xychshow.type` 与 `addon.xychshow.location` 改为按任务配置原样显示，不再自动补 `[]`。

### 新增

- 内置 `%xychemdahshow_*%` PlaceholderAPI 变量注册，方便 `questhud.yml` 的标题或静态文本读取玩家任务数量、任务名称等信息。

### 性能

- 占位符读取继续复用 Chemdah 当前玩家任务数据，不新增每 tick 轮询。
## 1.4.1-lite - 2026-07-24

### 新增

- 新增 `progress-refresh-delay` 配置项，任务进度推进可使用独立短延迟刷新。
- 新增 Chemdah `PluginReloadEvent.Quest` 联动重载：检测到 Chemdah 任务重载后，XyChemdahShow 会同步重载自身配置与任务视图数据。
- 新增 `addon.xychshow` 可选展示字段读取，支持任务配置内声明 `type`、`location`、`target`、`detail`。
- 默认 `questhud.yml` 更新为新的任务栏贴图布局。

### 调整

- 默认标题文本调整为 `当前共 [数量] 项委托待完成` 格式。
- Chemdah reload 联动刷新时不强制重开 HUD，尽量避免玩家拖动位置被重置。

### 性能

- 进度推进事件和普通任务事件使用独立的刷新合并队列。击杀、挖掘等高频进度事件默认 3 tick 后刷新，普通任务事件仍使用 `huddelay`。
## 1.4-lite - 2026-07-24

### 新增

- 新增 Chemdah 计数型子任务实时进度显示。
- 支持显示 `当前/目标` 格式，例如击杀任务从 `0/10` 推进到 `1/10`。
- 新增 `task-progress-enabled` 配置项，可开关子任务进度显示。
- 新增 `task-progress-format` 配置项，可自定义未完成子任务进度格式。
- 新增 `task-completed-progress-format` 配置项，可自定义已完成子任务进度格式。
- 新增 `CHANGELOG.md` 更新记录。
- 新增 `AI_USAGE.md` AI 协作使用记录。

### 调整

- 插件主命令由 `/xychemdahshow` 调整为 `/xychshow`。
- README 同步补充命令、实时进度、性能说明。

### 性能

- 任务 HUD 进度刷新继续依赖 Chemdah 事件触发，不新增常驻轮询。
- 同一玩家在刷新延迟内触发多次任务事件时，会合并为一次延迟刷新，减少高频击杀/挖掘任务下的重复 HUD 更新。
- 子任务进度优先读取 Chemdah Objective 的 `Progress`，仅在无法读取时兜底读取任务数据中的 `amount`。

### 兼容

- 对没有计数目标的 Chemdah 子任务不强制显示 `0/1`。
- 对已完成的计数子任务，会显示为目标值完成状态。

## 1.3-lite - 2026-07-17

### 新增

- 插件整体重构为 XyChemdahShow。
- 自动读取 Chemdah 玩家身上的任务。
- 支持显示 Chemdah 任务 `meta.name` 与子任务 `task.*.meta.name`。
- 新增内部 HUD 变量：玩家名、任务数量、任务名列表、已完成子任务数量。

### 修复

- 移除 DragonCore Deprecated ConfigLoadEvent 监听，避免服务器启动时产生弃用警告。
- Chemdah 重载后刷新 HUD 内容时不强制重开 HUD，减少拖动位置被重置的情况。
