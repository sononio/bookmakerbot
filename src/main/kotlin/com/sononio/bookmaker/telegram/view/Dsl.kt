package com.sononio.bookmaker.telegram.view

object message {

    operator fun invoke(init: Plain.() -> Unit): Plain {
        val context = Plain()
        context.init()
        return context
    }
}

interface Multiline {
    fun newline(count: Int = 1) {
        if (this !is Formatted) return
        this.text += "\n".repeat(count)
    }
}

abstract class Formatted(private val surroundBy: Char?, private val count: Int = 0) {
    var text = ""
    protected var doEscape = true

    protected open fun symbolsToEscape(): Array<Char> =
            arrayOf('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!')

    private val border: String get() = surroundBy?.toString()?.repeat(count) ?: ""

    private val escapedPayload: String get() {
        if (!doEscape) return text

        val needToEscape = symbolsToEscape()
        val regexEscape = arrayOf('.', '*', '+', '?', '^', '$', '|', '[', ']', '(', ')', '{', '}')
        var tmp = text
        for (char in needToEscape) {
            val prepared = if (regexEscape.contains(char)) "\\$char" else "$char"
            val escapeMatches = prepared.toRegex().findAll(tmp)

            for ((index, match) in escapeMatches.withIndex()) {
                val charIndex = match.range.last + index
                tmp = tmp.substring(0 until charIndex) + "\\" +
                        tmp.substring(charIndex until tmp.length)
            }
        }

        return tmp
    }

    override fun toString(): String = "$border${escapedPayload}$border"
}

abstract class CommonFormatted(surroundBy: Char?, count: Int = 0) : Formatted(surroundBy, count) {

    fun bold(init: Bold.() -> Unit) = proceed(Bold(), init)
    fun italic(init: Italic.() -> Unit) = proceed(Italic(), init)
    fun strike(init: Strike.() -> Unit) = proceed(Strike(), init)
    fun plain(init: Plain.() -> Unit) = proceed(Plain(), init)

    val dot: Unit get() = plain { text = "." }

    fun <T : Any?> required(exists: T, init: CommonFormatted.() -> Unit) {
        if (exists == null) return

        init(this)
        doEscape = false
    }

    fun <T : Any?> requiredNull(exists: T, init: CommonFormatted.() -> Unit) {
        if (exists != null) return

        init(this)
        doEscape = false
    }

    fun <T : Boolean?> requiredTrue(condidion: T, init: CommonFormatted.() -> Unit) {
        if (condidion != true) return

        init(this)
        doEscape = false
    }

    fun <T : Boolean?> requiredFalse(condition: T, init: CommonFormatted.() -> Unit) {
        if (condition != false) return

        init(this)
        doEscape = false
    }

    fun import(create: () -> Plain) {
        val imported = create()
        doEscape = false
        text += imported
    }

    private fun <T : Formatted> proceed(tag: T, init: T.() -> Unit) {
        tag.init()
        doEscape = false
        text += tag
    }
}

class Plain : CommonFormatted(null), Multiline
class Bold : CommonFormatted('*', 1), Multiline
class Italic: CommonFormatted('_', 1), Multiline
class Strike: CommonFormatted('~', 1), Multiline
