package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.enums.Command.Companion.PREFIX
import fi.nuortimo.typetalkbot.enums.Command.Companion.getCommand
import fi.nuortimo.typetalkbot.enums.Command.HELLO
import fi.nuortimo.typetalkbot.enums.Command.UNSUPPORTED
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("Command")
class CommandUT : UT {

    @Test
    @DisplayName("Unsupported command returns unsupported")
    fun unsupportedCommandReturnsUnsupported() {
        assertThat(getCommand("${PREFIX}notCommand"), equalTo(UNSUPPORTED))
    }

    @Test
    @DisplayName("Supported command returns command")
    fun supportedCommandReturnsCommand() {
        assertThat(getCommand("${PREFIX}hello"), equalTo(HELLO))
    }

    @Test
    @DisplayName("First word is parsed as command")
    fun firstWordIsParsedAsCommand() {
        assertThat(getCommand("${PREFIX}hello there"), equalTo(HELLO))
    }

    @Test
    @DisplayName("Not command returns null")
    fun notCommandReturnsNull() {
        assertThat(getCommand("hello"), nullValue())
    }
}