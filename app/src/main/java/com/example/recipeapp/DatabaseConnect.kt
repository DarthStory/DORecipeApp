package com.example.recipeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseConnect(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "recipeApp.db"
        private const val DATABASE_VERSION = 2

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
        // Create Recipes Table
        val createRecipesTable = """
            CREATE TABLE $TABLE_RECIPE (
                $COLUMN_RECIPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_NAME TEXT,
                $COLUMN_INSTRUCTIONS TEXT
            )
        """.trimIndent()

        // Create Ingredients Table
        val createIngredientsTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_INGREDIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RECIPE_ID_FK INTEGER,
                $COLUMN_INGREDIENT_NAME TEXT,
                $COLUMN_AMOUNT TEXT,
                FOREIGN KEY ($COLUMN_RECIPE_ID_FK) REFERENCES $TABLE_RECIPE($COLUMN_RECIPE_ID)
            )
        """.trimIndent()

        db?.execSQL(createRecipesTable)
        db?.execSQL(createIngredientsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        onCreate(db)
    }

    // Add Recipe
    fun addRecipe(recipe: Recipe): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipe.name)
            put(COLUMN_INSTRUCTIONS, recipe.instructions)
        }
        val recipeId = db.insert(TABLE_RECIPE, null, contentValues)
        db.close()
        return recipeId
    }

    // Add Ingredients for a Recipe
    fun addIngredients(recipeId: Long, ingredients: List<Ingredient>) {
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

    // Fetch Recipe with Ingredients
    fun getRecipeWithIngredients(recipeId: Long): Pair<Recipe, List<Ingredient>> {
        val db = this.readableDatabase

        // Fetch Recipe
        val recipeCursor = db.query(TABLE_RECIPE, null, "$COLUMN_RECIPE_ID = ?", arrayOf(recipeId.toString()), null, null, null)
        var recipe = Recipe("", "")
        if (recipeCursor.moveToFirst()) {
            recipe = Recipe(
                recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))
            )
        }
        recipeCursor.close()

        // Fetch Ingredients
        val ingredientsCursor = db.query(TABLE_INGREDIENTS, null, "$COLUMN_RECIPE_ID_FK = ?", arrayOf(recipeId.toString()), null, null, null)
        val ingredients = mutableListOf<Ingredient>()
        while (ingredientsCursor.moveToNext()) {
            ingredients.add(
                Ingredient(
                    ingredientsCursor.getString(ingredientsCursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME)),
                    ingredientsCursor.getString(ingredientsCursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                )
            )
        }
        ingredientsCursor.close()
        db.close()

        return Pair(recipe, ingredients)
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("username", user.username)
            put("password", user.password)
        }
        val result = db.insert("users", null, contentValues)
        db.close()
        return result
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM users WHERE username = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }


}
