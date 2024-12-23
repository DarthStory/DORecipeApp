package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseConnect(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "recipeApp.db"
        private const val DATABASE_VERSION = 3

        // Users Table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // Recipes Table
        private const val TABLE_RECIPE = "recipes"
        private const val COLUMN_RECIPE_ID = "id"
        private const val COLUMN_RECIPE_NAME = "name"
        private const val COLUMN_INSTRUCTIONS = "instructions"

        // Ingredients Table
        private const val TABLE_INGREDIENTS = "recipe_ingredients"
        private const val COLUMN_INGREDIENT_ID = "id"
        private const val COLUMN_RECIPE_ID_FK = "recipe_id"
        private const val COLUMN_INGREDIENT_NAME = "ingredient_name"
        private const val COLUMN_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """.trimIndent()

        val createRecipesTable = """
            CREATE TABLE $TABLE_RECIPE (
                $COLUMN_RECIPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_NAME TEXT,
                $COLUMN_INSTRUCTIONS TEXT
            )
        """.trimIndent()

        val createIngredientsTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_INGREDIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_ID_FK INTEGER,
                $COLUMN_INGREDIENT_NAME TEXT,
                $COLUMN_AMOUNT TEXT,
                FOREIGN KEY ($COLUMN_RECIPE_ID_FK) REFERENCES $TABLE_RECIPE($COLUMN_RECIPE_ID)
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createRecipesTable)
        db?.execSQL(createIngredientsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        onCreate(db)
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }

    fun addRecipe(recipe: Recipe): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipe.name)
            put(COLUMN_INSTRUCTIONS, recipe.instructions)
        }
        val recipeId = db.insert(TABLE_RECIPE, null, contentValues)
        db.close()

        // Return ID as Int (if insert fails, it will return -1, so handle accordingly)
        return recipeId.toInt()
    }

    fun addIngredients(recipeId: Int, ingredients: List<Ingredient>) {
        val db = this.writableDatabase
        ingredients.forEach { ingredient ->
            val contentValues = ContentValues().apply {
                put(COLUMN_RECIPE_ID_FK, recipeId)
                put(COLUMN_INGREDIENT_NAME, ingredient.name)
                put(COLUMN_AMOUNT, ingredient.amount)
            }
            db.insert(TABLE_INGREDIENTS, null, contentValues)
        }
        db.close()
    }

    fun getRecipeWithIngredients(recipeId: Int): Pair<Recipe, List<Ingredient>>? {
        val db = this.readableDatabase

        // Fetch Recipe
        Log.d("DatabaseConnect", "Fetching recipe with ID: $recipeId")
        val recipeCursor = db.query(
            TABLE_RECIPE,
            null,
            "$COLUMN_RECIPE_ID = ?",
            arrayOf(recipeId.toString()),
            null, null, null
        )
        var recipe: Recipe? = null
        if (recipeCursor.moveToFirst()) {
            recipe = Recipe(
                id = recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                name = recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                instructions = recipeCursor.getString(
                    recipeCursor.getColumnIndexOrThrow(
                        COLUMN_INSTRUCTIONS
                    )
                )
            )
            Log.d("DatabaseConnect", "Fetched Recipe: $recipe")
        }
        recipeCursor.close()

        if (recipe == null) {
            Log.d("DatabaseConnect", "No recipe found with ID: $recipeId")
            db.close()
            return null
        }

        // Fetch Ingredients
        Log.d("DatabaseConnect", "Fetching ingredients for recipe ID: $recipeId")
        val ingredientsCursor = db.query(
            TABLE_INGREDIENTS,
            null,
            "$COLUMN_RECIPE_ID_FK = ?",
            arrayOf(recipeId.toString()),
            null, null, null
        )
        val ingredients = mutableListOf<Ingredient>()

        try {
            while (ingredientsCursor.moveToNext()) {
                val name = ingredientsCursor.getString(
                    ingredientsCursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME)
                ) ?: "Unknown"
                val amount = ingredientsCursor.getString(
                    ingredientsCursor.getColumnIndexOrThrow(COLUMN_AMOUNT)
                ) ?: "Unknown"
                val ingredient = Ingredient(name, amount)
                ingredients.add(ingredient)
                Log.d("DatabaseConnect", "Fetched Ingredient: $ingredient")
            }
        } catch (e: Exception) {
            Log.e("DatabaseConnect", "Error fetching ingredients: ${e.message}")
        } finally {
            ingredientsCursor.close()
            db.close()
        }

        return Pair(recipe, ingredients)
    }


        fun getAllRecipes(): List<Recipe> {
        val db = this.readableDatabase
        val recipeList = mutableListOf<Recipe>()

        val cursor = db.query(
            TABLE_RECIPE,
            arrayOf(COLUMN_RECIPE_ID, COLUMN_RECIPE_NAME, COLUMN_INSTRUCTIONS),
            null, null, null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val recipe = Recipe(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                    instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))
                )
                recipeList.add(recipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return recipeList
    }

    fun searchRecipes(query: String): List<Recipe> {
        val db = this.readableDatabase
        val recipes = mutableListOf<Recipe>()

        val searchQuery = """
        SELECT DISTINCT $TABLE_RECIPE.$COLUMN_RECIPE_ID, $TABLE_RECIPE.$COLUMN_RECIPE_NAME, $TABLE_RECIPE.$COLUMN_INSTRUCTIONS
        FROM $TABLE_RECIPE
        LEFT JOIN $TABLE_INGREDIENTS
        ON $TABLE_RECIPE.$COLUMN_RECIPE_ID = $TABLE_INGREDIENTS.$COLUMN_RECIPE_ID_FK
        WHERE $TABLE_RECIPE.$COLUMN_RECIPE_NAME LIKE ? OR $TABLE_INGREDIENTS.$COLUMN_INGREDIENT_NAME LIKE ?
    """
        val cursor = db.rawQuery(searchQuery, arrayOf("%$query%", "%$query%"))

        while (cursor.moveToNext()) {
            val recipe = Recipe(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))
            )
            recipes.add(recipe)
        }
        cursor.close()
        db.close()

        return recipes
    }

    fun deleteRecipe(recipeId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_RECIPE, "$COLUMN_RECIPE_ID = ?", arrayOf(recipeId.toString()))
        db.delete(TABLE_INGREDIENTS, "$COLUMN_RECIPE_ID_FK = ?", arrayOf(recipeId.toString())) // Cascade delete ingredients
        db.close()
    }

    fun updateRecipe(recipe: Recipe) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipe.name)
            put(COLUMN_INSTRUCTIONS, recipe.instructions)
        }
        db.update(TABLE_RECIPE, contentValues, "$COLUMN_RECIPE_ID = ?", arrayOf(recipe.id.toString()))
        db.close()
    }

    fun updateIngredients(recipeId: Int, ingredients: List<Ingredient>) {
        val db = writableDatabase

        // Delete old ingredients
        db.delete(TABLE_INGREDIENTS, "$COLUMN_RECIPE_ID_FK = ?", arrayOf(recipeId.toString()))

        // Add updated ingredients
        ingredients.forEach { ingredient ->
            val values = ContentValues().apply {
                put(COLUMN_RECIPE_ID_FK, recipeId)
                put(COLUMN_INGREDIENT_NAME, ingredient.name)
                put(COLUMN_AMOUNT, ingredient.amount)
            }
            db.insert(TABLE_INGREDIENTS, null, values)
        }
        db.close()
    }

}
