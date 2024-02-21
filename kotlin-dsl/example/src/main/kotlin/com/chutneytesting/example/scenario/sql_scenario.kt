package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.AssertAction
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SqlAction
import com.chutneytesting.kotlin.dsl.spEL

const val SQL_TARGET_NAME = "MOVIES_TARGET"
const val MOVIES_TABLE_NAME = "MOVIES"


data class Film(
    val title: String,
    val director: String,
    val year: String,
    val rating: String
)

var MOVIES = listOf(
    Film(title = "Castle in the Sky", director = "Hayao Miyazaki", year = "1986-08-02", rating = "78"),
    Film(title = "Grave of the Fireflies", director = "Isao Takahata", year = "1988-04-16", rating = "94"),
    Film(title = "My Neighbor Totoro", director = "Hayao Miyazaki", year = "1988-04-16", rating = "86")
)

val sql_scenario = Scenario(title = "Films library") {
    Given("I insert films") {
        SqlAction(
            target = SQL_TARGET_NAME,
            statements = listOf(
                generateInsertStatement(MOVIES)),
            validations = mapOf(
                "allFilmsWereInserted" to "affectedRows.equals(${MOVIES.size})".spEL()
            )
        )
    }

    When("I search films of my favorite director") {
        SqlAction(
            target = SQL_TARGET_NAME,
            statements = listOf(
                "SELECT * FROM $MOVIES_TABLE_NAME WHERE DIRECTOR = 'Hayao Miyazaki'"
            ),
            outputs = mapOf(
                "favoriteFilmsCount" to "rows.count()".spEL(),
                "bestMoviesTitles" to "rows.get('title')".spEL()
            )
        )
    }

    Then("I check that got required films") {
        AssertAction(
            asserts = listOf(
                "favoriteFilmsCount.equals(2)".spEL(),
                "bestMoviesTitles.contains('Castle in the Sky')".spEL(),
                "bestMoviesTitles.contains('My Neighbor Totoro')".spEL(),
            )
        )
    }
}

private fun generateInsertStatement(films: List<Film>) = """
            INSERT INTO $MOVIES_TABLE_NAME (TITLE, DIRECTOR, RELEASED_AT, RATING)
            VALUES
            ${films.joinToString(", ") { " ('${it.title}', '${it.director}', '${it.year}', '${it.rating}' )" }}
        """.trimIndent()

