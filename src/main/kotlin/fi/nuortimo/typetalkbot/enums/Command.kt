package fi.nuortimo.typetalkbot.enums

enum class Command {
    HELLO,
    UNSUPPORTED;

    companion object {
        private const val COMMAND_PREFIX: Char = '!'

        private fun isCommand(message: String): Boolean {
            return message.startsWith(COMMAND_PREFIX)
        }

        fun getCommand(message: String): Command? {
            if(!isCommand(message)) return null
            return when (message.split(" ")[0].drop(1).toUpperCase()) {
                HELLO.name -> HELLO
                else -> UNSUPPORTED
            }
        }
    }
}