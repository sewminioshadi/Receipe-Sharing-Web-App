Here's a well-structured document based on your output, arranged for clarity and professional presentation:

---

# Recipe Sharing App — Domain Model & Database Design (V1)

## Table of Contents
1. [Design Principles](#design-principles)
2. [Entity Relationship Overview](#entity-relationship-overview)
3. [Entity Definitions](#entity-definitions)
4. [Enums](#enums)
5. [Normalization Decisions](#normalization-decisions)
6. [Future Entities (V2+)](#future-entities-v2)
7. [Package Structure](#package-structure)
8. [Implementation Checklist](#implementation-checklist)

---

## Design Principles

The following principles guide every decision in this design:

| Principle | Why It Matters |
|-----------|---------------|
| **3NF Normalization** | Eliminates data redundancy (e.g., Category is its own table, not duplicated per recipe) |
| **JPA Best Practices** | PascalCase entities, snake_case tables/columns, FK columns named `{entity}_id` |
| **Separation of Concerns** | Each entity has a single responsibility — no "god tables" |
| **Audit Fields Everywhere** | `createdAt` / `updatedAt` on all user-interactive entities for traceability |
| **Cascade Strategy** | Only `ALL` + `orphanRemoval` where deletion of parent must delete children (Recipe → Ingredient). Social entities (Comment, Rating, Favorite, Like) are intentionally not cascaded — deleting a user shouldn't silently erase community content |
| **Lazy Fetching** | All `@ManyToOne` relationships use `FetchType.LAZY` to avoid N+1 queries |

---

## Entity Relationship Overview

```
┌──────────┐       ┌──────────┐       ┌────────────┐
│   User   │──────<│  Recipe  │>──────│  Category  │
└────┬─────┘       └────┬─────┘       └────────────┘
     │                  │
     │           ┌──────┴──────┐
     │           │  Ingredient │
     │           └─────────────┘
     │
     ├──────< Comment >────── Recipe
     ├──────< Rating  >────── Recipe
     ├──────< Favorite>────── Recipe
     └──────< Like    >────── Recipe
```

**8 entities total for V1.**

---

## Entity Definitions

### 1. User

| Aspect | Detail |
|--------|--------|
| **Purpose** | Store authentication credentials and profile information |
| **Table** | `users` |
| **Why separate from Recipe?** | A user is an independent domain concept. Recipes reference users, not the other way around |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `username` | `String` | `@NotBlank`, unique |
| `email` | `String` | `@NotBlank`, `@Email`, unique |
| `password` | `String` | `@NotBlank` |
| `firstName` | `String` | nullable |
| `lastName` | `String` | nullable |
| `profilePictureUrl` | `String` | nullable |
| `role` | `Enum(Role)` | `@Enumerated(STRING)` |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `updatedAt` | `LocalDateTime` | `@LastModifiedDate` |

#### Relationships

| Target | Type | mappedBy / joinCol |
|--------|------|-------------------|
| Recipe | `OneToMany` | `mappedBy = "user"` |
| Comment | `OneToMany` | `mappedBy = "user"` |
| Rating | `OneToMany` | `mappedBy = "user"` |
| Favorite | `OneToMany` | `mappedBy = "user"` |
| Like | `OneToMany` | `mappedBy = "user"` |

> **Design decision:** No cascade on User → social entities. If a user is deleted, their comments/ratings should be anonymized or soft-deleted, not silently removed. This is handled at the service layer.

---

### 2. Recipe

| Aspect | Detail |
|--------|--------|
| **Purpose** | Core domain object — stores all recipe information |
| **Table** | `recipes` |
| **Why the heart of the model?** | Every other entity (except User/Category) connects to Recipe |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `title` | `String` | `@NotBlank` |
| `description` | `String` | `@Lob (TEXT)` |
| `instructions` | `String` | `@Lob (TEXT)` |
| `cookingTime` | `Integer` | nullable |
| `difficulty` | `Enum(Difficulty)` | `@Enumerated(STRING)`, nullable |
| `servings` | `Integer` | nullable |
| `imageUrl` | `String` | nullable |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `updatedAt` | `LocalDateTime` | `@LastModifiedDate` |

#### Relationships

| Target | Type | Join/MappedBy | Cascade |
|--------|------|--------------|---------|
| User | `ManyToOne` | `@JoinColumn(name = "user_id")` | none |
| Category | `ManyToOne` | `@JoinColumn(name = "category_id")` | none |
| Ingredient | `OneToMany` | `mappedBy = "recipe"` | ALL + orphanRemoval |
| Comment | `OneToMany` | `mappedBy = "recipe"` | none |
| Rating | `OneToMany` | `mappedBy = "recipe"` | none |
| Favorite | `OneToMany` | `mappedBy = "recipe"` | none |
| Like | `OneToMany` | `mappedBy = "recipe"` | none |

> **Why Difficulty is nullable:** Not every recipe needs a difficulty level. It's optional metadata.

---

### 3. Ingredient

| Aspect | Detail |
|--------|--------|
| **Purpose** | Store individual ingredients belonging to a recipe |
| **Table** | `ingredients` |
| **Why separate entity (not @Lob)?** | Individual ingredients can be searched, filtered, and counted. A text blob can't. |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `name` | `String` | `@NotBlank` |
| `amount` | `String` | nullable |
| `unit` | `String` | nullable |
| `recipe` | `Recipe` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "recipe_id")` |

#### Relationships

| Target | Type | Join/MappedBy |
|--------|------|--------------|
| Recipe | `ManyToOne` | `@JoinColumn(name = "recipe_id")` |

> **Design decision:** `amount` is `String` not `Double` because recipes often say "1/2" or "a pinch" — forcing numeric parsing adds complexity with no real benefit in V1.

---

### 4. Category

| Aspect | Detail |
|--------|--------|
| **Purpose** | Classify recipes into browsable groups |
| **Table** | `categories` |
| **Why separate from Recipe?** | Avoids duplicating category strings across thousands of recipes; enables search/filter by category |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `name` | `String` | `@NotBlank`, unique |
| `description` | `String` | nullable |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |

#### Relationships

| Target | Type | Join/MappedBy |
|--------|------|--------------|
| Recipe | `OneToMany` | `mappedBy = "category"` |

> **Why not hierarchical (parent-child)?** V1 keeps categories flat. Future versions could add a `parentId` self-reference if subcategories are needed.

---

### 5. Comment

| Aspect | Detail |
|--------|--------|
| **Purpose** | Allow users to discuss recipes |
| **Table** | `comments` |
| **Why a separate entity?** | Comments have their own lifecycle (edit, delete) and are user-generated content |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `content` | `String` | `@NotBlank`, `@Lob (TEXT)` |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `updatedAt` | `LocalDateTime` | `@LastModifiedDate` |
| `user` | `User` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "user_id")` |
| `recipe` | `Recipe` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "recipe_id")` |

#### Relationships

| Target | Type | Join/MappedBy |
|--------|------|--------------|
| User | `ManyToOne` | `@JoinColumn(name = "user_id")` |
| Recipe | `ManyToOne` | `@JoinColumn(name = "recipe_id")` |

> **No cascade:** Deleting a user or recipe should not silently destroy comments. Handle in service layer with soft-delete or anonymization.

---

### 6. Rating

| Aspect | Detail |
|--------|--------|
| **Purpose** | Allow users to rate recipes numerically |
| **Table** | `ratings` |
| **Why a separate entity?** | Ratings are distinct from comments; they contribute to an aggregate score |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `score` | `Integer` | `@NotNull`, `@Min(1)`, `@Max(5)` |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `updatedAt` | `LocalDateTime` | `@LastModifiedDate` |
| `user` | `User` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "user_id")` |
| `recipe` | `Recipe` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "recipe_id")` |

#### Constraints

| Constraint | Columns |
|------------|---------|
| Unique | `(user_id, recipe_id)` |

#### Relationships

| Target | Type | Join/MappedBy |
|--------|------|--------------|
| User | `ManyToOne` | `@JoinColumn(name = "user_id")` |
| Recipe | `ManyToOne` | `@JoinColumn(name = "recipe_id")` |

> **Design decision:** Average rating can be computed via a JPA query (`AVG(score)`) rather than stored denormalized on Recipe. This avoids update anomalies. If performance becomes an issue, add an `averageRating` field on Recipe and update it via trigger or service logic.

---

### 7. Favorite

| Aspect | Detail |
|--------|--------|
| **Purpose** | Allow users to bookmark recipes for later |
| **Table** | `favorites` |
| **Why a separate entity (not just a column)?** | A join table with timestamp enables "recently favorited" sorting |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `user` | `User` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "user_id")` |
| `recipe` | `Recipe` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "recipe_id")` |

#### Constraints

| Constraint | Columns |
|------------|---------|
| Unique | `(user_id, recipe_id)` |

---

### 8. Like

| Aspect | Detail |
|--------|--------|
| **Purpose** | Allow users to express appreciation for recipes (social signal) |
| **Table** | `likes` |
| **Why separate from Favorite?** | Different semantics: Favorites are private bookmarks; Likes are public signals that can drive recommendations later |

#### Attributes

| Field | Type | Constraints |
|-------|------|-------------|
| `id` | `Long` | PK, auto-generated |
| `createdAt` | `LocalDateTime` | `@CreatedDate`, not null, not updatable |
| `user` | `User` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "user_id")` |
| `recipe` | `Recipe` | `@ManyToOne(LAZY)`, `@JoinColumn(name = "recipe_id")` |

#### Constraints

| Constraint | Columns |
|------------|---------|
| Unique | `(user_id, recipe_id)` |

---

## Enums

*To be created in `com.application.recipesharing.enums` package*

### Role

| Value | Meaning |
|-------|---------|
| `USER` | Regular user — can CRUD own recipes, comment, rate, favorite, like |
| `ADMIN` | Admin — can manage categories, users, reports (future) |

### Difficulty

| Value | Meaning |
|-------|---------|
| `EASY` | Beginner-friendly |
| `MEDIUM` | Some experience needed |
| `HARD` | Advanced cooking skills |

---

## Normalization Decisions

| Decision | Reasoning |
|----------|-----------|
| **Ingredients as separate table** | 1NF: eliminates repeating groups. A recipe with 10 ingredients shouldn't store them in a delimited string |
| **Category as separate table** | 2NF/3NF: category name depends only on `category_id`, not `recipe_id`. Avoids string duplication |
| **Favorite/Like as tables (not columns)** | Follows junction table pattern for ManyToMany with extra metadata (timestamp) |
| **Rating as table** | Same as above, plus the score attribute is per-user-per-recipe |
| **No `averageRating` on Recipe** | 3NF: derived data stored as base data causes update anomalies. Compute on read |

---

## Future Entities (V2+)

| Entity | Purpose |
|--------|---------|
| `RecipeImage` | Multiple images per recipe (gallery) |
| `Tag` | ManyToMany with Recipe — more flexible than Category |
| `Notification` | In-app notifications (someone commented, etc.) |
| `Report` | Report inappropriate content |
| `RecipeView` | Track recipe view counts / analytics |
| `Difficulty` (as entity) | If difficulty needs a description or icon |

---

## Package Structure (Recommended)

```
com.application.recipesharing/
├── config/
│   └── JpaConfig.java              (@EnableJpaAuditing)
├── entity/
│   ├── User.java
│   ├── Recipe.java
│   ├── Ingredient.java
│   ├── Category.java
│   ├── Comment.java
│   ├── Rating.java
│   ├── Favorite.java
│   └── Like.java
├── enums/
│   ├── Role.java
│   └── Difficulty.java
├── repository/                      (later)
├── service/                         (later)
├── controller/                      (later)
├── dto/                             (later)
├── security/                        (later)
└── exception/                       (later)
```

---

## Implementation Checklist

### Summary of file count for V1 entities

| Action | Files |
|--------|-------|
| Modify | `entity/recipe.java` → `Recipe.java` (rename + rewrite) |
| Create | `entity/User.java` |
| Create | `entity/Ingredient.java` |
| Create | `entity/Category.java` |
| Create | `entity/Comment.java` |
| Create | `entity/Rating.java` |
| Create | `entity/Favorite.java` |
| Create | `entity/Like.java` |
| Create | `enums/Role.java` |
| Create | `enums/Difficulty.java` |
| Create | `config/JpaConfig.java` |

**Total: 11 files** (1 modify + 10 create)

---

*Document generated from OpenCode agent conversation. Ready for implementation.*