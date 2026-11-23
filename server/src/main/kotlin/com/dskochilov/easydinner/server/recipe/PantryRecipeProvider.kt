package com.dskochilov.easydinner.server.recipe

import com.dskochilov.easydinner.shared.RecipeDto
import com.dskochilov.easydinner.shared.RecipeSearchRequestDto
import com.dskochilov.easydinner.shared.RecipeSearchResponseDto
import com.dskochilov.easydinner.shared.RecipeSourceDto

/** Searchable local catalogue for installations without a provider key. */
class PantryRecipeProvider : RecipeProvider {
    override suspend fun search(request: RecipeSearchRequestDto): RecipeSearchResponseDto {
        val pantry = request.ingredients.mapTo(mutableSetOf()) { raw ->
            val name = raw.trim().lowercase()
            ingredientAliases[name] ?: name
        }
        val servings = request.preferences.servings ?: 2
        val maxTime = request.preferences.maxReadyTimeMinutes ?: Int.MAX_VALUE
        val matches = dishes
            .filter { it.minutes <= maxTime && (!it.needsOven || request.preferences.useOven != false) }
            .mapNotNull { dish ->
                val (have, buy) = dish.ingredients.partition { it in pantry }
                if (have.isEmpty()) return@mapNotNull null
                RecipeDto(
                    id = dish.id, title = dish.title, readyInMinutes = dish.minutes,
                    servings = servings, usedIngredients = have, missedIngredients = buy,
                    allIngredients = dish.ingredients, summary = dish.summary,
                    instructions = dish.method(servings),
                )
            }
            .sortedWith(compareByDescending<RecipeDto> { it.usedIngredients.size }
                .thenBy { it.missedIngredients.size }
                .thenBy { it.readyInMinutes })
        // Preserve the source identifier understood by existing clients.
        return RecipeSearchResponseDto(recipes = matches, source = RecipeSourceDto.Mock)
    }
}

private data class Dish(
    val id: Int,
    val title: String,
    val minutes: Int,
    val ingredients: List<String>,
    val summary: String,
    val needsOven: Boolean = false,
    val method: (Int) -> String,
)

private val ingredientAliases = mapOf(
    "egg" to "eggs", "яйцо" to "eggs", "яйца" to "eggs",
    "tomato" to "tomatoes", "помидор" to "tomatoes", "помидоры" to "tomatoes",
    "шпинат" to "spinach", "сыр" to "cheese", "рис" to "rice",
    "горох" to "peas", "горошек" to "peas", "паста" to "pasta",
    "макароны" to "pasta", "фасоль" to "beans", "картофель" to "potatoes",
    "картошка" to "potatoes", "potato" to "potatoes",
)

// Negative IDs keep saved catalogue recipes distinct from external provider IDs.
private val dishes = listOf(
    Dish(-101, "Spinach and cheese omelette", 12, listOf("eggs", "spinach", "cheese"),
        "A folded omelette with wilted greens.") { n ->
        "Wilt spinach in a lightly oiled pan. Beat ${n * 2} eggs and pour over the greens. Cook until set, sprinkle with ${n * 20} g cheese and fold."
    },
    Dish(-102, "Tomato pasta", 20, listOf("pasta", "tomatoes", "cheese"),
        "Chopped tomatoes become a quick sauce while the pasta cooks.") { n ->
        "Boil ${n * 80} g dried pasta. Soften ${n * 2} chopped tomatoes in olive oil. Toss with the pasta, a splash of cooking water and ${n * 15} g grated cheese."
    },
    Dish(-103, "Pea and egg fried rice", 15, listOf("rice", "peas", "eggs"),
        "A one-pan dinner made with cooked rice.") { n ->
        "Scramble $n beaten egg(s) in a hot oiled pan. Add ${n * 50} g peas and ${n * 150} g chilled cooked rice. Stir until steaming hot throughout."
    },
    Dish(-104, "Tomato and bean skillet", 18, listOf("tomatoes", "beans", "spinach"),
        "A tomato skillet finished with beans and fresh greens.") { n ->
        "Soften ${n * 2} chopped tomatoes in a little oil. Add ${n * 120} g drained cooked beans and a splash of water. Simmer for 10 minutes, then wilt spinach in the sauce."
    },
    Dish(-105, "Cheesy potato bake", 45, listOf("potatoes", "cheese", "eggs"),
        "Thin slices baked under a golden cheese crust.", needsOven = true) { n ->
        "Heat the oven to 190 °C. Thinly slice ${n * 180} g potatoes and simmer for 8 minutes. Drain, layer with ${n * 30} g cheese and $n beaten egg(s), then bake until tender and set, about 30 minutes."
    },
)
