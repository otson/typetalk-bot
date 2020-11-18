package fi.nuortimo.typetalkbot

import fi.nuortimo.typetalkbot.enums.Command
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("Command")
class CommandUT {

    @Test
    @DisplayName("Unsupported command returns unsupported")
    fun unsupportedCommandReturnsUnsupported() {
        assertThat(Command.getCommand("!notCommand"), equalTo(Command.UNSUPPORTED))
    }

    @Test
    @DisplayName("Supported command returns command")
    fun supportedCommandReturnsCommand() {
        assertThat(Command.getCommand("!hello"), equalTo(Command.HELLO))
    }

    @Test
    @DisplayName("First word is parsed as command")
    fun firstWordIsParsedAsCommand() {
        assertThat(Command.getCommand("!hello there"), equalTo(Command.HELLO))
    }

    @Test
    @DisplayName("Not command returns null")
    fun notCommandReturnsNull() {
        assertThat(Command.getCommand("?hello"), nullValue())
    }
}