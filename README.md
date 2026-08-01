# XyChemdahShow

轻量 Chemdah 任务 HUD 视图插件，不依赖 RedmiAssyLib。

## 依赖

运行依赖：
- DragonCore
- Chemdah

可选依赖：
- PlaceholderAPI
- XyCore（用于导航等玩家玩法提示统一读取 `messages.prefix`；未安装时使用XyChemdahShow自己的本地前缀兜底）

## 功能范围

- 玩家进入服务器后自动打开 DragonCore 任务 HUD。
- 自动读取 Chemdah 玩家身上的实时任务，不需要额外在本插件里登记任务。
- 新安装时默认只生成 `config.yml` 与 `questhud.yml`，不再生成旧版 `Quest` 示例目录。
- 显示 Chemdah 任务名与子任务名。
- 对 Chemdah 计数型子任务显示实时进度，例如 `1/10`。
- Chemdah 任务接取、推进、完成、失败、重启、重载后自动刷新 HUD。
- 支持可配置 HUD 保活刷新，缓解 DragonCore 任务栏待久后自动消失或内容丢失的问题。
- 支持任务导航：Bukkit 粒子路线或 DragonCore 平面箭头路线。
- 导航等玩家玩法提示安装 XyCore 时优先读取 `plugins/XyCore/config.yml -> messages.prefix`，未安装时使用本插件前缀独立运行；help、reload、后台日志等管理提示保留本插件前缀。
- 保留奖励配置解析接口，当前轻量版不提供旧版背包奖励预览界面。

## 更新日志

### 1.6.6 - 2026-08-02

- 在 GitHub 最新 `1.6.5` 基础上按服主最终确认复核前缀语义。
- 玩家导航提示继续使用 XyCore `messages.prefix`；help、reload、无权限、只能玩家执行和后台日志继续使用 XyChemdahShow 本地前缀。
- 版本提升至 `1.6.6`，用于区分本次统一前缀语义修正版。

### 1.6.5 - 2026-08-01

- 新增 HUD 保活刷新配置 `hud-keep-alive`，缓解 DragonCore 任务栏待久后自动消失或内容丢失的问题。
- 玩家没有可导航任务，或任务没有导航坐标时，自动隐藏 `任务导航按钮`。
- 任务消失、任务完成或导航坐标失效时，自动停止残留导航并清理地面箭头。
- 玩家导航提示优先使用 XyCore 0.3.11+ 统一前缀 API；XyCore 不可用时回退到本插件前缀。
- 后台日志、插件重载和管理类提示继续使用 XyChemdahShow 自身前缀。
- 保活默认每 `100 tick` 轻量刷新一次，不默认重发完整 HUD 配置。

### 1.6.4 - 2026-07-26

- 默认 `questhud.yml` 同步为新版任务栏布局。
- 保留 `任务导航按钮.tip` 原生悬浮提示。
- 新安装时不再自动生成旧版 `Quest` 目录和任务示例文件。

### 1.6.3 - 2026-07-26

- 导航启动前检测玩家是否处于目标世界，不在目标世界时提示先传送。
- 玩家切换到非目标世界时，自动停止导航并清理箭头。

### 1.6.2 - 2026-07-26

- 修复导航按钮 `tip` 配置位置，悬浮提示恢复正常。

### 1.6.1 - 2026-07-26

- 新增任务标题独立组件 `任务标题_label`。
- 导航按钮移动到任务标题左侧。
- 结构化任务信息支持类型、地点、目标、详情。
- 修复旧版 HUD 的兼容显示逻辑。

### 1.6.0 - 2026-07-26

- 新增 DragonCore 平面箭头导航。
- 新增箭头贴图、间距、刷新频率、旋转、透明度、穿墙和发光配置。
- 新增上一帧箭头状态缓存，减少重复 WorldTexture 发包。
- 支持 XyCore 软依赖。

## 命令

- `/xychshow refresh`：刷新自己的任务 HUD。
- `/xychshow nav`：开始或停止当前任务导航。
- `/xychshow reload`：重载配置并刷新在线玩家，需要 `xychemdahshow.admin` 权限。

## HUD 界面配置

界面布局、标题文本、背景、坐标、显示样式全部交给 `questhud.yml` 控制。插件运行时会刷新 `任务标题_label.texts` 与 `任务信息_label.texts`；如果旧配置没有 `任务标题_label`，会自动退回旧版整段写入 `任务信息_label` 的模式。

插件会按 `questhud.yml` 原模板刷新包含 `%xychemdahshow_` 的文本组件；不再通过 `config.yml` 写死标题内容。

如果任务栏在客户端待久后自动消失，可在 `config.yml` 调整 HUD 保活：

```yml
hud-keep-alive:
  enabled: true
  interval: 100
  reopen-hud: false
```

默认只轻量刷新组件文本、标题变量和导航按钮显示状态，不重发完整 HUD 配置。如果实测 DragonCore 会把整个 HUD 关闭，导致轻量刷新无效，再把 `reopen-hud` 改为 `true`。

## 内部 HUD 变量

这些变量可写在任务内容和 `questhud.yml` 文本组件的 `texts` 中。插件会扫描 `questhud.yml`，只刷新包含 `%xychemdahshow_` 的文本组件，组件位置、大小、颜色和模板仍由 `questhud.yml` 决定。

- `%xychemdahshow_player%`：玩家名。
- `%xychemdahshow_task_amount%`：玩家当前正在进行的 Chemdah 任务数量。
- `%xychemdahshow_task_names%`：玩家当前正在进行的 Chemdah 任务名称，多个任务用逗号分隔。
- `%xychemdahshow_completed_amount%`：当前正在进行任务中已完成的子目标数量。

示例：

```yml
标题_字:
  type: "文本"
  texts: "§7当前共 §a[%xychemdahshow_task_amount%] §7项委托待完成"
```

这些变量也会注册到 PlaceholderAPI，供其他支持 PAPI 的插件读取。

## 结构化任务视图

可在 Chemdah 任务配置中写入 `addon.xychshow` 作为 XyChemdahShow 的展示增强字段：

- `type`：展示类型，优先于 `meta.type`。
- `location`：展示地点，例如 墨源城山丘。
- `target`：展示目标，例如 击杀10只獠牙赤猪。
- `detail`：展示详情，例如 前往墨源城山丘处击杀10只獠牙赤猪。未配置时不显示详情，不再自动引用 `addon.ui.description`。

结构化任务视图的前缀文字可在 `config.yml` 中修改：

- `structured-labels.type`：默认 `类型`。
- `structured-labels.location`：默认 `地点`。
- `structured-labels.target`：默认 `目标`。
- `structured-labels.detail`：默认 `详情`。
- `structured-line-format`：默认 `%label%: %value%`，可改成 `%label%=%value%` 等格式。

未写 `addon.xychshow` 的任务会保持自动简易显示。

## 任务导航

可在 Chemdah 任务配置中写入 `addon.xychshow.nav` 作为导航目标：

```yml
addon:
  xychshow:
    nav:
      world: world
      x: 120
      y: 64
      z: -80
```

如果任务已经使用 Chemdah 追踪坐标，也可以直接写在 `addon.track` 下，插件会作为兼容回退读取：

```yml
addon:
  track:
    world: world
    x: 63
    y: 22
    z: 118
```

玩家点击 `questhud.yml` 中的导航按钮后，会执行 `/xychshow nav`。插件会从玩家当前进行中的任务里寻找第一条带 `addon.xychshow.nav` 或 `addon.track` 坐标的任务，并在玩家前方生成指向目标坐标的地面粒子箭头。再次点击会停止导航，进入目标附近会自动停止。

如果玩家不在任务导航坐标对应的世界，插件不会开启路线，会提示玩家先传送到 `addon.xychshow.location` 配置的地点，例如 `§e墨源城`。

当玩家没有正在进行的任务，或当前任务没有可读取的导航坐标时，插件会隐藏 `任务导航按钮`，并静默清理残留的地面箭头。

默认 HUD 已包含导航按钮组件：

```yml
任务导航按钮:
  type: "图片"
  texture: "gui/任务栏/导航.png"
  actions:
    click_left: |-
      方法.播放声音;
      方法.聊天('/xychshow nav');
```

默认 HUD 会把第一行任务标题单独写入 `任务标题_label`，因此导航按钮可以放在标题左侧，而 `类型/地点/目标/详情` 等内容仍由 `任务信息_label` 单独控制位置。

导航粒子可在 `config.yml` 的 `navigation` 节点调整：

- `navigation-enabled`：任务导航总开关，建议放在配置最上方。
- `navigation.particle-interval`：粒子刷新频率，20 tick = 1 秒，越小刷新越快。
- `navigation.arrive-distance`：距离目标多少格内自动停止。
- `navigation.particle-spacing`：路线粒子密集程度，越小越密集。
- `navigation.arrow-head-length`：箭头头部长度。
- `navigation.max-points`：单次刷新最多生成多少个路线粒子点，数值越大线路越长，但粒子数量也越多。
- `navigation.ground-follow-enabled`：是否让路线粒子跟随地面高度。
- `navigation.ground-search-up`：贴地搜索时允许从上一粒子高度向上找多少格。
- `navigation.ground-search-down`：贴地搜索时允许从上一粒子高度向下找多少格，悬崖较深时可调大。
- `navigation.ground-offset`：粒子离地高度。
- `navigation.particle`：路线粒子效果，可写 Bukkit 粒子名，也可写预设名。

可用预设名：

- `绿色箭头`：映射到 `VILLAGER_HAPPY`。
- `白色光点`：映射到 `END_ROD`。
- `魔法紫`：映射到 `SPELL_WITCH`。
- `蓝紫闪光`：映射到 `CRIT_MAGIC`。
- `火焰`：映射到 `FLAME`。
- `经验球风格`：映射到 `TOTEM`，只是粒子模拟，不会生成经验球实体。

### DragonCore 箭头路线

如果要使用平面箭头贴图铺在地上，可在 `questhud.yml` 的 `任务导航按钮.navigation` 中配置：

```yml
任务导航按钮:
  type: "图片"
  texture: "gui/任务栏/导航.png"
  navigation:
    render-mode: dragoncore-arrow
    dragoncore-arrow:
      texture: "任务栏/导航箭头.png"
      width: 0.65
      height: 0.65
      spacing: 1.4
      max-points: 48
      update-interval: 2
      rotation-x: 90
      rotation-y-offset: 0
      rotation-z: 0
      alpha: 1.0
      through: false
      glow: true
```

`rotation-y-offset` 用来校正贴图自身方向。如果路线铺设方向正确，但箭头图案朝左或朝右，优先尝试 `0`、`90`、`-90`、`180`。

`dragoncore-arrow.texture` 属于 DragonCore WorldTexture 世界贴图路径。本版本默认跟随当前服务器资源写作 `任务栏/导航箭头.png`；如果某些 DragonCore 版本显示为空白，建议改成纯英文资源路径，例如 `xychemdahshow/nav_arrow.png`。HUD 按钮本身的 `texture` 可以继续使用原来的 GUI 贴图路径。

`dragoncore-arrow.update-interval` 控制 DragonCore 箭头路线刷新间隔，20 tick = 1 秒。数值越小，玩家移动时箭头越连贯；建议先用 `2`，如果在线导航人数较多再调到 `3` 或 `4`。

插件会优先读取 `questhud.yml` 中的 `任务导航按钮.navigation`；旧版写在 `config.yml` 的 `navigation.render-mode` 与 `navigation.dragoncore-arrow` 仍作为兼容回退。

## XyCore 前缀

玩家导航提示使用 XyCore 风格前缀：

- 已安装并启用 XyCore 0.3.12+：通过 XyCore 统一前缀 API 读取 `messages.prefix`。
- 未安装、未启用或旧版 XyCore 无法提供前缀API：使用本插件 `config.yml -> messages.prefix`，默认 `&7[&bXyChemdahShow&7]&r `。

后台日志、插件重载和管理类提示仍使用 XyChemdahShow 自身前缀。XyCore 是软依赖，不会影响 XyChemdahShow 独立运行。

## 性能说明

插件不做每 tick 轮询。HUD 刷新由玩家进服、手动刷新、Chemdah 任务事件与 Chemdah 重载事件触发。普通刷新使用 `huddelay`，进度推进刷新使用 `progress-refresh-delay`。同一个玩家在刷新延迟内连续触发多次同类事件时，只会排队一次 HUD 刷新，避免击杀、挖掘等高频任务造成重复刷新。

HUD 保活刷新由 `hud-keep-alive.interval` 控制，默认 `100 tick`。默认模式只刷新文本、变量和按钮显隐；`reopen-hud: true` 会额外重新打开 HUD，建议仅在轻量保活无法恢复客户端显示时启用。

导航仅对正在导航的玩家运行。粒子路线使用 `navigation.particle-interval`，DragonCore 箭头路线使用 `dragoncore-arrow.update-interval`。DragonCore 箭头会缓存上一帧的贴图位置与参数；玩家不动、箭头未变化时不会重复发送同一批 WorldTexture 更新包。

贴地路线需要检查方块高度，主要消耗来自 `ground-search-up/down` 与 `max-points`。如果在线导航人数较多，优先把 `dragoncore-arrow.update-interval` 调到 `3-4`，或适当降低 `max-points`、`ground-search-down`。
