rootProject.name = "Rain"

fun includeProject(name: String, dir: String? = null) {
    include(name)
    dir?.let { project(name).projectDir = file(it) }
}

includeProject(":function", "tools/function")
includeProject(":api", "api")

includeProject(":classloader", "enhance/classloader")
includeProject(":hook", "enhance/hook")

includeProject(":di", "di")
includeProject(":event", "event")
includeProject(":job", "job")
includeProject(":controller", "controller")


fun test(name: String) {
    includeProject(":test-$name", "test/$name")
}
test("base")