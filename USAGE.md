# XyChemdahShow 使用说明

本文面向服务器管理员，说明如何安装、配置和验证 XyChemdahShow 1.4.2。

## 快速开始

1. 安装 DragonCore 与 Chemdah。
2. 将 `XyChemdahShow-1.4.2.jar` 放入 `plugins` 目录。
3. 需要标题变量时安装 PlaceholderAPI。
4. 启动服务器，等待生成 `plugins/XyChemdahShow` 配置目录。
5. 修改 `questhud.yml` 调整 HUD 外观。
6. 修改 `config.yml` 调整刷新延迟、空任务文字和进度格式。
7. 进入游戏后使用 `/xychshow refresh` 验证显示效果。

## 推荐配置流程

### 1. 先调 HUD 外观

优先编辑 `questhud.yml`。1.4.2 起插件不再运行时覆盖标题，所以标题、背景、坐标、字体、颜色都可以在 DragonCore GUI 配置里直接维护。

任务正文组件请保留名称 `任务信息_label`，插件会向它的 `texts` 字段写入任务内容。

### 2. 再调刷新策略

`config.yml` 中常用项：

```yaml
huddelay: 30
progress-refresh-delay: 3
joindelay: 60
deletehud: false
empty-text: '§7暂无正在进行的任务'
task-progress-enabled: true
task-progress-format: ' §8[§a%current%§7/§e%target%§8]'
task-completed-progress-format: ' §8[§a%current%§7/§a%target%§8]'
```

击杀、采集、挖掘这类高频任务建议保留较低的 `progress-refresh-delay`。如果服务器任务事件非常频繁，可以适当调高。

### 3. 给任务补展示字段

在 Chemdah 任务配置中加入：

```yaml
addon:
  xychshow:
    type: 日常委托
    location: 墨源城山丘
    target: 击杀10只獠牙赤猪
    detail: 前往墨源城山丘处击杀10只獠牙赤猪。
```

这些字段只影响 HUD 展示，不改变 Chemdah 任务逻辑。未填写时插件会自动读取任务名和子任务名进行简易显示。

## 可用变量

- `%xychemdahshow_player%`：玩家名。
- `%xychemdahshow_task_amount%`：正在进行的任务数量。
- `%xychemdahshow_task_names%`：正在进行的任务名列表。
- `%xychemdahshow_completed_amount%`：已完成子目标数量。

任务正文中的变量由插件内部替换。`questhud.yml` 标题或静态文本中的变量需要 PlaceholderAPI 与 DragonCore 侧解析支持。

## 重载与验证

- 修改 `config.yml` 或 `questhud.yml` 后执行 `/xychshow reload`。
- 只想刷新自己的 HUD 时执行 `/xychshow refresh`。
- Chemdah 任务重载时，插件会尝试同步重载任务视图并刷新在线玩家。

## 常见问题

### HUD 标题为什么不跟着 config.yml 变了？

1.4.2 起标题完全交给 `questhud.yml`。这是为了让界面外观统一由 DragonCore GUI 配置管理，插件只负责写入任务正文。

### 没装 PlaceholderAPI 能不能用？

可以。任务 HUD 正文刷新不依赖 PlaceholderAPI。只有当你要在 HUD 标题或静态文本中使用 `%xychemdahshow_*%` 变量时，才需要 PlaceholderAPI。

### 任务没有进度数字怎么办？

确认 `task-progress-enabled` 为 `true`，并确认 Chemdah 子任务本身提供可读取的计数目标。没有计数目标的任务不会强制显示 `0/1`。