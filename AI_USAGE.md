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