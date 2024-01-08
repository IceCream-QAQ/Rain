rootProject.name = "Rain"

fun includeProject(name: String, dir: String? = null) {
    include(name)
    dir?.let { project(name).projectDir = file(it) }
}

includeProject(":function", "tools/function")
includeProject(":api", "tools/api")

includeProject(":classloader", "enhance/classloader")
includeProject(":hook", "enhance/hook")

includeProject(":di", "module/di")
includeProject(":event", "module/event")
includeProject(":job", "module/job")
includeProject(":controller", "module/controller")


includeProject(":application", "module/application")


fun test(name: String) {
    includeProject(":test-$name", "test/$name")
}
test("base")