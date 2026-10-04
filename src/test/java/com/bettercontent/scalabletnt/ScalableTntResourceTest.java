package com.bettercontent.scalabletnt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScalableTntResourceTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Path RESOURCES = Path.of("src/main/resources");

    @Test
    void shapelessRecipesUseOnlyTheTwoBoundedNineIngredientRatios() throws IOException {
        final JsonNode weak = JSON.readTree(RESOURCES.resolve("data/scalable_tnt/recipes/low_yield_tnt.json").toFile());
        final JsonNode strong = JSON.readTree(RESOURCES.resolve("data/scalable_tnt/recipes/high_yield_tnt.json").toFile());

        assertRecipe(weak, 6, 3, "scalable_tnt:low_yield_tnt");
        assertRecipe(strong, 3, 6, "scalable_tnt:high_yield_tnt");
    }

    @Test
    void explosionMixinIsPackagedForRuntimeGameTestVerification() throws IOException {
        final JsonNode mixins = JSON.readTree(RESOURCES.resolve("scalable_tnt.mixins.json").toFile());
        assertEquals(List.of("PrimedTntExplosionMixin"), readStrings(mixins.path("mixins")));
        assertEquals("JAVA_17", mixins.path("compatibilityLevel").asText());
        assertFalse(mixins.has("refmap"));
    }

    private static void assertRecipe(
            final JsonNode recipe,
            final int expectedSand,
            final int expectedPowder,
            final String output) {
        assertEquals("minecraft:crafting_shapeless", recipe.path("type").asText());
        final List<JsonNode> ingredients = new ArrayList<>();
        recipe.path("ingredients").forEach(ingredients::add);
        assertEquals(9, ingredients.size());
        assertEquals(expectedSand, ingredients.stream().filter(i -> i.path("tag").asText().equals("kubejs:ordinary_sand")).count());
        assertEquals(expectedPowder, ingredients.stream().filter(i -> i.path("item").asText().equals("minecraft:gunpowder")).count());
        assertEquals(output, recipe.path("result").path("item").asText());
        assertEquals(1, recipe.path("result").path("count").asInt());
    }

    private static List<String> readStrings(final JsonNode values) {
        final List<String> result = new ArrayList<>();
        values.forEach(value -> result.add(value.asText()));
        return result;
    }
}
