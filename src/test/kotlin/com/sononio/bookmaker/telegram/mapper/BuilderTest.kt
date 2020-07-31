package com.sononio.bookmaker.telegram.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class BuilderTest {

    @Test
    fun withShouldSaveInfoAboutProperty() {
        val builder = Mapper<Container>()
        builder.with(Container::a, "TestValue")

        assertEquals("TestValue", builder.getField(Container::a))
    }

    @Test
    fun buildShouldCreateObjectByPrimaryConstructor() {
        val builder = Mapper<Container>()
                .with(Container::a, "TestValueA")
                .with(Container::b, "TestValueB")
                .with(Container::c, "TestValueC")
        val container = builder.map()

        assertAll(
                { assertEquals("TestValueA", container.a) },
                { assertEquals("TestValueB", container.b) },
                { assertEquals("TestValueC", container.c) }
        )
    }

    @Test
    fun buildShouldSetAllTheProperties() {
        val builder = Mapper<Container>()
                .with(Container::a, "TestValueA")
                .with(Container::b, "TestValueB")
                .with(Container::c, "TestValueC")
                .with(Container::d, "TestValueD")
        val container = builder.map()

        assertAll(
                { assertEquals("TestValueA", container.a) },
                { assertEquals("TestValueB", container.b) },
                { assertEquals("TestValueC", container.c) },
                { assertEquals("TestValueD", container.d) }
        )
    }

    @Test
    fun buildShouldUseBasedOn() {
        val containerBasedOn = Container("TestValueA", "TestValueB", "TestValueC")

        val builder = Mapper<Container>()
                .withBasedOn(containerBasedOn)
                .with(Container::a, "TestValueAChanged")
        val container = builder.map()

        assertAll(
                { assertEquals("TestValueAChanged", container.a) },
                { assertEquals("TestValueB", container.b) },
                { assertEquals("TestValueC", container.c) }
        )
    }

    class Container(
            var a: String,
            var b: String,
            var c: String,
            var d: String? = null) {

        constructor(a: String, b: String) : this("fakeA", "fakeB", "fakeC")

    }
}