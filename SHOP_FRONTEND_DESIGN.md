# 普通用户购物页面设计

## 一、概述

为非 Admin 角色（ROLE_USER）设计商品浏览购买和购物车功能。后端复用已有公开接口，购物车数据存数据库。前端新增 2 个页面，在侧边栏对普通用户显示。

## 二、后端新增

### 2.1 购物车表

```sql
CREATE TABLE `cart` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id`     BIGINT   NOT NULL               COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL               COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1     COMMENT '数量',
    `checked`     TINYINT  NOT NULL DEFAULT 1     COMMENT '是否选中: 0=未选, 1=选中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
```

### 2.2 购物车 API（需登录）

| 方法 | URL | 说明 |
|------|-----|------|
| GET | `/api/cart` | 我的购物车列表（含商品名/价格/主图） |
| POST | `/api/cart` | 加入购物车 `{productId, quantity}` |
| PUT | `/api/cart/{id}` | 更新数量 `{quantity}` |
| PUT | `/api/cart/{id}/check` | 勾选/取消 `{checked: 0\|1}` |
| DELETE | `/api/cart/{id}` | 移除商品 |
| DELETE | `/api/cart/clear` | 清空购物车 |

### 2.3 新建后端文件

| 文件 | 说明 |
|------|------|
| `entity/Cart.java` | 购物车实体 |
| `mapper/CartMapper.java` | extends BaseMapper<Cart> |
| `service/CartService.java` + `impl/CartServiceImpl.java` | 加入购物车（同商品则加数量）、我的购物车（JOIN product 表） |
| `controller/CartController.java` | 6 个接口 |
| `model/vo/CartVO.java` | 购物车视图（含 productName/price/mainImage） |

### 2.4 已有可复用接口

| 接口 | 权限 | 用途 |
|------|------|------|
| `GET /api/products` | 公开 | 商品列表（搜索/分类/价格/排序/分页） |
| `GET /api/products/{id}` | 公开 | 商品详情 |
| `GET /api/categories/tree` | 公开 | 分类筛选 |

## 三、前端新增

### 3.1 页面路由

| 路径 | 页面 | 权限 |
|------|------|------|
| `/shop` | 商品浏览 | 所有登录用户 |
| `/cart` | 购物车 | 所有登录用户 |

### 3.2 页面 1：商品浏览页

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  🛍 商品浏览                                │
│          ├────────────────────────────────────────────┤
│          │  [🔍 搜索商品...]  分类：[全部 ▾]           │
│          │  价格：¥[___]~[___]  排序：[默认 ▾]         │
│          │                                            │
│          │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐     │
│          │  │ 📦   │ │ 📦   │ │ 📦   │ │ 📦   │     │
│          │  │ 商品名 │ │ 商品名 │ │ 商品名 │ │ 商品名 │     │
│          │  │ ¥99   │ │ ¥199  │ │ ¥599  │ │ ¥79   │     │
│          │  │ [加购] │ │ [加购] │ │ [加购] │ │ [加购] │     │
│          │  └──────┘ └──────┘ └──────┘ └──────┘     │
│          │                                            │
│          │  ◀ 1  2  3 ▶  共 156 件                   │
└──────────┴────────────────────────────────────────────┘
```

卡片式展示，每行 4 列。每张卡片显示：商品图、商品名、价格、分类名、[加入购物车]按钮。

**交互**：
- 点击商品图/名称 → 弹出详情弹窗（含描述、副图）
- 搜索栏实时筛选（分类树、价格区间、排序）
- 加购按钮 → `POST /api/cart` → ElMessage.success('已加入购物车')

### 3.3 页面 2：购物车页

```
┌──────────┬────────────────────────────────────────────┐
│  侧边栏   │  🛒 购物车                                  │
│          ├────────────────────────────────────────────┤
│          │  [全选]                    [清空购物车]     │
│          │                                            │
│          │  ┌──────────────────────────────────────┐  │
│          │  │ ☑ │ 📦 图 │ 商品名 │ ¥99 │ [-] 1 [+]│  │
│          │  │   │       │        │      │    🗑    │  │
│          │  ├──────────────────────────────────────┤  │
│          │  │ ☑ │ 📦 图 │ 商品名2│¥199 │ [-] 2 [+]│  │
│          │  │   │       │        │      │    🗑    │  │
│          │  └──────────────────────────────────────┘  │
│          │                                            │
│          │  ─────────────────────────────────────     │
│          │  已选 2 件，合计：¥497.00    [去结算]       │
└──────────┴────────────────────────────────────────────┘
```

表格展示，每行：勾选框、商品缩略图、名称、单价、数量增减器、删除按钮。

**底部栏**：全选框、已选数量、合计金额（仅计算已勾选）。

## 四、前端文件清单

| 文件 | 说明 |
|------|------|
| `src/api/cart.js` | 购物车 API 封装 |
| `src/views/shop/ShopView.vue` | 商品浏览页 |
| `src/views/cart/CartView.vue` | 购物车页 |
| `src/router/index.js` | 加 `/shop` `/cart` 路由 |
| `src/layouts/AdminLayout.vue` | 侧边栏加菜单（对所有登录用户可见） |

## 五、侧边栏调整

普通用户（非 Admin）看到的菜单：
```
仪表盘
🛍 商品浏览    ← 新增
🛒 购物车      ← 新增
```

Admin 用户照旧看到完整管理菜单。

## 六、实施顺序

1. 数据库建 `cart` 表
2. 后端 Cart 完整链路：entity → mapper → service → controller → VO
3. 前端 cart.js API 封装
4. 前端 ShopView 商品浏览页
5. 前端 CartView 购物车页
6. 更新路由和侧边栏
