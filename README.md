# XyChemdahShow

XyChemdahShow 是一个轻量 Chemdah 任务 HUD 视图插件，用 DragonCore 在玩家屏幕上显示当前正在进行的 Chemdah 任务，不依赖 RedmiAssyLib。

## 版本

当前源码版本：`1.4.2`

## 依赖

运行依赖：

- DragonCore
- Chemdah

可选依赖：

- PlaceholderAPI：用于在 DragonCore HUD 的标题、静态文本等位置解析 `%xychemdahshow_*%` 变量。

## 功能范围

- 玩家进入服务器后自动打开 DragonCore 任务 HUD。
- 自动读取 Chemdah 玩家身上的实时任务，不需要额外在本插件里登记任务。
- 显示 Chemdah 任务名与子任务名。
- 支持 `addon.xychshow` 结构化展示字段，用于显示类型、地点、目标和详情。
- 对 Chemdah 计数型子任务显示实时进度，例如 `1/10`。
- Chemdah 任务接取、推进、完成、失败、重启、重载后自动刷新 HUD。
- 提供内置 PlaceholderAPI 变量，方便 HUD 标题和静态组件读取任务数量、任务名等信息。
- 保留奖励配置解析接口，当前轻量版不提供旧版背包奖励预览界面。

## 安装

1. 将 `XyChemdahShow-1.4.2.jar` 放入服务器 `plugins` 目录。
2. 确认服务器已安装 DragonCore 与 Chemdah。
3. 如需在 HUD 标题或静态文本中使用 `%xychemdahshow_*%` 变量，请安装 PlaceholderAPI。
4. 启动服务器生成默认配置。
5. 根据需要修改 `plugins/XyChemdahShow/config.yml` 与 `plugins/XyChemdahShow/questhud.yml`。
6. 使用 `/xychshow reload` 重载配置，或重启服务器。

## 命令

- `/xychshow refresh`：刷新自己的任务 HUD。
- `/xychshow reload`：重载配置并刷新在线玩家，需要 `xychemdahshow.admin` 权限。

## 权限

- `xychemdahshow.admin`：允许使用 `/xychshow reload`。

## 配置说明

`config.yml` 负责插件行为：

- `huddelay`：普通任务事件刷新延迟，20 tick = 1 秒。
- `progress-refresh-delay`：任务进度推进刷新延迟，击杀、挖掘等高频任务建议保持较低。
- `joindelay`：玩家进服后首次显示 HUD 的延迟。
- `deletehud`：玩家没有可显示任务时是否自动关闭 HUD。
- `empty-text`：`deletehud` 为 `false` 且玩家没有任务时显示的文字。
- `task-progress-enabled`：是否在子任务后显示 Chemdah 计数进度。
- `task-progress-format`：未完成子任务进度格式。
- `task-completed-progress-format`：已完成子任务进度格式。

`questhud.yml` 负责 DragonCore HUD 外观：

- 标题、背景、坐标、字体、颜色和静态文本都建议在 `questhud.yml` 中修改。
- 插件运行时只会刷新 `任务信息_label.texts`，不会再覆盖标题组件。
- 如果标题需要显示任务数量，可以在 `questhud.yml` 里写 `%xychemdahshow_task_amount%`。

## HUD 变量

插件会在内部任务文本中替换以下变量；安装 PlaceholderAPI 后，也会注册为 PAPI 变量：

- `%xychemdahshow_player%`：玩家名。
- `%xychemdahshow_task_amount%`：正在进行的 Chemdah 任务数量。
- `%xychemdahshow_task_names%`：正在进行的 Chemdah 任务名，逗号分隔。
- `%xychemdahshow_completed_amount%`：当前正在进行任务中已完成的子目标数量。

如果在 `questhud.yml` 的标题或静态文本里使用这些变量，需要服务器启用 PlaceholderAPI，并由 DragonCore 解析 PAPI 变量。任务内容中的变量仍会由插件内部替换。

## Chemdah 任务展示增强

可在 Chemdah 任务配置中写入 `addon.xychshow` 作为 XyChemdahShow 的展示增强字段：

```yaml
addon:
  xychshow:
    type: 日常委托
    location: 墨源城山丘
    target: 击杀10只獠牙赤猪
    detail: 前往墨源城山丘处击杀10只獠牙赤猪。
```

字段说明：

- `type`：展示类型，优先于 Chemdah `meta.type`。
- `location`：展示地点。
- `target`：展示目标；单目标任务会自动在末尾拼接进度。
- `detail`：展示详情；为空时会尝试读取 `addon.ui.description`。

未写 `addon.xychshow` 的任务会保持自动简易显示。

## 性能说明

插件不做每 tick 轮询。HUD 刷新由玩家进服、手动刷新、Chemdah 任务事件与 Chemdah 重载事件触发。普通刷新使用 `huddelay`，进度推进刷新使用 `progress-refresh-delay`。同一个玩家在刷新延迟内连续触发多次同类事件时，只会排队一次 HUD 刷新，避免击杀、挖掘等高频任务造成重复刷新。

## 常见排查

- HUD 不显示：确认 DragonCore 正常加载，玩家客户端资源可用，并检查 `/xychshow refresh` 是否能手动刷新。
- 标题变量不解析：确认已安装 PlaceholderAPI，并确认 DragonCore 当前组件会解析 PAPI 变量。
- 任务内容不更新：确认 Chemdah 中玩家确实存在正在进行的任务，并检查控制台是否有 Chemdah 任务事件报错。
- 没任务时不想显示 HUD：将 `deletehud` 改为 `true`。