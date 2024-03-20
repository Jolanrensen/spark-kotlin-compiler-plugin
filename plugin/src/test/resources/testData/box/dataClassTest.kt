package foo.bar

annotation class Sparkify

fun box(): String {
    val user = User()
    val name = User::class.java.getMethod("name").invoke(user)
    val age = User::class.java.getMethod("age").invoke(user)

    if (name != "John Doe" || age != 25) {
        return "Could not invoke functions name() and age() from Java"
    }

    try {
        val normalUser = NormalUser()
        val name = NormalUser::class.java.getMethod("name").invoke(user)
        val age = NormalUser::class.java.getMethod("age").invoke(user)
    } catch (e: Exception) {
        return "OK"
    }

    return "Fail"
}

@Sparkify
data class User(
    val name: String = "John Doe",
    val age: Int = 25,
)

data class NormalUser(
    val name: String = "John Doe",
    val age: Int = 25,
)