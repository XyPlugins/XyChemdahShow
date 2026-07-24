# XyChemdahShow

轻量 Chemdah 任务 HUD 视图插件，不依赖 RedmiAssyLib。

## 依赖

运行依赖：
- DragonCore
- Chemdah

可选依赖：
- PlaceholderAPI

## 功能范围

- 玩家进入服务器后自动打开 DragonCore 任务 HUD。
- 自动读取 Chemdah 玩家身上的实时任务，不需要额外在本插件里登记任务。
- 显示 Chemdah 任务名与子任务名。
- 对 Chemdah 计数型子任务显示实时进度，例如 `1/10`。
- Chemdah 任务接取、推进、完成、失败、重启、重载后自动刷新 HUD。
- 保留奖励配置解析接口，当前轻量版不提供旧版背包奖励预览界面。

## 命令

- `/xychshow refresh`：刷新自己的任务 HUD。
- `/xychshow reload`：重载配置并刷新在线玩家，需要 `xychemdahshow.admin` 权限。

## 内部 HUD 变量

- `%xychemdahshow_player%`：玩家名。
- `%xychemdahshow_task_amount%`：正在进行的 Chemdah 任务数量。
- `%xychemdahshow_task_names%`：正在进行的 Chemdah 任务名，逗号分隔。
- `%xychemdahshow_completed_amount%`：当前正在进行任务中已完成的子目标数量。

## 结构化任务视图

可在 Chemdah 任务配置中写入 `addon.xychshow` 作为 XyChemdahShow 的展示增强字段：

- `type`：展示类型，优先于 `meta.type`。
- `location`：展示地点，例如 墨源城山丘。
- `target`：展示目标，例如 击杀10只獠牙赤猪。
- `detail`：展示详情，例如 前往墨源城山丘处击杀10只獠牙赤猪。

未写 `addon.xychshow` 的任务会保持自动简易显示。

## 性能说明

插件不做每 tick 轮询。HUD 刷新由玩家进服、手动刷新、Chemdah 任务事件与 Chemdah 重载事件触发。普通刷新使用 `huddelay`，进度推进刷新使用 `progress-refresh-delay`。同一个玩家在刷新延迟内连续触发多次同类事件时，只会排队一次 HUD 刷新，避免击杀、挖掘等高频任务造成重复刷新。