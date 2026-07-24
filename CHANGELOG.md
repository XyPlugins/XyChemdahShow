# 更新记录

## 1.4.2 - 2026-07-25

### 新增

- 新增内置 PlaceholderAPI 扩展，安装 PlaceholderAPI 后可直接使用 `%xychemdahshow_*%` 变量。
- 新增 `%xychemdahshow_player%`，返回当前玩家名称。
- 新增 `%xychemdahshow_task_amount%`，返回当前玩家正在进行的 Chemdah 任务数量。
- 新增 `%xychemdahshow_task_names%`，返回当前玩家正在进行的任务名列表，多个任务以逗号分隔。
- 新增 `%xychemdahshow_completed_amount%`，返回当前正在进行任务中已完成的子目标数量。
- 新增 `hook.XychPlaceholderExpansion`，由插件启用/卸载时自动注册和注销，无需额外安装独立变量扩展。

### HUD 调整

- 取消运行时覆盖 HUD 标题文本，插件现在只刷新 `任务信息_label.texts` 里的任务内容。
- 界面标题、背景图片、坐标、字体、静态文案等全部交给 `questhud.yml` 管理，便于服主直接改 DragonCore GUI。
- 默认配置移除 `title-text` 与 `title-component`，避免误以为标题仍由 `config.yml` 接管。
- 保留任务内容中的内部变量替换能力，`empty-text` 和任务显示文本仍支持 `%xychemdahshow_player%` 等内部变量。

### 任务展示

- `addon.xychshow.type` 与 `addon.xychshow.location` 改为按任务配置原样显示，不再自动补 `[]`。
- `addon.xychshow.target` 在单目标任务中会自动拼接计数进度，例如 `击杀10只怪 1/10`。
- `addon.xychshow.detail` 为空时继续尝试读取 `addon.ui.description` 作为详情兜底。
- 未配置 `addon.xychshow` 的任务继续使用自动简易显示，不影响旧任务配置。

### 配置与使用

- `questhud.yml` 成为 HUD 外观的主要配置入口，推荐把标题中的任务数量写成 `%xychemdahshow_task_amount%`。
- `config.yml` 继续负责刷新延迟、空任务显示、子任务进度格式和 HUD 自动关闭策略。
- `README.md` 补充依赖、安装、命令、HUD 变量、Chemdah 任务配置示例和常见排查说明。
- 新增 `USAGE.md` 使用说明，方便服务器管理员按步骤安装、配置和验证 HUD。

### 兼容

- PlaceholderAPI 仍为可选依赖；未安装 PlaceholderAPI 时，插件内部任务内容刷新不受影响。
- DragonCore HUD 静态组件中的 PAPI 变量需要服务器安装 PlaceholderAPI，并由 DragonCore 侧负责解析。
- Chemdah 任务读取逻辑继续基于玩家当前任务数据，不要求在本插件里额外登记任务。

### 性能

- 占位符读取继续复用 Chemdah 当前玩家任务数据，不新增每 tick 轮询。
- HUD 刷新仍由玩家进服、手动刷新、Chemdah 任务事件与 Chemdah 重载事件触发。
- 高频任务进度推进继续使用 `progress-refresh-delay` 合并刷新，减少击杀、挖掘等任务连续触发时的重复 HUD 更新。

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