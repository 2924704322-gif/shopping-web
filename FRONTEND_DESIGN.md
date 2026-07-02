# 购物平台管理系统 — 前端设计方案

## 一、技术选型

| 技术 | 用途 |
|------|------|
| Vue 3 (Composition API) | 前端框架 |
| Vite | 构建工具 + 开发代理 |
| Element Plus | UI 组件库（表格、表单、弹窗、导航） |
| Vue Router 4 | Hash 路由 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 + 拦截器 |

## 二、项目结构

```
shopping-admin/
├── index.html                 # 管理后台入口
├── login.html                 # 登录页入口
├── package.json
├── vite.config.js             # 多页面配置 + /api 代理 → localhost:8080
└── src/
    ├── main.js                # 后台入口（注册 Router + Pinia）
    ├── login.js               # 登录页入口（独立 Vue 实例）
    ├── App.vue                # 根组件 → <router-view />
    ├── api/
    │   ├── request.js         # Axios 实例 + 请求/响应拦截器
    │   ├── auth.js            # 登录、登出
    │   ├── user.js            # 用户 CRUD + 状态切换 + 角色分配
    │   ├── category.js        # 分类 CRUD + 分类树
    │   ├── product.js         # 商品 CRUD + 上下架
    │   └── upload.js          # 图片上传
    ├── router/
    │   └── index.js           # 路由表 + beforeEach 守卫
    ├── stores/
    │   └── auth.js            # Pinia：token, userInfo, 登录/登出动作
    ├── utils/
    │   └── token.js           # localStorage 存取 token
    ├── composables/
    │   └── usePage.js         # 列表页复用逻辑（分页、搜索、删除、状态切换）
    ├── layouts/
    │   └── AdminLayout.vue    # 侧边栏 + 顶栏 + <router-view>
    ├── components/
    │   ├── ImageUpload.vue    # el-upload 封装（类型校验、自动携带 Token）
    │   └── StatusTag.vue      # 启用/禁用、上架/下架状态标签
    └── views/
        ├── login/
        │   └── LoginView.vue
        ├── dashboard/
        │   └── DashboardView.vue
        ├── user/
        │   ├── UserListView.vue
        │   ├── UserFormDialog.vue
        │   └── RoleAssignDialog.vue
        ├── category/
        │   ├── CategoryListView.vue
        │   └── CategoryFormDialog.vue
        └── product/
            ├── ProductListView.vue
            └── ProductFormDialog.vue
```

## 三、页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/login.html` | 登录页 | 独立入口，无需路由 |
| `/` | 仪表盘 | 统计卡片 + 快捷入口 |
| `/users` | 用户管理 | 表格 + 搜索 + CRUD 弹窗 |
| `/categories` | 分类管理 | 树形表格 + CRUD 弹窗 |
| `/products` | 商品管理 | 表格 + 搜索 + 图片上传 + CRUD 弹窗 |

路由守卫：未登录自动跳转 `login.html`。

## 四、页面布局

### 整体布局（AdminLayout）

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏  │              顶部导航栏                     │
│          ├────────────────────────────────────────────┤
│  📊 仪表盘 │                                            │
│  👤 用户   │              <router-view />               │
│  📁 分类   │                                            │
│  📦 商品   │                                            │
│          │                                            │
│          │                                            │
└──────────┴────────────────────────────────────────────┘
```

左侧深色侧边栏（宽度 220px），菜单项使用 Element Plus `el-menu` 的 router 模式自动激活。顶部栏显示当前用户昵称 + 下拉菜单（个人中心 / 退出登录）。

---

### 4.1 登录页

```
┌──────────────────────────────────────┐
│                                      │
│           🛒 购物平台管理系统          │
│                                      │
│   ┌──────────────────────────────┐   │
│   │  用户名                       │   │
│   │  ┌──────────────────────────┐│   │
│   │  │                          ││   │
│   │  └──────────────────────────┘│   │
│   │  密码                         │   │
│   │  ┌──────────────────────────┐│   │
│   │  │ ●●●●●●●●                 ││   │
│   │  └──────────────────────────┘│   │
│   │                              │   │
│   │  [        登  录        ]    │   │
│   └──────────────────────────────┘   │
│                                      │
└──────────────────────────────────────┘
```

**交互逻辑**：
1. 输入用户名、密码，前端 `@NotBlank` 校验不为空
2. 调用 `POST /api/auth/login`，成功后存 token 到 localStorage，跳转 `index.html`
3. 失败弹错误提示（用户名或密码错误 / 账户已禁用）

---

### 4.2 仪表盘

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  欢迎回来，系统管理员          [退出登录]   │
│          ├────────────────────────────────────────────┤
│          │                                            │
│          │  ┌────────┐ ┌────────┐ ┌────────┐ ┌─────┐ │
│          │  │ 👤 用户 │ │ 📦 商品 │ │ ✅ 上架 │ │ 📁分 │ │
│          │  │   12   │ │  156   │ │  120   │ │  8  │ │
│          │  └────────┘ └────────┘ └────────┘ └─────┘ │
│          │                                            │
│          │  快捷操作                                   │
│          │  [＋新增用户]  [＋新增商品]  [📁管理分类]    │
│          │                                            │
└──────────┴────────────────────────────────────────────┘
```

统计卡片调用 `UserService.count()` 和 `ProductService.count()` 获取实时数据。

---

### 4.3 用户管理

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  👤 用户管理                                │
│          ├────────────────────────────────────────────┤
│          │  搜索：[___________]  状态：[全部 ▾]        │
│          │  [🔍 搜索]  [🔄 重置]         [＋ 新增用户] │
│          │                                            │
│          │  ┌───────────────────────────────────────┐ │
│          │  │ID│用户名│昵称│邮箱│电话│状态│创建时间│操作│ │
│          │  │1 │admin │系统│a@..│138 │✅  │01-01  │✏️🗑️🔑│ │
│          │  │2 │user  │普通│u@..│139 │❌  │01-02  │✏️🗑️🔑│ │
│          │  └───────────────────────────────────────┘ │
│          │  ◀ 1  2  3 ... 10 ▶  共 50 条  10条/页    │
└──────────┴────────────────────────────────────────────┘
```

**操作列按钮**：
- ✏️ 编辑 → 弹出编辑弹窗（不含用户名和密码）
- 🗑️ 删除 → `ElMessageBox.confirm` 确认后删除（不能删除自己）
- 🔓/🔒 启用/禁用 → 行内切换（不能禁用自己）
- 🔑 分配角色 → 弹出复选框弹窗，勾选角色保存

**新增用户弹窗**：
```
┌── 新增用户 ──────────────────┐
│ 用户名 *  [              ]   │
│ 密码 *    [              ]   │
│ 昵称      [              ]   │
│ 邮箱      [              ]   │
│ 手机号    [              ]   │
│ 性别      ○ 未知  ● 男  ○ 女│
│ 生日      [📅 选择日期   ]   │
│ 状态      [● 启用]           │
│                              │
│         [取消]  [确定]       │
└──────────────────────────────┘
```

**编辑用户弹窗**：去掉用户名和密码字段，其余相同。

**分配角色弹窗**：
```
┌── 分配角色 ──────────────────┐
│ ☑ 系统管理员                 │
│ ☑ 普通用户                   │
│ ☐ 商家                       │
│                              │
│         [取消]  [保存]       │
└──────────────────────────────┘
```
角色列表从 RoleService 获取，默认勾选用户当前已有角色。

---

### 4.4 分类管理

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  📁 分类管理                                │
│          ├────────────────────────────────────────────┤
│          │                        [＋ 新增分类]        │
│          │                                            │
│          │  ┌────────────────────────────────────┐    │
│          │  │ 名称          │层级│排序│状态│操作  │    │
│          │  │ ▶ 电子产品    │一级│ 1  │✅  │✏️🗑️  │    │
│          │  │   📱 手机     │二级│ 1  │✅  │✏️🗑️  │    │
│          │  │   💻 电脑     │二级│ 2  │❌  │✏️🗑️  │    │
│          │  │ ▶ 服装鞋帽    │一级│ 2  │✅  │✏️🗑️  │    │
│          │  │   👗 女装     │二级│ 1  │✅  │✏️🗑️  │    │
│          │  └────────────────────────────────────┘    │
└──────────┴────────────────────────────────────────────┘
```

使用 Element Plus 树形表格（`row-key="id"` + `tree-props`），前端将扁平列表转为树结构。

**删除保护**：后端检查有子分类时拒绝删除，前端捕获异常提示「请先删除子分类」。

**新增/编辑弹窗**：
```
┌── 新增分类 ──────────────────┐
│ 名称 *    [              ]   │
│ 父分类    [无（一级分类）▾]  │
│ 排序      [  0  ]            │
│ 图标      [🖼 点击上传]      │
│ 状态      [● 启用]           │
│                              │
│         [取消]  [确定]       │
└──────────────────────────────┘
```

父分类下拉使用 `el-tree-select`，数据源为 `GET /api/categories/tree`。选择"无"表示创建一级分类。

---

### 4.5 商品管理

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  📦 商品管理                                │
│          ├────────────────────────────────────────────┤
│          │  搜索：[_______]  分类：[全部 ▾]            │
│          │  价格：[___] ~ [____]  状态：[全部 ▾]       │
│          │             排序：[默认 ▾]                  │
│          │  [🔍 搜索]  [🔄 重置]         [＋ 新增商品] │
│          │                                            │
│          │  ┌──────────────────────────────────────┐  │
│          │  │图片│名称│分类│价格  │库存│销量│状态│操作│  │
│          │  │🖼  │手机│电子│¥6999│100 │50  │✅  │✏️🗑️│  │
│          │  │🖼  │T恤 │服装│¥99  │200 │120 │✅  │✏️🗑️│  │
│          │  │🖼  │球鞋│鞋类│¥599 │50  │30  │❌  │✏️🗑️│  │
│          │  └──────────────────────────────────────┘  │
│          │  ◀ 1  2  3 ... 10 ▶  共 156 条  10条/页   │
└──────────┴────────────────────────────────────────────┘
```

**搜索栏说明**：
- 关键字：模糊匹配商品名称
- 分类：`el-tree-select` 从 `/api/categories/tree` 加载，选择后传 `categoryId`
- 价格区间：两个 `el-input-number`，单位 ¥
- 排序：下拉选择（默认/价格升序/价格降序/销量），映射 `sortField` + `asc`

**操作列**：编辑、删除、上架/下架切换。

**新增/编辑商品弹窗**：
```
┌── 新增商品 ──────────────────┐
│ 商品名称 *    [          ]   │
│ 所属分类 *    [请选择 ▾]    │
│                              │
│ 主图          ┌──────────┐   │
│               │  🖼 上传  │   │
│               └──────────┘   │
│ 副图          ┌──┐┌──┐┌──┐  │
│               │🖼││🖼││＋│  │
│               └──┘└──┘└──┘  │
│                              │
│ 售价 *    ¥ [  0.00     ]   │
│ 原价      ¥ [  0.00     ]   │
│ 库存 *      [  0         ]   │
│ 单位        [  件        ]   │
│ 排序        [  0         ]   │
│ 描述        [               ]│
│ 上架        [● 是  ○ 否]    │
│                              │
│         [取消]  [确定]       │
└──────────────────────────────┘
```

**图片上传**：调用 `POST /api/upload/image`（multipart/form-data），上传前校验文件类型（jpg/png/gif/webp）和大小（≤10MB）。上传成功返回 URL 填入表单。副图支持多选和拖拽排序。

**编辑模式**：表单预填商品详情（`GET /api/admin/products/{id}`），副图 JSON 反序列化为列表展示。

---

## 五、Axios 封装

### 请求拦截器
```
每个请求自动附加 → Authorization: Bearer {token}
```

### 响应拦截器
```
code = 200 → 返回 res.data 给调用方
code ≠ 200 → ElMessage.error(res.message)
code = 401/403 → 清除 token → window.location = '/login.html'
网络错误 → ElMessage.error('网络错误')
```

### API 调用示例

```js
// 获取用户列表
const res = await getUserPage({ pageNum: 1, pageSize: 10, keyword: 'admin' })
// res.data.records  → 用户数组
// res.data.total     → 总数

// 创建用户
await createUser({ username: 'test', password: '123456', nickname: '测试' })

// 上传图片
const res = await uploadImage(file)  // file 是 File 对象
// res.data.url → /uploads/2026/07/02/xxx.png
```

## 六、usePage 组合式函数

所有列表页共享同一模式，一行代码封装全部列表逻辑：

```js
const {
  loading,      // 加载状态
  list,         // 当前页数据
  total,        // 总条数
  query,        // 响应式查询参数 { pageNum, pageSize, keyword, ... }
  loadData,     // 加载数据
  handleSearch, // 搜索（重置到第一页）
  handleReset,  // 重置条件
  handlePageChange,    // 翻页
  handleSizeChange,    // 切换每页条数
  handleDelete,        // 删除（含确认弹窗）
  handleToggleStatus,  // 状态切换
} = usePage({
  fetchFn:  getUserPage,     // 列表 API
  deleteFn: deleteUser,      // 删除 API
  statusFn: setUserStatus,   // 状态切换 API
})
```

封装内容：
- `loading` 状态自动管理
- `ElMessageBox.confirm` 删除确认
- `ElMessage.success/error` 操作提示
- 删除/状态切换后自动刷新列表

## 七、状态管理

```
Pinia auth Store
├── state
│   ├── token         ← localStorage 读取
│   └── userInfo      ← { id, username, nickname, roles[] }
├── getters
│   ├── isLoggedIn    ← !!token
│   └── isAdmin       ← roles.includes('ROLE_ADMIN')
└── actions
    ├── login(form)   → 调 API → 存 token + userInfo → 跳转
    └── logout()      → 清 token + userInfo → 跳转登录页
```

`userInfo` 在登录成功后从 API 返回的 `LoginResponse.user` 获取，存入 localStorage 以支持页面刷新恢复。

## 八、Vite 配置

```js
// vite.config.js
export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      input: {
        index: resolve(__dirname, 'index.html'),
        login: resolve(__dirname, 'login.html'),
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api':     { target: 'http://localhost:8080', changeOrigin: true },
      '/uploads': { target: 'http://localhost:8080', changeOrigin: true },
    }
  }
})
```

开发时访问 `http://localhost:3000/login.html`，API 请求自动代理到后端 `localhost:8080`，无需跨域配置。

## 九、文件清单

| 分类 | 文件数 | 文件 |
|------|--------|------|
| 入口 | 3 | login.html, index.html, package.json |
| 配置 | 1 | vite.config.js |
| API | 6 | request.js, auth.js, user.js, category.js, product.js, upload.js |
| 路由 | 1 | router/index.js |
| 状态 | 1 | stores/auth.js |
| 工具 | 2 | utils/token.js, composables/usePage.js |
| 布局 | 1 | layouts/AdminLayout.vue |
| 组件 | 2 | ImageUpload.vue, StatusTag.vue |
| 页面 | 10 | 5 个列表页 + 4 个弹窗 + 1 个仪表盘 |
| **合计** | **27** | |

## 十、实施步骤

| 步骤 | 内容 | 产出 |
|------|------|------|
| 1 | 脚手架搭建 | `npm create vite` + 安装 `vue-router` `pinia` `axios` `element-plus` |
| 2 | 登录页 | LoginView + auth API + token 工具 + Pinia store |
| 3 | 管理后台壳 | AdminLayout + Sidebar + Router + beforeEach 守卫 |
| 4 | 用户管理 | UserListView + UserFormDialog + RoleAssignDialog + usePage |
| 5 | 分类管理 | CategoryListView + CategoryFormDialog（树形表格） |
| 6 | 商品管理 | ProductListView + ProductFormDialog + ImageUpload |
| 7 | 仪表盘 | 统计卡片 + 快捷入口 |
| 8 | 联调测试 | 启动后端 → 登录 → 各模块 CRUD → 权限验证 |
