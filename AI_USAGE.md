# AI 使用记录

## 2026-07-24

### 使用目标

- 协助 XyChemdahShow 从“任务名显示”升级为“任务实时进度显示”。
- 将插件主命令调整为 `/xychshow`。
- 补充本次更新的记录文件，便于后续维护和发布追踪。

### 使用方式

- 由开发者提供 Chemdah 任务示例 `新手击杀.yml` 与 Chemdah 相关文档入口。
- AI 辅助阅读本地插件源码结构，确认 HUD 更新入口、Chemdah 事件刷新入口、命令注册入口。
- AI 辅助通过本地 Chemdah 插件 jar 的公开类信息确认 `Task#getObjective()`、`Objective#getProgress()`、`Progress#getValue()`、`Progress#getTarget()`、`PlayerProfile#dataOperator(Task)` 等接口可用性。
- AI 辅助实现通用计数进度读取逻辑，而不是针对单个任务或单个怪物名称写死。
- AI 辅助增加刷新合并逻辑，降低连续任务事件造成的重复 HUD 刷新。

### 人工确认点

- 需求由开发者确认：进度显示必须面向以后所有任务，而不是只服务“击杀10只獠牙赤猪”。
- 任务示例由开发者提供，实际服务器内显示效果仍需在 Chemdah + DragonCore 环境中测试。

### 验证记录

- 已进行 Java 编译验证。
- 已检查输出 jar 中包含新的 `plugin.yml`、`config.yml`、`HudService.class`、`PluginListener.class`、`PluginSettings.class`。
- 运行期表现需要在服务器中测试：接取计数任务后应显示 `0/10`，击杀或推进目标后应刷新为 `1/10` 等实时数值。

## 2026-07-24 / 1.4.1-lite

### 使用目标

- 协助修复任务进度 HUD 刷新延迟过长的问题。
- 协助加入 Chemdah reload 后自动同步重载 XyChemdahShow 的功能。
- 协助替换默认 DragonCore `questhud.yml` 为开发者提供的新任务栏布局。
- 协助设计并实现 `addon.xychshow` 可选展示字段读取。

### 使用方式

- AI 辅助阅读现有监听器、设置类、HUD 服务类与默认资源配置。
- AI 辅助将 Chemdah Objective 进度事件和普通任务事件拆分为两类刷新延迟。
- AI 辅助保持 Chemdah reload 联动刷新不强制重开 HUD，减少拖动位置被重置的风险。

### 验证记录

- 已进行 Java 编译验证。
- 已检查输出 jar 中包含新版 `plugin.yml`、`config.yml`、`questhud.yml` 与更新后的监听器/HUD 类。
## 2026-07-25 / 1.4.2

### 使用目标

- 协助取消插件对 DragonCore HUD 标题组件的运行时覆盖。
- 协助将 `addon.xychshow.type` 与 `addon.xychshow.location` 改为原样显示，不再自动添加中括号。
- 协助补齐 `%xychemdahshow_*%` PlaceholderAPI 变量，使 `questhud.yml` 可直接负责标题等界面文本。

### 使用方式

- AI 辅助检查 `HudService`、`PluginSettings`、`PlaceholderBridge` 与默认资源配置。
- AI 辅助新增轻量 PlaceholderAPI 变量注册逻辑，并保持任务内容刷新仍走 Chemdah 事件触发。
- AI 辅助同步更新版本号、README 与更新记录，便于测试后推送 GitHub。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：修改 `questhud.yml` 的 `标题_字.texts` 后不再被插件覆盖；需要中括号时由任务配置字段自行填写。
## 2026-07-25 / 1.4.3

### 使用目标

- 协助调整 `addon.xychshow.detail` 的展示逻辑，使删除 `detail` 后不再自动显示 `addon.ui.description`。
- 协助新增结构化任务视图标签配置，让 `类型`、`地点`、`目标`、`详情` 这些前缀可在 `config.yml` 修改。
- 记录 `questhud.yml` 静态文本中的 `%xychemdahshow_task_amount%` 变量失效问题，留作后续模板绑定修复。

### 使用方式

- AI 辅助检查 `HudService` 与 `PluginSettings` 中结构化任务视图相关逻辑。
- AI 辅助新增 `structured-labels` 与 `structured-line-format` 默认配置。
- AI 辅助同步更新 README、CHANGELOG 与版本号，准备测试 jar。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：未配置 `addon.xychshow.detail` 时不显示详情；修改 `structured-labels` 后 HUD 前缀随配置变化。
## 2026-07-25 / 1.4.4

### 使用目标

- 协助修复 `questhud.yml` 静态文本中的 `%xychemdahshow_task_amount%` 等内部变量不刷新的问题。
- 协助整理 GitHub 使用说明中的内部变量列表，明确每个变量的含义。

### 使用方式

- AI 辅助检查 `PluginSettings`、`HudService` 与默认 `questhud.yml`。
- AI 辅助实现 HUD 文本组件模板扫描：只记录 `texts` 中包含 `%xychemdahshow_` 的组件。
- AI 辅助保持界面配置仍由 `questhud.yml` 控制，插件只按模板替换变量并刷新组件内容。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：`标题_字.texts` 中的 `%xychemdahshow_task_amount%` 会显示为真实任务数量。

## 2026-07-25 / 1.5.0

### 使用目标

- 协助新增第一版任务导航功能，让任务栏导航按钮可以触发 `/xychshow nav`。
- 协助从 Chemdah 任务配置 `addon.xychshow.nav` 读取导航坐标。
- 协助实现轻量地面粒子箭头指引，不引入 DragonGPS 或客户端模组依赖。

### 使用方式

- AI 辅助检查命令类、主类、配置类与 HUD 默认资源。
- AI 辅助新增导航服务，维护玩家导航状态并按配置间隔刷新粒子箭头。
- AI 辅助将开发者提供的 `questhud.yml` 同步为默认 HUD，并保留其中的导航按钮组件。
- AI 辅助更新 README 与 CHANGELOG 中的导航配置和使用说明。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：点击导航按钮可执行 `/xychshow nav`，带 `addon.xychshow.nav` 的任务会显示地面粒子箭头。

## 2026-07-25 / 1.5.1

### 使用目标

- 协助修复默认 DragonCore HUD 配置中导航按钮的方法名兼容问题。

### 使用方式

- 由开发者提供服务器报错：`Gui/questhud.yml` 存在错误的方法名 `执行命令`。
- 由开发者确认当前 DragonCore GUI 中可用写法为 `方法.聊天('/ljt open')`。
- AI 辅助将导航按钮动作改为 `方法.聊天('/xychshow nav')`，保持由玩家点击后自行触发命令。

### 验证记录

- 已重新打包 jar 并检查默认 `questhud.yml` 中不再包含 `方法.执行命令`。
- 运行期需在服务器中确认：点击导航按钮不会再触发 DragonCore 方法名错误。

## 2026-07-25 / 1.5.2

### 使用目标

- 协助修复任务配置已写 `addon.track.world/x/y/z` 但 `/xychshow nav` 提示没有可导航线路的问题。

### 使用方式

- 由开发者提供实际 Chemdah 任务配置，确认坐标位于 `addon.track` 节点。
- AI 辅助调整导航目标读取顺序：优先读取 `addon.xychshow.nav`，没有专用导航节点时回退读取 `addon.track`。
- AI 辅助同步更新 README 与 CHANGELOG，明确两种导航坐标写法。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：使用 `addon.track.world/x/y/z` 的任务可直接被 `/xychshow nav` 识别。

## 2026-07-25 / 1.5.3

### 使用目标

- 协助优化导航粒子显示长度，让导航效果从短箭头变为更像路线的连续粒子指引。

### 使用方式

- 由开发者反馈当前导航粒子可见，但长度偏短、不像直达导航地点。
- AI 辅助将导航绘制逻辑改为沿目标方向铺设路线粒子，并保留箭头头部。
- AI 辅助新增 `particle-spacing`、`arrow-head-length`、`max-points` 配置，方便按服务器性能和视觉效果调整。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：导航粒子线路长度、密度和性能表现符合实际地图使用。

## 2026-07-26 / 1.5.4

### 使用目标

- 协助将导航路线粒子效果做成更容易测试的配置项。
- 协助加入中文粒子预设，方便在 `config.yml` 中切换不同导航视觉效果。

### 使用方式

- 由开发者提出希望先在配置中提供可选粒子效果，用于测试“经验球风格”等路线表现。
- AI 辅助保留 `navigation.particle` 配置，并增加中文预设到 Bukkit 粒子的映射。
- AI 辅助明确 `经验球风格` 只是粒子模拟，不生成经验球实体，避免实体负担和被玩家吸收的问题。
- AI 辅助将粒子导航总开关整理为配置顶部的 `particle-navigation-enabled`，并保留旧 `navigation.enabled` 兼容。
- AI 辅助明确 `navigation.particle-interval` 控制刷新频率，`navigation.particle-spacing` 控制粒子密集程度。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：修改 `navigation.particle` 后 `/xychshow reload` 能切换导航粒子效果。

## 2026-07-26 / 1.5.5

### 使用目标

- 协助优化导航路线高度，让粒子尽量贴着地面走，减少悬崖、坡道处悬空的问题。

### 使用方式

- 由开发者反馈当前导航线遇到悬崖会直接悬浮在空中。
- AI 辅助将路线粒子点改为按 X/Z 位置寻找附近可站立表面，并跟随上一粒子高度继续搜索。
- AI 辅助新增贴地搜索配置：`ground-follow-enabled`、`ground-search-up`、`ground-search-down`、`ground-offset`。
- AI 辅助避免使用最高方块逻辑，降低室内或洞穴路线被吸到屋顶上的概率。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：路线在悬崖、台阶、斜坡和室内场景中的贴地效果符合预期。

## 2026-07-26 / 1.5.6

### 使用目标

- 协助新增 DragonCore 平面箭头贴图导航路线，让箭头图标逐个平铺在地面并指向导航点。

### 使用方式

- 由开发者明确期望：使用平面箭头图标，一个一个铺到导航点，并且每个箭头方向正确。
- AI 辅助检查 DragonCore `CoreAPI.setPlayerWorldTexture` 与 `removePlayerWorldTexture` 接口。
- AI 辅助新增 `navigation.render-mode`，支持 `particle` 与 `dragoncore-arrow` 两种渲染模式。
- AI 辅助新增 `navigation.dragoncore-arrow.rotation-y-offset`，用于修正贴图自身默认朝左/朝右导致的方向偏差。
- AI 辅助保留贴地逻辑，使 DragonCore 箭头路线也能跟随地面高度。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：`dragoncore-arrow` 模式下箭头贴图平铺、清理、贴地和方向偏移符合预期。

## 2026-07-26 / 1.5.7

### 使用目标

- 协助将 DragonCore 平面箭头贴图路线的视觉配置迁移到 `questhud.yml`。

### 使用方式

- 由开发者确认：龙核平面箭头应该作为界面视觉配置放在 `questhud.yml` 中。
- AI 辅助将 `render-mode` 与 `dragoncore-arrow` 配置迁移到 `任务导航按钮.navigation` 下。
- AI 辅助保留旧版 `config.yml` 中 `navigation.render-mode` 与 `navigation.dragoncore-arrow` 的兼容回退。
- AI 辅助在发送 HUD 给 DragonCore 客户端前移除插件专用的 `任务导航按钮.navigation` 内存节点，避免客户端解析到未知配置。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：`questhud.yml` 中调整 `任务导航按钮.navigation` 后，`/xychshow reload` 可切换 DragonCore 箭头路线配置。

## 2026-07-26 / 1.5.8

### 使用目标

- 协助修正 DragonCore 平面箭头路线使用中文资源路径时显示空白的问题。
- 将世界贴图默认路径调整为已实测可显示的英文路径 `xychemdahshow/nav_arrow.png`。

### 使用方式

- 由开发者在服务器实测确认：`gui/任务栏/导航.png` 作为 WorldTexture 路线贴图会空白，而 `xychemdahshow/nav_arrow.png` 可正常显示。
- AI 辅助区分 DragonCore HUD 普通图片路径与 WorldTexture 世界贴图路径的兼容差异。
- AI 辅助同步更新默认 `questhud.yml`、代码兜底默认值、README 与 CHANGELOG。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：使用 `dragoncore-arrow` 模式时，`xychemdahshow/nav_arrow.png` 路径下的箭头贴图可稳定显示。

## 2026-07-26 / 1.5.9

### 使用目标

- 协助优化 DragonCore 平面箭头路线在玩家移动时的视觉卡顿。
- 在保持性能可控的前提下，让箭头刷新更顺滑。

### 使用方式

- 由开发者反馈：玩家从一个坐标移动到下一个坐标时，箭头像瞬移，视觉比较僵硬。
- AI 辅助将 DragonCore 箭头路线刷新间隔从粒子刷新间隔中拆出，新增 `dragoncore-arrow.update-interval` 配置。
- AI 辅助让插件重载后自动重启导航计时器，使新刷新间隔无需重开导航即可生效。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 运行期需在服务器中确认：`update-interval: 2` 下箭头移动观感更顺滑；若在线导航人数较多，可测试调到 `3` 或 `4`。

## 2026-08-02 / 1.6.1

### 使用目标

- 协助落实服主确认的Xy系列玩家聊天前缀规则。
- 让XyChemdahShow在安装XyCore时统一显示Core前缀，未安装时保留自己的插件前缀。

### 使用方式

- AI辅助调整 `XyChemdahShow.log`：玩家消息使用统一前缀，控制台消息使用本地前缀。
- AI辅助改为通过反射读取XyCore `getMessagePrefix()`，保持XyCore软依赖，不将Core打入插件。
- AI辅助将命令帮助中直接发送的聊天行改为走统一日志/消息入口。
- AI辅助同步更新README、USAGE、CHANGELOG、默认配置注释和版本号。

### 验证记录

- 已进行Java编译验证；运行期需在服务器分别确认有XyCore和无XyCore两种情况下的前缀显示。

## 2026-07-26 / 1.6.0

### 使用目标

- 协助将开发者提供的 `questhud.yml` 作为默认 HUD 配置打包。
- 协助补充导航相关新增功能的中文注释。
- 协助接入 XyCore 风格插件消息前缀。
- 协助复查 DragonCore 导航路线的性能消耗点，并做轻量优化。

### 使用方式

- 由开发者提供最新 `questhud.yml`，默认启用 `dragoncore-arrow` 平面箭头导航。
- AI 辅助将 XyCore 作为软依赖读取；1.6.1起未安装XyCore时改为使用本插件本地前缀。
- AI 辅助为 `任务导航按钮.navigation.dragoncore-arrow` 中贴图、大小、间距、最大数量、刷新间隔、旋转、透明度、穿墙和发光配置补充中文注释。
- AI 辅助检查性能热点：导航定时器、WorldTexture 发包、贴地搜索方块检查、HUD 刷新队列。
- AI 辅助新增 DragonCore 箭头渲染状态缓存，避免玩家不移动时重复发送完全相同的箭头贴图更新。

### 验证记录

- 已进行 Java 编译验证与 jar 内容检查。
- 已确认 `plugin.yml` 版本为 `1.6.0`，并包含 XyCore 软依赖。
- 运行期需在服务器中确认：XyCore 前缀、默认 HUD、DragonCore 箭头路径与性能参数符合实际服务端环境。
