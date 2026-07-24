# 更新记录

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