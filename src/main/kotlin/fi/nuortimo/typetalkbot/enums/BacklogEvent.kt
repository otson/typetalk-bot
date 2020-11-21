package fi.nuortimo.typetalkbot.enums

enum class BacklogEvent(type: Int) {
    NEW_ISSUE(1);
    var value: Int = type
}