# 随机数生成器

<p align="center">
  <img src="app/src/main/res/drawable/ic_dice.xml" width="80" height="80" alt="App Icon">
</p>

<p align="center">
  <b>一个非常好用的随机数生成器，基于 Material Design 3 设计，Android 12+，完全开源。</b>
</p>

<p align="center">
  <a href="https://github.com/hpgeyyds/Random_Number"><img src="https://img.shields.io/badge/GitHub-仓库-blue?style=flat-square&logo=github" alt="GitHub"></a>
  <a href="https://www.gnu.org/licenses/gpl-3.0.html"><img src="https://img.shields.io/badge/License-GPL%203.0-green?style=flat-square" alt="License"></a>
  <img src="https://img.shields.io/badge/Android-12+-brightgreen?style=flat-square&logo=android" alt="Android">
  <img src="https://img.shields.io/badge/Material%20Design%203-支持-6200EE?style=flat-square&logo=material-design" alt="MD3">
</p>

---

## ✨ 功能特性

### 🎲 核心功能
- **开始/停止抽取**：点击开始按钮，数字快速跳动；点击停止，获得最终结果
- **自定义范围**：支持任意范围内的随机数生成
- **快速选择**：提供4组可自定义的快速取值范围
- **历史记录**：自动保存抽取记录，格式为"于 xx 与 yy 间取得 zz"
- **复制结果**：一键复制随机数到剪贴板

### 🎨 界面设计
- **Material Design 3**：采用最新的 Material Design 3 设计规范
- **莫奈动态取色**：支持 Android 12+ 系统莫奈（Monet）动态取色
- **非线性动画**：所有动画采用 PathInterpolator，流畅自然
- **深色模式**：支持浅色/深色主题切换

### ⚙️ 设置选项
- **主题模式**：跟随系统、浅色模式、深色模式
- **重复抽取**：可选择是否允许重复抽取同一数字
- **快速范围**：自定义4组快速选择按钮的数值范围

### 🔒 安全特性
- **数据溢出检测**：防止输入过大或过小的数值导致溢出
- **范围验证**：自动检测最小值是否大于最大值

---

## 📱 界面预览

| 主页 | 设置 | 关于 |
|:---:|:---:|:---:|
| 随机数生成界面 | 应用设置界面 | 应用信息界面 |

---

## 🚀 技术栈

- **语言**：Kotlin
- **最低 SDK**：Android 13 (API 33)
- **目标 SDK**：Android 16 (API 36)
- **架构**：单 Activity + 多 Fragment
- **UI 组件**：Material Design 3 Components
- **动画**：Android Property Animation

---

## 📦 项目结构

```
app/src/main/java/my/wyh/randomnumber/
├── MainActivity.kt          # 主Activity（底部导航）
├── HomeFragment.kt          # 主页Fragment（随机数生成）
├── SettingsFragment.kt      # 设置Fragment
├── AboutFragment.kt         # 关于Fragment
├── HistoryAdapter.kt        # 历史记录适配器
├── HistoryItem.kt           # 历史记录数据类
└── ...

app/src/main/res/
├── layout/                  # 布局文件
├── drawable/                # 图标资源
├── menu/                    # 菜单资源
├── values/                  # 字符串、主题等资源
└── ...
```

---

## 🛠️ 构建说明

### 环境要求
- Android Studio Koala 或更高版本
- JDK 17
- Gradle 8.13+

### 构建步骤
1. 克隆仓库
   ```bash
   git clone https://github.com/hpgeyyds/Random_Number.git
   ```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 并构建项目
   ```bash
   ./gradlew build
   ```

4. 运行到设备或模拟器
   ```bash
   ./gradlew installDebug
   ```

---

## 📄 开源协议

本项目采用 [GNU General Public License v3.0 (GPL 3.0)](https://www.gnu.org/licenses/gpl-3.0.html) 开源协议。

您可以自由使用、修改和分发本软件，但需遵守 GPL 3.0 协议条款。

---

## 👨‍💻 开发者

**核平鸽** (hpgeyyds)

- GitHub: [@hpgeyyds](https://github.com/hpgeyyds)
- 项目地址: [https://github.com/hpgeyyds/Random_Number](https://github.com/hpgeyyds/Random_Number)

---

## 🙏 致谢

<p align="center">
  <b>Powered By</b>
</p>

<p align="center">
  <a href="https://www.trae.ai"><img src="https://img.shields.io/badge/Trae-IDE-blue?style=flat-square" alt="Trae"></a>
  &amp;
  <a href="https://www.moonshot.cn"><img src="https://img.shields.io/badge/Kimi%202.5-AI-green?style=flat-square" alt="Kimi"></a>
</p>

感谢 [Trae](https://www.trae.ai) 和 [Kimi 2.5](https://www.moonshot.cn) 对本项目的支持！

---

## 📧 反馈建议

如果您有任何建议或发现了 bug，欢迎通过以下方式联系：

- 在 GitHub 上提交 [Issue](https://github.com/hpgeyyds/Random_Number/issues)
- 发送邮件反馈

---

<p align="center">
  <b>© 2026 核平鸽 版权所有</b>
</p>
