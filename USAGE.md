# XyChemdahShow 使用说明

## 安装

1. 将 `XyChemdahShow-1.6.6.jar` 放入服务器 `plugins` 目录。
2. 确认服务器已安装 DragonCore 与 Chemdah。
3. 可选安装 PlaceholderAPI 与 XyCore。
4. 启动服务器生成 `plugins/XyChemdahShow/config.yml` 与 `questhud.yml`。
5. 修改配置后使用 `/xychshow reload` 重载。

## 命令

- `/xychshow refresh`：刷新自己的任务 HUD。
- `/xychshow nav`：开始或停止当前任务导航。
- `/xychshow reload`：重载配置并刷新在线玩家，需要 `xychemdahshow.admin`。

## Chemdah 任务展示字段

可在 Chemdah 任务配置中写入：

```yml
addon:
  xychshow:
    type: "主线"
    location: "墨源城"
    target: "击杀10只獠牙赤猪"
    detail: "前往墨源城山丘处击杀獠牙赤猪"
```

`type/location/target/detail` 的前缀文字可在 `config.yml` 的 `structured-labels` 修改。

## 导航坐标

推荐写法：

```yml
addon:
  xychshow:
    nav:
      world: world
      x: 63
      y: 22
      z: 118
```

兼容 Chemdah `addon.track`：

```yml
addon:
  track:
    world: world
    x: 63
    y: 22
    z: 118
```

玩家点击任务栏导航按钮后会执行 `/xychshow nav`。

如果任务导航坐标写在其他世界，玩家不在目标世界时不会直接开启路线，会提示先传送到 `addon.xychshow.location` 配置的地点，例如 `§e墨源城`。

## HUD 变量

- `%xychemdahshow_player%`：玩家名。
- `%xychemdahshow_task_amount%`：玩家当前进行中的 Chemdah 任务数量。
- `%xychemdahshow_task_names%`：玩家当前任务名称，多个任务用逗号分隔。
- `%xychemdahshow_completed_amount%`：当前正在进行任务中已完成的子目标数量。

这些变量可写在 `questhud.yml` 的文本组件中，也会注册到 PlaceholderAPI。

## HUD 保活

如果任务栏在 DragonCore 客户端待久后自动消失，或内容偶尔被清空，可在 `config.yml` 调整：

```yml
hud-keep-alive:
  enabled: true
  interval: 100
  reopen-hud: false
```

默认每 100 tick 轻量刷新一次任务文本、标题变量和导航按钮显示状态。`reopen-hud` 默认关闭，避免频繁重新打开 HUD 影响玩家收起状态；如果轻量刷新仍无法恢复显示，再改为 `true` 测试。

## DragonCore 箭头导航

默认配置位于 `questhud.yml -> 任务导航按钮.navigation`：

```yml
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

如果箭头图片空白，优先把 `texture` 改成纯英文路径，例如 `xychemdahshow/nav_arrow.png`，并确认资源包中存在该贴图。

默认 HUD 使用：

- `任务标题_label`：显示任务第一行标题，例如 `[主线] 初入浮世`。
- `任务信息_label`：显示类型、地点、目标、详情等内容。
- `任务导航按钮`：固定在标题左侧。
- `任务导航按钮.tip`：鼠标悬停导航按钮时显示的 DragonCore 原生提示。

玩家没有正在进行的任务，或任务没有导航坐标时，插件会隐藏 `任务导航按钮`；如果玩家之前已经开启导航，也会清理残留地面箭头。

## 性能建议

默认不会全服每 tick 扫任务。HUD 刷新由 Chemdah 事件、玩家进服、手动刷新和重载触发。

HUD 保活默认 100 tick 运行一次，只刷新组件文本和按钮状态，不默认重发完整 HUD 配置。

导航只对正在导航的玩家运行。DragonCore 箭头路线默认 `update-interval: 2`，更顺滑但发包更频繁；在线导航人数多时建议调到 `3` 或 `4`。`max-points`、`ground-search-down` 越大，贴地路线检查方块越多。

## XyCore 前缀

安装 XyCore 0.3.12+ 时，玩家导航提示会通过 XyCore 统一前缀 API 读取 `messages.prefix`。未安装、未启用或旧版 XyCore 不可用时，使用本插件 `config.yml -> messages.prefix`，默认 `&7[&bXyChemdahShow&7]&r `，不会影响插件启动。

后台日志、插件重载和管理命令仍使用 `XyChemdahShow` 自身前缀，方便在控制台区分插件来源。
