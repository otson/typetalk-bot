package fi.nuortimo.typetalkbot.enums

enum class Command {
    HELLO,
    TODAY,
    UNSUPPORTED,
    SUB;

    companion object {
        const val PREFIX: Char = '!'

        private fun isCommand(message: String): Boolean {
            return message.startsWith(PREFIX)
        }

        fun getCommand(message: String): Command? {
            if (!isCommand(message)) return null
            val cmd = message.split(" ")[0].drop(1).toUpperCase()
            return values().firstOrNull { it.name == cmd } ?: UNSUPPORTED
        }
    }
}